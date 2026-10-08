from funasr import AutoModel
import tempfile
import os
from config import Config
from tool.aiUtils import LocalAIClient

class AsrService:
    def __init__(self):
        self.model = AutoModel(model=Config.AsrModelPath, device=Config.AsrDevice)
        self.ai_client = LocalAIClient()

    async def recognize(self, audioBytes: bytes) -> str:
        import tempfile, os
        with tempfile.NamedTemporaryFile(delete=False, suffix=".wav") as tmp:
            tmp.write(audioBytes)
            tmp_path = tmp.name
        try:
            result = self.model.generate(tmp_path)[0]["text"] # 获取转换到的文本
            print("识别文本:", result)
            prompt = ("我把这段信息格式化，用json给我输入,输出样例："
                      "{\"id\":\"156454651\",\"name\":\"王小明\",\"phone\":\"15651333444\","
                      "\"email\":\"wangxing@qq.com\",\"price\":\"456123\",\"unit\":\"元/亩\"},"
                      "id作为身份证号,如果用户没有说邮箱地址，那么就默认姓名的全拼加上@qq.com"
                      "，如果文本不标准的话也默认转化成邮箱格式吧，你面对的可能是一个普通话不标准的用户，"
                      "请通过这些文字可以进行一个猜测，然后把大写数字转为阿拉伯数字(如果有的话),"
                      "返回的key值必须严格按照我给的样例,没有的数据默认留空:")+result
            ai_response = self.ai_client.ask(prompt=prompt)
            print("大模型回答:", ai_response)
            return ai_response
        finally:
            os.remove(tmp_path)
