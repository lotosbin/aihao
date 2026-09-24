package com.yuanjingtech.aihao.content

/**
 * 徽章表面工艺（规格 5.2「特效」列）。
 */
enum class Finish(val displayName: String) {
    MATTE("哑光"),
    BRUSHED("拉丝"),
    POLISHED("抛光"),
    MIRROR("镜面"),
    STARLIGHT("星光"),
    HOLOGRAPHIC("流光"),
}

/**
 * 徽章材质与配色（规格 5.2）。
 *
 * 颜色一律以 `#RRGGBB` 字符串保存：core 模块刻意不依赖任何 UI 框架，
 * 以便 app/shared、server 与后续小程序共用同一份内容源。
 * 各端渲染时再转换为平台颜色对象（见 app/shared 的 BadgePalette.kt）。
 *
 * @property displayName 材质名（规格 5.2「材质」列）。
 * @property rankPrefix 段位名称中的材质前缀（规格 5.3）。规格示例写的是「黄金」，
 *   因此前三级与「铂金 / 紫钻 / 炫彩」保持两字对齐：青铜 / 白银 / 黄金。
 * @property primary 主色。炫彩材质取渐变起始色。
 * @property secondary 辅色。炫彩材质取渐变结束色。
 * @property gradient 徽章渐变的停靠色。非炫彩材质为「主色 → 辅色」两档；
 *   炫彩材质为多色流光，主色/辅色仅作为起止锚点。
 * @property finish 表面工艺。
 */
enum class BadgeMaterial(
    val displayName: String,
    val rankPrefix: String,
    val primary: String,
    val secondary: String,
    val gradient: List<String>,
    val finish: Finish,
) {
    COPPER(
        displayName = "铜",
        rankPrefix = "青铜",
        primary = "#CD7F32",
        secondary = "#8B4513",
        gradient = listOf("#CD7F32", "#8B4513"),
        finish = Finish.MATTE,
    ),
    SILVER(
        displayName = "银",
        rankPrefix = "白银",
        primary = "#C0C0C0",
        secondary = "#808080",
        gradient = listOf("#C0C0C0", "#808080"),
        finish = Finish.BRUSHED,
    ),
    GOLD(
        displayName = "金",
        rankPrefix = "黄金",
        primary = "#FFD700",
        secondary = "#DAA520",
        gradient = listOf("#FFD700", "#DAA520"),
        finish = Finish.POLISHED,
    ),
    PLATINUM(
        displayName = "铂金",
        rankPrefix = "铂金",
        primary = "#E5E4E2",
        secondary = "#A9A9A9",
        gradient = listOf("#E5E4E2", "#A9A9A9"),
        finish = Finish.MIRROR,
    ),
    PURPLE_DIAMOND(
        displayName = "紫钻",
        rankPrefix = "紫钻",
        primary = "#9400D3",
        secondary = "#4B0082",
        gradient = listOf("#9400D3", "#4B0082"),
        finish = Finish.STARLIGHT,
    ),
    HOLOGRAM(
        displayName = "炫彩",
        rankPrefix = "炫彩",
        primary = "#FF0080",
        secondary = "#00E5FF",
        gradient = listOf("#FF0080", "#FFD400", "#00E5FF", "#7C4DFF", "#00E676"),
        finish = Finish.HOLOGRAPHIC,
    ),
}

/**
 * 六个段位（规格 5.2 / 5.3）。枚举声明顺序即为段位高低，可直接比较。
 */
enum class Tier(val displayName: String, val material: BadgeMaterial) {
    ENTRY("入门级", BadgeMaterial.COPPER),
    INTERMEDIATE("进阶级", BadgeMaterial.SILVER),
    MASTER_TEACHER("封师级", BadgeMaterial.GOLD),
    MASTER("大师级", BadgeMaterial.PLATINUM),
    DEMIGOD("封神级", BadgeMaterial.PURPLE_DIAMOND),
    GREAT_GOD("大神级", BadgeMaterial.HOLOGRAM),
    ;

    /** 段位名称的材质前缀，如「黄金」。 */
    val rankPrefix: String get() = material.rankPrefix

    /** 徽章主色。 */
    val primaryColor: String get() = material.primary

    /** 徽章辅色。 */
    val secondaryColor: String get() = material.secondary

    /** 徽章渐变停靠色。 */
    val gradient: List<String> get() = material.gradient

    /** 徽章表面工艺。 */
    val finish: Finish get() = material.finish

    companion object {
        /** 按名称查找段位，接受「入门级」等中文名或枚举名。 */
        fun fromDisplayName(value: String): Tier? = values().firstOrNull {
            it.displayName == value || it.name.equals(value, ignoreCase = true)
        }
    }
}
