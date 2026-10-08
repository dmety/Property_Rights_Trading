# BlockTrade AI service

Optional Python service for speech recognition, AI chat, and MCP tools. It is kept as an isolated service module so the Android/Java/contract project does not depend on it to start.

Install Python dependencies from `requirements.txt`, then configure these environment variables as needed:

| Variable | Purpose | Default |
| --- | --- | --- |
| `OPENAI_API_KEY` | API credential for the OpenAI-compatible chat endpoint | empty |
| `OPENAI_API_BASE_URL` | Chat API base URL | `https://api.openai.com/v1` |
| `MCP_MODEL_NAME` | Chat model name | `gpt-4o-mini` |
| `MCP_MODEL_SCRIPT` | MCP server command | `python tool/mcpServer.py` |
| `ASR_MODEL_PATH` | Local speech model file | `asrMod/whisper/large-v3.pt` |
| `ASR_DEVICE` | Inference device | `cpu` |
| `SERVER_HOST` / `SERVER_PORT` | FastAPI bind address | `127.0.0.1:8000` |
| `LOCAL_AI_URL` | Optional local chat-completion endpoint | `http://127.0.0.1:11434/v1/chat/completions` |

The original archive contains `asrMod/whisper/large-v3.pt` (about 2.9 GB). It is intentionally excluded from Git; put the model file at the configured path or set `ASR_MODEL_PATH` to an existing local model. Do not place API credentials in `config.py`.

Run from this directory with `python main.py` after configuring the required model and service credentials.
