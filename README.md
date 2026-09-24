This is a Kotlin Multiplatform project targeting Android, Desktop (JVM), iOS, Web, Server, built with the [Kotlin Toolchain](https://kotlin-toolchain.org/dev/).

- [/app/androidApp](./app/androidApp) contains the Android application.
- [/app/desktopApp](./app/desktopApp) contains the desktop (JVM) application.
- [/app/iosApp](./app/iosApp) contains the iOS application.
- [/app/shared](./app/shared) holds the code shared across your applications — Compose UI, business logic, and platform-specific implementations. [src](./app/shared/src) is for common code; the sibling `src@<platform>` folders (for example `src@android`) hold code compiled only for the platform named in the folder.
- [/app/webApp](./app/webApp) contains the web application, compiled to WebAssembly with Kotlin/Wasm.
- [/core](./core) holds the code shared across the project: the greeting helper plus the 【沉迷啥】content system (`core/src/content`).
- [/server](./server) contains the Ktor server application.
- [/docs](./docs) holds the content specification. See [沉迷啥内容体系规格](./docs/chenmisha-content-system.md) for the tier, badge and hobby-matrix spec that `core/src/content` implements.

### 【沉迷啥】内容体系

The hobby content system (6 tiers × 2 gender editions × 7 hobbies, plus festival / regional / brand limited content, hidden tags and easter eggs) lives in `core/src/content` and is consumed by `app/shared/src/ui/ChenmiShaApp.kt`. `core` has no UI dependency, so the server can serve the same content source.

- Spec: [docs/chenmisha-content-system.md](./docs/chenmisha-content-system.md) — the single source of truth for copy; content changes must update the code and this doc together.
- Entry point: `com.yuanjingtech.aihao.content.ChenmiSha`.
- Content lint (spec 8.1): `com.yuanjingtech.aihao.content.ContentAudit`, run by `core/test/ContentLibraryTest.kt`.
- Chinese font: 霞鹜文楷 (LXGW WenKai) is bundled as a subset and applied to all Material 3 text styles by `ChenmiShaTheme`; see [docs/字体与中文显示.md](./docs/字体与中文显示.md) and regenerate with `bash tools/subset-lxgw-font.sh`.
- Unlock rules: pick hobbies to light up badges, implemented in `core/src/content/Progression.kt` and covered by `core/test/ProgressionTest.kt`.
- Doc/code consistency: `python3 tools/check-content-doc.py` compares every table in the spec against the content library.
- Deployment: pushes to `main` build the Web app and publish it to GitHub Pages — see [docs/部署到 GitHub Pages.md](./docs/部署到 GitHub Pages.md) and `.github/workflows/deploy-webapp-pages.yml` (one-time setup: set Pages source to "GitHub Actions").
- Preview the badges: `./kotlin run -m desktopApp` or `./kotlin run -m webApp`.

The `kotlin` (macOS/Linux) and `kotlin.bat` (Windows) scripts in the project root are self-bootstrapping wrappers for the Kotlin Toolchain: they download the pinned toolchain version on first use, so no separate installation is required. Build the whole project with `./kotlin build`.

### Running

Use the run configurations provided by the run widget in your IDE's toolbar. You can also run each module from the command line:

- Android app: `./kotlin run -m androidApp`
- Desktop app: `./kotlin run -m desktopApp`
- iOS app: `./kotlin run -m iosApp`
- Web app: `./kotlin run -m webApp`
- Server: `./kotlin run -m server`

### Testing

Run the project's tests with `./kotlin test`, or `./kotlin test -m <module>` for a single module.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html), [Kotlin Toolchain](https://kotlin-toolchain.org/dev/), [Compose Multiplatform](https://kotlinlang.org/compose-multiplatform/), [Kotlin/Wasm](https://kotl.in/wasm/), [Ktor](https://ktor.io/).
