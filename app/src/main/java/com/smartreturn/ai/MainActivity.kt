package com.smartreturn.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.smartreturn.ai.ui.navigation.SmartReturnNavGraph
import com.smartreturn.ai.ui.theme.BackgroundDeep
import com.smartreturn.ai.ui.theme.SmartReturnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen before super.onCreate
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartReturnTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundDeep
                ) {
                    val navController = rememberNavController()
                    SmartReturnNavGraph(navController = navController)
                }
            }
        }
    }
}
