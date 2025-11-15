# 🍎 Aplikasi Deteksi Buah untuk Anak SD

> **Tugas Akhir 1**  
> Cornelius Ardhani Yoga Pratama (NIM: 672022204)  
> Program Studi Teknik Informatika  
> Universitas Kristen Satya Wacana

---

## 📱 Tentang Aplikasi

Aplikasi Android edukasi yang dapat mendeteksi 15 jenis buah-buahan secara real-time menggunakan kamera dan teknologi Image Detection berbasis Machine Learning (TensorFlow Lite).

### ✨ Fitur Utama:
- ✅ Deteksi buah real-time menggunakan kamera
- ✅ Switch kamera depan/belakang
- ✅ Toggle flash ON/OFF
- ✅ Menampilkan nama buah + confidence score
- ✅ Fun facts edukatif tentang setiap buah
- ✅ UI sederhana dan ramah anak

### 🍓 Buah yang Dapat Dideteksi (15 jenis):
```
Apel, Pisang, Jeruk, Mangga, Anggur, Semangka, 
Pepaya, Jambu, Nanas, Strawberry, Melon, 
Alpukat, Kelengkeng, Rambutan, Durian
```

---

## 🗂️ Struktur Repository

```
.
├── README.md                      # File ini
├── INSTRUCTIONS.md                # Panduan lengkap untuk AI Agent/Developer
├── dataset_preparation.py         # Script persiapan dataset
├── train_fruit_model.py          # Script training model
├── requirements.txt              # Python dependencies
│
├── app/                          # Android app code
│   ├── src/main/
│   │   ├── java/.../
│   │   ├── assets/
│   │   │   ├── fruit_model_quantized.tflite
│   │   │   ├── class_names.json
│   │   │   └── fruit_info.json
│   │   └── res/
│   └── build.gradle.kts
│
├── fruit-dataset-prepared/       # Dataset hasil preparation
│   ├── train/
│   ├── val/
│   ├── class_names.json
│   ├── fruit_info.json
│   └── dataset_summary.json
│
└── model_output/                 # Hasil training
    ├── models/
    │   ├── fruit_detector_best.keras
    │   ├── fruit_detector_float32.tflite
    │   ├── fruit_detector_float16.tflite
    │   └── fruit_detector_quantized.tflite  ⭐
    ├── plots/
    ├── logs/
    └── model_metadata.json
```

---

## 🚀 Quick Start

### 1️⃣ Setup Environment (Python untuk Training)

```bash
# Clone repository
git clone <your-repo-url>
cd fruit-detection-app

# Create virtual environment (recommended)
python -m venv venv
source venv/bin/activate  # Linux/Mac
# atau
venv\Scripts\activate  # Windows

# Install dependencies
pip install -r requirements.txt
```

**requirements.txt:**
```
tensorflow>=2.14.0
numpy>=1.24.0
matplotlib>=3.7.0
scikit-learn>=1.3.0
seaborn>=0.12.0
kaggle>=1.5.16
Pillow>=10.0.0
```

### 2️⃣ Persiapan Dataset

**⚠️ IMPORTANT: Dataset sudah di-download? Lewati download, langsung preparation!**

```bash
# STEP 1: Update path di dataset_preparation.py
# Edit line 15-16:
DATASET_BASE_DIR = "fruits-360-dataset/fruits-360_100x100/fruits-360"
# Sesuaikan dengan lokasi dataset Anda!

# STEP 2 (Optional): Inspect dataset dulu
python inspect_dataset.py
# Menu:
# - Option 3: Generate mapping untuk buah Indonesia
# - Option 4: Lihat jumlah gambar per class
# - Option 5: Check struktur dataset

# STEP 3: Run preparation
python dataset_preparation.py

# Script akan:
# ✅ Check dataset lokal (TIDAK download ulang)
# ✅ Filter hanya 15 buah yang relevan
# ✅ Reorganisasi dan rename ke Bahasa Indonesia
# ✅ Balance dataset (250 gambar per class)
# ✅ Generate class_names.json dan fruit_info.json
```

**Struktur Dataset yang Dibutuhkan:**
```
fruits-360-dataset/
└── fruits-360_100x100/
    └── fruits-360/
        ├── Training/
        │   ├── Apple Braeburn/
        │   ├── Banana/
        │   └── ... (225 classes)
        └── Test/
            ├── Apple Braeburn/
            ├── Banana/
            └── ... (225 classes)
```

**Output:**
```
fruit-dataset-prepared/
├── train/
│   ├── Apel/        (250 images)
│   ├── Pisang/      (250 images)
│   └── ...
├── val/
│   ├── Apel/        (62 images)
│   ├── Pisang/      (62 images)
│   └── ...
└── class_names.json
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
│   └── fruit_detector_quantized.tflite  ⭐ (untuk Android)
├── plots/
│   ├── training_history_[timestamp].png
│   └── confusion_matrix_[timestamp].png
└── class_names.json
```

### 4️⃣ Setup Android Project

```bash
# Buka Android Studio
File > Open > pilih folder 'app'

# Copy file ke assets/
cp model_output/models/fruit_detector_quantized.tflite app/src/main/assets/
cp fruit-dataset-prepared/class_names.json app/src/main/assets/
cp fruit-dataset-prepared/fruit_info.json app/src/main/assets/

# Sync Gradle
# Build > Make Project

# Run di device atau emulator
# ⚠️ Untuk testing akurat, gunakan REAL DEVICE (bukan emulator)
```

---

## 📊 Target Akurasi

Berdasarkan penelitian terdahulu (Dewi et al., 2023) dengan YOLOv8, target akurasi adalah:

- **Training Accuracy:** >95%
- **Validation Accuracy:** >90% ✅
- **Top-3 Accuracy:** >98%

Model yang sudah ditraining dengan MobileNetV2 + Transfer Learning diharapkan mencapai akurasi **92-94%** pada validation set.

---

## 🛠️ Tech Stack

### Machine Learning:
- **Framework:** TensorFlow 2.14 + Keras
- **Model:** MobileNetV2 (Transfer Learning)
- **Input Size:** 224x224 RGB
- **Output:** 15 classes (Softmax)
- **Optimization:** INT8 Quantization untuk mobile

### Android App:
- **Language:** Kotlin
- **Build System:** Gradle KTS
- **UI Framework:** Jetpack Compose
- **Camera:** CameraX Library
- **ML Runtime:** TensorFlow Lite
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 35 (Android 15)

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
3. **Readable** - Font minimal 18sp, menggunakan Poppins (double-story 'a')
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

### Manual Testing Checklist:
- [ ] Kamera bisa membuka dengan lancar
- [ ] Switch camera (depan/belakang) berfungsi
- [ ] Flash toggle ON/OFF berfungsi
- [ ] Deteksi real-time smooth (min 15 FPS)
- [ ] Confidence score akurat (>70% untuk deteksi valid)
- [ ] Fun fact muncul sesuai buah yang terdeteksi
- [ ] Tidak crash saat rotate screen
- [ ] Permission handling berjalan baik

---

## 📚 Referensi

1. **Dewi et al. (2023)** - "Automated Fruit Classification Based on Deep Learning Utilizing Yolov8" - Akurasi 97.5%
2. **Wibi et al. (2020)** - "Deteksi Buah dengan CNN Berbasis YOLOv3" - Akurasi 70-90%
3. **Ahmed & Reddy (2021)** - "Mobile-Based System for Plant Disease Detection using Deep Learning"
4. **Google Documentation** - CameraX, TensorFlow Lite, Jetpack Compose

---

## 🐛 Known Issues & Solutions

### Issue 1: Dataset Download Gagal
```
Error: 403 Forbidden / 401 Unauthorized
Solution: 
1. Login ke Kaggle.com
2. Go to Account > API > Create New Token
3. Download kaggle.json
4. Jalankan ulang dataset_preparation.py
```

### Issue 2: Out of Memory saat Training
```
Error: ResourceExhaustedError
Solution:
1. Reduce BATCH_SIZE dari 32 ke 16 atau 8
2. Gunakan Google Colab dengan GPU
3. Close aplikasi lain saat training
```

### Issue 3: Model Tidak Load di Android
```
Error: Failed to load model
Solution:
1. Pastikan file .tflite ada di assets/
2. Pastikan filename exact match
3. Check file tidak corrupt (size > 0 bytes)
4. Rebuild project (Build > Clean Project > Rebuild)
```

### Issue 4: Akurasi Rendah pada Real Testing
```
Error: Confidence score selalu <50%
Solution:
1. Pastikan preprocessing sesuai training: (pixel/127.5)-1.0
2. Test dengan lighting yang cukup
3. Test dengan buah yang ada di training set
4. Pastikan kamera fokus sebelum deteksi
```

---

## 🤝 Contributing

Ini adalah project Tugas Akhir individu, tapi saran dan feedback sangat diterima!

Jika menemukan bug atau punya ide improvement:
1. Buat Issue di GitHub
2. Atau kontak langsung via email

---

## 📄 License

Project ini dibuat untuk keperluan Tugas Akhir di Universitas Kristen Satya Wacana.

**Dataset:** Fruits-360 dataset by Horea Muresan, Mihai Oltean (moltean/fruits on Kaggle)

---

## 👨‍💻 Author

**Cornelius Ardhani Yoga Pratama**  
NIM: 672022204  
Program Studi Teknik Informatika  
Fakultas Teknologi Informasi  
Universitas Kristen Satya Wacana

**Dosen Pembimbing:** [Nama Dosen]

---

## 🎯 Roadmap

- [x] Setup project structure
- [x] Dataset preparation script
- [x] Model training script
- [ ] Android app development
- [ ] Testing dengan anak SD
- [ ] Bug fixing & optimization
- [ ] Publikasi jurnal nasional terakreditasi
- [ ] Pengajuan HaKI

---

## 📞 Support

Jika Anda adalah **AI Agent/Future Developer** yang akan melanjutkan project ini:

1. ✅ Baca **INSTRUCTIONS.md** terlebih dahulu
2. ✅ Ikuti step-by-step di Quick Start
3. ✅ Jika ada masalah, check Known Issues section
4. ✅ Test di real device, bukan emulator
5. ✅ Keep it simple - ingat target user adalah anak SD!

**Good luck! 🚀🍎🍌**

---

*Last updated: November 2025*