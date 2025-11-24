#!/usr/bin/env python3
"""
Dataset Preparation Script - Multi Dataset Version
Menggabungkan berbagai dataset buah menjadi satu dataset terstruktur
untuk training model deteksi buah anak SD.

Target: 21 jenis buah dengan nama Indonesia
"""

import os
import shutil
import json
from pathlib import Path
from collections import defaultdict

# ============================================================================
# KONFIGURASI PATH - SESUAIKAN DENGAN LOKASI DATASET ANDA
# ============================================================================

# Path ke berbagai dataset
DATASET_PATHS = {
    "moltean_100x100": "/kaggle/input/fruits/fruits-360_100x100/fruits-360/Training",
    "indian_fruits": "/kaggle/input/fruits-classification/Fruits Classification/train",
    "fresh_rotten": "/kaggle/input/fruits-fresh-and-rotten-for-classification/dataset/train",
    "fruits262": "/kaggle/input/fruits262/Fruit-262",
    "fruit_recognition": "/kaggle/input/fruit-recognition"
}

# Output folder
OUTPUT_DIR = "fruit-dataset-full-varieties"

# ============================================================================
# MAPPING BUAH - 20 JENIS BUAH INDONESIA
# ============================================================================

FRUIT_MAPPING = {
    "Apel": {
        "moltean_100x100": [
            "Apple 5", "Apple 6", "Apple 7", "Apple 8", "Apple 9",
            "Apple 10", "Apple 11", "Apple 12", "Apple 13", "Apple 14",
            "Apple 17", "Apple 18", "Apple 19", "Apple Core 1", "Apple hit 1",
            "Apple Braeburn 1", "Apple Crimson Snow 1", "Apple worm 1",
            "Apple Golden 1", "Apple Golden 2", "Apple Golden 3",
            "Apple Granny Smith 1", "Apple Pink Lady 1", "Apple Rotten 1"
            "Apple Red 1", "Apple Red 2", "Apple Red 3",
            "Apple Red Delicious 1", "Apple Red Yellow 1", "Apple Red Yellow 2"
        ],
        "indian_fruits": ["Apple"],
        "fresh_rotten": ["freshapples", "rottenapples"],
        "fruits262": ["apple"],
        "fruit_recognition": ["Apple"]
    },

    "Pisang": {
        "moltean_100x100": [
            "Banana 1", "Banana 3", "Banana 4",
            "Banana Lady Finger 1", "Banana Red 1"
        ],
        "indian_fruits": ["Banana"],
        "fresh_rotten": ["freshbanana", "rottenbanana"],
        "fruits262": ["banana"],
        "fruit_recognition": ["Banana"]
    },

    "Jeruk": {
        "moltean_100x100": ["Orange 1", "Clementine 1", "Mandarine 1"],
        "fresh_rotten": ["freshoranges", "rottenoranges"],
        "fruits262": ["orange", "mandarine", "clementine"],
        "fruit_recognition": ["Orange"]
    },

    "Mangga": {
        "moltean_100x100": ["Mango 1", "Mango Red 1"],
        "indian_fruits": ["Mango"],
        "fruits262": ["mango"],
        "fruit_recognition": ["Mango"]
    },

    "Anggur": {
        "moltean_100x100": [
            "Grape Blue 1", "Grape Pink 1",
            "Grape White 1", "Grape White 2", "Grape White 3", "Grape White 4"
        ],
        "indian_fruits": ["Grape"],
        "fruits262": ["grape", "grapefruit"]
    },
    
    "Semangka": {
        "moltean_100x100": ["Watermelon 1"],
        "fruits262": ["watermelon"]
    },
    
    "Pepaya": {
        "moltean_100x100": ["Papaya 1"],
        "fruits262": ["papaya", "mountain papaya"]
    },
    
    "Jambu": {
        "moltean_100x100": ["Guava 1"],
        "fruits262": ["guava", "strawberry guava"],
        "fruit_recognition": ["Guava"]
    },
    
    "Nanas": {
        "moltean_100x100": ["Pineapple 1", "Pineapple Mini 1"],
        "fruits262": ["pineapple"]
    },
    
    "Strawberry": {
        "moltean_100x100": ["Strawberry 1", "Strawberry Wedge 1"],
        "indian_fruits": ["Strawberry"],
        "fruits262": ["strawberry"]
    },
    
    "Rambutan": {
        "moltean_100x100": ["Rambutan 1"],
        "fruits262": ["rambutan"]
    },
    
    "Kelengkeng": {
        "moltean_100x100": ["Lychee 1"],
        "fruits262": ["lychee", "longan"]
    },
    
    "Salak": {
        "moltean_100x100": ["Salak 1"],
        "fruits262": ["salak"]
    },
    
    "Alpukat": {
        "moltean_100x100": [
            "Avocado 1", "Avocado Black 1", "Avocado Black 2",
            "Avocado Green 1", "Avocado ripe 1"
        ],
        "fruits262": ["avocado"]
    },
    
    "Melon": {
        "moltean_100x100": ["Cantaloupe 1", "Cantaloupe 2", "Melon Piel de Sapo 1"],
        "fruits262": ["cantaloupe", "muskmelon", "galia melon"],
        "fruit_recognition": ["muskmelon"]
    },
    
    "Pir": {
        "moltean_100x100": [
            "Pear 1", "Pear 2", "Pear 3", "Pear 5", "Pear 6",
            "Pear 7", "Pear 8", "Pear 9", "Pear 10", "Pear 11",
            "Pear 12", "Pear 13", "Pear Abate 1", "Pear Forelle 1",
            "Pear Kaiser 1", "Pear Monster 1", "Pear Red 1",
            "Pear Stone 1", "Pear Williams 1"
        ],
        "fruits262": ["pear", "yali pear"],
        "fruit_recognition": ["Pear"]
    },
    
    "Durian": {
        "moltean_100x100": [],  # Tidak ada di fruits-360
        "fruits262": ["durian"]
    },
    
    "Manggis": {
        "moltean_100x100": ["Mangostan 1"],
        "fruits262": ["mangosteen"]
    },
    
    "Kelapa": {
        "moltean_100x100": ["Cocos 1"],
        "fruits262": ["coconut"]
    },
    
    "Belimbing": {
        "moltean_100x100": ["Carambula 1"],
        "fruits262": ["carambola"],
        "fruit_recognition": ["Carambola"]
    },
    
    "Tomat": {
        "moltean_100x100": [
            "Tomato 1", "Tomato 2", "Tomato 3", "Tomato 4", "Tomato 5",
            "Tomato 7", "Tomato 8", "Tomato 9", "Tomato 10",
            "Tomato Cherry Maroon 1", "Tomato Cherry Orange 1",
            "Tomato Cherry Red 1", "Tomato Cherry Red 2",
            "Tomato Cherry Yellow 1", "Tomato Heart 1",
            "Tomato Maroon 1", "Tomato Maroon 2", "Tomato Yellow 1"
        ],
        "fruits262": ["tomato"],
        "fruit_recognition": ["Tomatoes"]
    }
}

# ============================================================================
# FRUIT INFO - Fun Facts untuk Anak SD
# ============================================================================

FRUIT_INFO = {
    "Apel": {
        "fun_fact": "Apel bisa mengapung di air karena ada udara di dalamnya! 🍎",
        "vitamin": "Vitamin C",
        "warna": "Merah, Hijau, atau Kuning",
        "manfaat": "Baik untuk tubuh dan perut",
        "asal": "Asia Tengah"
    },
    "Pisang": {
        "fun_fact": "Pisang termasuk buah berry, lho! 🍌",
        "vitamin": "Kalium dan Vitamin B6",
        "warna": "Kuning",
        "manfaat": "Memberi energi dan kuatkan otot",
        "asal": "Asia Tenggara"
    },
    "Jeruk": {
        "fun_fact": "Jeruk sudah dimakan sejak ribuan tahun lalu! 🍊",
        "vitamin": "Vitamin C",
        "warna": "Oranye",
        "manfaat": "Membuat tubuh tidak mudah sakit",
        "asal": "China"
    },
    "Mangga": {
        "fun_fact": "Mangga jadi buah favorit di banyak negara! 🥭",
        "vitamin": "Vitamin A dan C",
        "warna": "Kuning atau Hijau",
        "manfaat": "Baik untuk mata dan kulit",
        "asal": "India"
    },
    "Anggur": {
        "fun_fact": "Jenis anggur ada ribuan macam! 🍇",
        "vitamin": "Vitamin C dan K",
        "warna": "Ungu, Hijau, atau Merah",
        "manfaat": "Baik untuk tubuh",
        "asal": "Timur Tengah"
    },
    "Semangka": {
        "fun_fact": "Semangka hampir seluruhnya air! 🍉",
        "vitamin": "Vitamin A dan C",
        "warna": "Merah di dalam, Hijau di luar",
        "manfaat": "Membuat tubuh segar",
        "asal": "Afrika"
    },
    "Pepaya": {
        "fun_fact": "Pepaya membantu perut bekerja dengan baik! 🍈",
        "vitamin": "Vitamin C",
        "warna": "Oranye",
        "manfaat": "Baik untuk pencernaan",
        "asal": "Amerika Tengah"
    },
    "Jambu": {
        "fun_fact": "Jambu punya vitamin C sangat banyak! 🍐",
        "vitamin": "Vitamin C",
        "warna": "Hijau atau Merah",
        "manfaat": "Membuat tubuh kuat",
        "asal": "Amerika Selatan"
    },
    "Nanas": {
        "fun_fact": "Nanas butuh waktu lama untuk tumbuh! 🍍",
        "vitamin": "Vitamin C",
        "warna": "Kuning",
        "manfaat": "Baik untuk perut",
        "asal": "Amerika Selatan"
    },
    "Strawberry": {
        "fun_fact": "Biji strawberry ada di luar buahnya! 🍓",
        "vitamin": "Vitamin C",
        "warna": "Merah",
        "manfaat": "Baik untuk jantung",
        "asal": "Eropa"
    },
    "Rambutan": {
        "fun_fact": "Namanya rambutan karena kulitnya seperti rambut! 🔴",
        "vitamin": "Vitamin C",
        "warna": "Merah atau Kuning",
        "manfaat": "Baik untuk daya tahan tubuh",
        "asal": "Asia Tenggara"
    },
    "Kelengkeng": {
        "fun_fact": "Bentuk kelengkeng seperti mata naga! 🐉",
        "vitamin": "Vitamin C",
        "warna": "Cokelat di luar, Putih di dalam",
        "manfaat": "Menambah energi",
        "asal": "China"
    },
    "Salak": {
        "fun_fact": "Kulit salak seperti sisik ular! 🐍",
        "vitamin": "Vitamin C",
        "warna": "Cokelat",
        "manfaat": "Baik untuk pencernaan",
        "asal": "Indonesia"
    },
    "Alpukat": {
        "fun_fact": "Alpukat adalah buah dengan biji besar! 🥑",
        "vitamin": "Vitamin E dan K",
        "warna": "Hijau",
        "manfaat": "Baik untuk energi tubuh",
        "asal": "Meksiko"
    },
    "Melon": {
        "fun_fact": "Melon masih satu keluarga dengan mentimun! 🍈",
        "vitamin": "Vitamin A dan C",
        "warna": "Oranye atau Hijau",
        "manfaat": "Baik untuk mata",
        "asal": "Afrika dan Timur Tengah"
    },
    "Pir": {
        "fun_fact": "Pir rasanya manis dan segar! 🍐",
        "vitamin": "Vitamin C dan K",
        "warna": "Hijau atau Kuning",
        "manfaat": "Baik untuk perut",
        "asal": "Eropa dan Asia Barat"
    },
    "Durian": {
        "fun_fact": "Durian adalah raja buah dengan bau khas! 👑",
        "vitamin": "Vitamin C dan B",
        "warna": "Kuning di dalam, Hijau di luar",
        "manfaat": "Memberi energi banyak",
        "asal": "Asia Tenggara"
    },
    "Manggis": {
        "fun_fact": "Manggis disebut ratu buah! 👸",
        "vitamin": "Vitamin C",
        "warna": "Ungu di luar, Putih di dalam",
        "manfaat": "Baik untuk daya tahan tubuh",
        "asal": "Asia Tenggara"
    },
    "Kelapa": {
        "fun_fact": "Kelapa bisa mengapung di laut! 🥥",
        "vitamin": "Vitamin C dan B",
        "warna": "Cokelat atau Hijau",
        "manfaat": "Membuat tubuh segar",
        "asal": "Asia Tenggara"
    },
    "Belimbing": {
        "fun_fact": "Kalau dipotong, belimbing bentuknya seperti bintang! ⭐",
        "vitamin": "Vitamin C",
        "warna": "Kuning atau Hijau",
        "manfaat": "Baik untuk pencernaan",
        "asal": "Asia Tenggara"
    },
    "Tomat": {
        "fun_fact": "Tomat sebenarnya adalah buah, bukan sayur! 🍅",
        "vitamin": "Vitamin C dan K",
        "warna": "Merah, Kuning, atau Hijau",
        "manfaat": "Baik untuk mata dan kulit",
        "asal": "Amerika Selatan"
    }
}

# ============================================================================
# FUNGSI UTAMA
# ============================================================================

def copy_images_from_dataset(dataset_name, dataset_path, class_name, target_fruit, output_train, output_val, split_ratio=0.8):
    """Copy images dari satu dataset ke output folder"""
    
    # Cek Training dan Test folder
    for split_type in ["Training", "Test", "train", "test", ""]:
        if split_type:
            source_train = os.path.join(dataset_path, split_type, class_name)
        else:
            source_train = os.path.join(dataset_path, class_name)
        
        if os.path.exists(source_train):
            images = [f for f in os.listdir(source_train) if f.lower().endswith(('.jpg', '.jpeg', '.png'))]
            
            # Split train/val
            split_idx = int(len(images) * split_ratio)
            train_images = images[:split_idx]
            val_images = images[split_idx:]
            
            # Copy train images
            for img in train_images:
                src = os.path.join(source_train, img)
                # Rename dengan prefix dataset untuk avoid collision
                dst_name = f"{dataset_name}_{class_name.replace(' ', '_')}_{img}"
                dst = os.path.join(output_train, dst_name)
                shutil.copy2(src, dst)
            
            # Copy val images
            for img in val_images:
                src = os.path.join(source_train, img)
                dst_name = f"{dataset_name}_{class_name.replace(' ', '_')}_{img}"
                dst = os.path.join(output_val, dst_name)
                shutil.copy2(src, dst)
            
            return len(train_images), len(val_images)
    
    return 0, 0


def prepare_multi_dataset():
    """Main function untuk prepare dataset dari multiple sources"""
    
    print("=" * 70)
    print("🍎 DATASET PREPARATION - MULTI DATASET VERSION")
    print("=" * 70)
    print(f"Target: {len(FRUIT_MAPPING)} jenis buah")
    print(f"Output: {OUTPUT_DIR}/")
    print()
    
    # Create output directories
    output_train = os.path.join(OUTPUT_DIR, "train")
    output_val = os.path.join(OUTPUT_DIR, "val")
    
    if os.path.exists(OUTPUT_DIR):
        print(f"⚠️  Folder {OUTPUT_DIR} sudah ada!")
        response = input("Hapus dan buat ulang? (y/n): ")
        if response.lower() == 'y':
            shutil.rmtree(OUTPUT_DIR)
        else:
            print("❌ Dibatalkan.")
            return
    
    os.makedirs(output_train, exist_ok=True)
    os.makedirs(output_val, exist_ok=True)
    
    # Create fruit folders
    for fruit_name in FRUIT_MAPPING.keys():
        os.makedirs(os.path.join(output_train, fruit_name), exist_ok=True)
        os.makedirs(os.path.join(output_val, fruit_name), exist_ok=True)
    
    # Statistics
    stats = defaultdict(lambda: {"train": 0, "val": 0, "sources": []})
    
    # Process each fruit
    print("\n📦 Memproses dataset...\n")
    
    for fruit_name, dataset_mapping in FRUIT_MAPPING.items():
        print(f"🍓 {fruit_name}:")
        
        for dataset_name, class_names in dataset_mapping.items():
            if not class_names:  # Skip empty list
                continue
            
            dataset_path = DATASET_PATHS.get(dataset_name)
            if not dataset_path or not os.path.exists(dataset_path):
                print(f"   ⚠️  {dataset_name}: Path tidak ditemukan, skip")
                continue
            
            for class_name in class_names:
                train_count, val_count = copy_images_from_dataset(
                    dataset_name, 
                    dataset_path, 
                    class_name, 
                    fruit_name,
                    os.path.join(output_train, fruit_name),
                    os.path.join(output_val, fruit_name)
                )
                
                if train_count > 0 or val_count > 0:
                    stats[fruit_name]["train"] += train_count
                    stats[fruit_name]["val"] += val_count
                    stats[fruit_name]["sources"].append(f"{dataset_name}:{class_name}")
                    print(f"   ✅ {dataset_name}/{class_name}: {train_count} train, {val_count} val")
        
        total = stats[fruit_name]["train"] + stats[fruit_name]["val"]
        print(f"   📊 Total {fruit_name}: {total} images\n")
    
    # Save metadata
    print("\n💾 Menyimpan metadata...\n")
    
    # class_names.json
    class_names = sorted(FRUIT_MAPPING.keys())
    with open(os.path.join(OUTPUT_DIR, "class_names.json"), "w", encoding="utf-8") as f:
        json.dump(class_names, f, ensure_ascii=False, indent=2)
    print(f"✅ Saved: {OUTPUT_DIR}/class_names.json")
    
    # fruit_info.json
    with open(os.path.join(OUTPUT_DIR, "fruit_info.json"), "w", encoding="utf-8") as f:
        json.dump(FRUIT_INFO, f, ensure_ascii=False, indent=2)
    print(f"✅ Saved: {OUTPUT_DIR}/fruit_info.json")
    
    # dataset_summary.json
    summary = {
        "total_classes": len(FRUIT_MAPPING),
        "class_names": class_names,
        "statistics": dict(stats),
        "total_images": {
            "train": sum(s["train"] for s in stats.values()),
            "val": sum(s["val"] for s in stats.values())
        }
    }
    
    with open(os.path.join(OUTPUT_DIR, "dataset_summary.json"), "w", encoding="utf-8") as f:
        json.dump(summary, f, ensure_ascii=False, indent=2)
    print(f"✅ Saved: {OUTPUT_DIR}/dataset_summary.json")
    
    # Print final summary
    print("\n" + "=" * 70)
    print("✅ DATASET PREPARATION SELESAI!")
    print("=" * 70)
    print(f"📁 Output folder: {OUTPUT_DIR}/")
    print(f"🍎 Total classes: {len(FRUIT_MAPPING)}")
    print(f"📊 Total images:")
    print(f"   - Training: {summary['total_images']['train']}")
    print(f"   - Validation: {summary['total_images']['val']}")
    print(f"   - Total: {summary['total_images']['train'] + summary['total_images']['val']}")
    print()
    print("📋 Detail per buah:")
    for fruit_name in sorted(stats.keys()):
        s = stats[fruit_name]
        total = s["train"] + s["val"]
        print(f"   {fruit_name:15s}: {total:4d} images ({s['train']} train, {s['val']} val)")
    print()
    print("🚀 Siap untuk training!")
    print("=" * 70)


if __name__ == "__main__":
    prepare_multi_dataset()
