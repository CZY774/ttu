# 🍎 TanyaBuah - Aplikasi Deteksi Buah untuk Anak SD

> **Tugas Akhir 1**  
> Cornelius Ardhani Yoga Pratama (NIM: 672022204)  
> Program Studi Teknik Informatika  
> Universitas Kristen Satya Wacana

---

## 📱 Tentang Aplikasi

Aplikasi Android edukasi yang dapat mendeteksi 21 jenis buah-buahan secara real-time menggunakan kamera dan teknologi Image Detection berbasis Machine Learning (TensorFlow Lite).

### ✨ Fitur Utama:
- ✅ Deteksi buah real-time menggunakan kamera
- ✅ Switch kamera depan/belakang
- ✅ Toggle flash ON/OFF
- ✅ Menampilkan nama buah + confidence score
- ✅ Fun facts edukatif tentang setiap buah
- ✅ UI sederhana dan ramah anak

### 🍓 Buah yang Dapat Dideteksi (21 jenis):
```
Alpukat, Anggur, Apel, Belimbing, Durian,
Jambu, Jeruk, Kelapa, Kelengkeng, Mangga,
Manggis, Melon, Nanas, Pepaya, Pir,
Pisang, Rambutan, Salak, Semangka, Strawberry, Tomat
```

---

## 🗂️ Struktur Repository

```
.
├── README.md                      # File ini
├── INSTRUCTIONS.md                # Panduan lengkap untuk AI Agent/Developer
├── extract_class_names.py        # Script 1: Extract & mapping class names
├── dataset_preparation.py         # Script 2: Persiapan dataset
├── train_fruit_model.py          # Script 3: Training model
├── convert_keras_to_tflite.py    # Script 4: Konversi model ke TFLite
├── model_metadata.json           # Metadata model yang sudah trained
│
├── app/                          # Android app code (Kotlin + Jetpack Compose)
│   ├── src/main/
│   │   ├── java/com/czy/ttu/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── FruitInfo.kt
│   │   │   │   └── FruitRepository.kt
│   │   │   ├── ml/
│   │   │   │   └── FruitClassifier.kt
│   │   │   └── ui/
│   │   │       ├── screens/
│   │   │       │   └── CameraScreen.kt
│   │   │       ├── components/
│   │   │       │   ├── CameraPreview.kt
│   │   │       │   ├── CameraControls.kt
│   │   │       │   └── DetectionResultCard.kt
│   │   │       └── theme/
│   │   │           ├── Color.kt
│   │   │           ├── Theme.kt
│   │   │           └── Type.kt
│   │   ├── assets/
│   │   │   ├── fruit_detector_quantized.tflite  ⭐
│   │   │   ├── class_names.json
│   │   │   └── fruit_info.json
│   │   └── res/
│   │       └── font/
│   │           └── inter_regular.ttf
│   └── build.gradle.kts
│
└── [Dataset folders - not included in repo]
    ├── fruit-dataset-full-varieties/  # Dataset hasil preparation
    └── model_output/                  # Hasil training
```

---

## 🚀 Quick Start

### 1️⃣ Setup Environment (Python untuk Training)

```bash
# Clone repository
git clone <your-repo-url>
cd TTU

# Create virtual environment (recommended)
python -m venv venv
source venv/bin/activate  # Linux/Mac
# atau
venv\Scripts\activate  # Windows

# Install dependencies
pip install tensorflow numpy matplotlib scikit-learn pillow
```

### 2️⃣ Persiapan Dataset

**⚠️ IMPORTANT: Dataset sudah di-download? Lewati download, langsung preparation!**

```bash
# STEP 1: Extract class names dari dataset
python extract_class_names.py
# Output: Mapping buah Indonesia ke class names dataset

# STEP 2: Update path di dataset_preparation.py
# Edit DATASET_PATHS sesuai lokasi dataset Anda

# STEP 3: Run preparation
python dataset_preparation.py

# Script akan:
# ✅ Check dataset lokal (TIDAK download ulang)
# ✅ Filter 21 buah yang relevan
# ✅ Reorganisasi dan rename ke Bahasa Indonesia
# ✅ Balance dataset (250 gambar per class)
# ✅ Generate class_names.json dan fruit_info.json
```

**Output:**
```
fruit-dataset-full-varieties/
├── train/
│   ├── Apel/        (250 images)
│   ├── Pisang/      (250 images)
│   └── ... (21 classes)
├── val/
│   ├── Apel/        (62 images)
│   ├── Pisang/      (62 images)
│   └── ... (21 classes)
├── class_names.json
└── fruit_info.json
```

### 3️⃣ Training Model

```bash
# Run training (bisa pakai Google Colab untuk GPU gratis)
python train_fruit_model.py

# Training akan berjalan ~30-45 menit dengan GPU
# ~2-3 jam dengan CPU

# Di tengah proses, Anda akan ditanya:
# "Fine-tune model? (Y/n):"
# - Ketik Y untuk akurasi lebih tinggi (lebih lama)
# - Ketik n untuk skip fine-tuning
```

**Output:**
```
model_output/
├── models/
│   └── fruit_detector_best.keras
├── plots/
│   ├── training_history_[timestamp].png
│   └── confusion_matrix_[timestamp].png
└── model_metadata.json
```

### 4️⃣ Konversi Model ke TFLite

```bash
# Konversi model Keras ke TensorFlow Lite
python convert_keras_to_tflite.py

# Script akan generate 3 versi:
# - Float32 (full precision)
# - Float16 (half precision)
# - INT8 Quantized (untuk Android) ⭐
```

**Output:**
```
model_output/
└── models/
    ├── fruit_detector_float32.tflite
    ├── fruit_detector_float16.tflite
    └── fruit_detector_quantized.tflite  ⭐ (3.5 MB)
```

### 5️⃣ Setup Android Project

```bash
# Buka Android Studio
File > Open > pilih folder project root (TTU)

# Model sudah ada di app/src/main/assets/:
# ✅ fruit_detector_quantized.tflite (3.5 MB)
# ✅ class_names.json
# ✅ fruit_info.json

# Sync Gradle
# Build > Make Project

# Run di device atau emulator
# ⚠️ Untuk testing akurat, gunakan REAL DEVICE (bukan emulator)
```

---

## 📊 Model Performance

Berdasarkan training terakhir (model_metadata.json):

- **Architecture:** MobileNetV2 + Custom Top Layers
- **Input Size:** 224x224x3 RGB
- **Number of Classes:** 21 buah
- **Model Size:** 3.5 MB (INT8 Quantized)
- **Preprocessing:** (pixel / 127.5) - 1.0 → [-1, 1]

**Target Akurasi:**
- Training Accuracy: >95%
- Validation Accuracy: >90% ✅
- Top-3 Accuracy: >98%

---

## 🛠️ Tech Stack

### Machine Learning:
- **Framework:** TensorFlow 2.14 + Keras
- **Model:** MobileNetV2 (Transfer Learning)
- **Input Size:** 224x224 RGB
- **Output:** 21 classes (Softmax)
- **Optimization:** INT8 Quantization untuk mobile

### Android App:
- **Language:** Kotlin
- **Build System:** Gradle KTS 8.14.3
- **UI Framework:** Jetpack Compose + Material Design 3
- **Camera:** CameraX Library
- **ML Runtime:** TensorFlow Lite
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)
- **Compile SDK:** 35

### Key Dependencies:

```gradle
// Jetpack Compose
implementation platform('androidx.compose:compose-bom:2023.10.01')
implementation 'androidx.compose.material3:material3'
implementation 'androidx.activity:activity-compose:1.7.2'
implementation 'androidx.navigation:navigation-compose:2.6.0'

// CameraX
implementation 'androidx.camera:camera-core:1.3.4'
implementation 'androidx.camera:camera-camera2:1.3.4'
implementation 'androidx.camera:camera-lifecycle:1.3.4'
implementation 'androidx.camera:camera-view:1.3.4'

// TensorFlow Lite
implementation 'org.tensorflow:tensorflow-lite:2.14.0'
implementation 'org.tensorflow:tensorflow-lite-support:0.4.4'
```

---

## 📖 Dokumentasi Lengkap

Lihat **`INSTRUCTIONS.md`** untuk:
- Setup detail Android project
- Penjelasan arsitektur aplikasi
- Code snippets lengkap
- Troubleshooting guide
- Testing checklist
- Tips untuk development

---

## 🎨 Design Principles

Aplikasi ini dirancang khusus untuk anak Sekolah Dasar dengan prinsip:

1. **Sederhana** - Maksimal 2-3 elemen per screen
2. **Colorful** - Warna cerah tapi tidak berlebihan
3. **Readable** - Font minimal 18sp, menggunakan Inter (double-story 'a')
4. **Child-Friendly** - Tidak ada teks kompleks atau navigasi rumit
5. **Educational** - Setiap deteksi disertai fun fact yang menarik

---

## 🧪 Testing

### Unit Testing:
```bash
# Run unit tests
./gradlew test
```

### Instrumentation Testing:
```bash
# Run di device/emulator
./gradlew connectedAndroidTest
```

---

## 🐛 Known Issues & Solutions

### Issue 1: Model Tidak Load di Android
```
Error: Failed to load model
Solution:
1. Pastikan file .tflite ada di app/src/main/assets/
2. Pastikan filename exact match: fruit_detector_quantized.tflite
3. Check file tidak corrupt (size ~3.5 MB)
4. Rebuild project (Build > Clean Project > Rebuild)
```

### Issue 2: Akurasi Rendah pada Real Testing
```
Error: Confidence score selalu <50%
Solution:
1. Pastikan preprocessing sesuai training: (pixel/127.5)-1.0
2. Test dengan lighting yang cukup
3. Test dengan buah yang ada di training set
4. Pastikan kamera fokus sebelum deteksi
```

### Issue 3: Out of Memory saat Training
```
Error: ResourceExhaustedError
Solution:
1. Reduce BATCH_SIZE dari 32 ke 16 atau 8
2. Gunakan Google Colab dengan GPU
3. Close aplikasi lain saat training
```

### Issue 4: Gradle Sync Failed
```
Error: Gradle sync failed
Solution:
1. Update Gradle wrapper ke 8.14.3
2. Invalidate Caches & Restart Android Studio
3. Check internet connection untuk download dependencies
```

---

## 👨‍💻 Author

**Cornelius Ardhani Yoga Pratama**  
NIM: 672022204  
Program Studi Teknik Informatika  
Fakultas Teknologi Informasi  
Universitas Kristen Satya Wacana

---

*Last updated: November 24, 2025*
