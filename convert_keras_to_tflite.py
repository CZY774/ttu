"""
convert_keras_to_tflite.py
Script standalone untuk convert model Keras (.keras) ke TensorFlow Lite
Dipakai kalau waktu GPU Colab habis setelah training selesai

Author: Cornelius Ardhani Yoga Pratama
Date: 2025-11-19
"""

import os
import json
import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
import tensorflow as tf
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from sklearn.metrics import confusion_matrix, classification_report
from pathlib import Path
from datetime import datetime

# ================== KONFIGURASI (EDIT SESUAI KEBUTUHAN) ==================

# Input: Model Keras yang sudah di-train
KERAS_MODEL_PATH = "/kaggle/working/model_output/models/fruit_detector_best.keras"

# Dataset validation untuk representative dataset
VAL_DIR = "/kaggle/working/fruit-dataset-full-varieties/val"

# Output directory
OUTPUT_DIR = "/kaggle/working/model_output/models"

# Model parameters
IMG_SIZE = 224
OUTPUT_NAME = "fruit_detector"
TIMESTAMP = datetime.now().strftime("%Y%m%d_%H%M%S")

# Class names (21 buah)
CLASS_NAMES = [
    "Alpukat", "Anggur", "Apel", "Belimbing", "Durian",
    "Jambu", "Jeruk", "Kelapa", "Kelengkeng", "Mangga",
    "Manggis", "Melon", "Nanas", "Pepaya", "Pir",
    "Pisang", "Rambutan", "Salak", "Semangka", "Strawberry", "Tomat"
]

# ================== CONVERSION ==================

def evaluate_and_plot(model, val_dir, output_dir):
    """Evaluate model dan generate plots"""
    print("\n" + "="*60)
    print("📊 EVALUATING MODEL & GENERATING PLOTS")
    print("="*60)
    
    # Create plots directory
    plots_dir = f"{output_dir}/plots"
    Path(plots_dir).mkdir(parents=True, exist_ok=True)
    
    # Prepare validation data
    print("\n📂 Loading validation data...")
    def preprocess_input(x):
        return (x / 127.5) - 1.0
    
    val_datagen = ImageDataGenerator(preprocessing_function=preprocess_input)
    val_gen = val_datagen.flow_from_directory(
        val_dir,
        target_size=(IMG_SIZE, IMG_SIZE),
        batch_size=32,
        class_mode='categorical',
        shuffle=False
    )
    
    # Evaluate
    print("\n🔍 Evaluating model...")
    results = model.evaluate(val_gen, verbose=1)
    
    print(f"\n{'='*60}")
    print("EVALUATION RESULTS")
    print(f"{'='*60}")
    print(f"Validation Loss: {results[0]:.4f}")
    print(f"Validation Accuracy: {results[1]*100:.2f}%")
    if len(results) > 2:
        print(f"Top-3 Accuracy: {results[2]*100:.2f}%")
    print(f"{'='*60}")
    
    # Generate predictions for confusion matrix
    print("\n🔄 Generating predictions...")
    val_gen.reset()
    y_pred = model.predict(val_gen, verbose=1)
    y_pred_classes = np.argmax(y_pred, axis=1)
    y_true = val_gen.classes
    
    # Confusion Matrix
    print("\n📊 Creating confusion matrix...")
    cm = confusion_matrix(y_true, y_pred_classes)
    
    plt.figure(figsize=(16, 14))
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
    
    cm_path = f"{plots_dir}/confusion_matrix_{TIMESTAMP}.png"
    plt.savefig(cm_path, dpi=300, bbox_inches='tight')
    print(f"✅ Confusion matrix saved: {cm_path}")
    plt.close()
    
    # Classification Report
    print("\n📋 Classification Report:")
    report = classification_report(y_true, y_pred_classes, target_names=CLASS_NAMES)
    print(report)
    
    # Save report to file
    report_path = f"{plots_dir}/classification_report_{TIMESTAMP}.txt"
    with open(report_path, 'w', encoding='utf-8') as f:
        f.write("CLASSIFICATION REPORT\n")
        f.write("="*60 + "\n\n")
        f.write(report)
        f.write("\n\nEVALUATION METRICS\n")
        f.write("="*60 + "\n")
        f.write(f"Validation Loss: {results[0]:.4f}\n")
        f.write(f"Validation Accuracy: {results[1]*100:.2f}%\n")
        if len(results) > 2:
            f.write(f"Top-3 Accuracy: {results[2]*100:.2f}%\n")
    print(f"✅ Report saved: {report_path}")
    
    # Per-class accuracy plot
    print("\n📊 Creating per-class accuracy plot...")
    class_correct = cm.diagonal()
    class_total = cm.sum(axis=1)
    class_accuracy = class_correct / class_total * 100
    
    plt.figure(figsize=(14, 8))
    bars = plt.bar(range(len(CLASS_NAMES)), class_accuracy, color='steelblue', alpha=0.8)
    plt.axhline(y=results[1]*100, color='red', linestyle='--', label=f'Overall Accuracy: {results[1]*100:.2f}%')
    plt.xlabel('Fruit Class', fontsize=12)
    plt.ylabel('Accuracy (%)', fontsize=12)
    plt.title('Per-Class Accuracy', fontsize=14, fontweight='bold')
    plt.xticks(range(len(CLASS_NAMES)), CLASS_NAMES, rotation=45, ha='right')
    plt.ylim(0, 105)
    plt.legend()
    plt.grid(axis='y', alpha=0.3)
    plt.tight_layout()
    
    acc_path = f"{plots_dir}/per_class_accuracy_{TIMESTAMP}.png"
    plt.savefig(acc_path, dpi=300, bbox_inches='tight')
    print(f"✅ Per-class accuracy plot saved: {acc_path}")
    plt.close()
    
    return results


def convert_to_tflite(model, output_dir, output_name):
    """
    Convert Keras model ke 3 format TFLite:
    1. Float32 (baseline)
    2. Float16 (reduced precision)
    3. INT8 Quantized (best for mobile)
    """
    
    print("\n" + "="*60)
    print("🔄 CONVERTING KERAS MODEL TO TFLITE")
    print("="*60)
    
    # Create output directory
    Path(output_dir).mkdir(parents=True, exist_ok=True)
    
    # 1. Float32 (baseline)
    print("\n1️⃣  Converting to Float32...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    tflite_model_f32 = converter.convert()
    
    float32_path = f"{output_dir}/{output_name}_float32.tflite"
    with open(float32_path, 'wb') as f:
        f.write(tflite_model_f32)
    
    float32_size = os.path.getsize(float32_path) / (1024 * 1024)
    print(f"✅ Float32 model saved: {float32_path} ({float32_size:.2f} MB)")
    
    # 2. Float16 (reduced precision)
    print("\n2️⃣  Converting to Float16...")
    converter = tf.lite.TFLiteConverter.from_keras_model(model)
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float16]
    tflite_model_f16 = converter.convert()
    
    float16_path = f"{output_dir}/{output_name}_float16.tflite"
    with open(float16_path, 'wb') as f:
        f.write(tflite_model_f16)
    
    float16_size = os.path.getsize(float16_path) / (1024 * 1024)
    print(f"✅ Float16 model saved: {float16_path} ({float16_size:.2f} MB)")
    
    # 3. INT8 Quantized (best for mobile)
    print("\n3️⃣  Converting to INT8 (Quantized)...")
    print("   Loading validation data for representative dataset...")
    
    # Representative dataset - SAMA PERSIS dengan train_fruit_model.py
    def representative_dataset():
        # Use REAL validation data for quantization
        val_datagen = ImageDataGenerator(
            rescale=1./127.5,
            preprocessing_function=lambda x: x - 1.0
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
    
    quantized_path = f"{output_dir}/{output_name}_quantized.tflite"
    with open(quantized_path, 'wb') as f:
        f.write(tflite_model_quant)
    
    quantized_size = os.path.getsize(quantized_path) / (1024 * 1024)
    print(f"✅ Quantized model saved: {quantized_path} ({quantized_size:.2f} MB)")
    
    # Summary
    print(f"\n{'='*60}")
    print("📊 MODEL SIZE COMPARISON")
    print(f"{'='*60}")
    print(f"Float32:    {float32_size:.2f} MB")
    print(f"Float16:    {float16_size:.2f} MB (↓{(1-float16_size/float32_size)*100:.1f}%)")
    print(f"Quantized:  {quantized_size:.2f} MB (↓{(1-quantized_size/float32_size)*100:.1f}%)")
    print(f"{'='*60}")
    
    print("\n✅ Conversion complete!")
    print(f"\n📁 Output files:")
    print(f"   - {float32_path}")
    print(f"   - {float16_path}")
    print(f"   - {quantized_path}")
    
    return {
        'float32': float32_path,
        'float16': float16_path,
        'quantized': quantized_path
    }


# ================== MAIN ==================

if __name__ == "__main__":
    print("\n" + "="*60)
    print("🚀 KERAS TO TFLITE CONVERTER")
    print("="*60)
    print(f"\nInput model: {KERAS_MODEL_PATH}")
    print(f"Validation dir: {VAL_DIR}")
    print(f"Output dir: {OUTPUT_DIR}")
    
    # Check files exist
    if not Path(KERAS_MODEL_PATH).exists():
        print(f"\n❌ Error: Model not found at {KERAS_MODEL_PATH}")
        exit(1)
    
    if not Path(VAL_DIR).exists():
        print(f"\n❌ Error: Validation directory not found at {VAL_DIR}")
        exit(1)
    
    # Load model
    print(f"\n📂 Loading Keras model...")
    model = tf.keras.models.load_model(KERAS_MODEL_PATH)
    print("✅ Model loaded successfully!")
    
    # Evaluate and generate plots
    evaluate_and_plot(model, VAL_DIR, "model_output")
    
    # Convert
    result = convert_to_tflite(
        model=model,
        output_dir=OUTPUT_DIR,
        output_name=OUTPUT_NAME
    )
    
    if result:
        print("\n" + "="*60)
        print("✅ ALL DONE!")
        print("="*60)
        print("\n📝 Next steps:")
        print("1. Check plots in model_output/plots/")
        print("2. Test models dengan test_float32.py dan quick_val_test.py")
        print("3. Copy quantized model ke app/src/main/assets/")
        print("4. Build Android app")
