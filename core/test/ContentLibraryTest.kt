package com.yuanjingtech.aihao.content

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContentLibraryTest {

    // ---------- 规格 5.1 矩阵完整性 ----------

    @Test
    fun `每个段位每版本都有 6 到 7 个主力爱好`() {
        Gender.values().forEach { gender ->
            Tier.values().forEach { tier ->
                val count = ChenmiSha.mainHobbies(gender, tier).size
                assertTrue(
                    count in 6..7,
                    "${gender.displayName}/${tier.displayName} 有 $count 个爱好，应为 6-7 个",
                )
            }
        }
    }

    @Test
    fun `主力爱好 id 全局唯一`() {
        val ids = Gender.values().flatMap { ChenmiSha.allMainHobbies(it) }.map { it.id }
        assertEquals(ids.size, ids.toSet().size, "存在重复 id：${ids.groupBy { it }.filterValues { it.size > 1 }.keys}")
    }

    @Test
    fun `主力爱好的后缀属于对应版本后缀库`() {
        Gender.values().forEach { gender ->
            ChenmiSha.allMainHobbies(gender).forEach { hobby ->
                val suffix = hobby.suffix ?: return@forEach
                assertTrue(
                    gender.supports(suffix),
                    "${hobby.id} 使用了 ${gender.displayName} 不支持的「${suffix.canonical}」",
                )
            }
        }
    }

    @Test
    fun `主力爱好均绑定自己的版本`() {
        Gender.values().forEach { gender ->
            ChenmiSha.allMainHobbies(gender).forEach { hobby ->
                assertEquals(gender, hobby.gender, "${hobby.id} 的版本不匹配")
            }
        }
    }

    // ---------- 规格 5.2 徽章材质与配色 ----------

    @Test
    fun `徽章材质配色与规格一致`() {
        assertEquals("#CD7F32", Tier.ENTRY.primaryColor)
        assertEquals("#8B4513", Tier.ENTRY.secondaryColor)
        assertEquals(Finish.MATTE, Tier.ENTRY.finish)

        assertEquals("#C0C0C0", Tier.INTERMEDIATE.primaryColor)
        assertEquals(Finish.BRUSHED, Tier.INTERMEDIATE.finish)

        assertEquals("#FFD700", Tier.MASTER_TEACHER.primaryColor)
        assertEquals("#DAA520", Tier.MASTER_TEACHER.secondaryColor)
        assertEquals(Finish.POLISHED, Tier.MASTER_TEACHER.finish)

        assertEquals("#E5E4E2", Tier.MASTER.primaryColor)
        assertEquals(Finish.MIRROR, Tier.MASTER.finish)

        assertEquals("#9400D3", Tier.DEMIGOD.primaryColor)
        assertEquals("#4B0082", Tier.DEMIGOD.secondaryColor)
        assertEquals(Finish.STARLIGHT, Tier.DEMIGOD.finish)

        assertEquals(Finish.HOLOGRAPHIC, Tier.GREAT_GOD.finish)
        assertTrue(Tier.GREAT_GOD.gradient.size >= 3, "炫彩应为多色渐变")
    }

    // ---------- 规格 5.3 段位名称生成 ----------

    /** 规格 5.3 的男性版示例，按「材质 + 爱好 + 后缀」逐字复现组合规则。 */
    @Test
    fun `段位名称规则复现男性版示例`() {
        assertEquals(
            "黄金钓鱼佬",
            ChenmiSha.rankName(Gender.MALE, Tier.MASTER_TEACHER, hobby("钓鱼", Gender.MALE, IdentitySuffix.LAO)).text,
        )
        assertEquals(
            "铂金木工",
            ChenmiSha.rankName(Gender.MALE, Tier.MASTER, hobby("木工", Gender.MALE, null)).text,
        )
        assertEquals(
            "紫钻太极宗师",
            ChenmiSha.rankName(Gender.MALE, Tier.DEMIGOD, hobby("太极", Gender.MALE, IdentitySuffix.ZONG)).text,
        )
        assertEquals(
            "炫彩公园之王",
            ChenmiSha.rankName(
                Gender.MALE,
                Tier.GREAT_GOD,
                hobby("公园晨练", Gender.MALE, IdentitySuffix.WANG, rankAlias = "公园"),
            ).text,
        )
    }

    /** 规格 5.3 的女性版示例。 */
    @Test
    fun `段位名称规则复现女性版示例`() {
        assertEquals(
            "黄金美甲师",
            ChenmiSha.rankName(Gender.FEMALE, Tier.MASTER_TEACHER, hobby("美甲", Gender.FEMALE, IdentitySuffix.SHI)).text,
        )
        assertEquals(
            "铂金搞钱女王",
            ChenmiSha.rankName(Gender.FEMALE, Tier.MASTER, hobby("搞钱", Gender.FEMALE, IdentitySuffix.NV_WANG)).text,
        )
        assertEquals(
            "紫钻灵性觉醒者",
            ChenmiSha.rankName(
                Gender.FEMALE,
                Tier.DEMIGOD,
                hobby("灵性", Gender.FEMALE, IdentitySuffix.JUE_XING_ZHE),
            ).text,
        )
        assertEquals(
            "炫彩佛系养生",
            ChenmiSha.rankName(Gender.FEMALE, Tier.GREAT_GOD, hobby("佛系养生", Gender.FEMALE, null)).text,
        )
    }

    @Test
    fun `取用户最高段位生成名称`() {
        val hobbies = listOf(
            ChenmiSha.mainHobbies(Gender.MALE, Tier.ENTRY).first(),
            ChenmiSha.mainHobbies(Gender.MALE, Tier.DEMIGOD).first(),
            ChenmiSha.mainHobbies(Gender.MALE, Tier.INTERMEDIATE).first(),
        )
        val rank = ChenmiSha.rankName(Gender.MALE, hobbies)
        assertEquals(Tier.DEMIGOD, rank?.tier)
        assertEquals("紫钻编程侠", rank?.text)
    }

    @Test
    fun `同段位并列时优先选择带身份后缀的爱好`() {
        val bare = hobby("电子DIY", Gender.MALE, null).copy(tier = Tier.DEMIGOD)
        val withSuffix = hobby("编程", Gender.MALE, IdentitySuffix.XIA).copy(tier = Tier.DEMIGOD)
        assertEquals("紫钻编程侠", ChenmiSha.rankName(Gender.MALE, listOf(bare, withSuffix))?.text)
    }

    @Test
    fun `爱好为空时没有段位名称`() {
        assertEquals(null, ChenmiSha.rankName(Gender.MALE, emptyList()))
        assertEquals(null, ChenmiSha.topTier(emptyList()))
    }

    @Test
    fun `跨版本取用时回退到该版本的首选后缀`() {
        val bathhouse = hobby("搓澡", Gender.MALE, IdentitySuffix.LAO)
        assertEquals("青铜搓澡佬", ChenmiSha.rankName(Gender.MALE, Tier.ENTRY, bathhouse).text)
        assertEquals("青铜搓澡师", ChenmiSha.rankName(Gender.FEMALE, Tier.ENTRY, bathhouse).text)
    }

    // ---------- 规格 6.1 / 6.2 评语 ----------

    @Test
    fun `评语与规格逐字一致`() {
        assertEquals(
            "你还在生活的表面浮沉，手机和短视频是你的精神食粮。别急，每个男人都是从这一步开始的。",
            ChenmiSha.tierCopy(Gender.MALE, Tier.ENTRY),
        )
        assertEquals(
            "种菜、养龟、织毛衣，你完成了人生的终极断舍离。不卷了，这世界爱谁谁。",
            ChenmiSha.tierCopy(Gender.FEMALE, Tier.GREAT_GOD),
        )
        assertEquals(6, ChenmiSha.tierCopies(Gender.MALE).size)
        assertEquals(6, ChenmiSha.tierCopies(Gender.FEMALE).size)
    }

    // ---------- 规格 7.x 限定内容 ----------

    @Test
    fun `三类限定内容都有覆盖`() {
        assertTrue(ChenmiSha.festivalHobbies().isNotEmpty())
        assertTrue(ChenmiSha.regionHobbies().isNotEmpty())
        assertTrue(ChenmiSha.brandHobbies().isNotEmpty())
        assertEquals(
            HobbySource.FESTIVAL,
            ChenmiSha.festivalHobbies("春节").single().source,
        )
        assertEquals(HobbySource.REGION, ChenmiSha.regionHobbies("东北").single().source)
        assertEquals(HobbySource.BRAND, ChenmiSha.brandHobbies("渔具品牌").single().source)
    }

    @Test
    fun `规格 7 表格给出的限定爱好均在库中`() {
        assertTrue(ChenmiSha.festivalHobbies("春节").any { it.hobby.name == "年夜饭大厨" })
        assertTrue(ChenmiSha.festivalHobbies("国庆").any { it.hobby.name == "自驾游达人" })
        assertTrue(ChenmiSha.festivalHobbies("双11").any { it.hobby.name == "剁手党" })
        assertTrue(ChenmiSha.festivalHobbies("世界杯").any { it.hobby.name == "熬夜看球" })
        assertTrue(ChenmiSha.regionHobbies("东北").any { it.hobby.name == "搓澡" })
        assertTrue(ChenmiSha.regionHobbies("广东").any { it.hobby.name == "早茶" })
        assertTrue(ChenmiSha.regionHobbies("四川").any { it.hobby.name == "麻将" })
        assertTrue(ChenmiSha.regionHobbies("北京").any { it.hobby.name == "盘串" })
        assertTrue(ChenmiSha.brandHobbies("渔具品牌").any { it.hobby.name == "路亚" })
        assertTrue(ChenmiSha.brandHobbies("咖啡品牌").any { it.hobby.name == "手冲" })
        assertTrue(ChenmiSha.brandHobbies("香薰品牌").any { it.hobby.name == "调香" })
    }

    @Test
    fun `限定爱好的段位与规格一致`() {
        assertEquals(Tier.ENTRY, ChenmiSha.festivalHobbies("春节").single().hobby.tier)
        assertEquals(Tier.ENTRY, ChenmiSha.festivalHobbies("双11").single().hobby.tier)
        assertEquals(Tier.INTERMEDIATE, ChenmiSha.festivalHobbies("国庆").single().hobby.tier)
        assertEquals(Tier.INTERMEDIATE, ChenmiSha.festivalHobbies("世界杯").single().hobby.tier)
        assertEquals(Tier.ENTRY, ChenmiSha.regionHobbies("东北").single().hobby.tier)
        assertEquals(Tier.MASTER_TEACHER, ChenmiSha.regionHobbies("广东").single().hobby.tier)
        assertEquals(Tier.INTERMEDIATE, ChenmiSha.regionHobbies("四川").single().hobby.tier)
        assertEquals(Tier.MASTER_TEACHER, ChenmiSha.regionHobbies("北京").single().hobby.tier)
        assertEquals(Tier.INTERMEDIATE, ChenmiSha.brandHobbies("渔具品牌").single().hobby.tier)
        assertEquals(Tier.MASTER_TEACHER, ChenmiSha.brandHobbies("咖啡品牌").single().hobby.tier)
        assertEquals(Tier.DEMIGOD, ChenmiSha.brandHobbies("香薰品牌").single().hobby.tier)
    }

    @Test
    fun `节日限定按公历窗口生效`() {
        assertTrue(ChenmiSha.activeLimitedHobbies(11, 5).any { it.hobby.name == "剁手党" })
        assertFalse(ChenmiSha.activeLimitedHobbies(11, 20).any { it.hobby.name == "剁手党" })
        assertTrue(ChenmiSha.activeLimitedHobbies(10, 3).any { it.hobby.name == "自驾游达人" })
        assertTrue(ChenmiSha.activeLimitedHobbies(12, 12).any { it.hobby.name == "囤货" })
    }

    @Test
    fun `跨年时间窗判定正确`() {
        val window = MonthDayRange(12, 25, 1, 5)
        assertTrue(window.contains(12, 25))
        assertTrue(window.contains(12, 31))
        assertTrue(window.contains(1, 3))
        assertFalse(window.contains(6, 1))
        assertFalse(window.contains(12, 24))
    }

    // ---------- 隐藏标签与彩蛋 ----------

    @Test
    fun `隐藏标签与彩蛋都不为空`() {
        assertTrue(ChenmiSha.hiddenTags().size >= 8)
        assertTrue(ChenmiSha.easterEggs().size >= 6)
        assertEquals(
            ChenmiSha.hiddenTags().size,
            ChenmiSha.hiddenTags().map { it.id }.toSet().size,
        )
        assertEquals(
            ChenmiSha.easterEggs().size,
            ChenmiSha.easterEggs().map { it.id }.toSet().size,
        )
    }

    // ---------- 规格 8.1 内容自检 ----------

    @Test
    fun `内容自检通过`() {
        val findings = ContentAudit.findings()
        assertTrue(findings.isEmpty(), findings.joinToString("\n") { "${it.rule} / ${it.target}: ${it.message}" })
    }

    private fun hobby(
        name: String,
        gender: Gender,
        suffix: IdentitySuffix?,
        rankAlias: String? = null,
    ): Hobby = Hobby(
        id = "fixture.$name",
        name = name,
        tier = Tier.ENTRY,
        gender = gender,
        suffix = suffix,
        rankAlias = rankAlias,
    )
}
