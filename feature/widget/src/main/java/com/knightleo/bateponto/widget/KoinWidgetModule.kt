package com.knightleo.bateponto.widget

import com.knightleo.bateponto.data.dataModule
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val module = module {
    includes(dataModule)
    viewModel { MarkerViewModel(get()) }
}