package com.knightleo.bateponto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.knightleo.bateponto.ui.AppScreens
import com.knightleo.bateponto.ui.theme.BatePontoTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BatePontoTheme {
                KoinAndroidContext {
                    AppScreens()
                }
            }
        }
    }
}