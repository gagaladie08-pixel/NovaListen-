// app/src/main/java/com/novalisten/app/presentation/MainActivity.kt

package com.novalisten.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.novalisten.app.presentation.navigation.NovaListenNavGraph
import com.novalisten.app.presentation.theme.NovaListenTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activité unique de NovaListen.
 * Toute la navigation se fait via Compose Navigation.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovaListenTheme {
                NovaListenNavGraph()
            }
        }
    }
}