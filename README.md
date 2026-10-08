# Agricultural Property Rights Easy Chain

农业产权交易与确权平台（项目客户端名称为 BlockTrade）。源码由客户端、业务后端、区块链合约、数据库结构和一个可选的 AI 语音/MCP 服务组成。

## 项目组成

| 目录 | 内容 | 技术栈 |
| --- | --- | --- |
| `mobile/` | Android 客户端 | Java、Android Gradle Plugin 8.4、compileSdk 34 |
| `backend/` | 业务 API、用户与产权交易、设备数据接口 | Java 8、Spring Boot 2.6、Maven、MyBatis Plus |
| `contracts/` | 产权、项目交易、DID 等合约及 ABI | Solidity 0.4.x–0.6.10、FISCO BCOS |
| `database/` | 不含演示用户数据的 MySQL 建表脚本 | MySQL |
| `python-services/block-trade/` | 可选语音识别、AI 对话和 MCP 服务 | Python、FastAPI、FunASR、OpenAI-compatible API |

原始压缩包已在工作目录解压。YOLO/植物病害识别、人体姿态演示、EMQX 运行时、LoRa 配置工具和部署视频与产权交易主流程分离，留在本机的忽略目录中，不会随本仓库提交。体积约 2.9 GB 的语音模型也不入库。

## 本地运行

### 1. 数据库

创建 MySQL 数据库 `blockexplore`，执行 [`database/schema.sql`](database/schema.sql)。脚本只包含表结构，不包含原压缩包中的演示账号、联系方式和设备遥测数据。

### 2. 后端

需要 Java 8、Maven、MySQL、FISCO BCOS/WeBASE 环境；若使用设备消息功能，还需要 MQTT broker。后端的数据库和 MQTT 地址从环境变量读取：

| 变量 | 用途 | 默认值 |
| --- | --- | --- |
| `DB_URL` | JDBC 地址 | `jdbc:mysql://127.0.0.1:3306/blockexplore` |
| `DB_USERNAME` / `DB_PASSWORD` | 数据库登录 | `root` / 空 |
| `MQTT_HOST_URL` | MQTT broker | `tcp://127.0.0.1:1883` |
| `MQTT_USERNAME` / `MQTT_PASSWORD` | MQTT 登录 | 空 |
| `WEB_BASE_URL` | WeBASE 前端地址 | `http://127.0.0.1:5002` |
| `BACKEND_BASE_URL` | 后端对外地址 | `http://127.0.0.1:8080` |
| `BLOCKCHAIN_ADMIN_PRIVATE_KEY` | 链上管理账户私钥 | 必须在本机设置 |
| `ADMIN_ADDRESS`、`SYSTEM_CONTRACT_ADDRESS`、`DID_CONTRACT_ADDRESS`、`RIGHT_CONTRACT_ADDRESS`、`PROJECT_CONTRACT_ADDRESS` | 部署账户和合约地址 | 部署后设置 |
| `MAIL_USERNAME`、`MAIL_PASSWORD`、`MAIL_SMTP_HOST` | 邮件发送配置 | 按需设置 |

在 `backend/` 目录执行 `mvn spring-boot:run`。合约 ABI 已保留；各合约地址和管理账户应按自己的链环境配置。

### 3. Android 客户端

使用 Android Studio 打开 `mobile/`。先将 [`mobile/local.properties.example`](mobile/local.properties.example) 复制为 `mobile/local.properties`，填写自己的后端地址及所需 AI/讯飞配置。该本地配置不会提交到 Git。模拟器访问宿主机通常使用 `10.0.2.2`；真机应填写开发电脑在局域网中的地址。

### 4. 可选 Python AI 服务

进入 `python-services/block-trade/`，按该目录 README 安装依赖。设置 `OPENAI_API_KEY`（或兼容服务的密钥）、模型服务地址以及本地 ASR 模型路径后，再运行 `python main.py`。默认模型文件不随仓库提交，详见该服务 README。

## 配置与数据安全

仓库不应保存数据库/MQTT 密码、邮件授权码、API token、链上私钥、个人数据或设备采集记录。对应配置已改为环境变量或 `mobile/local.properties`。请勿把真实密钥写回受版本控制的文件。

压缩包来源、拆分判断和未纳入目标仓库的内容见 [`docs/source-inventory.md`](docs/source-inventory.md)。
