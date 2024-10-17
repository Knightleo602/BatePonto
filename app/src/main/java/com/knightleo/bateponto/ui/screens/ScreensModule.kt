package com.knightleo.bateponto.ui.screens

import com.knightleo.bateponto.ui.screens.daylist.DayListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val screensModule = module {
    viewModel { DayListViewModel(get(), get(), get()) }
}