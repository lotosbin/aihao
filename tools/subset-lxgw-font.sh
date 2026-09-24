#!/usr/bin/env bash
#
# 重新生成内置中文字体子集（app/shared/composeResources/font/lxgw_wenkai_regular.ttf）。
#
# 为什么需要子集化：霞鹜文楷发行版全量 TTF 约 24 MB（46867 个字形，覆盖 GB18030 与扩展区），
# 对 Web(Wasm) 与移动端来说过重；这里裁剪到「GB2312 全集 + 仓库实际用字 + 拉丁/标点/全角」，
# 约 3.5 MB（8232 个字形），足以覆盖正常简体中文文案。
#
# 许可：霞鹜文楷采用 SIL Open Font License 1.1，其 ADDITIONAL PERMISSION 明确允许
# 为 Web 字体交付做子集化（详见 licenses/LXGWWenKai-OFL.txt）。
#
# 注意：生僻字（GB18030 扩展区）不在子集内会显示为豆腐块。若内容需要生僻字，
# 请重新运行本脚本（新内容会重新扫描进字符集），或改用发行版全量字体。
#
# 用法：
#   python3 -m venv /tmp/ftenv && /tmp/ftenv/bin/pip install fonttools brotli
#   bash tools/subset-lxgw-font.sh
#
set -euo pipefail

LXGW_VERSION="v1.522"
BASE_URL="https://github.com/lxgw/LxgwWenKai/releases/download/${LXGW_VERSION}"
WORK_DIR="${WORK_DIR:-/tmp/lxgw-subset}"
PYFTSUBSET="${PYFTSUBSET:-/tmp/ftenv/bin/pyftsubset}"
PYTHON="${PYTHON:-/tmp/ftenv/bin/python}"

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_FONT="${PROJECT_ROOT}/app/shared/composeResources/font/lxgw_wenkai_regular.ttf"

mkdir -p "${WORK_DIR}"
SRC_FONT="${WORK_DIR}/LXGWWenKai-Regular.ttf"

if [ ! -f "${SRC_FONT}" ]; then
    echo "→ 下载霞鹜文楷 ${LXGW_VERSION} 全量字体"
    curl -fL --max-time 600 -o "${SRC_FONT}" "${BASE_URL}/LXGWWenKai-Regular.ttf"
fi

echo "→ 生成字符集（GB2312 全集 + 仓库实际用字 + 拉丁/标点/全角）"
PYTHONPATH="" "${PYTHON}" - "${PROJECT_ROOT}" "${WORK_DIR}/subset-chars.txt" <<'PY'
import os, sys

root, out_path = sys.argv[1], sys.argv[2]
chars = set()

# GB2312 全集：6763 个汉字 + 符号，覆盖正常简体中文写作
for b1 in range(0xA1, 0xF8):
    for b2 in range(0xA1, 0xFF):
        try:
            chars.add(bytes([b1, b2]).decode("gb2312"))
        except UnicodeDecodeError:
            pass

# ASCII、拉丁补充、常用标点、CJK 标点、全角字符
for lo, hi in ((0x20, 0x7E), (0xA0, 0xFF), (0x2000, 0x206F), (0x3000, 0x303F), (0xFF00, 0xFFEF)):
    chars.update(chr(c) for c in range(lo, hi + 1))

# 仓库里实际出现的字符，防止 GB2312 之外的用字漏掉
for dirpath, dirnames, filenames in os.walk(root):
    dirnames[:] = [d for d in dirnames if d not in {".git", "build", "node_modules", ".kotlin"}]
    for fn in filenames:
        if not fn.endswith((".kt", ".md", ".html", ".css", ".yaml", ".txt", ".toml")):
            continue
        with open(os.path.join(dirpath, fn), encoding="utf-8", errors="ignore") as f:
            chars.update(f.read())

chars -= {"\n", "\r", "\t"}
with open(out_path, "w", encoding="utf-8") as f:
    f.write("".join(sorted(chars)))
print(f"   字符集 {len(chars)} 个")
PY

echo "→ 子集化"
"${PYFTSUBSET}" "${SRC_FONT}" \
    --text-file="${WORK_DIR}/subset-chars.txt" \
    --output-file="${OUT_FONT}" \
    --layout-features='*' --glyph-names --symbol-cmap --legacy-cmap \
    --notdef-glyph --notdef-outline --recommended-glyphs \
    --name-IDs='*' --name-legacy --name-languages='*'

echo "→ 校验覆盖（内容库与 UI 源码中的字符必须全部命中）"
PYTHONPATH="" "${PYTHON}" - "${PROJECT_ROOT}" "${OUT_FONT}" <<'PY'
import glob, os, sys
from fontTools.ttLib import TTFont

root, font_path = sys.argv[1], sys.argv[2]
cmap = set()
for table in TTFont(font_path, lazy=True)["cmap"].tables:
    cmap.update(table.cmap.keys())

targets = glob.glob(f"{root}/core/src/content/*.kt") + glob.glob(f"{root}/app/shared/src/**/*.kt", recursive=True)
missing = {}
for path in targets:
    text = open(path, encoding="utf-8").read()
    miss = {c for c in text if ord(c) > 0x2000 and ord(c) not in cmap}
    if miss:
        missing[os.path.basename(path)] = "".join(sorted(miss))

if missing:
    for name, chars in missing.items():
        print(f"   ✗ {name} 缺字: {chars}")
    sys.exit(1)
print(f"   ✓ {len(targets)} 个源文件的中文字符全部被子集覆盖")
PY

ls -l "${OUT_FONT}" | awk '{printf "→ 完成：%s（%.2f MB）\n", $9, $5/1048576}'
