package com.yuanjingtech.aihao.content

/**
 * 爱好来源（规格 7.x）：主力矩阵，或节日 / 地域 / 品牌三类限定内容。
 */
enum class HobbySource(val displayName: String) {
    MAIN("主力爱好"),
    FESTIVAL("节日限定"),
    REGION("地域限定"),
    BRAND("品牌联名"),
}

/**
 * 一个爱好条目。
 *
 * @property id 稳定标识，用于埋点、持久化与共创提名，不随文案调整而变化。
 * @property name 展示名称。
 * @property tier 所属段位。
 * @property gender 适用版本；null 表示男女通用（限定内容里常见）。
 * @property suffix 段位名称使用的身份后缀；null 表示名称本身已经构成身份，
 *   不再缀后缀，例如「铂金木工」「炫彩佛系养生」。
 * @property source 来源，决定它是否属于限定内容。
 * @property rankAlias 段位名称中的替代名称，用于把组合调顺口，
 *   例如「公园晨练」在段位名称中只取「公园」（炫彩公园之王）。
 */
data class Hobby(
    val id: String,
    val name: String,
    val tier: Tier,
    val gender: Gender? = null,
    val suffix: IdentitySuffix? = null,
    val source: HobbySource = HobbySource.MAIN,
    val rankAlias: String? = null,
) {
    /** 段位名称中使用的名称部分。 */
    val rankToken: String get() = rankAlias ?: name

    /** 是否属于限定内容。 */
    val isLimited: Boolean get() = source != HobbySource.MAIN

    /** 该条目是否适用于指定版本。 */
    fun appliesTo(gender: Gender): Boolean = this.gender == null || this.gender == gender
}

/**
 * 节日限定的公历时间窗（规格 7.1「持续时间」）。
 *
 * 支持跨年窗口（如 12.25–1.5）：当 [startMonth]/[startDay] 晚于
 * [endMonth]/[endDay] 时，窗口跨过 12 月 31 日。
 *
 * 农历节日（春节、元宵、端午、中秋）没有固定公历日期，其 [LimitedHobby.window]
 * 为 null，仅保留 [LimitedHobby.windowLabel] 文案，由运营按当年日历配置具体窗口。
 */
data class MonthDayRange(
    val startMonth: Int,
    val startDay: Int,
    val endMonth: Int,
    val endDay: Int,
) {
    init {
        require(startMonth in 1..12 && endMonth in 1..12) { "月份必须在 1..12 之间" }
        require(startDay in 1..31 && endDay in 1..31) { "日期必须在 1..31 之间" }
    }

    /** 判断给定公历日期是否落在窗口内。 */
    fun contains(month: Int, day: Int): Boolean {
        val current = month * 100 + day
        val start = startMonth * 100 + startDay
        val end = endMonth * 100 + endDay
        return if (start <= end) current in start..end else current >= start || current <= end
    }
}

/**
 * 限定爱好条目：在 [Hobby] 之上补充触发来源与时间窗。
 *
 * @property sourceLabel 触发来源，如「春节」「东北」「渔具品牌」。
 * @property windowLabel 面向用户的持续时长文案，如「除夕前 7 天」「赛事期间」。
 * @property window 可计算的公历窗口；农历节日与赛事类为 null。
 */
data class LimitedHobby(
    val hobby: Hobby,
    val sourceLabel: String,
    val windowLabel: String? = null,
    val window: MonthDayRange? = null,
) {
    val source: HobbySource get() = hobby.source

    /** 在指定公历日期上是否生效。只有带 [window] 的条目才会自动生效。 */
    fun isActiveOn(month: Int, day: Int): Boolean = window?.contains(month, day) == true
}
