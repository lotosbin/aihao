# 部署到 GitHub Pages

> 本文说明「沉迷啥」Web 应用（Kotlin/Wasm）如何通过 GitHub Actions 发布到 GitHub Pages。
> 工作流文件：`.github/workflows/deploy-webapp-pages.yml`。

## 1. 前置条件（首次需要手动做一次）

仓库 **Settings → Pages → Build and deployment → Source** 选择 **GitHub Actions**。

没有这一步，`deploy` 步骤会报 `Pages is not enabled`。工作流刻意没有使用
`actions/configure-pages` 的自动开启能力，因为那需要仓库管理员权限的令牌，
在受限环境下会给出更难排查的报错。

部署地址（项目站点，子路径）：

```
https://<owner>.github.io/<repo>/
```

以本仓库为例：`https://lotosbin.github.io/aihao/`

## 2. 工作流做什么

| 阶段 | 内容 |
| :--- | :--- |
| 触发 | 推送到 `main`（限 `app/` `core/` `server/` `docs/` `tools/` 等影响产物的路径）、或在 Actions 页面手动触发 |
| 自检 | `python3 tools/check-content-doc.py` —— 文档与代码逐条比对，不一致即中断 |
| 测试 | `./kotlin test -m core -p jvm` —— 内容体系、解锁规则与段位名称 |
| 构建 | `./kotlin build -m webApp -p wasmJs -v release` |
| 组装 | 把构建产物复制到 `dist/`，并写入 `.nojekyll` |
| 部署 | `actions/upload-pages-artifact@v3` + `actions/deploy-pages@v4` |

自检与测试放在部署之前：**测试不过就不发版**，避免把坏内容推到线上。

## 3. 两个刻意的技术选择

### 3.1 为什么是 `-m webApp -p wasmJs`

CI 里没有、也不该有 Android SDK 许可；不限定平台时构建会去碰 Android 目标并失败
（本地同理，见 AGENTS.md）。Web 部署只需要 `wasmJs` 一个平台。

### 3.2 为什么产物路径写死在 `build/tasks/...`

`./kotlin package` **目前不支持 wasm-js 应用**，直接报 `No package tasks were found`：

```
$ ./kotlin package -m webApp -p wasmJs
ERROR: No package tasks were found
```

因此只能取构建任务目录：

```
build/tasks/_webApp_buildWasmJsAppWasmJsRelease/
```

目录名由「模块 + 任务 + 平台 + 变体」拼成，属于工具链的内部约定，**升级工具链后可能变化**。
工作流里对此做了显式校验：找不到 `index.html` 就打印 `build/tasks` 下的实际目录并以非零码退出，
而不是静默传一个空产物上去。

## 4. 为什么子路径部署不需要配 base path

Pages 项目站点部署在 `/<repo>/` 下，绝对路径（以 `/` 开头）会 404。本项目的 Web 产物
**全部使用相对路径**，因此无需额外配置：

| 引用 | 实际写法 |
| :--- | :--- |
| 入口脚本 | `<script src="import-map-loader.js">`、`<script src="webApp.mjs">` |
| import map | `./vendors/@js-joda/core/dist/js-joda.esm.js` |
| skiko 运行时 | `"skiko.wasm"` |
| Compose 资源 | `composeResources/com.yuanjingtech.aihao.resources/font/lxgw_wenkai_regular.ttf` |

验证方法（构建后检查产物里有没有以 `/` 开头的资源引用）：

```bash
cd build/tasks/_webApp_buildWasmJsAppWasmJsRelease
grep -oE '"[^"]*\.(wasm|mjs|js|ttf)"' index.html import-map-loader.js webApp.mjs skiko.mjs | sort -u
```

只要输出里没有 `"/xxx"` 形式的绝对路径，`/<repo>/` 子路径部署就是安全的。

## 5. 产物构成

release 产物约 36 MB（目录），主要文件：

| 文件 | 体积 | 说明 |
| :--- | :--- | :--- |
| `webApp.wasm` | 约 9.0 MB | 应用本体。**release 变体比 debug 小得多**（debug 约 22.7 MB） |
| `skiko.wasm` | 约 8.6 MB | Compose 的渲染运行时 |
| `lxgw_wenkai_regular.ttf` | 3.55 MB | 内置中文字体子集（见 `docs/字体与中文显示.md`） |
| `vendors/` | 约 1 MB | npm 依赖（`@js-joda/core`） |

首屏需要下载 wasm 与字体，合计约 18 MB，首次打开会慢一些。

**MIME 不是阻塞项**：产物里的 wasm 加载器是 Emscripten 生成的，
先尝试 `WebAssembly.instantiateStreaming`，失败后回退到 `arrayBuffer` + 实例化。
因此即使 Pages 没有返回 `application/wasm`，应用仍能启动。

## 6. 本地复现

```bash
python3 tools/check-content-doc.py            # 文档自检
./kotlin test -m core -p jvm                  # 测试
./kotlin build -m webApp -p wasmJs -v release # 构建
ls build/tasks/_webApp_buildWasmJsAppWasmJsRelease/
```

想在本地预览 release 产物（等价于 Pages 上的效果）：

```bash
cd build/tasks/_webApp_buildWasmJsAppWasmJsRelease
python3 -m http.server 8000
# 打开 http://127.0.0.1:8000/
```

注意：必须通过 HTTP 访问，直接 `file://` 打开会因为模块与 wasm 的跨域限制而失败。

## 7. 排查

| 现象 | 原因与处理 |
| :--- | :--- |
| `Pages is not enabled` | 见 §1，去 Settings → Pages 把 Source 设为 GitHub Actions |
| `未找到 Web 产物目录` | 工具链升级改了产物目录名；按报错里打印的 `build/tasks` 实际目录更新工作流 |
| 页面能打开但资源 404 | 检查是否出现了绝对路径引用（§4 的 grep），或在仓库名变更后未重新部署 |
| 中文字显示成方块 | 字体资源缺失；确认 `composeResources/.../lxgw_wenkai_regular.ttf` 在产物里，且子集覆盖了该文案用字（跑 `tools/subset-lxgw-font.sh`） |
| 部署成功但看到旧内容 | Pages CDN 有缓存，等待一两分钟或强制刷新（Shift + 重新加载） |

## 8. 重新部署与回滚

- **重新部署当前 `main`**：Actions → `Deploy webApp to GitHub Pages` → Run workflow。
- **回滚**：`git revert` 出问题的提交再推送，工作流会重新构建并发布；
  Pages 的每一次部署都是一次 artifact 覆盖，没有历史版本可切换。
