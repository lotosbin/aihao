#!/usr/bin/env python3
"""校验《沉迷啥内容体系规格》与 core 内容库的数据是否逐条一致。

背景：文档与代码同源（见 AGENTS.md），但表格数据靠人工核对容易漂移。本脚本把
「改完要核对」变成一条可执行命令，供本地与 CI 使用。

校验范围：
  1. §3 主力爱好矩阵（84 行）：段位 / 爱好名 / 生成段位名称
  2. §7 限定内容（节日 / 地域 / 品牌，共 40 条）：来源 / 爱好名 / 段位
  3. §5 隐藏标签名称（12 个）、§6 彩蛋名称（8 个）
  4. §10.2 彩蛋触发器（5 个可由选择直接判定的彩蛋）
  5. §4 段位评语（12 条，逐字比对）
  6. §1 徽章色值（5 个非渐变态的主色 / 辅色）

不校验（属人工判断）：文案质量、审核口径、交互手感。

用法：python3 tools/check-content-doc.py
退出码：0 全部一致；1 存在不一致并打印差异。
"""
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
DOC = ROOT / "docs" / "chenmisha-content-system.md"

TIER = {"ENTRY": "入门级", "INTERMEDIATE": "进阶级", "MASTER_TEACHER": "封师级",
        "MASTER": "大师级", "DEMIGOD": "封神级", "GREAT_GOD": "大神级"}
PREFIX = {"ENTRY": "青铜", "INTERMEDIATE": "白银", "MASTER_TEACHER": "黄金",
          "MASTER": "铂金", "DEMIGOD": "紫钻", "GREAT_GOD": "炫彩"}
SUFFIX = {"LAO": "佬", "SHI": "师", "JIANG": "匠", "XIA": "侠", "ZONG": "宗师", "WANG": "之王",
          "DA_REN": "达人", "NV_WANG": "女王", "XIAN_NV": "仙女",
          "SHENG_HUO_JIA": "生活家", "JUE_XING_ZHE": "觉醒者"}

problems: list[str] = []

# 章节结束标记。必须带换行，否则会命中表格分隔行里的 `---`。
RULE = "\n---\n"


def fail(message: str) -> None:
    problems.append(message)


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def section(text: str, start: str, end: str) -> str:
    return text.split(start, 1)[1].split(end, 1)[0]


def table_rows(text: str, columns: int) -> list[tuple]:
    """取 markdown 表格数据行，跳过表头与分隔行。

    判定方式：分隔行（`| :--- |`）内容只由 `|-: ` 组成；表头行紧随分隔行之前。
    注意不能用 `split("---")` 切分章节 —— 表格自身的分隔行里就含 `---`。
    """
    lines = text.splitlines()
    rows = []
    for index, line in enumerate(lines):
        if not line.startswith("|"):
            continue
        following = lines[index + 1] if index + 1 < len(lines) else ""
        if following.startswith("|") and set(following) <= set("|-: "):
            continue
        cells = [cell.strip() for cell in line.strip().strip("|").split("|")]
        if len(cells) == columns and not set("".join(cells)) <= set(":- "):
            rows.append(tuple(cells))
    return rows


def check_main_matrix(doc: str) -> None:
    """§3 主力爱好矩阵：段位 / 爱好名 / 段位名称。"""
    source = read("core/src/content/MainHobbies.kt")
    male_src, female_src = source.split("internal val FEMALE_MAIN_HOBBIES")
    pattern = re.compile(
        r'Hobby\("([^"]+)",\s*"([^"]+)",\s*(\w+),\s*([A-Za-z.]+),\s*(null|[\w.]+)'
        r'(?:,\s*rankAlias\s*=\s*"([^"]+)")?\)')

    def parse(src: str) -> list[tuple[str, str, str, str]]:
        out = []
        for hobby_id, name, tier, _gender, suffix, alias in pattern.findall(src):
            suffix = suffix.split(".")[-1]
            token = alias or name
            rank = PREFIX[tier] + token + (SUFFIX[suffix] if suffix != "null" else "")
            out.append((TIER[tier], name, rank, hobby_id))
        return out

    code = {"男性版": parse(male_src), "女性版": parse(female_src)}
    sec = section(doc, "## 3. 主力爱好矩阵", "## 4.")
    doc_male = table_rows(section(sec, "### 3.1 男性版", "### 3.2 女性版"), 4)
    doc_female = table_rows(section(sec, "### 3.2 女性版", RULE), 4)
    documented = {
        "男性版": [(r[0], r[2], r[3]) for r in doc_male],
        "女性版": [(r[0], r[2], r[3]) for r in doc_female],
    }

    for label in ("男性版", "女性版"):
        rows, doc_rows = code[label], documented[label]
        if len(rows) != 42:
            fail(f"§3：{label}爱好应为 42 条，代码实际 {len(rows)} 条")
        if len(doc_rows) != 42:
            fail(f"§3：{label}矩阵应有 42 行，文档实际 {len(doc_rows)} 行")
        for index, (code_row, doc_row) in enumerate(zip(rows, doc_rows), start=1):
            if (code_row[0], code_row[1], code_row[2]) != doc_row:
                fail(f"§3：{label}第 {index} 行不一致 → 代码 {code_row[:3]} / 文档 {doc_row}")


def check_limited(doc: str) -> None:
    """§7 限定内容：来源 / 爱好名 / 段位。"""
    source = read("core/src/content/LimitedHobbies.kt")
    multiline = re.compile(
        r'id = "([^"]+)",\s*\n\s*name = "([^"]+)",\s*\n\s*tier = (\w+),'
        r'\s*\n\s*source = (\w+),\s*\n\s*sourceLabel = "([^"]+)"')
    oneline = re.compile(
        r'limited\("([^"]+)",\s*"([^"]+)",\s*(\w+),\s*(\w+),\s*"([^"]+)"')
    code: dict[str, set] = {}
    for _id, name, tier, src, label in multiline.findall(source) + oneline.findall(source):
        code.setdefault(src, set()).add((label, name, TIER[tier]))

    sec = section(doc, "## 7. 内容扩展机制", "### 7.4")
    doc_sets = {
        "FESTIVAL": {(r[0], r[1], r[2]) for r in table_rows(section(sec, "### 7.1", "### 7.2"), 5)},
        "REGION": {(r[0], r[1], r[2]) for r in table_rows(section(sec, "### 7.2", "### 7.3"), 4)},
        "BRAND": {(r[0], r[1], r[2]) for r in table_rows(section(sec, "### 7.3", RULE), 4)},
    }

    for kind, doc_rows in doc_sets.items():
        code_rows = code.get(kind, set())
        if len(code_rows) != len(doc_rows):
            fail(f"§7：{kind} 条目数不一致 → 代码 {len(code_rows)} / 文档 {len(doc_rows)}")
        for missing in sorted(code_rows - doc_rows):
            fail(f"§7：{kind} 代码中有但文档缺失 {missing}")
        for extra in sorted(doc_rows - code_rows):
            fail(f"§7：{kind} 文档中有但代码缺失 {extra}")


def check_hidden_content(doc: str) -> None:
    """§5 隐藏标签、§6 彩蛋名称。"""
    source = read("core/src/content/HiddenContent.kt")
    code_tags = re.findall(r'HiddenTag\(\s*\n\s*id = "[^"]+",\s*\n\s*name = "([^"]+)"', source)
    code_eggs = re.findall(r'EasterEgg\(\s*\n\s*id = "[^"]+",\s*\n\s*name = "([^"]+)"', source)

    tag_rows = [r[0] for r in table_rows(section(doc, "## 5. 隐藏标签", "## 6."), 3)]
    egg_rows = [r[0] for r in table_rows(section(doc, "## 6. 彩蛋", "## 7."), 3)]

    if code_tags != tag_rows:
        fail(f"§5：隐藏标签名称不一致 → 代码 {code_tags} / 文档 {tag_rows}")
    if code_eggs != egg_rows:
        fail(f"§6：彩蛋名称不一致 → 代码 {code_eggs} / 文档 {egg_rows}")


def check_egg_triggers(doc: str) -> None:
    """§10.2 可由选择直接触发的彩蛋。"""
    source = read("core/src/content/HiddenContent.kt")
    blocks = re.findall(r'EasterEgg\((.*?)\n    \),', source, re.S)
    code_triggered = [re.search(r'name = "([^"]+)"', b).group(1)
                      for b in blocks if "trigger =" in b]

    sec = section(doc, "## 10. 段位解锁与交互机制", "## 11.")
    doc_triggered = [r[0] for r in table_rows(section(sec, "### 10.2", "### 10.3"), 3)]

    if code_triggered != doc_triggered:
        fail(f"§10.2：可触发彩蛋不一致 → 代码 {code_triggered} / 文档 {doc_triggered}")


def check_tier_copy(doc: str) -> None:
    """§4 段位评语逐字比对。"""
    source = read("core/src/content/TierCopy.kt")
    male_src, female_src = source.split("private val FEMALE")
    code = {
        "男性版": re.findall(r'Tier\.\w+ to "([^"]+)"', male_src),
        "女性版": re.findall(r'Tier\.\w+ to "([^"]+)"', female_src),
    }
    sec = section(doc, "## 4. 段位评语", "## 5.")
    doc_copy = {
        "男性版": [r[1] for r in table_rows(section(sec, "### 4.1", "### 4.2"), 2)],
        "女性版": [r[1] for r in table_rows(section(sec, "### 4.2", RULE), 2)],
    }
    for label in ("男性版", "女性版"):
        if code[label] != doc_copy[label]:
            fail(f"§4：{label}评语与代码不一致（共 {len(code[label])} 条）")


def check_badge_colors(doc: str) -> None:
    """§1 徽章色值。"""
    source = read("core/src/content/Badge.kt")
    sec = section(doc, "## 1. 段位与徽章材质", "## 2.")
    materials = {
        "COPPER": ("#CD7F32", "#8B4513"),
        "SILVER": ("#C0C0C0", "#808080"),
        "GOLD": ("#FFD700", "#DAA520"),
        "PLATINUM": ("#E5E4E2", "#A9A9A9"),
        "PURPLE_DIAMOND": ("#9400D3", "#4B0082"),
    }
    for name, (primary, secondary) in materials.items():
        block = source.split(f"{name}(")[1].split("),")[0]
        if primary not in block or secondary not in block:
            fail(f"§1：Badge.kt 的 {name} 色值不符合规格 {primary}/{secondary}")
        if primary not in sec or secondary not in sec:
            fail(f"§1：文档缺少 {name} 的色值 {primary}/{secondary}")


def main() -> int:
    doc = DOC.read_text(encoding="utf-8")
    check_main_matrix(doc)
    check_limited(doc)
    check_hidden_content(doc)
    check_egg_triggers(doc)
    check_tier_copy(doc)
    check_badge_colors(doc)

    if problems:
        print(f"✗ 文档与代码存在 {len(problems)} 处不一致：")
        for problem in problems:
            print(f"  - {problem}")
        return 1
    print("✓ 文档与代码一致（矩阵 84 / 限定 40 / 标签 12 / 彩蛋 8 / 可触发彩蛋 5 / 评语 12 / 色值）")
    return 0


if __name__ == "__main__":
    sys.exit(main())
