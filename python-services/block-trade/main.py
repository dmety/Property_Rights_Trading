from fastapi import FastAPI
from api.mcpApi import router as mcpRouter
from api.asrApi import router as asrRouter
from config import Config
from tool import mcpServer
import uvicorn

app = FastAPI()
# app.include_router(mcpRouter, prefix="/mcp", tags=["MCP"])
app.include_router(asrRouter, prefix="/asr", tags=["ASR"])

if __name__ == '__main__':
    uvicorn.run("main:app", host=Config.ServerHost, port=Config.ServerPort)
