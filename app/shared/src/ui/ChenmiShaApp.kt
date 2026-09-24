package com.yuanjingtech.aihao.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.yuanjingtech.aihao.content.ChenmiSha
import com.yuanjingtech.aihao.content.EasterEgg
import com.yuanjingtech.aihao.content.Gender
import com.yuanjingtech.aihao.content.Hobby
import com.yuanjingtech.aihao.content.Progression
import com.yuanjingtech.aihao.content.Tier
import kotlin.random.Random

/**
 * 【沉迷啥】段位徽章互动页。
 *
 * 玩法：**先挑爱好，才点亮徽章**。
 * - 入门级永远可选；每点亮一层就开放下一层的选择权（规则见 core 的 `Progression`）。
 * - 徽章解锁范围 = 达到过的最高段位及其以下全部段位。
 * - 段位名称、评语、彩蛋都随选择实时变化。
 *
 * 徽章材质与配色（规格 5.2）、段位名称生成（5.3）、段位评语（6.1/6.2）、
 * 彩蛋（6.4）全部来自 core 内容库，UI 只负责呈现。
 */
@Composable
fun ChenmiShaApp(modifier: Modifier = Modifier) {
    var gender by remember { mutableStateOf(Gender.MALE) }
    var selected by remember { mutableStateOf<List<Hobby>>(emptyList()) }
    var lastUnlocked by remember { mutableStateOf<Tier?>(null) }
    var flash by remember { mutableStateOf<String?>(null) }

    val progress = Progression.progress(selected)
    val rank = ChenmiSha.rankName(gender, selected)
    val eggs = Progression.triggeredEasterEggs(selected)

    /** 切换一个爱好：已选则取消，未开放则忽略。 */
    fun onToggle(hobby: Hobby) {
        val wasSelected = selected.any { it.id == hobby.id }
        if (!wasSelected && !Progression.canSelect(hobby, selected)) return

        val before = progress.unlockedCount
        val next = Progression.toggle(selected, hobby)
        val after = Progression.progress(next)
        selected = next
        lastUnlocked = when {
            after.unlockedCount > before -> after.unlockedTiers.last()
            after.unlockedCount < before -> null
            else -> lastUnlocked
        }
        flash = (if (wasSelected) "已熄灭：" else "已点亮：") +
            ChenmiSha.rankName(gender, hobby.tier, hobby).text
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("沉迷啥 · 段位徽章", style = MaterialTheme.typography.headlineSmall)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Gender.values().forEach { option ->
                Button(onClick = {
                    if (option != gender) {
                        gender = option
                        selected = emptyList()
                        lastUnlocked = null
                        flash = null
                    }
                }) {
                    Text(if (option == gender) "● ${option.displayName}" else option.displayName)
                }
            }
        }

        // ---- 当前结果 ----
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                rank?.text ?: "还没点亮任何徽章",
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                if (progress.notStarted) {
                    "从下面的入门级挑 1 个爱好，第一枚徽章就会亮起来"
                } else {
                    "已挑 ${progress.selectedCount} 个爱好 · 已解锁 ${progress.unlockedCount}/${progress.totalTiers} 段位"
                },
                style = MaterialTheme.typography.bodySmall,
            )
            flash?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }

        // ---- 徽章墙 ----
        Tier.values().forEach { tier ->
            BadgeCard(
                tier = tier,
                gender = gender,
                unlocked = Progression.isUnlocked(tier, selected),
                pick = selected.firstOrNull { it.tier == tier },
                isNew = tier == lastUnlocked,
                nextTier = progress.nextTier,
            )
        }

        // ---- 彩蛋 ----
        EggPanel(eggs = eggs, total = Progression.selectionTriggeredEggCount())

        // ---- 挑爱好 ----
        Text("挑爱好", style = MaterialTheme.typography.titleMedium)
        val accessible = Progression.accessibleTier(selected)
        Tier.values().forEach { tier ->
            val locked = tier.ordinal > accessible.ordinal
            HobbyPickerRow(
                tier = tier,
                locked = locked,
                hobbies = if (locked) emptyList() else ChenmiSha.mainHobbies(gender, tier),
                selectedIds = selected.map { it.id }.toSet(),
                onToggle = ::onToggle,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                selected = randomProfile(gender)
                lastUnlocked = Progression.progress(selected).unlockedTiers.lastOrNull()
                flash = "🎲 " + (ChenmiSha.rankName(gender, selected)?.text ?: "")
            }) {
                Text("🎲 手气不错")
            }
            Button(onClick = {
                selected = emptyList()
                lastUnlocked = null
                flash = null
            }) {
                Text("重新开始")
            }
        }

        Text(
            "限定内容共 ${ChenmiSha.limitedHobbies().size} 条（节日 / 地域 / 品牌），按活动时间上线",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/** 锁定徽章上的提示文案。 */
private fun lockedHint(tier: Tier, nextTier: Tier?): String = when {
    nextTier == null -> "已全部解锁"
    tier == nextTier -> "点亮 1 个「${tier.displayName}」爱好即可解锁"
    else -> "先解锁「${Tier.values()[tier.ordinal - 1].displayName}」"
}

/** 一张段位徽章卡片：已解锁显示材质渐变与评语，未解锁显示悬念与解锁条件。 */
@Composable
private fun BadgeCard(
    tier: Tier,
    gender: Gender,
    unlocked: Boolean,
    pick: Hobby?,
    isNew: Boolean,
    nextTier: Tier?,
) {
    val material = tier.material
    val shape = RoundedCornerShape(18.dp)
    val scale by animateFloatAsState(if (isNew) 1.03f else 1f, label = "badgeScale")
    val base = Modifier
        .fillMaxWidth()
        .graphicsLayer(scaleX = scale, scaleY = scale)
        .clip(shape)

    if (unlocked) {
        val foreground = material.contentColor()
        Column(
            modifier = base
                .background(Brush.linearGradient(material.composeGradient()))
                .border(2.dp, foreground.copy(alpha = 0.25f), shape)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    pick?.let { ChenmiSha.rankName(gender, tier, it).text } ?: "${tier.displayName} · 已解锁",
                    color = foreground,
                    style = MaterialTheme.typography.titleLarge,
                )
                if (isNew) {
                    Text(
                        "   NEW",
                        color = foreground,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            Text(
                "${tier.displayName} · ${material.displayName} · ${material.finish.displayName}",
                color = foreground.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                ChenmiSha.tierCopy(gender, tier),
                color = foreground.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    } else {
        val lockedForeground = MaterialTheme.colorScheme.onSurfaceVariant
        Column(
            modifier = base
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(2.dp, lockedForeground.copy(alpha = 0.2f), shape)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("🔒 ？？？", color = lockedForeground, style = MaterialTheme.typography.titleLarge)
            Text(
                "${tier.displayName} · ${material.displayName} · ${material.finish.displayName}",
                color = lockedForeground.copy(alpha = 0.8f),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                lockedHint(tier, nextTier),
                color = lockedForeground.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

/** 彩蛋面板：显示已触发彩蛋与可触发总数。 */
@Composable
private fun EggPanel(eggs: List<EasterEgg>, total: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            "彩蛋　已发现 ${eggs.size}/$total",
            style = MaterialTheme.typography.titleMedium,
        )
        if (eggs.isEmpty()) {
            Text(
                "多挑几个同段位的爱好，说不定能凑出点什么。",
                style = MaterialTheme.typography.bodySmall,
            )
        } else {
            eggs.forEach { egg ->
                Text(
                    "🎉 ${egg.name}：${egg.reward}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

/** 一行爱好选择器；未解锁的段位显示锁定提示。 */
@Composable
private fun HobbyPickerRow(
    tier: Tier,
    locked: Boolean,
    hobbies: List<Hobby>,
    selectedIds: Set<String>,
    onToggle: (Hobby) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            if (locked) "${tier.displayName} · 🔒 未开放" else tier.displayName,
            style = MaterialTheme.typography.labelLarge,
            color = if (locked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
        )
        if (!locked) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                hobbies.forEach { hobby ->
                    HobbyChip(
                        label = hobby.name,
                        selected = hobby.id in selectedIds,
                        onClick = { onToggle(hobby) },
                    )
                }
            }
        }
    }
}

/** 可点击的爱好标签。自绘而不是用 FilterChip，避免依赖实验性 API。 */
@Composable
private fun HobbyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val background = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val foreground = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(label, color = foreground, style = MaterialTheme.typography.labelLarge)
    }
}

/** 「手气不错」：随机选到某一层，每层随机一个爱好。 */
private fun randomProfile(gender: Gender): List<Hobby> {
    val target = Random.nextInt(1, Tier.values().size)
    return (0..target).map { index ->
        ChenmiSha.mainHobbies(gender, Tier.values()[index]).random()
    }
}
