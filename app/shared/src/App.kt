package com.yuanjingtech.aihao

import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yuanjingtech.aihao.ui.ChenmiShaApp
import com.yuanjingtech.aihao.ui.ChenmiShaTheme
import org.jetbrains.compose.reload.DevelopmentEntryPoint

@Composable
@Preview
@DevelopmentEntryPoint
fun App() {
    ChenmiShaTheme {
        ChenmiShaApp(modifier = Modifier.safeContentPadding())
    }
}
