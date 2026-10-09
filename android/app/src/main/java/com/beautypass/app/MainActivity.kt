package com.beautypass.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.beautypass.app.navigation.BeautyPassApp
import com.beautypass.app.theme.BeautyPassTheme

/**
 * MainActivity: Ponto de entrada nativo do aplicativo Android BeautyPass.
 * Inicializa o tema oficial M3 (Serene Mint & Teal) e o orquestrador BeautyPassApp.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BeautyPassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BeautyPassApp()
                }
            }
        }
    }
}
