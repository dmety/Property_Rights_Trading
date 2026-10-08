import requests
from config import Config

class LocalAIClient:
    def __init__(self, base_url=Config.MyAiURL, model="mod", temperature=0.1):
        self.base_url = base_url
        self.model = model
        self.temperature = temperature

    def ask(self, prompt, system_msg=None, history=None, stream=False):
        """
        prompt: 用户输入内容
        system_msg: 系统指令（可选）
        history: 历史对话 [{"role": "user", "content": "..."}]  (可选)
        stream: 是否流式输出（默认False）
        """
        # 构造messages
        messages = []
        if system_msg:
            messages.append({"role": "system", "content": system_msg})
        if history:
            messages.extend(history)
        messages.append({"role": "user", "content": prompt})

        payload = {
            "model": self.model,
            "messages": messages,
            "temperature": self.temperature,
            "stream": stream
        }
        try:
            resp = requests.post(self.base_url, json=payload, timeout=180)
            resp.raise_for_status()
            result = resp.json()
            # 通常大模型返回如下结构
            # {'choices': [{'message': {'role': 'assistant', 'content': 'xxx'}}]}
            return result['choices'][0]['message']['content']
        except Exception as e:
            print("请求大模型出错:", e)
            return None
