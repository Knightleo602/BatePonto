package com.knightleo.bateponto.ui

import androidx.compose.ui.Modifier

inline fun Modifier.condition(condition: Boolean, onTrue: (Modifier) -> Modifier): Modifier =
    if(condition) then(onTrue(Modifier))
    else this