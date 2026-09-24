package com.yuanjingtech.aihao.content

/**
 * 身份后缀（规格 5.3「身份后缀库」）。
 *
 * @property canonical 后缀库中的规范写法。
 * @property attached 拼接到爱好名称后的实际写法。个别后缀需要扩展才顺口，
 *   例如「宗」→「宗师」（紫钻太极宗师）、「王」→「之王」（炫彩公园之王）。
 */
enum class IdentitySuffix(val canonical: String, val attached: String) {
    LAO("佬", "佬"),
    SHI("师", "师"),
    JIANG("匠", "匠"),
    XIA("侠", "侠"),
    ZONG("宗", "宗师"),
    WANG("王", "之王"),
    NV_WANG("女王", "女王"),
    DA_REN("达人", "达人"),
    XIAN_NV("仙女", "仙女"),
    SHENG_HUO_JIA("生活家", "生活家"),
    JUE_XING_ZHE("觉醒者", "觉醒者"),
    ;

    companion object {
        /** 男女通用条目可使用的后缀（两个后缀库的交集）。 */
        val SHARED: List<IdentitySuffix> = listOf(SHI)
    }
}

/**
 * 男女双版（规格 5.3、6.1、6.2）。
 *
 * @property suffixes 该版本的身份后缀库，顺序即默认优先级。
 */
enum class Gender(val displayName: String, val suffixes: List<IdentitySuffix>) {
    MALE(
        displayName = "男性版",
        suffixes = listOf(
            IdentitySuffix.LAO,
            IdentitySuffix.SHI,
            IdentitySuffix.JIANG,
            IdentitySuffix.XIA,
            IdentitySuffix.ZONG,
            IdentitySuffix.WANG,
        ),
    ),
    FEMALE(
        displayName = "女性版",
        suffixes = listOf(
            IdentitySuffix.SHI,
            IdentitySuffix.NV_WANG,
            IdentitySuffix.DA_REN,
            IdentitySuffix.XIAN_NV,
            IdentitySuffix.SHENG_HUO_JIA,
            IdentitySuffix.JUE_XING_ZHE,
        ),
    ),
    ;

    /** 跨版本兜底时使用的首选后缀（男性版「佬」、女性版「师」）。 */
    val preferredSuffix: IdentitySuffix get() = suffixes.first()

    /** 该后缀是否属于本版本的后缀库。 */
    fun supports(suffix: IdentitySuffix): Boolean = suffixes.contains(suffix)
}
