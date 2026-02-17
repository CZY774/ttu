"""
TanyaBuah - AI Fruit Recognition Application

Copyright © 2024-2026 Cornelius Ardhani Yoga Pratama & Pratyaksa Ocsa Nugraha Saian
All Rights Reserved.

Protected by Intellectual Property Rights (HKI)
Registration No: 001138316
Directorate General of Intellectual Property (DGIP)
Ministry of Law and Human Rights, Republic of Indonesia

This code is proprietary and confidential.
Unauthorized copying, modification, or distribution is prohibited.
"""


"""
Extract Class Names from All Datasets
Extracts unique class names from 5 fruit datasets for manual mapping
Auto-downloads from Kaggle if dataset not found locally

Usage:
1. Upload kaggle.json to this folder
2. Comment/uncomment datasets you want to process (see ACTIVE_DATASETS below)
3. Run: python extract_class_names.py
4. Repeat for other datasets
5. Use output class_names_extracted.json for manual mapping

Tips:
- Process one dataset at a time to avoid long download times
- Uncomment only the dataset you're currently working on
"""

import os
import json
import shutil
from pathlib import Path

# ===== ACTIVE DATASETS =====
# Comment/uncomment datasets you want to process
# Process ONE at a time to avoid excessive downloads!

ACTIVE_DATASETS = [
    "moltean_100x100",      # Uncomment to process Moltean
    "fruits262",          # Uncomment to process Fruits-262
    "indian_fruits",      # Uncomment to process Indian Fruits
    "fruit_recognition",  # Uncomment to process Fruit Recognition
    "fresh_rotten",       # Uncomment to process Fresh & Rotten
]

# ===== KAGGLE DATASETS CONFIG =====
KAGGLE_DATASETS = {
    "moltean_100x100": {
        "kaggle_id": "moltean/fruits",
        "local_path": "/kaggle/input/fruits/fruits-360_100x100/fruits-360/Training",
        "extract_path": "fruits-360-dataset"
    },
    "fruits262": {
        "kaggle_id": "aelchimminut/fruits262",
        "local_path": "/kaggle/input/fruits262/Fruit-262",
        "extract_path": "fruits-262-dataset"
    },
    "indian_fruits": {
        "kaggle_id": "utkarshsaxenadn/fruits-classification",
        "local_path": "/kaggle/input/fruits-classification/Fruits Classification/train",
        "extract_path": "indian-fruits-dataset"
    },
    "fruit_recognition": {
        "kaggle_id": "chrisfilo/fruit-recognition",
        "local_path": "/kaggle/input/fruit-recognition",
        "extract_path": "fruit-recognition-dataset"
    },
    "fresh_rotten": {
        "kaggle_id": "sriramr/fruits-fresh-and-rotten-for-classification",
        "local_path": "/kaggle/input/fruits-fresh-and-rotten-for-classification/dataset/train",
        "extract_path": "fresh-rotten-dataset"
    }
}

KAGGLE_JSON_PATH = "kaggle.json"

# ===== KAGGLE SETUP =====

def setup_kaggle():
    """Setup Kaggle API credentials"""
    kaggle_json = Path(KAGGLE_JSON_PATH)
    
    if not kaggle_json.exists():
        print(f"❌ {KAGGLE_JSON_PATH} not found!")
        print(f"\n📝 Please:")
        print(f"   1. Go to https://www.kaggle.com/settings")
        print(f"   2. Click 'Create New Token'")
        print(f"   3. Upload kaggle.json to this folder")
        return False
    
    # Setup credentials
    kaggle_dir = Path.home() / ".kaggle"
    kaggle_dir.mkdir(exist_ok=True)
    
    dest_path = kaggle_dir / "kaggle.json"
    shutil.copy2(kaggle_json, dest_path)
    dest_path.chmod(0o600)
    
    print(f"✅ Kaggle credentials configured")
    return True


def download_dataset(kaggle_id: str, extract_path: str):
    """Download dataset from Kaggle"""
    print(f"   📥 Downloading {kaggle_id}...")
    print(f"   ⏳ This may take a few minutes...")
    
    try:
        import kaggle
        
        # Create extract directory
        Path(extract_path).mkdir(parents=True, exist_ok=True)
        
        # Download and unzip
        kaggle.api.dataset_download_files(
            kaggle_id,
            path=extract_path,
            unzip=True,
            quiet=False
        )
        
        print(f"   ✅ Downloaded to {extract_path}")
        return True
        
    except Exception as e:
        print(f"   ❌ Download failed: {e}")
        return False


# ===== EXTRACT CLASS NAMES =====

def extract_class_names(dataset_path):
    """Extract folder names as class names"""
    if not os.path.exists(dataset_path):
        return []
    
    classes = []
    for item in os.listdir(dataset_path):
        item_path = os.path.join(dataset_path, item)
        if os.path.isdir(item_path):
            classes.append(item)
    
    return sorted(classes)


def load_existing_results():
    """Load existing results if file exists"""
    output_file = "class_names_extracted.json"
    if os.path.exists(output_file):
        with open(output_file, 'r', encoding='utf-8') as f:
            return json.load(f)
    return {}


def main():
    print("=" * 70)
    print("EXTRACTING CLASS NAMES FROM SELECTED DATASETS")
    print("=" * 70)
    
    # Show active datasets
    print(f"\n📋 Active datasets ({len(ACTIVE_DATASETS)}):")
    for ds in ACTIVE_DATASETS:
        print(f"   ✓ {ds}")
    
    if not ACTIVE_DATASETS:
        print("\n⚠️  No datasets selected!")
        print("   Edit ACTIVE_DATASETS list to uncomment datasets you want to process")
        return
    
    print("\n" + "=" * 70)
    
    # Setup Kaggle
    kaggle_ready = setup_kaggle()
    
    # Load existing results
    all_results = load_existing_results()
    if all_results:
        print(f"\n📂 Loaded existing results ({len(all_results)} datasets)")
    
    # Process only active datasets
    for dataset_name in ACTIVE_DATASETS:
        if dataset_name not in KAGGLE_DATASETS:
            print(f"\n❌ Unknown dataset: {dataset_name}")
            continue
        
        config = KAGGLE_DATASETS[dataset_name]
        
        print(f"\n📂 {dataset_name.upper()}")
        print(f"   Kaggle: {config['kaggle_id']}")
        print(f"   Local path: {config['local_path']}")
        
        dataset_path = config['local_path']
        
        # Check if exists locally
        if not os.path.exists(dataset_path):
            print(f"   ⚠️  Not found locally")
            
            if kaggle_ready:
                print(f"   🔄 Downloading from Kaggle...")
                success = download_dataset(config['kaggle_id'], config['extract_path'])
                
                if not success:
                    all_results[dataset_name] = {
                        "status": "download_failed",
                        "classes": []
                    }
                    continue
            else:
                print(f"   ❌ Skipping (Kaggle not configured)")
                all_results[dataset_name] = {
                    "status": "not_found",
                    "classes": []
                }
                continue
        else:
            print(f"   ✅ Found locally")
        
        # Extract class names
        classes = extract_class_names(dataset_path)
        
        if not classes:
            print(f"   ⚠️  No classes found (check path structure)")
            all_results[dataset_name] = {
                "status": "empty",
                "classes": []
            }
            continue
        
        print(f"   ✅ Extracted {len(classes)} classes")
        
        all_results[dataset_name] = {
            "status": "success",
            "count": len(classes),
            "classes": classes
        }
        
        # Print preview
        print(f"   Preview (first 15):")
        for cls in classes[:15]:
            print(f"      - {cls}")
        if len(classes) > 15:
            print(f"      ... and {len(classes) - 15} more")
    
    # Save to JSON (merge with existing)
    output_file = "class_names_extracted.json"
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(all_results, f, indent=2, ensure_ascii=False)
    
    print("\n" + "=" * 70)
    print(f"✅ Results saved to: {output_file}")
    print("=" * 70)
    
    # Summary
    print("\n📊 SUMMARY (All datasets in file):")
    success_count = 0
    total_datasets = len(KAGGLE_DATASETS)
    
    for dataset_name in KAGGLE_DATASETS.keys():
        result = all_results.get(dataset_name, {"status": "not_processed", "count": 0})
        status = result['status']
        count = result.get('count', 0)
        
        if status == "success":
            icon = "✅"
            success_count += 1
        elif status == "not_processed":
            icon = "⏸️ "
        elif status == "not_found":
            icon = "❌"
        elif status == "download_failed":
            icon = "⚠️ "
        else:
            icon = "⚠️ "
        
        print(f"   {icon} {dataset_name}: {count} classes ({status})")
    
    print(f"\n📈 Progress: {success_count}/{total_datasets} datasets completed")
    
    if success_count < total_datasets:
        remaining = [ds for ds in KAGGLE_DATASETS.keys() if ds not in all_results or all_results[ds]['status'] != 'success']
        print(f"\n💡 Next: Uncomment these datasets in ACTIVE_DATASETS:")
        for ds in remaining:
            print(f"   - {ds}")
    else:
        print("\n🎉 All datasets processed!")
        print("   Next step: Use class_names_extracted.json to fill FRUIT_MAPPING")
        print("   in dataset_preparation_multi.py")


if __name__ == "__main__":
    main()
