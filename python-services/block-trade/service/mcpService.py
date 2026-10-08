from openai import OpenAI, OpenAIError
from fastmcp import Client
import json
import logging
from config import Config
import asyncio

logger = logging.getLogger(__name__)

class McpService:
    def __init__(self):
        self.model = Config.MCPModelName
        self.client = OpenAI(
            api_key=Config.OpenAIApiKey,
            base_url=Config.OpenAIApiBaseUrl
        )
        self.script = Config.MCPModelScript

    async def chat(self, messages):
        # 1. 先获取工具列表（短 session）
        async with Client(self.script) as session:
            try:
                toolsList = await session.list_tools()
                tools = [
                    {
                        "type": "function",
                        "function": {
                            "name": tool.name,
                            "description": tool.description,
                            "input_schema": tool.inputSchema,
                        }
                    }
                    for tool in toolsList
                ]
            except Exception as e:
                logger.error(f"Failed to prepare tools: {str(e)}")
                tools = []

        # 2. openai消息生成（必须线程池方式，不进任何 async with）
        loop = asyncio.get_running_loop()
        try:
            response = await loop.run_in_executor(
                None,
                lambda: self.client.chat.completions.create(
                    model=self.model,
                    messages=messages,
                    tools=tools,
                )
            )
        except OpenAIError as e:
            logger.error(f"OpenAI API error: {str(e)}")
            return {"role": "assistant", "content": "Service temporarily unavailable"}
        except Exception as e:
            logger.error(f"Unexpected error: {str(e)}")
            return {"role": "assistant", "content": "Internal server error"}

        if not response.choices:
            return {"role": "assistant", "content": "No response from model"}
        message = response.choices[0].message

        # 3. 无工具调用，直接返回
        if message.content and not message.tool_calls:
            return {"role": "assistant", "content": message.content}

        # 4. 工具调用阶段
        if message.tool_calls:
            for tool_call in message.tool_calls:
                tool_name = tool_call.function.name
                try:
                    arguments = json.loads(tool_call.function.arguments)
                    async with Client(self.script) as session:
                        result = await session.call_tool(tool_name, arguments)
                    # 把工具结果和tool调用描述都append到messages里
                    messages.append({
                        "role": "assistant",
                        "content": None,
                        "tool_calls": [
                            {
                                "id": tool_call.id,
                                "type": "function",
                                "function": {
                                    "name": tool_name,
                                    "arguments": tool_call.function.arguments
                                }
                            }
                        ]
                    })
                    messages.append({
                        "role": "tool",
                        "content": result[0].text if result else "No result",
                        "name": tool_name,
                        "tool_call_id": tool_call.id
                    })
                except Exception as e:
                    logger.error(f"Tool call error: {str(e)}")
            # 关键：带上工具结果，再次让模型生成“最终回复”！！！
            try:
                final_response = await loop.run_in_executor(
                    None,
                    lambda: self.client.chat.completions.create(
                        model=self.model,
                        messages=messages,
                        tools=tools,
                    )
                )
                if not final_response.choices:
                    return {"role": "assistant", "content": "No response after tool"}
                final_message = final_response.choices[0].message
                return {"role": "assistant", "content": final_message.content}
            except Exception as e:
                logger.error(f"Final openai after tool error: {str(e)}")
                return {"role": "assistant", "content": "工具调用完成但模型未能回复"}
        # 兜底
        return {"role": "assistant", "content": message.content if message.content else "No content"}