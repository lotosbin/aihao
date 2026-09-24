package com.yuanjingtech.aihao.content

import com.yuanjingtech.aihao.content.Gender.MALE
import com.yuanjingtech.aihao.content.IdentitySuffix.JIANG
import com.yuanjingtech.aihao.content.IdentitySuffix.LAO
import com.yuanjingtech.aihao.content.IdentitySuffix.SHI
import com.yuanjingtech.aihao.content.IdentitySuffix.WANG
import com.yuanjingtech.aihao.content.IdentitySuffix.XIA
import com.yuanjingtech.aihao.content.IdentitySuffix.ZONG
import com.yuanjingtech.aihao.content.Tier.DEMIGOD
import com.yuanjingtech.aihao.content.Tier.ENTRY
import com.yuanjingtech.aihao.content.Tier.GREAT_GOD
import com.yuanjingtech.aihao.content.Tier.INTERMEDIATE
import com.yuanjingtech.aihao.content.Tier.MASTER
import com.yuanjingtech.aihao.content.Tier.MASTER_TEACHER

/**
 * 男性版主力爱好矩阵（规格 5.1）。每段位 7 个，共 42 个。
 *
 * 段位内的先后顺序即展示顺序，也是段位名称生成时的稳定并列规则。
 * [Hobby.suffix] 为 null 表示该名称本身就是身份，不再缀后缀。
 */
internal val MALE_MAIN_HOBBIES: List<Hobby> = listOf(
    // 入门级 · 青铜 · 哑光 —— 生活在表面浮沉
    Hobby("m.entry.short-video", "刷短视频", ENTRY, MALE, LAO),
    Hobby("m.entry.mobile-game", "打手游", ENTRY, MALE, LAO),
    Hobby("m.entry.staying-up", "熬夜", ENTRY, MALE, XIA),
    Hobby("m.entry.instant-noodle", "泡面", ENTRY, MALE, JIANG),
    Hobby("m.entry.price-check", "网购比价", ENTRY, MALE, SHI),
    Hobby("m.entry.sofa-slouch", "沙发瘫", ENTRY, MALE, WANG),
    Hobby("m.entry.meme", "表情包收藏", ENTRY, MALE, XIA),

    // 进阶级 · 银 · 拉丝 —— 找到逃离日常的出口
    Hobby("m.intermediate.fishing", "钓鱼", INTERMEDIATE, MALE, LAO),
    Hobby("m.intermediate.motorcycle", "摩托", INTERMEDIATE, MALE, XIA),
    Hobby("m.intermediate.camping", "露营", INTERMEDIATE, MALE, SHI),
    Hobby("m.intermediate.lifting", "撸铁", INTERMEDIATE, MALE, JIANG),
    Hobby("m.intermediate.cycling", "骑行", INTERMEDIATE, MALE, XIA),
    Hobby("m.intermediate.hiking", "徒步", INTERMEDIATE, MALE, XIA),
    Hobby("m.intermediate.mystery-game", "剧本杀", INTERMEDIATE, MALE, WANG),

    // 封师级 · 金 · 抛光 —— 进入了「讲究」的阶段
    Hobby("m.teacher.tea", "品茶", MASTER_TEACHER, MALE, SHI),
    Hobby("m.teacher.pour-over", "手冲咖啡", MASTER_TEACHER, MALE, SHI),
    Hobby("m.teacher.nas", "NAS", MASTER_TEACHER, MALE, WANG),
    Hobby("m.teacher.keyboard", "机械键盘", MASTER_TEACHER, MALE, JIANG),
    Hobby("m.teacher.hifi", "HiFi音响", MASTER_TEACHER, MALE, LAO),
    Hobby("m.teacher.watch", "腕表", MASTER_TEACHER, MALE, SHI),
    Hobby("m.teacher.fountain-pen", "钢笔", MASTER_TEACHER, MALE, JIANG),

    // 大师级 · 铂金 · 镜面 —— 动手能力 MAX
    Hobby("m.master.woodworking", "木工", MASTER, MALE, null),
    Hobby("m.master.home-repair", "家修", MASTER, MALE, XIA),
    Hobby("m.master.renovation", "装修", MASTER, MALE, JIANG),
    Hobby("m.master.car-tuning", "汽车改装", MASTER, MALE, XIA),
    Hobby("m.master.leathercraft", "皮具", MASTER, MALE, JIANG),
    Hobby("m.master.electronics-diy", "电子DIY", MASTER, MALE, null),
    Hobby("m.master.model-kit", "模型拼装", MASTER, MALE, JIANG),

    // 封神级 · 紫钻 · 星光 —— 开始向内探索
    Hobby("m.demigod.programming", "编程", DEMIGOD, MALE, XIA),
    Hobby("m.demigod.calligraphy", "书法", DEMIGOD, MALE, ZONG),
    Hobby("m.demigod.tai-chi", "太极", DEMIGOD, MALE, ZONG),
    Hobby("m.demigod.go", "围棋", DEMIGOD, MALE, ZONG),
    Hobby("m.demigod.guqin", "古琴", DEMIGOD, MALE, SHI),
    Hobby("m.demigod.chinese-studies", "国学", DEMIGOD, MALE, ZONG),
    Hobby("m.demigod.seal-carving", "篆刻", DEMIGOD, MALE, JIANG),

    // 大神级 · 炫彩 · 流光 —— 活成了传说
    Hobby("m.greatgod.fishkeeping", "养鱼", GREAT_GOD, MALE, LAO),
    Hobby("m.greatgod.birdkeeping", "养鸟", GREAT_GOD, MALE, LAO),
    Hobby("m.greatgod.park-morning", "公园晨练", GREAT_GOD, MALE, WANG, rankAlias = "公园"),
    Hobby("m.greatgod.street-chess", "街头象棋", GREAT_GOD, MALE, WANG),
    Hobby("m.greatgod.walnut", "文玩核桃", GREAT_GOD, MALE, LAO),
    Hobby("m.greatgod.opera", "听戏", GREAT_GOD, MALE, LAO),
    Hobby("m.greatgod.strolling", "遛弯", GREAT_GOD, MALE, null),
)

/**
 * 女性版主力爱好矩阵（规格 5.1）。每段位 7 个，共 42 个。
 */
internal val FEMALE_MAIN_HOBBIES: List<Hobby> = listOf(
    // 入门级 · 青铜 · 哑光 —— 日常里的小确幸
    Hobby("f.entry.shopping", "买买买", ENTRY, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.entry.drama", "追剧", ENTRY, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.entry.short-video", "刷短视频", ENTRY, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.entry.milk-tea", "喝奶茶", ENTRY, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.entry.lipstick", "口红试色", ENTRY, Gender.FEMALE, SHI),
    Hobby("f.entry.fandom", "追星", ENTRY, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.entry.group-buy", "拼单团购", ENTRY, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),

    // 进阶级 · 银 · 拉丝 —— 懂得悦己
    Hobby("f.intermediate.nail-art", "美甲", INTERMEDIATE, Gender.FEMALE, SHI),
    Hobby("f.intermediate.yoga", "瑜伽", INTERMEDIATE, Gender.FEMALE, SHI),
    Hobby("f.intermediate.cafe-hopping", "探店", INTERMEDIATE, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.intermediate.pilates", "普拉提", INTERMEDIATE, Gender.FEMALE, SHI),
    Hobby("f.intermediate.outfit", "穿搭", INTERMEDIATE, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.intermediate.photo-editing", "拍照修图", INTERMEDIATE, Gender.FEMALE, SHI),
    Hobby("f.intermediate.travel", "旅行", INTERMEDIATE, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),

    // 封师级 · 金 · 抛光 —— 把日子过成了诗
    Hobby("f.teacher.flower", "插花", MASTER_TEACHER, Gender.FEMALE, SHI),
    Hobby("f.teacher.aromatherapy", "香薰", MASTER_TEACHER, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),
    Hobby("f.teacher.journaling", "手帐", MASTER_TEACHER, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.teacher.baking", "烘焙", MASTER_TEACHER, Gender.FEMALE, SHI),
    Hobby("f.teacher.organizing", "家居收纳", MASTER_TEACHER, Gender.FEMALE, SHI),
    Hobby("f.teacher.pour-over", "手冲咖啡", MASTER_TEACHER, Gender.FEMALE, SHI),
    Hobby("f.teacher.plants", "养绿植", MASTER_TEACHER, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),

    // 大师级 · 铂金 · 镜面 —— 人间清醒
    Hobby("f.master.money", "搞钱", MASTER, Gender.FEMALE, IdentitySuffix.NV_WANG),
    Hobby("f.master.investing", "理财", MASTER, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.master.self-media", "自媒体", MASTER, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.master.certification", "考证", MASTER, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.master.parenting", "带娃", MASTER, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),
    Hobby("f.master.time-management", "时间管理", MASTER, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.master.group-selling", "开团购群", MASTER, Gender.FEMALE, IdentitySuffix.NV_WANG),

    // 封神级 · 紫钻 · 星光 —— 信命，但更信自己
    Hobby("f.demigod.tarot", "塔罗", DEMIGOD, Gender.FEMALE, SHI),
    Hobby("f.demigod.meditation", "冥想", DEMIGOD, Gender.FEMALE, IdentitySuffix.JUE_XING_ZHE),
    Hobby("f.demigod.web-novel", "写网文", DEMIGOD, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.demigod.spirituality", "灵性", DEMIGOD, Gender.FEMALE, IdentitySuffix.JUE_XING_ZHE),
    Hobby("f.demigod.psychology", "心理学", DEMIGOD, Gender.FEMALE, SHI),
    Hobby("f.demigod.podcast", "播客", DEMIGOD, Gender.FEMALE, IdentitySuffix.DA_REN),
    Hobby("f.demigod.metaphysics", "玄学", DEMIGOD, Gender.FEMALE, IdentitySuffix.JUE_XING_ZHE),

    // 大神级 · 炫彩 · 流光 —— 终极断舍离
    Hobby("f.greatgod.vegetable", "种菜", GREAT_GOD, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),
    Hobby("f.greatgod.turtle", "养龟", GREAT_GOD, Gender.FEMALE, IdentitySuffix.XIAN_NV),
    Hobby("f.greatgod.knitting", "织毛衣", GREAT_GOD, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),
    Hobby("f.greatgod.wellness", "佛系养生", GREAT_GOD, Gender.FEMALE, null),
    Hobby("f.greatgod.foot-soak", "泡脚", GREAT_GOD, Gender.FEMALE, IdentitySuffix.XIAN_NV),
    Hobby("f.greatgod.sutra", "抄经", GREAT_GOD, Gender.FEMALE, IdentitySuffix.JUE_XING_ZHE),
    Hobby("f.greatgod.decluttering", "断舍离", GREAT_GOD, Gender.FEMALE, IdentitySuffix.SHENG_HUO_JIA),
)
