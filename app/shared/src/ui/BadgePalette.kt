package com.yuanjingtech.aihao.ui

import androidx.compose.ui.graphics.Color
import com.yuanjingtech.aihao.content.BadgeMaterial

/**
 * 把 core 模块里的 `#RRGGBB` 颜色字符串转成 Compose 颜色。
 *
 * core 不依赖任何 UI 框架，所以颜色转换只发生在 app/shared 这一层，
 * iOS / Android / Web / Desktop 共用同一份实现。
 */
internal fun hexColor(hex: String): Color {
    val rgb = hex.removePrefix("#").toLongOrNull(16) ?: 0xFF000000L
    return Color(0xFF000000L or (rgb and 0xFFFFFFL))
}

/** 徽章渐变停靠色。 */
internal fun BadgeMaterial.composeGradient(): List<Color> = gradient.map { hexColor(it) }

/** 文字压在徽章上的前景色：浅色材质用深色字，深色材质用浅色字。 */
internal fun BadgeMaterial.contentColor(): Color {
    val value = primary.removePrefix("#").toLongOrNull(16) ?: 0L
    val r = (value shr 16) and 0xFF
    val g = (value shr 8) and 0xFF
    val b = value and 0xFF
    // ITU-R BT.601 亮度，阈值 0.6 偏向保证浅色金属上的可读性
    val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255.0
    return if (luminance > 0.6) Color(0xFF1B1B1B) else Color.White
}
