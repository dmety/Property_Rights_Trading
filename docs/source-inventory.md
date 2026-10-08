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
| `yolo.zip` | `third_party/yolo/` | 原包包含完整 YOLO 工具库、模型集合、数据集和标注工具。仅将植物病害桌面识别源码整理到[独立项目](https://github.com/dmety/plant-disease-detection)；原始训练集、训练产物和大部分第三方工具仍不发布。 |
| `python服务端/pose.zip` | `python-services/pose/` | 人体姿态示例整理到[独立项目](https://github.com/dmety/human-pose-demo)；去除原始人像样例、IDE 配置和超大模型权重。 |
| `emqx-5.3.2-windows-amd64.zip` | `third_party/emqx-5.3.2-windows-amd64/` | 第三方 broker 的 Windows 运行发行包，不是本项目源码；需要时单独安装 EMQX。 |
| `Lora设置软件V1.5.7.zip` | `third_party/lora-config-tool/` | 独立的 Windows 设备配置软件，不是本项目源码。原文件实际为 RAR 格式，虽扩展名为 `.zip`，已用兼容方式解压。 |
| `AI部署.zip` | `reference-materials/AI-deployment/` | 仅含部署演示视频，不属于源代码。 |

EMQX、LoRa 配置工具、部署视频、完整训练集、训练输出和语音大模型保留在本机且不进入产权交易主仓库。两个独立项目的工作副本位于被根 `.gitignore` 排除的 `separate-projects/` 目录，并各自作为 Git 仓库维护。根目录 `.gitignore` 也排除了原始压缩包和大模型权重。
