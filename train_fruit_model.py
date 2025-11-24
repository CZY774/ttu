"""
train_fruit_model.py
Script untuk training model deteksi buah menggunakan TensorFlow & Keras
dengan export ke TensorFlow Lite untuk Android

Author: Cornelius Ardhani Yoga Pratama
Date: 2025
Target: Akurasi >90% untuk 15 jenis buah
"""

import os
import json
import numpy as np
import matplotlib.pyplot as plt
from pathlib import Path
from datetime import datetime

import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau, ModelCheckpoint

# ================== KONFIGURASI ==================

# Dataset paths
DATASET_DIR = "/kaggle/working/fruit-dataset-full-varieties"
TRAIN_DIR = f"{DATASET_DIR}/train"
VAL_DIR = f"{DATASET_DIR}/val"

# Model parameters
IMG_SIZE = 224  # MobileNetV2 input size
BATCH_SIZE = 32
EPOCHS = 30
LEARNING_RATE = 0.0001

# Output paths
OUTPUT_DIR = "model_output"
MODEL_NAME = "fruit_detector"
TIMESTAMP = datetime.now().strftime("%Y%m%d_%H%M%S")

# Class names (sesuai dengan dataset_preparation.py)
CLASS_NAMES = [
    "Alpukat", "Anggur", "Apel", "Belimbing", "Durian",
    "Jambu", "Jeruk", "Kelapa", "Kelengkeng", "Mangga",
    "Manggis", "Melon", "Nanas", "Pepaya", "Pir",
    "Pisang", "Rambutan", "Salak", "Semangka", "Strawberry", "Tomat"
]

NUM_CLASSES = len(CLASS_NAMES)

# ================== SETUP ==================

def setup_directories():
    """Create output directories if not exist"""
    print("📁 Setting up directories...")
    Path(OUTPUT_DIR).mkdir(exist_ok=True)
    Path(f"{OUTPUT_DIR}/models").mkdir(exist_ok=True)
    Path(f"{OUTPUT_DIR}/plots").mkdir(exist_ok=True)
    Path(f"{OUTPUT_DIR}/logs").mkdir(exist_ok=True)
    print("✅ Directories ready!")


def check_gpu():
    """Check if GPU is available"""
    print("\n🔍 Checking GPU availability...")
    gpus = tf.config.list_physical_devices('GPU')
    
    if gpus:
        print(f"✅ GPU Available: {len(gpus)} device(s)")
        for gpu in gpus:
            print(f"   - {gpu.name}")
        
        try:
            for gpu in gpus:
                tf.config.experimental.set_memory_growth(gpu, True)
            print("✅ GPU memory growth enabled")
        except RuntimeError as e:
            print(f"⚠️  {e}")
    else:
        print("⚠️  No GPU found. Training will use CPU (slower)")
    
    return len(gpus)


def setup_multi_gpu():
    """Setup single GPU training strategy"""
    print("\n🔧 Setting up GPU Strategy...")
    
    gpus = tf.config.list_physical_devices('GPU')
    
    # Force single GPU usage
    if len(gpus) >= 1:
        print(f"✅ Found {len(gpus)} GPU(s) - Using SINGLE GPU (GPU:0)")
        print(f"   Batch size: {BATCH_SIZE}")
        return tf.distribute.get_strategy()
    else:
        print(f"⚠️  No GPU found - Using CPU")
        return tf.distribute.get_strategy()


# ================== DATA LOADING ==================

def create_data_generators():
    """Create data generators dengan augmentation"""
    print("\n📊 Creating data generators...")

    # Preprocessing: (pixel / 127.5) - 1.0 untuk range [-1, 1]
    def preprocess_input(x):
        return (x / 127.5) - 1.0

    train_datagen = ImageDataGenerator(
        rotation_range=20,
        width_shift_range=0.1,
        height_shift_range=0.1,
        shear_range=0.1,
        zoom_range=0.2,
        horizontal_flip=True,
        brightness_range=[0.8, 1.2],
        fill_mode='nearest',
        preprocessing_function=preprocess_input
    )

    val_datagen = ImageDataGenerator(
        preprocessing_function=preprocess_input
    )

    train_generator = train_datagen.flow_from_directory(
        TRAIN_DIR,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        shuffle=True,
        seed=42
    )

    val_generator = val_datagen.flow_from_directory(
        VAL_DIR,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=BATCH_SIZE,
        class_mode='categorical',
        shuffle=False
    )

    print(f"\n📈 Dataset Info:")
    print(f"   Training samples: {train_generator.samples}")
    print(f"   Validation samples: {val_generator.samples}")
    print(f"   Number of classes: {train_generator.num_classes}")
    print(f"   Batch size: {BATCH_SIZE}")
    print(f"   Steps per epoch: {len(train_generator)}")

    detected_classes = list(train_generator.class_indices.keys())
    print(f"\n📋 Detected classes: {sorted(detected_classes)}")

    return train_generator, val_generator


# ================== MODEL BUILDING ==================

def build_model(strategy):
    """Build model dengan Transfer Learning - inside strategy scope"""
    print("\n🏗️  Building model...")

    with strategy.scope():
        base_model = MobileNetV2(
            input_shape=(IMG_SIZE, IMG_SIZE, 3),
            include_top=False,
            weights='imagenet'
        )
        base_model.trainable = False

        model = keras.Sequential([
            layers.Input(shape=(IMG_SIZE, IMG_SIZE, 3)),
            base_model,
            layers.GlobalAveragePooling2D(),
            layers.BatchNormalization(),
            layers.Dense(512, activation='relu'),
            layers.Dropout(0.5),
            layers.Dense(256, activation='relu'),
            layers.Dropout(0.3),
            layers.Dense(NUM_CLASSES, activation='softmax')
        ])

        model.compile(
            optimizer=keras.optimizers.Adam(
                learning_rate=LEARNING_RATE,
                clipnorm=1.0  # Gradient clipping untuk stabilitas
            ),
            loss='categorical_crossentropy',
            metrics=['accuracy', keras.metrics.TopKCategoricalAccuracy(k=3, name='top_3_accuracy')]
        )

    print("\n📝 Model Architecture:")
    model.summary()

    total_params = model.count_params()
    trainable_params = sum([np.prod(v.shape) for v in model.trainable_weights])
    non_trainable_params = total_params - trainable_params

    print(f"\n📊 Model Parameters:")
    print(f"   Total: {total_params:,}")
    print(f"   Trainable: {trainable_params:,}")
    print(f"   Non-trainable: {non_trainable_params:,}")

    return model


def create_callbacks():
    """Create training callbacks"""
    print("\n⚙️  Setting up callbacks...")

    callbacks = [
        EarlyStopping(
            monitor='val_loss',
            patience=10,
            restore_best_weights=True,
            verbose=1
        ),
        ReduceLROnPlateau(
            monitor='val_loss',
            factor=0.5,
            patience=5,
            min_lr=1e-7,
            verbose=1
        ),
        ModelCheckpoint(
            filepath=f"{OUTPUT_DIR}/models/{MODEL_NAME}_best.keras",
            monitor='val_accuracy',
            save_best_only=True,
            verbose=1
        ),
        keras.callbacks.TensorBoard(
            log_dir=f"{OUTPUT_DIR}/logs/{TIMESTAMP}",
            histogram_freq=1
        )
    ]

    print("✅ Callbacks ready!")
    return callbacks


# ================== TRAINING ==================

def train_model(model, train_gen, val_gen, callbacks):
    """Train the model"""
    print("\n" + "="*60)
    print("🚀 STARTING TRAINING")
    print("="*60)

    history = model.fit(
        train_gen,
        epochs=EPOCHS,
        validation_data=val_gen,
        callbacks=callbacks,
        verbose=1
    )

    print("\n" + "="*60)
    print("✅ TRAINING COMPLETE")
    print("="*60)

    return history


def fine_tune_model(model, train_gen, val_gen, callbacks, strategy):
    """Fine-tuning dengan strategy scope"""
    print("\n" + "="*60)
    print("🔧 FINE-TUNING MODEL")
    print("="*60)

    base_model = None
    for layer in model.layers:
        if 'mobilenetv2' in layer.name.lower():
            base_model = layer
            break

    if base_model is None:
        print("❌ Error: MobileNetV2 base model not found!")
        return None

    with strategy.scope():
        base_model.trainable = True
        for layer in base_model.layers[:-30]:
            layer.trainable = False

        model.compile(
            optimizer=keras.optimizers.Adam(
                learning_rate=LEARNING_RATE / 10,
                clipnorm=1.0
            ),
            loss='categorical_crossentropy',
            metrics=['accuracy', keras.metrics.TopKCategoricalAccuracy(k=3, name='top_3_accuracy')]
        )

    print(f"🔓 Unfroze last 30 layers of base model")
    print(f"📉 Learning rate reduced to: {LEARNING_RATE / 10}")

    history_fine = model.fit(
        train_gen,
        epochs=20,
        validation_data=val_gen,
        callbacks=callbacks,
        verbose=1
    )

    return history_fine


# ================== EVALUATION ==================

def plot_training_history(history, history_fine=None):
    """Plot training & validation accuracy and loss"""
    print("\n📊 Plotting training history...")

    fig, axes = plt.subplots(1, 2, figsize=(15, 5))

    if history_fine:
        acc = history.history['accuracy'] + history_fine.history['accuracy']
        val_acc = history.history['val_accuracy'] + history_fine.history['val_accuracy']
        loss = history.history['loss'] + history_fine.history['loss']
        val_loss = history.history['val_loss'] + history_fine.history['val_loss']
        fine_tune_epoch = len(history.history['accuracy'])
    else:
        acc = history.history['accuracy']
        val_acc = history.history['val_accuracy']
        loss = history.history['loss']
        val_loss = history.history['val_loss']
        fine_tune_epoch = None

    epochs_range = range(len(acc))

    axes[0].plot(epochs_range, acc, label='Training Accuracy', linewidth=2)
    axes[0].plot(epochs_range, val_acc, label='Validation Accuracy', linewidth=2)
    if fine_tune_epoch:
        axes[0].axvline(x=fine_tune_epoch, color='r', linestyle='--', label='Fine-tuning Start')
    axes[0].set_xlabel('Epoch', fontsize=12)
    axes[0].set_ylabel('Accuracy', fontsize=12)
    axes[0].set_title('Model Accuracy', fontsize=14, fontweight='bold')
    axes[0].legend()
    axes[0].grid(True, alpha=0.3)

    axes[1].plot(epochs_range, loss, label='Training Loss', linewidth=2)
    axes[1].plot(epochs_range, val_loss, label='Validation Loss', linewidth=2)
    if fine_tune_epoch:
        axes[1].axvline(x=fine_tune_epoch, color='r', linestyle='--', label='Fine-tuning Start')
    axes[1].set_xlabel('Epoch', fontsize=12)
    axes[1].set_ylabel('Loss', fontsize=12)
    axes[1].set_title('Model Loss', fontsize=14, fontweight='bold')
    axes[1].legend()
    axes[1].grid(True, alpha=0.3)

    plt.tight_layout()

    plot_path = f"{OUTPUT_DIR}/plots/training_history_{TIMESTAMP}.png"
    plt.savefig(plot_path, dpi=300, bbox_inches='tight')
    print(f"✅ Plot saved to: {plot_path}")
    plt.show()


def evaluate_model(model, val_gen):
    """Evaluate model on validation set"""
    print("\n📊 Evaluating model on validation set...")

    results = model.evaluate(val_gen, verbose=1)

    print(f"\n{'='*60}")
    print("EVALUATION RESULTS")
    print(f"{'='*60}")
    print(f"Validation Loss: {results[0]:.4f}")
    print(f"Validation Accuracy: {results[1]*100:.2f}%")
    print(f"Top-3 Accuracy: {results[2]*100:.2f}%")
    print(f"{'='*60}")

    return results


def create_confusion_matrix(model, val_gen):
    """Create confusion matrix"""
    print("\n🔄 Generating confusion matrix...")

    from sklearn.metrics import confusion_matrix, classification_report
    import seaborn as sns

    val_gen.reset()
    y_pred = model.predict(val_gen, verbose=1)
    y_pred_classes = np.argmax(y_pred, axis=1)
    y_true = val_gen.classes

    cm = confusion_matrix(y_true, y_pred_classes)

    plt.figure(figsize=(14, 12))
    sns.heatmap(
        cm,
        annot=True,
        fmt='d',
        cmap='Blues',
        xticklabels=CLASS_NAMES,
        yticklabels=CLASS_NAMES,
        cbar_kws={'label': 'Count'}
    )
    plt.title('Confusion Matrix', fontsize=16, fontweight='bold')
    plt.ylabel('True Label', fontsize=12)
    plt.xlabel('Predicted Label', fontsize=12)
    plt.xticks(rotation=45, ha='right')
    plt.yticks(rotation=0)
    plt.tight_layout()

    cm_path = f"{OUTPUT_DIR}/plots/confusion_matrix_{TIMESTAMP}.png"
    plt.savefig(cm_path, dpi=300, bbox_inches='tight')
    print(f"✅ Confusion matrix saved to: {cm_path}")
    plt.show()

    print("\n📋 Classification Report:")
    print(classification_report(y_true, y_pred_classes, target_names=CLASS_NAMES))


# ================== MODEL EXPORT ==================

def convert_to_tflite(model, output_name):
    """Convert Keras model to TensorFlow Lite format"""
    print("\n🔄 Converting model to TensorFlow Lite...")

    # Float32
    print("\n1️⃣  Converting to Float32...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model = converter.convert()
    float32_path = f"{OUTPUT_DIR}/models/{output_name}_float32.tflite"
    with open(float32_path, 'wb') as f:
        f.write(tflite_model)
    float32_size = os.path.getsize(float32_path) / (1024 * 1024)
    print(f"✅ Float32 model saved: {float32_path} ({float32_size:.2f} MB)")

    # Float16
    print("\n2️⃣  Converting to Float16...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]
    tflite_model_f16 = converter.convert()
    float16_path = f"{OUTPUT_DIR}/models/{output_name}_float16.tflite"
    with open(float16_path, 'wb') as f:
        f.write(tflite_model_f16)
    float16_size = os.path.getsize(float16_path) / (1024 * 1024)
    print(f"✅ Float16 model saved: {float16_path} ({float16_size:.2f} MB)")

    # INT8 Quantized
    print("\n3️⃣  Converting to INT8 (Quantized)...")

    def representative_dataset():
        def preprocess_input(x):
            return (x / 127.5) - 1.0
        
        val_datagen = ImageDataGenerator(
            preprocessing_function=preprocess_input
        )
        val_gen = val_datagen.flow_from_directory(
            VAL_DIR,
            target_size=(IMG_SIZE, IMG_SIZE),
            batch_size=1,
            class_mode='categorical',
            shuffle=True
        )
        for i in range(100):
            data = next(val_gen)[0]
            yield [data]

    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.representative_dataset = representative_dataset
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
    converter.inference_input_type = tf.uint8
    converter.inference_output_type = tf.uint8
    tflite_model_quant = converter.convert()
    quantized_path = f"{OUTPUT_DIR}/models/{output_name}_quantized.tflite"
    with open(quantized_path, 'wb') as f:
        f.write(tflite_model_quant)
    quantized_size = os.path.getsize(quantized_path) / (1024 * 1024)
    print(f"✅ Quantized model saved: {quantized_path} ({quantized_size:.2f} MB)")

    print(f"\n{'='*60}")
    print("MODEL SIZE COMPARISON")
    print(f"{'='*60}")
    print(f"Float32:    {float32_size:.2f} MB")
    print(f"Float16:    {float16_size:.2f} MB (↓{(1-float16_size/float32_size)*100:.1f}%)")
    print(f"Quantized:  {quantized_size:.2f} MB (↓{(1-quantized_size/float32_size)*100:.1f}%)")
    print(f"{'='*60}")
    print(f"💡 Recommendation: Use '{output_name}_quantized.tflite' for Android")
    print(f"{'='*60}")

    return float32_path, float16_path, quantized_path


def save_model_metadata():
    """Save metadata tentang model"""
    print("\n💾 Saving model metadata...")

    metadata = {
        "model_name": MODEL_NAME,
        "timestamp": TIMESTAMP,
        "architecture": "MobileNetV2 + Custom Top Layers",
        "input_shape": [IMG_SIZE, IMG_SIZE, 3],
        "num_classes": NUM_CLASSES,
        "class_names": CLASS_NAMES,
        "training_config": {
            "batch_size": BATCH_SIZE,
            "epochs": EPOCHS,
            "learning_rate": LEARNING_RATE,
            "optimizer": "Adam",
            "loss": "categorical_crossentropy"
        },
        "preprocessing": {
            "normalization": "[-1, 1]",
            "method": "(pixel / 127.5) - 1.0"
        }
    }

    metadata_path = f"{OUTPUT_DIR}/model_metadata.json"
    with open(metadata_path, 'w') as f:
        json.dump(metadata, f, indent=2)
    print(f"✅ Metadata saved to: {metadata_path}")


# ================== MAIN EXECUTION ==================

def main():
    """Main training pipeline with multi-GPU support"""
    print("="*60)
    print("🍎 FRUIT DETECTION MODEL TRAINING - MULTI-GPU")
    print("="*60)

    # Setup
    setup_directories()
    num_gpus = check_gpu()
    strategy = setup_multi_gpu()

    # Load data
    train_gen, val_gen = create_data_generators()

    # Build model (inside strategy scope)
    model = build_model(strategy)

    # Setup callbacks
    callbacks = create_callbacks()

    # Train
    history = train_model(model, train_gen, val_gen, callbacks)

    # Fine-tune
    print("\n❓ Fine-tune model? (Y/n): ", end="")
    do_finetune = input().strip().lower()

    history_fine = None
    if do_finetune != 'n':
        history_fine = fine_tune_model(model, train_gen, val_gen, callbacks, strategy)

    # Evaluate
    plot_training_history(history, history_fine)
    evaluate_model(model, val_gen)
    create_confusion_matrix(model, val_gen)

    # Convert to TFLite
    float32_path, float16_path, quantized_path = convert_to_tflite(model, MODEL_NAME)

    # Save metadata
    save_model_metadata()

    # Save class names
    class_names_path = f"{OUTPUT_DIR}/class_names.json"
    with open(class_names_path, 'w', encoding='utf-8') as f:
        json.dump(CLASS_NAMES, f, ensure_ascii=False, indent=2)
    print(f"✅ class_names.json saved to: {class_names_path}")

    print("\n" + "="*60)
    print("✅ TRAINING PIPELINE COMPLETE!")
    print("="*60)
    print(f"\n📂 Output files:")
    print(f"   - TFLite Quantized: {quantized_path} ⭐")
    print(f"   - Class names: {class_names_path}")
    print(f"\n📱 Next: Copy files to Android app/assets/")
    print("="*60)


if __name__ == "__main__":
    print(f"TensorFlow version: {tf.__version__}")
    print(f"Keras version: {keras.__version__}")
    main()
