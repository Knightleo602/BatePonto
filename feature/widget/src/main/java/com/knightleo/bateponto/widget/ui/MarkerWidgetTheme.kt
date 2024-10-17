package com.knightleo.bateponto.widget.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import androidx.glance.text.TextStyle

object WidgetColorScheme {
    private val light = lightColorScheme()
    private val dark = darkColorScheme()

    val color = ColorProviders(
        light = light,
        dark = dark
    )
}

object WidgetTypography {
    val normalText
        @Composable get() = TextStyle(
            color = GlanceTheme.colors.onBackground,
            fontSize = 12.sp
        )

    val subText
        @Composable get() = TextStyle(
            color = GlanceTheme.colors.onSurfaceVariant,
            fontSize = 11.sp,
        )
}

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
    GlanceTheme(
        colors = WidgetColorScheme.color,
        content = content
    )
}

