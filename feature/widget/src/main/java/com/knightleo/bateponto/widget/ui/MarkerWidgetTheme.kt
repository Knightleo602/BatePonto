package com.knightleo.bateponto.widget.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders

object WidgetColorScheme {
    private val light = lightColorScheme()
    private val dark = darkColorScheme()

    val color = ColorProviders(
        light = light,
        dark = dark
    )
}

@Composable
fun WidgetTheme(content: @Composable () -> Unit) {
    GlanceTheme(
        colors = WidgetColorScheme.color,
        content = content
    )
}

