# 工作约定（本仓库）

## 一、语言：中文优先

- 回复、文档、代码注释、提交信息一律用中文。
- 标识符、包名、第三方 API 名、平台术语保持英文原文，不硬译
  （如 `ChenmiShaApp`、`BadgeMaterial`、`webApp`、`Layout`）。
- 中英文混排时，中英文之间留一个空格（如「使用 Compose 渲染徽章」）。
- 中文正文使用全角标点；代码与标识符保持半角。

## 二、文档规范优先

- **先读文档再动代码**：本仓库的规格以 `docs/` 为唯一口径，动手前先读相关文档。
- **文档与代码同源**：内容、口径、配置类改动必须同时更新文档与代码，不允许只改一边。
  - 例：`core/src/content/*` ↔ `docs/chenmisha-content-system.md`
- **先文档后实现**：新增模块或对外行为之前，先补设计文档（口径、边界、待确认项），再写实现。
- **不替产品做决定**：文档中必须显式标注「与原文冲突」和「待确认」的地方，并给出建议方案；
  不得把推断当成既定事实写进正文。
- **文档要可验证**：表格数据需与代码逐条对得上，改完要核对。

## 三、本仓库的既有约定

- 内容体系唯一入口：`com.yuanjingtech.aihao.content.ChenmiSha`；
  审核自检：`ContentAudit`；UI 接入：`app/shared/src/ui/ChenmiShaApp.kt`。
- `core` 不依赖任何 UI 框架，颜色等平台相关转换放在 `app/shared` 层。
- 验证命令：

  ```bash
  ./kotlin test -m core -p jvm      # 内容体系与段位名称规则
  ./kotlin test -m shared -p jvm    # 共享 UI 与既有测试
  ```

- 本机未接受 Android SDK 许可，整包 `./kotlin build` 会在 Android 目标上失败。
  验证时用 `-p jvm` / `-p wasmJs` 指定平台；**不要替用户接受许可协议**。
- 改动内容体系后，除跑测试外，还需核对 `docs/chenmisha-content-system.md`
  的表格与 `core/src/content` 的数据是否一致。
