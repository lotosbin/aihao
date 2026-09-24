package com.yuanjingtech.aihao.content

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProgressionTest {

    private fun hobby(id: String): Hobby =
        ChenmiSha.hobbyById(id) ?: error("找不到爱好 $id")

    private fun male(tier: Tier, index: Int = 0): Hobby = ChenmiSha.mainHobbies(Gender.MALE, tier)[index]

    private fun female(tier: Tier, index: Int = 0): Hobby = ChenmiSha.mainHobbies(Gender.FEMALE, tier)[index]

    // ---------- 解锁阶梯 ----------

    @Test
    fun `未选择任何爱好时入门级可挑、但没有任何徽章解锁`() {
        val selected = emptyList<Hobby>()
        assertEquals(Tier.ENTRY, Progression.accessibleTier(selected))
        assertFalse(Progression.isUnlocked(Tier.ENTRY, selected))
        assertTrue(Progression.progress(selected).notStarted)
        assertEquals(0, Progression.progress(selected).unlockedCount)
        assertEquals(Tier.ENTRY, Progression.progress(selected).nextTier)
    }

    @Test
    fun `挑一个入门级爱好就点亮入门级徽章并开放进阶级`() {
        val entry = male(Tier.ENTRY)
        val selected = Progression.toggle(emptyList(), entry)

        assertEquals(listOf(entry), selected)
        assertTrue(Progression.isUnlocked(Tier.ENTRY, selected))
        assertFalse(Progression.isUnlocked(Tier.INTERMEDIATE, selected))
        assertEquals(Tier.INTERMEDIATE, Progression.accessibleTier(selected))
        assertEquals(1, Progression.progress(selected).unlockedCount)
        assertEquals(Tier.INTERMEDIATE, Progression.progress(selected).nextTier)
    }

    @Test
    fun `未开放段位的爱好不可选`() {
        val selected = listOf(male(Tier.ENTRY))
        // 入门级已选 → 进阶级开放；封师级仍未开放
        assertTrue(Progression.canSelect(male(Tier.INTERMEDIATE), selected))
        assertFalse(Progression.canSelect(male(Tier.MASTER_TEACHER), selected))
        assertEquals(selected, Progression.toggle(selected, male(Tier.MASTER_TEACHER)))
    }

    @Test
    fun `徽章解锁范围是最高段位及其以下全部段位`() {
        val selected = listOf(
            male(Tier.ENTRY),
            male(Tier.INTERMEDIATE),
            male(Tier.MASTER_TEACHER),
        )
        assertEquals(
            listOf(Tier.ENTRY, Tier.INTERMEDIATE, Tier.MASTER_TEACHER),
            Progression.unlockedTiers(selected),
        )
        assertTrue(Progression.isUnlocked(Tier.ENTRY, selected))
        assertFalse(Progression.isUnlocked(Tier.MASTER, selected))
    }

    @Test
    fun `取消最高段位的爱好后段位回退、徽章重新锁上`() {
        val selected = listOf(male(Tier.ENTRY), male(Tier.INTERMEDIATE))
        val rolled = Progression.toggle(selected, male(Tier.INTERMEDIATE))

        assertFalse(Progression.isUnlocked(Tier.INTERMEDIATE, rolled))
        assertTrue(Progression.isUnlocked(Tier.ENTRY, rolled))
        assertEquals(1, Progression.progress(rolled).unlockedCount)
    }

    @Test
    fun `可以取消已选爱好`() {
        val entry = male(Tier.ENTRY)
        val selected = Progression.toggle(emptyList(), entry)
        assertTrue(Progression.toggle(selected, entry).isEmpty())
    }

    @Test
    fun `逐层点亮可以走到满级`() {
        var selected = emptyList<Hobby>()
        Tier.values().forEach { tier ->
            selected = Progression.toggle(selected, male(tier))
            assertTrue(Progression.isUnlocked(tier, selected), "挑了 ${tier.displayName} 后该段位应解锁")
        }
        val progress = Progression.progress(selected)
        assertTrue(progress.fullyUnlocked)
        assertEquals(6, progress.unlockedCount)
        assertNull(progress.nextTier)
    }

    @Test
    fun `跨层选择后低段位无具体爱好时也保持解锁`() {
        // 先选入门级 → 再选进阶级 → 取消入门级
        var selected = Progression.toggle(emptyList(), male(Tier.ENTRY))
        selected = Progression.toggle(selected, male(Tier.INTERMEDIATE))
        selected = Progression.toggle(selected, male(Tier.ENTRY))

        assertTrue(selected.none { it.tier == Tier.ENTRY })
        assertTrue(Progression.isUnlocked(Tier.ENTRY, selected), "最高段位仍在进阶级，入门级徽章不应回退")
    }

    // ---------- 彩蛋触发 ----------

    @Test
    fun `数码中年彩蛋由 NAS 加机械键盘加 HiFi 触发`() {
        val selected = listOf(
            hobby("m.teacher.nas"),
            hobby("m.teacher.keyboard"),
            hobby("m.teacher.hifi"),
        )
        assertTrue(Progression.triggeredEasterEggs(selected).any { it.id == "egg.digital-midlife" })
    }

    @Test
    fun `退休预备役彩蛋由钓鱼加品茶加太极触发`() {
        val selected = listOf(
            hobby("m.intermediate.fishing"),
            hobby("m.teacher.tea"),
            hobby("m.demigod.tai-chi"),
        )
        assertTrue(Progression.triggeredEasterEggs(selected).any { it.id == "egg.retirement-ready" })
    }

    @Test
    fun `养生局发起人彩蛋由泡脚加抄经加佛系养生触发`() {
        val selected = listOf(
            hobby("f.greatgod.foot-soak"),
            hobby("f.greatgod.sutra"),
            hobby("f.greatgod.wellness"),
        )
        assertTrue(Progression.triggeredEasterEggs(selected).any { it.id == "egg.wellness-host" })
    }

    @Test
    fun `满级触发满级人类彩蛋`() {
        val selected = Tier.values().flatMap { listOf(female(it)) }
        assertTrue(Progression.progress(selected).fullyUnlocked)
        assertTrue(Progression.triggeredEasterEggs(selected).any { it.id == "egg.max-level" })
    }

    @Test
    fun `组合不全时不触发彩蛋`() {
        val selected = listOf(hobby("m.teacher.nas"), hobby("m.teacher.keyboard"))
        assertTrue(Progression.triggeredEasterEggs(selected).isEmpty())
    }

    @Test
    fun `依赖时间与跨版本的彩蛋不会由选择触发`() {
        val ids = Progression.triggeredEasterEggs(
            Tier.values().flatMap { listOf(male(it)) },
        ).map { it.id }
        assertFalse(ids.contains("egg.midnight-show"))
        assertFalse(ids.contains("egg.crossover"))
        assertFalse(ids.contains("egg.co-creator"))
    }

    @Test
    fun `可由选择直接触发的彩蛋共 5 个`() {
        assertEquals(5, Progression.selectionTriggeredEggCount())
    }
}
