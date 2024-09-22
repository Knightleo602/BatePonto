package com.knightleo.bateponto

import com.knightleo.bateponto.data.dataModule
import com.knightleo.bateponto.ui.screens.screensModule
import org.koin.dsl.module

val appModule = module {
    includes(dataModule, screensModule)
}