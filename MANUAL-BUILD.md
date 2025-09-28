# 🚀 Manual GitHub Actions Build Instructions

## Langkah-langkah untuk menjalankan build di GitHub:

### 1. **Commit & Push Changes**
```bash
cd /home/joy/brightnessAPP

# Add semua perubahan terbaru
git add .

# Commit dengan pesan yang jelas
git commit -m "🔧 Final fix - Sync Android SDK versions (30) with build.gradle"

# Push ke GitHub
git push origin main
```

### 2. **Manual Trigger Build di GitHub**
1. Buka repository: `https://github.com/farisgozi/brightness-app`
2. Klik tab **"Actions"**
3. Klik workflow **"Build Brightness Control APK"**
4. Klik tombol **"Run workflow"** (kanan atas)
5. Pilih branch **"main"**
6. Klik **"Run workflow"** hijau

### 3. **Monitor Build Progress**
- Build akan memakan waktu 5-15 menit
- Watch untuk setiap step:
  - ✅ Checkout code
  - ✅ Set up JDK 17
  - ✅ Setup Android SDK
  - ✅ Setup Gradle
  - ✅ Cache Gradle packages
  - ✅ Accept SDK licenses
  - ✅ Validate Gradle Wrapper
  - ✅ Clean project
  - ✅ Build debug APK
  - ✅ Build release APK
  - ✅ Upload artifacts

### 4. **Download APK (Jika Build Berhasil)**
**Via Artifacts:**
- Klik workflow yang selesai
- Scroll ke bawah ke section "Artifacts"
- Download "brightness-control-debug" atau "brightness-control-release"

**Via Releases (Auto-created):**
- Klik tab "Releases" di main page repository
- Download APK dari release terbaru

## 🔧 **Perbaikan Terakhir yang Dilakukan:**
- ✅ Synced Android SDK version: `android-30` (sesuai build.gradle)
- ✅ Synced build-tools version: `30.0.3` (sesuai build.gradle)
- ✅ Added Gradle validation & auto-recreation
- ✅ Increased memory allocation: `-Xmx3072m`
- ✅ Added extensive debugging output
- ✅ Added conditional steps untuk error handling

## 🎯 **Jika Build Masih Gagal:**

### Common Issues & Solutions:

**Issue: Gradle Wrapper tidak valid**
- Solution: Workflow akan auto-recreate gradle wrapper

**Issue: Android SDK license not accepted**
- Solution: Workflow menggunakan `yes |` untuk auto-accept

**Issue: Memory issues**
- Solution: Menggunakan `-Xmx3072m` (3GB RAM)

**Issue: APK tidak ditemukan**
- Solution: Step "List APK outputs" akan show debug info

## 📱 **Setelah APK Ready:**
1. Download APK dari GitHub
2. Transfer ke Oppo A5s Anda
3. Install dengan "Unknown Sources" enabled
4. Grant SuperUser permission
5. Enable overlay & write settings permissions
6. Start brightness service
7. Enjoy brightness control tanpa batas! 🌟

## ⚡ **Quick Action:**
Jika ingin build langsung sekarang:
1. `git add . && git commit -m "Final sync" && git push`
2. Go to GitHub Actions → Run workflow
3. Wait 10 minutes
4. Download APK!

**Repository Anda: https://github.com/farisgozi/brightness-app**
