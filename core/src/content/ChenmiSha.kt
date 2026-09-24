package com.yuanjingtech.aihao.content

/**
 * 内容体系对外的唯一入口。app/shared 与 server 都通过它读取爱好、评语与段位名称，
 * 保证多端共用同一份内容源（规格「总结」：爱好内容体系是核心资产）。
 */
object ChenmiSha {

    // ---------- 主力爱好矩阵（规格 5.1） ----------

    /** 指定版本的全部主力爱好。 */
    fun allMainHobbies(gender: Gender): List<Hobby> = when (gender) {
        Gender.MALE -> MALE_MAIN_HOBBIES
        Gender.FEMALE -> FEMALE_MAIN_HOBBIES
    }

    /** 指定版本、指定段位的主力爱好，按展示顺序返回。 */
    fun mainHobbies(gender: Gender, tier: Tier): List<Hobby> =
        allMainHobbies(gender).filter { it.tier == tier }

    /** 遍历六个段位取主力爱好。 */
    fun mainHobbiesByTier(gender: Gender): List<Pair<Tier, List<Hobby>>> =
        Tier.values().map { it to mainHobbies(gender, it) }

    // ---------- 评语（规格 6.1 / 6.2） ----------

    /** 指定版本、指定段位的评语。 */
    fun tierCopy(gender: Gender, tier: Tier): String = TierCopy.of(gender, tier)

    /** 指定版本的全部评语，按段位从低到高。 */
    fun tierCopies(gender: Gender): List<Pair<Tier, String>> = TierCopy.all(gender)

    // ---------- 限定内容（规格 7.1 / 7.2 / 7.3） ----------

    /** 全部限定爱好。 */
    fun limitedHobbies(): List<LimitedHobby> = LIMITED_HOBBIES

    /** 节日限定。 */
    fun festivalHobbies(): List<LimitedHobby> = FESTIVAL_HOBBIES

    /** 地域限定。 */
    fun regionHobbies(): List<LimitedHobby> = REGION_HOBBIES

    /** 品牌联名限定。 */
    fun brandHobbies(): List<LimitedHobby> = BRAND_HOBBIES

    /** 按节日名取限定爱好，如「春节」。 */
    fun festivalHobbies(name: String): List<LimitedHobby> =
        FESTIVAL_HOBBIES.filter { it.sourceLabel == name }

    /** 按地域名取限定爱好，如「东北」。 */
    fun regionHobbies(label: String): List<LimitedHobby> =
        REGION_HOBBIES.filter { it.sourceLabel == label }

    /** 按品牌类型取限定爱好，如「渔具品牌」。 */
    fun brandHobbies(label: String): List<LimitedHobby> =
        BRAND_HOBBIES.filter { it.sourceLabel == label }

    /** 适用于指定版本的限定爱好（含男女通用条目）。 */
    fun limitedHobbiesFor(gender: Gender): List<LimitedHobby> =
        LIMITED_HOBBIES.filter { it.hobby.appliesTo(gender) }

    /**
     * 指定公历日期上自动生效的限定爱好。
     *
     * 只有配置了 [LimitedHobby.window] 的条目会命中；农历节日与赛事类条目
     * （春节、元宵、端午、中秋、世界杯、奥运会）需要运营按当年日历另行配置。
     */
    fun activeLimitedHobbies(month: Int, day: Int): List<LimitedHobby> =
        LIMITED_HOBBIES.filter { it.isActiveOn(month, day) }

    // ---------- 段位名称（规格 5.3） ----------

    /** 按用户爱好生成段位名称；爱好为空时返回 null。 */
    fun rankName(gender: Gender, hobbies: List<Hobby>): RankName? =
        RankNameGenerator.generate(gender, hobbies)

    /** 按显式段位与爱好生成段位名称。 */
    fun rankName(gender: Gender, tier: Tier, hobby: Hobby): RankName =
        RankNameGenerator.generate(gender, tier, hobby)

    /** 用户的最高段位；爱好为空时返回 null。 */
    fun topTier(hobbies: List<Hobby>): Tier? = RankNameGenerator.topTier(hobbies)

    // ---------- 隐藏标签与彩蛋 ----------

    /** 隐藏标签库。 */
    fun hiddenTags(): List<HiddenTag> = HIDDEN_TAGS

    /** 彩蛋库。 */
    fun easterEggs(): List<EasterEgg> = EASTER_EGGS

    // ---------- 查询 ----------

    /** 按 id 查爱好，覆盖主力与限定内容。 */
    fun hobbyById(id: String): Hobby? =
        MALE_MAIN_HOBBIES.firstOrNull { it.id == id }
            ?: FEMALE_MAIN_HOBBIES.firstOrNull { it.id == id }
            ?: LIMITED_HOBBIES.firstOrNull { it.hobby.id == id }?.hobby
}
