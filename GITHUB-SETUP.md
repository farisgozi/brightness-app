# 🚀 GitHub Actions Setup Guide

## 📋 Langkah-langkah Setup:

### 1. **Create GitHub Repository**
```bash
# Di GitHub.com, buat repository baru
# Nama: brightnessAPP atau brightness-control
# Set sebagai Public (untuk GitHub Actions gratis)
```

### 2. **Upload Project ke GitHub**
```bash
# Dari terminal di folder project:
cd /home/joy/brightnessAPP

# Initialize git (jika belum)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit - Brightness Control App for Oppo A5s"

# Add remote (ganti USERNAME dengan username GitHub Anda)
git remote add origin https://github.com/USERNAME/brightnessAPP.git

# Push to GitHub
git push -u origin main
```

### 3. **GitHub Actions akan Auto-Run**
- Setelah push, buka repository di GitHub
- Klik tab "Actions" 
- Lihat workflow "Build Brightness Control APK" running
- Tunggu sekitar 5-10 menit hingga selesai

### 4. **Download APK**
Setelah build selesai, ada 2 cara download:

**Cara 1: Via Actions Artifacts**
- Di tab Actions → klik workflow yang selesai
- Scroll ke bawah → download "brightness-control-debug" atau "brightness-control-release"

**Cara 2: Via Releases (Auto-created)**
- Di main page repository → klik "Releases" (kanan)
- Download APK dari release terbaru

## ✅ **Keuntungan GitHub Actions:**

1. **Gratis** - 2000 menit build per bulan
2. **Otomatis** - Build setiap kali push code
3. **Clean Environment** - Selalu build dari environment bersih
4. **Multiple APK** - Debug & Release APK sekaligus
5. **Auto Release** - APK langsung tersedia di Releases
6. **No Setup** - Tidak perlu install Android SDK lokal

## 🔄 **Workflow Features:**

- ✅ Build Debug APK (untuk testing)
- ✅ Build Release APK (untuk production)
- ✅ Auto upload ke Artifacts
- ✅ Auto create GitHub Release
- ✅ Cache Gradle untuk build lebih cepat
- ✅ Support manual trigger (workflow_dispatch)

## 📱 **Setelah APK Ready:**

1. Download APK dari GitHub Releases
2. Transfer ke Oppo A5s Anda
3. Install APK (enable Unknown Sources)
4. Grant SuperUser permission
5. Enable overlay & write settings permissions
6. Start brightness service
7. Enjoy unlimited brightness control! 🌟

## 🆘 **Troubleshooting:**

**Build Failed?**
- Check Actions tab untuk error log
- Pastikan semua file ter-commit dengan benar
- Coba trigger manual: Actions → Build APK → Run workflow

**APK tidak muncul?**
- Tunggu hingga workflow selesai (hijau ✅)
- Check di Artifacts atau Releases
- Pastikan repository public (untuk Actions gratis)

## 🎯 **Next Steps:**

Setelah setup ini, setiap kali Anda:
- Push code update ke GitHub
- APK baru akan otomatis di-build
- Download dari Releases untuk versi terbaru

**Happy building! 🚀**
