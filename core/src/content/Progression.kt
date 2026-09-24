package com.yuanjingtech.aihao.content

/**
 * 段位解锁进度。
 *
 * @property selectedCount 已选爱好数量。
 * @property highestTier 已选爱好中段位最高的一层；未选择任何爱好时为 null。
 * @property unlockedTiers 已解锁的段位，从入门级到 [highestTier] 连续排列。
 * @property nextTier 下一个待解锁的段位；六段全部解锁后为 null。
 */
data class TierProgress(
    val selectedCount: Int,
    val highestTier: Tier?,
    val unlockedTiers: List<Tier>,
    val nextTier: Tier?,
) {
    /** 已解锁段位数量。 */
    val unlockedCount: Int get() = unlockedTiers.size

    /** 段位总数。 */
    val totalTiers: Int get() = Tier.values().size

    /** 是否已点亮全部六个段位。 */
    val fullyUnlocked: Boolean get() = unlockedCount == totalTiers

    /** 是否还没有选择任何爱好。 */
    val notStarted: Boolean get() = selectedCount == 0
}

/**
 * 段位解锁与选择规则。
 *
 * ## 解锁阶梯
 * - **入门级永远可选**，用户从入门级开始挑爱好。
 * - 每点亮一层，就开放**下一层**的选择权：选了入门级 → 开放进阶级；选了进阶级 → 开放封师级……
 * - **徽章解锁范围**：已解锁段位 = 达到过的最高段位**及其以下全部段位**。
 *   即「有封神级爱好」意味着入门级到封神级的徽章都已点亮。
 * - 解锁状态**跟随当前选择**：取消掉某层的爱好会让段位回退、徽章重新锁上。
 *   这是刻意的可逆设计，便于用户反复试玩；若要「解锁后永久保留」，需要服务端记录达成历史。
 *
 * ## 为什么放在 core
 * 这是纯规则、无 UI 依赖，且必须与段位/爱好数据一起被测试覆盖；各端（含未来的小程序）应共用同一套判定。
 */
object Progression {

    /** 已选爱好中段位最高的那一层；未选择任何爱好时为 null。 */
    fun highestTier(selected: List<Hobby>): Tier? =
        selected.maxByOrNull { it.tier.ordinal }?.tier

    /** 已解锁的段位：最高段位及其以下全部解锁；未选择任何爱好时为空。 */
    fun unlockedTiers(selected: List<Hobby>): List<Tier> {
        val highest = highestTier(selected) ?: return emptyList()
        return Tier.values().filter { it.ordinal <= highest.ordinal }
    }

    /** 指定段位的徽章是否已解锁。 */
    fun isUnlocked(tier: Tier, selected: List<Hobby>): Boolean =
        highestTier(selected)?.let { tier.ordinal <= it.ordinal } ?: false

    /** 当前可选择的最高段位：未开始时为入门级，之后为已解锁段位的下一层。 */
    fun accessibleTier(selected: List<Hobby>): Tier {
        val highest = highestTier(selected) ?: return Tier.ENTRY
        val next = highest.ordinal + 1
        return Tier.values()[next.coerceAtMost(Tier.values().lastIndex)]
    }

    /** 该爱好当前是否可选（只判段位开放情况，版本过滤由调用方负责）。 */
    fun canSelect(hobby: Hobby, selected: List<Hobby>): Boolean =
        hobby.tier.ordinal <= accessibleTier(selected).ordinal

    /** 切换选中状态；已选中则取消，未开放则该爱好不可选、原样返回。 */
    fun toggle(selected: List<Hobby>, hobby: Hobby): List<Hobby> = when {
        selected.any { it.id == hobby.id } -> selected.filterNot { it.id == hobby.id }
        canSelect(hobby, selected) -> selected + hobby
        else -> selected
    }

    /** 计算解锁进度。 */
    fun progress(selected: List<Hobby>): TierProgress {
        val unlocked = unlockedTiers(selected)
        return TierProgress(
            selectedCount = selected.size,
            highestTier = highestTier(selected),
            unlockedTiers = unlocked,
            nextTier = if (unlocked.size == Tier.values().size) null else accessibleTier(selected),
        )
    }

    /**
     * 由「已选爱好」直接触发的彩蛋。
     *
     * 只返回配置了 [EasterEgg.trigger] 的彩蛋；依赖时间、服务端行为的彩蛋
     * （午夜场、共创者等）不在此列，需要接入埋点后另行判定。
     */
    fun triggeredEasterEggs(selected: List<Hobby>): List<EasterEgg> {
        val ids = selected.map { it.id }.toSet()
        return ChenmiSha.easterEggs().filter { egg ->
            when (val trigger = egg.trigger) {
                null -> false
                is EggTrigger.RequiresHobbies -> ids.containsAll(trigger.hobbyIds)
                EggTrigger.AllTiersUnlocked -> Tier.values().all { isUnlocked(it, selected) }
            }
        }
    }

    /** 可由选择直接触发的彩蛋总数，用于「已发现 x/y」的进度展示。 */
    fun selectionTriggeredEggCount(): Int =
        ChenmiSha.easterEggs().count { it.trigger != null }
}
