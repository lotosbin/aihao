package com.yuanjingtech.aihao.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.yuanjingtech.aihao.content.ChenmiSha
import com.yuanjingtech.aihao.content.Gender
import com.yuanjingtech.aihao.content.Hobby
import com.yuanjingtech.aihao.content.Tier

/**
 * 【沉迷啥】段位徽章预览页。
 *
 * 它是 core 内容库在 Compose 端的接入示例：徽章材质与配色（规格 5.2）、
 * 段位名称生成（规格 5.3）与段位评语（规格 6.1 / 6.2）全部来自 core，
 * 三端（Android / iOS / Desktop / Web）共用同一份内容。
 */
@Composable
fun ChenmiShaApp(modifier: Modifier = Modifier) {
    var gender by remember { mutableStateOf(Gender.MALE) }
    val profile = remember(gender) { sampleProfile(gender) }
    val rank = ChenmiSha.rankName(gender, profile)

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
                Button(onClick = { gender = option }) {
                    Text(if (option == gender) "● ${option.displayName}" else option.displayName)
                }
            }
        }

        Text("当前段位：${rank?.text ?: "还没有爱好"}", style = MaterialTheme.typography.titleMedium)
        rank?.let {
            Text(
                "材质 ${it.material.displayName} · ${it.material.finish.displayName}",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Tier.values().forEach { tier ->
            val sample = ChenmiSha.mainHobbies(gender, tier).first()
            BadgeCard(tier = tier, gender = gender, sample = sample)
        }

        Text("限定内容", style = MaterialTheme.typography.titleMedium)
        ChenmiSha.limitedHobbies().take(4).forEach { limited ->
            val rankName = ChenmiSha.rankName(gender, limited.hobby.tier, limited.hobby)
            Text(
                "· ${limited.sourceLabel}：${rankName.text}" +
                    (limited.windowLabel?.let { "（$it）" } ?: ""),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Text(
            "隐藏标签 ${ChenmiSha.hiddenTags().size} 个 · 彩蛋 ${ChenmiSha.easterEggs().size} 个",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/** 一张段位徽章卡片：渐变取自材质，文字颜色按材质亮度自动反转。 */
@Composable
private fun BadgeCard(tier: Tier, gender: Gender, sample: Hobby) {
    val material = tier.material
    val shape = RoundedCornerShape(18.dp)
    val foreground = material.contentColor()
    val rankName = ChenmiSha.rankName(gender, tier, sample)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Brush.linearGradient(material.composeGradient()))
            .border(2.dp, foreground.copy(alpha = 0.25f), shape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(rankName.text, color = foreground, style = MaterialTheme.typography.titleLarge)
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
}

/** 预览用的示例用户：入门、进阶、封神三层各一个爱好，最高段位落在封神级。 */
private fun sampleProfile(gender: Gender): List<Hobby> = listOf(
    ChenmiSha.mainHobbies(gender, Tier.ENTRY).first(),
    ChenmiSha.mainHobbies(gender, Tier.INTERMEDIATE).first(),
    ChenmiSha.mainHobbies(gender, Tier.DEMIGOD).first(),
)
