package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.StartMenuScreen
import com.example.ui.StartMenuViewModel
import com.example.ui.theme.PadStartTheme

class MainActivity : ComponentActivity() {

    private val viewModel: StartMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Translucent floating window configuration
        window.setBackgroundDrawableResource(android.R.color.transparent)
        enableEdgeToEdge()

        setContent {
            PadStartTheme {
                StartMenuScreen(
                    viewModel = viewModel,
                    onDismiss = {
                        finish()
                        @Suppress("DEPRECATION")
                        overridePendingTransition(0, android.R.anim.fade_out)
                    }
                )
            }
        }
    }
}
