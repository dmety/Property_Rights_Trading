from pydantic import BaseModel

class AsrResponse(BaseModel):
    code: int
    text: str = ""
    msg: str = ""
