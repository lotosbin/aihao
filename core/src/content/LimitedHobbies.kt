package com.yuanjingtech.aihao.content

import com.yuanjingtech.aihao.content.HobbySource.BRAND
import com.yuanjingtech.aihao.content.HobbySource.FESTIVAL
import com.yuanjingtech.aihao.content.HobbySource.REGION
import com.yuanjingtech.aihao.content.IdentitySuffix.DA_REN
import com.yuanjingtech.aihao.content.IdentitySuffix.LAO
import com.yuanjingtech.aihao.content.IdentitySuffix.SHI
import com.yuanjingtech.aihao.content.IdentitySuffix.XIA
import com.yuanjingtech.aihao.content.IdentitySuffix.XIAN_NV
import com.yuanjingtech.aihao.content.Tier.DEMIGOD
import com.yuanjingtech.aihao.content.Tier.ENTRY
import com.yuanjingtech.aihao.content.Tier.INTERMEDIATE
import com.yuanjingtech.aihao.content.Tier.MASTER
import com.yuanjingtech.aihao.content.Tier.MASTER_TEACHER

private fun limited(
    id: String,
    name: String,
    tier: Tier,
    source: HobbySource,
    sourceLabel: String,
    gender: Gender? = null,
    suffix: IdentitySuffix? = null,
    rankAlias: String? = null,
    windowLabel: String? = null,
    window: MonthDayRange? = null,
): LimitedHobby = LimitedHobby(
    hobby = Hobby(
        id = id,
        name = name,
        tier = tier,
        gender = gender,
        suffix = suffix,
        source = source,
        rankAlias = rankAlias,
    ),
    sourceLabel = sourceLabel,
    windowLabel = windowLabel,
    window = window,
)

/**
 * 节日限定爱好（规格 7.1）。
 *
 * 农历节日（春节、元宵、端午、中秋）没有固定公历日期，[LimitedHobby.window] 为 null，
 * 只给出面向用户的 [LimitedHobby.windowLabel]；运营按当年日历为其配置具体窗口，
 * 或用 [ChenmiSha.festivalHobbies] 直接按节日名取用。
 */
internal val FESTIVAL_HOBBIES: List<LimitedHobby> = listOf(
    limited(
        id = "festival.spring-festival.cook",
        name = "年夜饭大厨",
        tier = ENTRY,
        source = FESTIVAL,
        sourceLabel = "春节",
        windowLabel = "除夕前 7 天",
    ),
    limited(
        id = "festival.lantern-festival.riddle",
        name = "猜灯谜",
        tier = ENTRY,
        source = FESTIVAL,
        sourceLabel = "元宵节",
        windowLabel = "正月十五前后",
    ),
    limited(
        id = "festival.qingming.outing",
        name = "踏青",
        tier = INTERMEDIATE,
        source = FESTIVAL,
        sourceLabel = "清明",
        windowLabel = "4.1 - 4.7",
        window = MonthDayRange(4, 1, 4, 7),
    ),
    limited(
        id = "festival.dragon-boat.zongzi",
        name = "包粽子",
        tier = MASTER_TEACHER,
        source = FESTIVAL,
        sourceLabel = "端午",
        windowLabel = "五月初五前后",
    ),
    limited(
        id = "festival.mid-autumn.mooncake",
        name = "月饼DIY",
        tier = MASTER_TEACHER,
        source = FESTIVAL,
        sourceLabel = "中秋",
        windowLabel = "八月十五前后",
    ),
    limited(
        id = "festival.national-day.road-trip",
        name = "自驾游达人",
        tier = INTERMEDIATE,
        source = FESTIVAL,
        sourceLabel = "国庆",
        windowLabel = "10.1 - 10.7",
        window = MonthDayRange(10, 1, 10, 7),
    ),
    limited(
        id = "festival.double-11.shopping",
        name = "剁手党",
        tier = ENTRY,
        source = FESTIVAL,
        sourceLabel = "双11",
        windowLabel = "11.1 - 11.11",
        window = MonthDayRange(11, 1, 11, 11),
    ),
    limited(
        id = "festival.double-12.stockpile",
        name = "囤货",
        tier = ENTRY,
        source = FESTIVAL,
        sourceLabel = "双12",
        windowLabel = "12.1 - 12.12",
        window = MonthDayRange(12, 1, 12, 12),
    ),
    limited(
        id = "festival.world-cup.night-owl",
        name = "熬夜看球",
        tier = INTERMEDIATE,
        source = FESTIVAL,
        sourceLabel = "世界杯",
        gender = Gender.MALE,
        suffix = LAO,
        windowLabel = "赛事期间",
    ),
    limited(
        id = "festival.olympics.chasing-events",
        name = "追赛事",
        tier = INTERMEDIATE,
        source = FESTIVAL,
        sourceLabel = "奥运会",
        windowLabel = "赛事期间",
    ),
)

/**
 * 地域限定爱好（规格 7.2）。文案口径为本地生活自豪感，不做任何高低评价。
 */
internal val REGION_HOBBIES: List<LimitedHobby> = listOf(
    limited("region.northeast.bathhouse", "搓澡", ENTRY, REGION, "东北", Gender.MALE, LAO),
    limited("region.beijing.bead-rubbing", "盘串", MASTER_TEACHER, REGION, "北京", Gender.MALE, LAO),
    limited("region.tianjin.crosstalk", "听相声", MASTER_TEACHER, REGION, "天津"),
    limited("region.guangdong.morning-tea", "早茶", MASTER_TEACHER, REGION, "广东"),
    limited("region.jiangnan.pingtan", "评弹", MASTER_TEACHER, REGION, "江浙"),
    limited("region.fujian.gongfu-tea", "功夫茶", MASTER_TEACHER, REGION, "福建", null, SHI),
    limited("region.yunnan.puer", "普洱", MASTER_TEACHER, REGION, "云南", Gender.MALE, LAO),
    limited("region.shanghai.cafe-hopping", "咖啡探店", MASTER_TEACHER, REGION, "上海", Gender.FEMALE, DA_REN),
    limited("region.sichuan.mahjong", "麻将", INTERMEDIATE, REGION, "四川", Gender.MALE, LAO),
    limited("region.chongqing.hotpot", "火锅", INTERMEDIATE, REGION, "重庆", Gender.MALE, XIA),
    limited("region.shandong.skewers", "撸串", INTERMEDIATE, REGION, "山东", Gender.MALE, XIA),
    limited("region.shaanxi.paomo", "泡馍", INTERMEDIATE, REGION, "陕西", Gender.MALE, LAO),
    limited("region.shaanxi.hanfu", "汉服", INTERMEDIATE, REGION, "陕西", Gender.FEMALE, XIAN_NV),
    limited("region.hubei.hot-dry-noodles", "热干面", ENTRY, REGION, "湖北", Gender.MALE, LAO),
    limited("region.hunan.rice-noodles", "嗦粉", ENTRY, REGION, "湖南", Gender.MALE, XIA),
    limited("region.hainan.beach-combing", "赶海", INTERMEDIATE, REGION, "海南", Gender.MALE, LAO),
    limited("region.inner-mongolia.horse-riding", "骑马", INTERMEDIATE, REGION, "内蒙", Gender.MALE, XIA),
    limited("region.xinjiang.pilaf", "手抓饭", INTERMEDIATE, REGION, "新疆", Gender.MALE, LAO),
)

/**
 * 品牌联名限定爱好（规格 7.3）。
 */
internal val BRAND_HOBBIES: List<LimitedHobby> = listOf(
    limited("brand.fishing.lure", "路亚", INTERMEDIATE, BRAND, "渔具品牌", Gender.MALE, LAO),
    limited("brand.coffee.pour-over", "手冲", MASTER_TEACHER, BRAND, "咖啡品牌", null, SHI),
    limited("brand.aromatherapy.blending", "调香", DEMIGOD, BRAND, "香薰品牌", Gender.FEMALE, SHI),
    limited("brand.outdoor.gear", "露营装备", INTERMEDIATE, BRAND, "户外品牌"),
    limited("brand.camera.street", "街拍", MASTER, BRAND, "相机品牌", Gender.MALE, SHI),
    limited("brand.tea.ceremony", "茶道", MASTER_TEACHER, BRAND, "茶品牌", null, SHI),
    limited("brand.stationery.collage", "拼贴", MASTER_TEACHER, BRAND, "文具品牌", Gender.FEMALE, DA_REN),
    limited("brand.sports.marathon", "马拉松", MASTER, BRAND, "运动品牌", null, SHI),
    limited("brand.pet.keeping", "养宠", INTERMEDIATE, BRAND, "宠物品牌"),
    limited("brand.appliance.smart-home", "智能家居", MASTER_TEACHER, BRAND, "家电品牌", Gender.MALE, SHI),
    limited("brand.car.offroad", "越野", MASTER, BRAND, "汽车品牌", Gender.MALE, XIA),
    limited("brand.alcohol.craft-beer", "精酿", MASTER_TEACHER, BRAND, "酒品牌", Gender.MALE, LAO),
)

/** 全部限定爱好。 */
internal val LIMITED_HOBBIES: List<LimitedHobby> =
    FESTIVAL_HOBBIES + REGION_HOBBIES + BRAND_HOBBIES
