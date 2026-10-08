from fastapi import APIRouter, HTTPException
from model.chatModel import ChatRequest
from service.mcpService import McpService
from fastmcp import Client
from config import Config

router = APIRouter()
mcpService = McpService()

@router.post("/chat")
async def chatApi(request: ChatRequest):
    messages = [m.dict() for m in request.messages]
    try:
        reply = await mcpService.chat(messages)
        return {"reply": reply["content"]}
    except Exception as e:
        raise HTTPException(status_code=500, detail="Internal server error")
