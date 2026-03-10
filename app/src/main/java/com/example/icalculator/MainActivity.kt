package com.example.icalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.icalculator.ui.screens.CalculatorScreen
import com.example.icalculator.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { false }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            AnimatedContent(
                targetState = showSplash,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                transitionSpec = {
                    fadeIn(tween(600)) togetherWith fadeOut(tween(400))
                },
                label = "nav"
            ) { isSplash ->
                if (isSplash) {
                    SplashScreen(onDone = { showSplash = false })
                } else {
                    CalculatorScreen()
                }
            }
        }
    }
}