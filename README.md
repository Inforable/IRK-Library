# IRK Library

Aplikasi Android (Kotlin + Jetpack Compose) untuk latihan materi IRK: Aljabar Linear (SPL, Determinan, Invers, Cramer), Kriptografi (Caesar, RSA), dan Huffman Coding. Arsitektur MVVM, navigasi via Bottom Navigation.

## Technology Stack

### Bahasa & Build
- Kotlin 2.0.21
- Android Gradle Plugin (AGP) 8.12.0
- Min SDK 24, Target/Compile SDK 36
- JVM target 11

## Installation Guide

### 1. Clone repo
```bash
git clone https://github.com/Inforable/IRK-Library.git
cd IRK-Library
```

### 2. Buka di Android Studio
- Pastikan JDK 11 aktif (Project Structure -> SDK Location).
- Biarkan Gradle Sync berjalan sampai selesai.

### 3. Konfigurasi Emulator (AVD)
- Create device: Pixel (apa saja) + Android API level yang sesuai (Android 14/15).
- Jika muncul error "device not have enough disk space to run AVD":
  AVD Manager → Edit → Wipe Data atau tambah Internal Storage (≥ 2–4 GB).

### 4. Run
- Pilih modul `app` sebagai konfigurasi run.
- Klik Run 'app' dan tunggu sampai emulator menampilkan Home IRK Library.

## Features Overview

### 1) Page 1 — Matrix & SPL (Gauss–Jordan)

- **Input matrix**
- **Solve SPL (Gauss–Jordan)**
- **Show Steps**
- **Determinant**
- **Inverse**
- **Cramer**

![Matrix Screenshot](screenshots/matrix-screen.png)

### 2) Page 2 — Cryptography (Lite)

- **Caesar 26 huruf**: Encrypt/Decrypt, Show Steps (p→c, mod 26).
- **RSA**:
  - **Keygen**
  - **Encrypt/Decrypt**
  - **Mapping sederhana**
  - **Show Steps**

![Crypto Screenshot](screenshots/crypto-screen.png)

### 3) Page 3 — Huffman (Lite)

- **Frequency table**
- **Build tree**
- **ASCII Tree + Progress slider**
- **Kode karakter**
- **Encode** & **Decode**
- **Show Steps**

![Huffman Screenshot](screenshots/huffman-screen.png)

## Architecture Overview

**Arsitektur**: MVVM — Model (core algoritma) / ViewModel (state & orchestration) / View (Compose UI).

```
app/src/main/java/com/inforable/irklibrary/
├── core/                    # Model: logika murni
│   ├── matrix/
│   │   ├── gaussjordan/…    # Gauss–Jordan (Greedy pivot), steps
│   │   ├── determinant/…    # Determinan (row ops / ko-faktor kecil)
│   │   ├── inverse/…        # Invers via Gauss–Jordan
│   │   ├── cramer/…         # Cramer's rule
│   │   └── steps/…          # Step & StepLogger (narasi langkah)
│   ├── crypto/
│   │   └── simple/…         # Caesar, RSA
│   └── huffman/             # Huffman (build/encode/decode + steps)
└── ui/                      # View + ViewModel (Compose)
    ├── matrix/…             # MatrixScreen + MatrixViewModel
    ├── crypto/…             # CryptoScreen + CryptoViewModel
    └── huffman/…            # HuffmanScreen + HuffmanViewModel
```

### Navigasi:
- NavHost dengan rute: Matrix, Crypto, Huffman.
- Bottom Navigation untuk berpindah antar page (tiap page Composable & ViewModel terpisah).

## Link Video: https://youtu.be/vkyA9SjICFU