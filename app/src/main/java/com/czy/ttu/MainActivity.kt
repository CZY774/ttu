package com.czy.ttu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.czy.ttu.ui.screens.CameraScreen
import com.czy.ttu.ui.theme.FruitDetectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FruitDetectionTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CameraScreen()
                }
            }
        }
    }
}
