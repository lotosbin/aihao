package com.yuanjingtech.aihao.content

/**
 * 主力段位评语（规格 6.1 / 6.2）。文案与规格逐字一致。
 */
object TierCopy {

    /** 男性版评语（规格 6.1）。 */
    private val MALE: Map<Tier, String> = mapOf(
        Tier.ENTRY to "你还在生活的表面浮沉，手机和短视频是你的精神食粮。别急，每个男人都是从这一步开始的。",
        Tier.INTERMEDIATE to "你开始有了自己的小爱好，钓鱼、摩托、露营……恭喜你，找到了逃离日常的出口。",
        Tier.MASTER_TEACHER to "你已经进入了‘讲究’的阶段。茶要品，咖啡要手冲，NAS要组RAID。你沉迷的不是物件，是那份掌控感。",
        Tier.MASTER to "动手能力MAX，家里没有你修不好的东西。你是朋友眼中的全能选手，也是自己心中的工匠。",
        Tier.DEMIGOD to "你开始向内探索，学编程、练书法、打太极。外面的世界再喧嚣，也打扰不了你内心的平静。",
        Tier.GREAT_GOD to "你已经活成了传说。养鱼、养鸟、打太极，看破红尘，返璞归真。你就是公园里那道最靓的风景。",
    )

    /** 女性版评语（规格 6.2）。 */
    private val FEMALE: Map<Tier, String> = mapOf(
        Tier.ENTRY to "买买买、追剧、刷短视频，你在日常中寻找小确幸。别小看这些，它们是你生活的充电站。",
        Tier.INTERMEDIATE to "你开始懂得悦己，美甲、瑜伽、探店。你沉迷的不是消费，是那个闪闪发光的自己。",
        Tier.MASTER_TEACHER to "插花、香薰、手帐，你把日子过成了诗。别人在生存，你在生活。",
        Tier.MASTER to "搞钱、理财、副业，你是真正的人间清醒。左手带娃，右手搞钱，你是自己的女王。",
        Tier.DEMIGOD to "塔罗、冥想、写网文，你开始向内探索。你信命，但更信自己。你是自己的神。",
        Tier.GREAT_GOD to "种菜、养龟、织毛衣，你完成了人生的终极断舍离。不卷了，这世界爱谁谁。",
    )

    /** 取指定版本的段位评语。 */
    fun of(gender: Gender, tier: Tier): String = when (gender) {
        Gender.MALE -> MALE.getValue(tier)
        Gender.FEMALE -> FEMALE.getValue(tier)
    }

    /** 指定版本的全部评语，按段位从低到高排列。 */
    fun all(gender: Gender): List<Pair<Tier, String>> =
        Tier.values().map { it to of(gender, it) }
}
