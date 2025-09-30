
# ==========================================
# FRUIT DETECTION MODEL - TFLite Generator
# Tech Stack: TensorFlow 2.x, MobileNetV2
# Target: Android App untuk Anak SD
# ==========================================
# ==========================================
# STEP 1: Setup & Dependencies
# ==========================================
!pip install tensorflow kaggle pillow matplotlib
import os
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.preprocessing.image import ImageDataGenerator
import numpy as np
import matplotlib.pyplot as plt
print("TensorFlow version:", tf.__version__)
print("GPU available:", tf.config.list_physical_devices('GPU'))


# ==========================================
# STEP 2: Download Dataset Fruits-360
# ==========================================
# Upload kaggle.json dari akun Kaggle
from google.colab import files
print("📁 Upload file kaggle.json:")
uploaded = files.upload()
# Setup Kaggle API
!mkdir -p ~/.kaggle
!cp kaggle.json ~/.kaggle/
!chmod 600 ~/.kaggle/kaggle.json
# Download dataset
print("📥 Downloading Fruits-360 dataset...")
!kaggle datasets download -d moltean/fruits
!unzip -q fruits.zip -d fruits


# ==========================================
# STEP 3: Data Preprocessing & Augmentation
# ==========================================
IMG_SIZE = (224, 224)  # Optimal untuk MobileNetV2
BATCH_SIZE = 16
NUM_EPOCHS = 50
LEARNING_RATE = 0.001
# Data augmentation untuk training (mencegah overfitting)
train_datagen = ImageDataGenerator(
    rescale=1./255,
    rotation_range=20,
    width_shift_range=0.2,
    height_shift_range=0.2,
    horizontal_flip=True,
    zoom_range=0.2,
    validation_split=0.2  # 20% untuk validation
)
# Data generator untuk test (hanya rescale)
test_datagen = ImageDataGenerator(rescale=1./255)
# Load training data
train_ds = train_datagen.flow_from_directory(
    "fruits/fruits-360_original-size/fruits-360-original-size/Training",
    target_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    class_mode='categorical',
    subset='training'
)
# Load validation data
val_ds = train_datagen.flow_from_directory(
    "fruits/fruits-360_original-size/fruits-360-original-size/Test",
    target_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    class_mode='categorical',
    subset='validation'
)
# Load test data
test_ds = test_datagen.flow_from_directory(
    "fruits/fruits-360_original-size/fruits-360-original-size/Test",
    target_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    class_mode='categorical'
)
# Info dataset
class_names = list(train_ds.class_indices.keys())
num_classes = len(class_names)
print(f"📊 Dataset Info:")
print(f"Total classes: {num_classes}")
print(f"Training samples: {train_ds.samples}")
print(f"Validation samples: {val_ds.samples}")
print(f"Test samples: {test_ds.samples}")
# Save class names untuk Android app
import json
with open('class_names.json', 'w') as f:
    json.dump(class_names, f, indent=2)
print("💾 Class names saved to class_names.json")


# ==========================================
# STEP 4: Build Model (Transfer Learning)
# ==========================================
def create_model():
    # Base model MobileNetV2
    base_model = MobileNetV2(
        input_shape=IMG_SIZE + (3,),
        include_top=False,
        weights='imagenet'
    )
    # Freeze base model initially
    base_model.trainable = False
    # Add custom classification head
    model = keras.Sequential([
        base_model,
        layers.GlobalAveragePooling2D(),
        layers.Dropout(0.2),  # Mencegah overfitting
        layers.Dense(128, activation='relu'),
        layers.Dropout(0.2),
        layers.Dense(num_classes, activation='softmax')
    ])
    # Compile model
    model.compile(
        optimizer=keras.optimizers.Adam(learning_rate=LEARNING_RATE),
        loss='categorical_crossentropy',
        metrics=['accuracy']
    )
    return model
model = create_model()
model.summary()


# ==========================================
# STEP 5: Training dengan Callbacks
# ==========================================
# Callbacks untuk optimasi training
callbacks = [
    keras.callbacks.ModelCheckpoint(
        'best_fruit_model.h5',
        save_best_only=True,
        monitor='val_accuracy',
        verbose=1
    ),
    keras.callbacks.EarlyStopping(
        monitor='val_accuracy',
        patience=10,
        verbose=1
    ),
    keras.callbacks.ReduceLROnPlateau(
        monitor='val_loss',
        factor=0.2,
        patience=5,
        min_lr=0.00001,
        verbose=1
    )
]
print("🚀 Starting training...")
history = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=NUM_EPOCHS,
    callbacks=callbacks,
    verbose=1
)
# Load best model
model = keras.models.load_model('best_fruit_model.h5')


# ==========================================
# STEP 6: Evaluate Model
# ==========================================
print("📈 Evaluating model on test set...")
test_loss, test_accuracy = model.evaluate(test_ds, verbose=1)
print(f"Test accuracy: {test_accuracy:.4f}")
# Plot training history
def plot_training_history(history):
    plt.figure(figsize=(12, 4))
    plt.subplot(1, 2, 1)
    plt.plot(history.history['accuracy'], label='Training Accuracy')
    plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
    plt.title('Model Accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.legend()
    plt.subplot(1, 2, 2)
    plt.plot(history.history['loss'], label='Training Loss')
    plt.plot(history.history['val_loss'], label='Validation Loss')
    plt.title('Model Loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend()
    plt.tight_layout()
    plt.show()
plot_training_history(history)


# ==========================================
# STEP 7: Fine-tuning (Optional)
# ==========================================
# Unfreeze top layers untuk fine-tuning
base_model = model.layers[0]
base_model.trainable = True
# Fine-tune from this layer onwards
fine_tune_at = 100
# Freeze all layers before fine_tune_at
for layer in base_model.layers[:fine_tune_at]:
    layer.trainable = False
# Recompile dengan learning rate lebih kecil
model.compile(
    optimizer=keras.optimizers.Adam(learning_rate=LEARNING_RATE/10),
    loss='categorical_crossentropy',
    metrics=['accuracy']
)
print("🔧 Starting fine-tuning...")
history_fine = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=10,  # Sedikit epoch untuk fine-tuning
    callbacks=callbacks,
    verbose=1
)



# ==========================================
# STEP 8: Save Model
# ==========================================
print("💾 Saving model...")
# Use model.export() to save in SavedModel format for TFLite conversion
model.export("fruit_detection_model")
print("✅ Model saved successfully in SavedModel format.")



# ==========================================
# STEP 9: Convert to TFLite
# ==========================================
def convert_to_tflite():
    converter = tf.lite.TFLiteConverter.from_saved_model("fruit_detection_model")
    # 1. Float32 model (highest accuracy)
    print("🔄 Converting to Float32 TFLite...")
    converter.optimizations = []
    float32_model = converter.convert()
    with open("fruit_model_float32.tflite", "wb") as f:
        f.write(float32_model)
    print(f"✅ Float32 model size: {len(float32_model) / 1024 / 1024:.2f} MB")
    # 2. Dynamic Range Quantization (recommended untuk mobile)
    print("🔄 Converting to INT8 Quantized TFLite...")
    converter = tf.lite.TFLiteConverter.from_saved_model("fruit_detection_model")
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    quantized_model = converter.convert()
    with open("fruit_model_quantized.tflite", "wb") as f:
        f.write(quantized_model)
    print(f"✅ Quantized model size: {len(quantized_model) / 1024 / 1024:.2f} MB")
    # 3. Full Integer Quantization (untuk edge devices)
    print("🔄 Converting to Full INT8 TFLite...")
    # Representative dataset untuk calibration
    def representative_dataset():
        for _ in range(100):  # 100 samples untuk calibration
            batch = next(iter(train_ds))
            yield [batch[0]]
    converter = tf.lite.TFLiteConverter.from_saved_model("fruit_detection_model")
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.representative_dataset = representative_dataset
    converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS_INT8]
    converter.inference_input_type = tf.uint8
    converter.inference_output_type = tf.uint8
    full_int8_model = converter.convert()
    with open("fruit_model_full_int8.tflite", "wb") as f:
        f.write(full_int8_model)
    print(f"✅ Full INT8 model size: {len(full_int8_model) / 1024 / 1024:.2f} MB")
convert_to_tflite()



# ==========================================
# STEP 10: Test TFLite Model
# ==========================================
def test_tflite_model(model_path):
    # Load TFLite model
    interpreter = tf.lite.Interpreter(model_path=model_path)
    interpreter.allocate_tensors()
    # Get input and output tensors
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    print(f"Testing {model_path}...")
    print(f"Input shape: {input_details[0]['shape']}")
    print(f"Output shape: {output_details[0]['shape']}")
    # Test dengan satu sample
    test_sample = next(iter(test_ds))
    test_image = test_sample[0][0:1]  # Ambil satu image
    test_label = test_sample[1][0:1]
    # Preprocess untuk TFLite
    if input_details[0]['dtype'] == np.uint8:
        test_image = tf.cast(test_image * 255, tf.uint8)
    # Set input tensor
    interpreter.set_tensor(input_details[0]['index'], test_image)
    # Run inference
    interpreter.invoke()
    # Get output
    output_data = interpreter.get_tensor(output_details[0]['index'])
    predicted_class = np.argmax(output_data[0])
    confidence = np.max(output_data[0])
    actual_class = np.argmax(test_label[0])
    print(f"Actual: {class_names[actual_class]}")
    print(f"Predicted: {class_names[predicted_class]} ({confidence:.2f})")
    print(f"Correct: {'✅' if predicted_class == actual_class else '❌'}")
    print("-" * 50)
# Test all models
test_tflite_model("fruit_model_float32.tflite")
test_tflite_model("fruit_model_quantized.tflite")
test_tflite_model("fruit_model_full_int8.tflite")



# ==========================================
# STEP 11: Generate Android Integration Code
# ==========================================
android_code = '''
// Android Integration Code (Kotlin)
// Add to build.gradle (app level):
// implementation 'org.tensorflow:tensorflow-lite:2.13.0'
// implementation 'org.tensorflow:tensorflow-lite-gpu:2.13.0'
class FruitClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    private val modelFileName = "fruit_model_quantized.tflite"
    init {
        loadModel()
    }
    private fun loadModel() {
        try {
            val model = loadModelFile()
            interpreter = Interpreter(model)
        } catch (e: Exception) {
            Log.e("FruitClassifier", "Error loading model", e)
        }
    }
    private fun loadModelFile(): ByteBuffer {
        val assetManager = context.assets
        val inputStream = assetManager.open(modelFileName)
        val byteArray = inputStream.readBytes()
        val byteBuffer = ByteBuffer.allocateDirect(byteArray.size)
        byteBuffer.put(byteArray)
        inputStream.close()
        return byteBuffer
    }
    fun classifyImage(bitmap: Bitmap): Pair<String, Float> {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false)
        val input = preprocessImage(resizedBitmap)
        val output = Array(1) { FloatArray(''' + str(num_classes) + ''') }
        interpreter?.run(input, output)
        val predictions = output[0]
        val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
        val confidence = predictions[maxIndex]
        return Pair(getClassName(maxIndex), confidence)
    }
    private fun preprocessImage(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
        val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }
        for (y in 0 until 224) {
            for (x in 0 until 224) {
                val pixel = bitmap.getPixel(x, y)
                input[0][y][x][0] = (Color.red(pixel) / 255.0f)
                input[0][y][x][1] = (Color.green(pixel) / 255.0f)
                input[0][y][x][2] = (Color.blue(pixel) / 255.0f)
            }
        }
        return input
    }
    private fun getClassName(index: Int): String {
        // Load class names dari assets/class_names.json
        return classNames[index]
    }
}
'''
with open('android_integration.txt', 'w') as f:
    f.write(android_code)
# ==========================================
# FINAL SUMMARY
# ==========================================
print("🎉 MODEL TRAINING COMPLETED! 🎉")
print("=" * 50)
print("📁 Generated Files:")
print("- fruit_model_quantized.tflite (RECOMMENDED untuk Android)")
print("- fruit_model_float32.tflite")
print("- fruit_model_full_int8.tflite")
print("- class_names.json")
print("- android_integration.txt")
print("=" * 50)
print("💡 Recommendations untuk Android App:")
print("1. Gunakan fruit_model_quantized.tflite (balance antara size & accuracy)")
print("2. Input size: 224x224 pixels")
print("3. Preprocessing: normalize ke [0,1]")
print("4. Add GPU delegate untuk performance boost")
print("=" * 50)
# Download files
files.download('fruit_model_quantized.tflite')
files.download('class_names.json')
files.download('android_integration.txt')

