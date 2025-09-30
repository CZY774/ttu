package com.czy.ttu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.czy.ttu.ui.theme.FruitGreen
import com.czy.ttu.ui.theme.FruitOrange
import com.czy.ttu.ui.theme.FruitYellow
import com.czy.ttu.ui.theme.TextDark
import com.czy.ttu.ui.theme.TextLight
import com.czy.ttu.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(FruitYellow, FruitOrange.copy(alpha = 0.3f))
                )
            )
    ) {
        // Top Bar
        TopAppBar(
            title = { Text("About Fruit Finder") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = FruitGreen,
                titleContentColor = White,
                navigationIconContentColor = White
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Icon
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(20.dp),
                color = FruitGreen
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🍎",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Fruit Finder",
                style = MaterialTheme.typography.headlineLarge,
                color = FruitGreen
            )

            Text(
                text = "v1.0.0",
                style = MaterialTheme.typography.bodyLarge,
                color = TextLight
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Info",
                            tint = FruitGreen
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "About This App",
                            style = MaterialTheme.typography.titleMedium,
                            color = FruitGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fruit Finder adalah aplikasi edukasi yang membantu anak-anak belajar mengenal berbagai jenis buah-buahan menggunakan teknologi pengenalan gambar (Image Recognition).",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDark,
                        textAlign = TextAlign.Justify
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Fitur:",
                        style = MaterialTheme.typography.titleSmall,
                        color = FruitGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val features = listOf(
                        "🔍 Deteksi buah secara real-time",
                        "📱 Interface ramah anak",
                        "🇮🇩 Nama buah dalam Bahasa Indonesia",
                        "💡 Fakta menarik tentang buah",
                        "🔦 Dukungan flash kamera",
                        "📷 Kamera depan dan belakang"
                    )

                    features.forEach { feature ->
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FruitGreen.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tugas Akhir",
                        style = MaterialTheme.typography.titleMedium,
                        color = FruitGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Perancangan Aplikasi Edukasi Deteksi Nama-Nama Buah untuk Anak Sekolah Dasar dengan Image Detection dan Android",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Teknologi yang Digunakan:",
                        style = MaterialTheme.typography.titleSmall,
                        color = FruitGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val technologies = listOf(
                        "• Kotlin & Gradle KTS",
                        "• Jetpack Compose",
                        "• TensorFlow Lite",
                        "• CameraX API"
                    )

                    technologies.forEach { tech ->
                        Text(
                            text = tech,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextDark
                        )
                    }
                }
            }
        }
    }
}