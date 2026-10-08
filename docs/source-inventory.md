# 源码包盘点与拆分记录

## 纳入本项目

| 原始包 | 整理结果 | 处理 |
| --- | --- | --- |
| `合约.zip` | `contracts/` | 保留 Solidity 源码及 ABI/编译产物 |
| `后端.zip` | `backend/` | 保留 Java/Maven 源码；不纳入包内 `.git`、`target/` 和 IDE 文件 |
| `手机端.zip` | `mobile/` | 保留 Android 源码、Gradle wrapper 和运行所需 SDK 文件；排除包内 `.git`、Gradle 缓存、APK/build 产物和本机配置 |
| `数据库.zip` 与后端内 SQL | `database/schema.sql` | 以包含 `right.block_number` 字段的数据库结构为准；去掉所有示例 INSERT 数据 |
| `python服务端/block_trade_pyServer - 副本.zip` | `python-services/block-trade/` | 保留 AI 对话、MCP 和语音服务源码；模型权重与 Python 缓存不入库 |

## 已解压但从目标仓库排除

| 原始包 | 本地解压位置 | 排除原因 |
| --- | --- | --- |
| `yolo.zip` | `third_party/yolo/` | 包含完整 YOLO 工具库、模型集合、数据集/标注工具和植物病害检测样例；没有发现它与产权交易后端或客户端的接口集成。若后续确认需要病害识别，应单独整理为视觉识别项目，并独立管理模型和数据集。 |
| `python服务端/pose.zip` | `python-services/pose/` | 独立的人体姿态识别演示，不属于产权交易业务。 |
| `emqx-5.3.2-windows-amd64.zip` | `third_party/emqx-5.3.2-windows-amd64/` | 第三方 broker 的 Windows 运行发行包，不是本项目源码；需要时单独安装 EMQX。 |
| `Lora设置软件V1.5.7.zip` | `third_party/lora-config-tool/` | 独立的 Windows 设备配置软件，不是本项目源码。原文件实际为 RAR 格式，虽扩展名为 `.zip`，已用兼容方式解压。 |
| `AI部署.zip` | `reference-materials/AI-deployment/` | 仅含部署演示视频，不属于源代码。 |

以上大型或独立内容保留在本机供后续拆分，不会被加入目标 Git 仓库。根目录 `.gitignore` 已排除这些解压目录、原始压缩包和大模型权重。
