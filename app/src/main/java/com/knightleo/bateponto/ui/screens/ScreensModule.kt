package com.knightleo.bateponto.ui.screens

import com.knightleo.bateponto.ui.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val screensModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
}