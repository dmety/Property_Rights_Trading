import os
from pathlib import Path


ROOT_DIR = Path(__file__).resolve().parent


class Config:
    MCPModelName = os.getenv("MCP_MODEL_NAME", "gpt-4o-mini")
    MCPModelScript = os.getenv("MCP_MODEL_SCRIPT", "python tool/mcpServer.py")
    OpenAIApiBaseUrl = os.getenv("OPENAI_API_BASE_URL", "https://api.openai.com/v1")
    OpenAIApiKey = os.getenv("OPENAI_API_KEY", "")
    AsrModelPath = os.getenv("ASR_MODEL_PATH", str(ROOT_DIR / "asrMod" / "whisper" / "large-v3.pt"))
    AsrDevice = os.getenv("ASR_DEVICE", "cpu")
    ServerHost = os.getenv("SERVER_HOST", "127.0.0.1")
    ServerPort = int(os.getenv("SERVER_PORT", "8000"))
    MyAiURL = os.getenv("LOCAL_AI_URL", "http://127.0.0.1:11434/v1/chat/completions")
