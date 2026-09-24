package com.yuanjingtech.aihao.content

/**
 * 隐藏标签：不在段位主流程展示，只在达成条件后出现在个人页（规格「总结」提到的隐藏标签）。
 *
 * @property id 稳定标识。
 * @property name 标签名。
 * @property condition 达成条件（运营可读）。
 * @property description 标签说明文案。
 * @property secret true 表示达成前完全不可见；false 表示可见但灰显，用来给用户目标感。
 */
data class HiddenTag(
    val id: String,
    val name: String,
    val condition: String,
    val description: String,
    val secret: Boolean = false,
)

/**
 * 彩蛋：满足特殊条件时触发的额外奖励（规格「总结」提到的彩蛋）。
 *
 * @property reward 触发后发放的奖励，通常是限定量段位名称或徽章特效。
 */
data class EasterEgg(
    val id: String,
    val name: String,
    val condition: String,
    val reward: String,
    val tier: Tier? = null,
)

/** 隐藏标签库。 */
internal val HIDDEN_TAGS: List<HiddenTag> = listOf(
    HiddenTag(
        id = "tag.night-owl",
        name = "深夜玩家",
        condition = "连续 7 天在 23:00 - 03:00 之间打开小程序",
        description = "你的精神食粮在凌晨两点最香。",
    ),
    HiddenTag(
        id = "tag.gear-head",
        name = "装备党",
        condition = "同时拥有 3 个及以上装备型爱好（钓鱼、露营、骑行、HiFi、机械键盘等）",
        description = "爱好未动，装备先行。",
    ),
    HiddenTag(
        id = "tag.three-minute",
        name = "三分钟热度",
        condition = "30 天内新增 5 个及以上爱好，且其中 3 个在 7 天内归档",
        description = "什么都想试，什么都试过，这也是一种博爱。",
    ),
    HiddenTag(
        id = "tag.old-soul",
        name = "老灵魂",
        condition = "最高段位达到封神级",
        description = "年纪不一定大，心态一定稳。",
    ),
    HiddenTag(
        id = "tag.park-regular",
        name = "公园常客",
        condition = "同时拥有 2 个及以上大神级爱好",
        description = "公园的清晨，你比保洁阿姨来得还早。",
    ),
    HiddenTag(
        id = "tag.craftsman",
        name = "手作人",
        condition = "大师级及以上爱好中至少 1 个属于动手类",
        description = "别人下单，你上手。",
    ),
    HiddenTag(
        id = "tag.cloud-keeper",
        name = "云养一切",
        condition = "收藏 20 个及以上爱好，但自报少于 3 个",
        description = "收藏等于会了，先存着。",
    ),
    HiddenTag(
        id = "tag.sober-mind",
        name = "人间清醒",
        condition = "同时拥有 3 个及以上大师级爱好",
        description = "左手搞钱，右手也不闲着。",
    ),
    HiddenTag(
        id = "tag.digital-midlife",
        name = "数码中年",
        condition = "同时拥有 NAS、机械键盘、HiFi 音响",
        description = "你的快乐，要组 RAID 才装得下。",
    ),
    HiddenTag(
        id = "tag.everything-rubbable",
        name = "万物皆可盘",
        condition = "拥有任意地域限定爱好，且同时拥有盘串或文玩核桃",
        description = "手上没点东西，就坐不住。",
    ),
    HiddenTag(
        id = "tag.decluttered",
        name = "断舍离完成者",
        condition = "最高段位为大神级，且包含佛系养生",
        description = "不卷了，这世界爱谁谁。",
    ),
    HiddenTag(
        id = "tag.nominator",
        name = "提名官",
        condition = "提交的爱好提名被采纳（规格 8.3）",
        description = "这个爱好是你带进来的。",
    ),
)

/** 彩蛋库。 */
internal val EASTER_EGGS: List<EasterEgg> = listOf(
    EasterEgg(
        id = "egg.retirement-ready",
        name = "退休预备役",
        condition = "同时拥有钓鱼、品茶、太极",
        reward = "解锁限定段位名称「退休预备役」",
        tier = Tier.DEMIGOD,
    ),
    EasterEgg(
        id = "egg.wellness-host",
        name = "养生局发起人",
        condition = "同时拥有泡脚、抄经、佛系养生",
        reward = "徽章增加暖色流光描边",
        tier = Tier.GREAT_GOD,
    ),
    EasterEgg(
        id = "egg.digital-midlife",
        name = "数码中年",
        condition = "同时拥有 NAS、机械键盘、HiFi 音响",
        reward = "徽章刻上「RAID 已就绪」小字",
        tier = Tier.MASTER_TEACHER,
    ),
    EasterEgg(
        id = "egg.park-king",
        name = "公园之王",
        condition = "最高段位为大神级且包含公园晨练",
        reward = "徽章叠加金色流光描边，段位名称固定为「炫彩公园之王」",
        tier = Tier.GREAT_GOD,
    ),
    EasterEgg(
        id = "egg.midnight-show",
        name = "午夜场",
        condition = "生日当天 00:00 - 02:00 之间打开小程序",
        reward = "当日徽章切换为星光特效",
    ),
    EasterEgg(
        id = "egg.max-level",
        name = "满级人类",
        condition = "六个段位全部点亮",
        reward = "解锁炫彩流光动态边框，称号「沉迷之神」",
        tier = Tier.GREAT_GOD,
    ),
    EasterEgg(
        id = "egg.crossover",
        name = "跨界玩家",
        condition = "男女双版爱好各拥有 2 个及以上",
        reward = "点亮「双修」隐藏标签",
    ),
    EasterEgg(
        id = "egg.co-creator",
        name = "共创者",
        condition = "提名的新爱好被采纳上线（规格 8.3）",
        reward = "发放限定徽章「最佳提名」",
    ),
)
