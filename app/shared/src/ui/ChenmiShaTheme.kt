package com.yuanjingtech.aihao.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import com.yuanjingtech.aihao.resources.Res
import com.yuanjingtech.aihao.resources.lxgw_wenkai_regular
import org.jetbrains.compose.resources.Font

/**
 * 默认中文字体：霞鹜文楷（LXGW WenKai）。
 *
 * **为什么要显式带字体**：Compose Multiplatform 在 Web(Wasm) 与部分平台上不接入浏览器/系统的
 * 中文字体，文字由 Skia 直接绘制到 canvas；如果不显式提供 CJK 字体，中文会渲染成空白或豆腐块
 * （tofu）。因此在 `composeResources/font` 中内置一份霞鹜文楷，并把它设为全站默认字体族。
 *
 * 字体文件是**子集化**版本（见 `tools/subset-lxgw-font.sh`）：覆盖 GB2312 全集 + 仓库实际用字
 * + 拉丁/常用标点/全角符号，24 MB → 3.55 MB。生僻字（GB18030 扩展区）不在子集内，
 * 若内容需要生僻字，需重新生成子集或改用发行版全量字体。
 *
 * 许可：SIL Open Font License 1.1，见 `licenses/LXGWWenKai-OFL.txt`；
 * 该许可的 ADDITIONAL PERMISSION 明确允许为 Web 字体交付做子集化。
 */
@Composable
fun chenmiShaFontFamily(): FontFamily = FontFamily(Font(Res.font.lxgw_wenkai_regular))

/**
 * 把霞鹜文楷应用到 Material 3 的全部 15 个文字样式上。
 *
 * Material 3 的 [Typography] 没有「统一换字体族」的入口，只能逐个样式 copy，
 * 因此这里显式列出全部样式，避免漏掉某个样式导致局部回退到无中文字形的默认字体。
 */
@Composable
fun ChenmiShaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = chenmiShaTypography(chenmiShaFontFamily()),
        content = content,
    )
}

/** 生成全站使用霞鹜文楷的 [Typography]。 */
internal fun chenmiShaTypography(family: FontFamily): Typography {
    val base = Typography()
    return Typography(
        displayLarge = base.displayLarge.copy(fontFamily = family),
        displayMedium = base.displayMedium.copy(fontFamily = family),
        displaySmall = base.displaySmall.copy(fontFamily = family),
        headlineLarge = base.headlineLarge.copy(fontFamily = family),
        headlineMedium = base.headlineMedium.copy(fontFamily = family),
        headlineSmall = base.headlineSmall.copy(fontFamily = family),
        titleLarge = base.titleLarge.copy(fontFamily = family),
        titleMedium = base.titleMedium.copy(fontFamily = family),
        titleSmall = base.titleSmall.copy(fontFamily = family),
        bodyLarge = base.bodyLarge.copy(fontFamily = family),
        bodyMedium = base.bodyMedium.copy(fontFamily = family),
        bodySmall = base.bodySmall.copy(fontFamily = family),
        labelLarge = base.labelLarge.copy(fontFamily = family),
        labelMedium = base.labelMedium.copy(fontFamily = family),
        labelSmall = base.labelSmall.copy(fontFamily = family),
    )
}
