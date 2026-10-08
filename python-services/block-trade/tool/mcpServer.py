from fastmcp import FastMCP
import re
from datetime import datetime
import asyncio
mcp = FastMCP()


@mcp.tool()
def getToday():
    """
        获取今天的时间，精确到秒
        """
    return datetime.today().strftime('%Y.%m.%d %H:%M:%S')



def run():
    asyncio.run(mcp.run_http_async(host="0.0.0.0", port=9000))


if __name__ == "__main__":
    # 你可以选择 http 或 streamable-http 或 sse 看你的Client配置
    # mcp.run_http(host="0.0.0.0", port=9000)
    # 或（推荐异步/高并发场景）：
    run()