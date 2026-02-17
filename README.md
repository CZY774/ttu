---

# 🍎 TanyaBuah - Fruit Detection App for Elementary Students

Educational Android application designed for elementary students, featuring real-time fruit recognition powered by MobileNetV2 deep learning model.

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![TensorFlow](https://img.shields.io/badge/TensorFlow-FF6F00?style=for-the-badge&logo=tensorflow&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)

</div>

---

## 📜 Intellectual Property Rights

This application is **officially registered** with the **Directorate General of Intellectual Property (DGIP)**, Ministry of Law and Human Rights, Republic of Indonesia.

### Registration Details:
- **Registration Number:** 001138316
- **Registration Date:** February 3, 2026
- **Title:** TanyaBuah
- **Type:** Computer Program (Mobile Application)
- **Protection Period:** 50 years from first publication
- **Creators:** Cornelius Ardhani Yoga Pratama & Pratyaksa Ocsa Nugraha Saian

### Protected Elements:
- Source code and implementation
- UI/UX design and user interface
- Machine learning model integration
- Educational content and methodology
- Application concept and workflow

### Verification:
Certificate verification available upon request for recruitment or licensing purposes.

---

## 📱 About the Application

Educational Android application that can detect 21 types of fruits in real-time using camera and Image Detection technology based on Machine Learning (TensorFlow Lite).

### ✨ Main Features:
- ✅ Real-time fruit detection using camera
- ✅ Front/back camera switch
- ✅ Toggle flash ON/OFF
- ✅ Display fruit name + confidence score
- ✅ Educational fun facts about each fruit
- ✅ Simple and child-friendly UI

### 🍓 Detectable Fruits (21 types):
```
Avocado, Grape, Apple, Starfruit, Durian,
Guava, Orange, Coconut, Longan, Mango,
Mangosteen, Melon, Pineapple, Papaya, Pear,
Banana, Rambutan, Salak, Watermelon, Strawberry, Tomato
```

---

## 🗂️ Repository Structure

```
.
├── README.md                      # This file
├── INSTRUCTIONS.md                # Complete guide for AI Agent/Developer
├── extract_class_names.py        # Script 1: Extract & mapping class names
├── dataset_preparation.py         # Script 2: Dataset preparation
├── train_fruit_model.py          # Script 3: Training model
├── convert_keras_to_tflite.py    # Script 4: Convert model to TFLite
├── model_metadata.json           # Trained model metadata
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
    ├── fruit-dataset-full-varieties/  # Dataset preparation results
    └── model_output/                  # Training results
```

---

## 🚀 Quick Start

### 1️⃣ Setup Environment (Python for Training)

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

### 2️⃣ Dataset Preparation

**⚠️ IMPORTANT: Has the dataset been downloaded? Skip the download, get straight to preparation!**

```bash
# STEP 1: Extract class names from the dataset
python extract_class_names.py
# Output: Mapping Indonesian fruits to dataset class names

# STEP 2: Update the path in dataset_preparation.py
# Edit DATASET_PATHS according to your dataset location

# STEP 3: Run preparation
python dataset_preparation.py

# The script will:
# ✅ Check the local dataset (NOT re-download)
# ✅ Filter 21 relevant fruits
# ✅ Reorganize and rename to Indonesian
# ✅ Balance the dataset (250 images per class)
# ✅ Generate class_names.json and fruit_info.json
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

### 3️⃣ Model Training

```bash
# Run training (you can use Google or Kaggle Colab for free GPUs)
python train_fruit_model.py

# Training will take ~30-45 minutes on a GPU
# ~2-3 hours on a CPU

# During the process, you will be asked:
# "Fine-tune model? (Y/n):"
# - Type Y for higher accuracy (longer)
# - Type n to skip fine-tuning
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

### 4️⃣ Convert Model to TFLite

```bash
# Convert Keras model to TensorFlow Lite
python convert_keras_to_tflite.py

# The script will generate 3 versions:
# - Float32 (full precision)
# - Float16 (half precision)
# - INT8 Quantized (for Android) ⭐
```

**Output:**
```
model_output/
└── models/
    ├── fruit_detector_float32.tflite
    ├── fruit_detector_float16.tflite
    └── fruit_detector_quantized.tflite  ⭐ (3.5 MB)
```

### 5️⃣ Android Project Setup

```bash
# Open Android Studio
File > Open > select the project root folder (TTU)

# The model already exists in app/src/main/assets/:
# ✅ fruit_detector_quantized.tflite (3.5 MB)
# ✅ class_names.json
# ✅ fruit_info.json

# Sync Gradle
# Build > Make Project

# Run on a device or emulator
# ⚠️ For accurate testing, use a REAL DEVICE (not a emulators)
```

---

## 📊 Model Performance

Based on last training (model_metadata.json):

- **Architecture:** MobileNetV2 + Custom Top Layers
- **Input Size:** 224x224x3 RGB
- **Number of Classes:** 21 buah
- **Model Size:** 3.5 MB (INT8 Quantized)
- **Preprocessing:** (pixel / 127.5) - 1.0 → [-1, 1]

**Target Accuracy:**
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
- **Optimization:** INT8 Quantization for mobile

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

## 📖 Documentation Complete

See **`INSTRUCTIONS.md`** for:
- Detailed Android project setup
- Application architecture explanation
- Complete code snippets
- Troubleshooting guide
- Testing checklist
- Development tips

---

## 🎨 Design Principles

This application is specifically designed for elementary school children with the following principles:

1. **Simple** - Maximum 2-3 elements per screen
2. **Colorful** - Bright but not excessive colors
3. **Readable** - Minimum 18sp font, using Inter (double-story 'a')
4. **Child-Friendly** - No complex text or complicated navigation
5. **Educational** - Each detection is accompanied by an interesting fun fact

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

### Issue 1: Model Not Loading on Android
```
Error: Failed to load model
Solution:
1. Ensure the .tflite file is in app/src/main/assets/
2. Ensure the filename exactly matches: fruit_detector_quantized.tflite
3. Check the file is not corrupted (size ~3.5 MB)
4. Rebuild the project (Build > Clean Project > Rebuild)
```

### Issue 2: Low Accuracy in Real-Time Testing
```
Error: Confidence score always <50%
Solution:
1. Ensure preprocessing matches training: (pixel/127.5)-1.0
2. Test with sufficient lighting
3. Test with fruit from the training set
4. Ensure the camera is in focus before detection
```

### Issue 3: Out of Memory during Training
```
Error: ResourceExhaustedError
Solution:
1. Reduce BATCH_SIZE from 32 to 16 or 8
2. Use Google Colab with GPU
3. Close other applications during training
```

### Issue 4: Gradle Sync Failed
```
Error: Gradle sync failed
Solution:
1. Update Gradle wrapper to 8.14.3
2. Invalidate Caches & Restart Android Studio
3. Check internet connection to download dependencies
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


---

## 📄 License

**Copyright © 2024-2026 Cornelius Ardhani Yoga Pratama & Pratyaksa Ocsa Nugraha Saian**  
**All Rights Reserved.**

This code is made available for **portfolio review and educational reference only**.

### ✅ Permitted Use:
- View code for learning purposes
- Review for recruitment/hiring evaluation
- Reference architecture patterns for educational purposes

### ❌ Prohibited Use:
- Commercial use or redistribution
- Copying or forking for production use
- Removing copyright notices
- Claiming as original work
- Registering for Intellectual Property Rights

**This project is protected by Indonesian copyright law (UU No. 28 Tahun 2014 tentang Hak Cipta).**

---

## 👨💻 Developers

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/CZY774">
        <img src="https://github.com/CZY774.png" width="100px;" alt="Cornelius Yoga"/>
        <br />
        <sub><b>Cornelius Ardhani Yoga Pratama</b></sub>
      </a>
      <br />
      <a href="https://corneliusyoga.vercel.app">Portfolio</a> •
      <a href="https://linkedin.com/in/cornelius-yoga-783b6a291">LinkedIn</a>
    </td>
    <td align="center">
      <sub><b>Pratyaksa Ocsa Nugraha Saian</b></sub>
      <br />
      Co-Creator
    </td>
  </tr>
</table>

---

<div align="center">

**Note for Recruiters:**  
This project demonstrates mobile development, machine learning integration, and educational software design capabilities. Full documentation and additional project details available upon request.

</div>
