# Fruit Finder - Aplikasi Deteksi Buah untuk Anak SD

## Deskripsi
Aplikasi Android edukasi yang menggunakan machine learning untuk mendeteksi berbagai jenis buah-buahan secara real-time melalui kamera smartphone.

## Fitur Utama
- 🔍 Real-time fruit detection menggunakan TensorFlow Lite
- 📱 UI ramah anak dengan design yang menarik
- 🇮🇩 Nama buah dalam Bahasa Indonesia
- 💡 Fakta menarik dan informasi nutrisi
- 🔦 Dukungan flash kamera
- 📷 Support kamera depan dan belakang
- 🎯 Confidence score untuk akurasi deteksi

## Tech Stack
- **Language**: Kotlin
- **Build System**: Gradle KTS
- **UI Framework**: Jetpack Compose
- **ML Framework**: TensorFlow Lite
- **Camera**: CameraX API
- **Architecture**: MVVM with Repository Pattern

## Struktur Project
```
app/
├── src/main/
│   ├── java/com/yourpackage/fruitdetection/
│   │   ├── ui/               # UI Components & Screens
│   │   ├── camera/           # Camera Management
│   │   ├── ml/               # Machine Learning
│   │   ├── data/             # Data Layer
│   │   ├── utils/            # Utilities
│   │   └── navigation/       # Navigation
│   ├── assets/
│   │   ├── models/           # TensorFlow Lite models
│   │   └── class_names.json  # Fruit class names
│   └── res/                  # Resources
```

## Setup
1. Clone repository
2. Tambahkan file .tflite models ke `assets/models/`
3. Update `class_names.json` sesuai dengan model
4. Build dan run aplikasi

## Model Requirements
- Input size: 224x224 pixels
- Format: RGB
- Supported formats: .tflite (float32, int8, quantized)

## Target Audience
Anak sekolah dasar (6-12 tahun) untuk pembelajaran mengenal buah-buahan.