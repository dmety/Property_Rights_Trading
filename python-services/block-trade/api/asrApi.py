from fastapi import APIRouter, File, UploadFile
from fastapi.responses import JSONResponse
from service.asrService import AsrService
from model.asrModel import AsrResponse

router = APIRouter()
asrService = AsrService()

@router.post("", response_model=AsrResponse)
async def recognizeApi(audio: UploadFile = File(...)):
    try:
        content = await audio.read()
        text = await asrService.recognize(content)
        return AsrResponse(code=0, text=text)
    except Exception as e:
        return AsrResponse(code=1, msg=str(e))
