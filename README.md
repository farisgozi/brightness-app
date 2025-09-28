# 🌟 Brightness Control App

[![Build APK](https://github.com/USERNAME/brightnessAPP/actions/workflows/build.yml/badge.svg)](https://github.com/USERNAME/brightnessAPP/actions/workflows/build.yml)
[![Download APK](https://img.shields.io/badge/Download-APK-green)](https://github.com/USERNAME/brightnessAPP/releases/latest)

Aplikasi kontrol brightness khusus untuk mengatasi masalah slider brightness yang tidak bisa mencapai nilai maksimal pada ROM GSI, khususnya untuk device Android yang sudah di-root.

## 📱 Download APK

**[⬇️ Download Latest Release](https://github.com/USERNAME/brightnessAPP/releases/latest)**

> 🔄 APK dibangun otomatis menggunakan GitHub Actions

## Fitur Utama

- **Root Access**: Mengakses langsung `/sys/class/leds/lcd-backlight/brightness` dan `/sys/class/leds/lcd-backlight/max_brightness`
- **Auto Startup**: Berjalan otomatis saat device boot
- **Background Service**: Service berjalan terus di background
- **Floating UI**: Widget slider brightness yang dapat diakses kapan saja
- **Efisien**: Minimal penggunaan resource dan battery

## Requirements

- **Android 5.0+** (API Level 21+)
- **Root Access** (Wajib)
- **Overlay Permission** (Untuk floating widget)
- **Write Settings Permission**

## Target Device

- **Oppo A5s** with **Ancient OS** (ROM GSI)
- **Android 10**
- **Max brightness value**: 2047

## Cara Penggunaan

1. **Install APK** dan buka aplikasi
2. **Grant Root Permission** saat diminta SuperUser
3. **Enable Overlay Permission** - Aplikasi akan redirect ke Settings
4. **Enable Write Settings Permission** - Aplikasi akan redirect ke Settings
5. **Start Service** - Tekan tombol "Start Service"
6. **Floating Widget** akan muncul di layar
7. **Tap floating icon** untuk expand/collapse slider
8. **Drag floating widget** untuk memindahkan posisi

## Struktur File Sistem

Aplikasi ini mengakses file sistem berikut dengan root access:

```
/sys/class/leds/lcd-backlight/brightness      # File brightness value (0-2047)
/sys/class/leds/lcd-backlight/max_brightness  # File max brightness value (2047)
```

## Permissions yang Dibutuhkan

- `SYSTEM_ALERT_WINDOW` - Untuk floating overlay
- `RECEIVE_BOOT_COMPLETED` - Auto start saat boot
- `FOREGROUND_SERVICE` - Background service
- `WAKE_LOCK` - Mencegah sleep saat mengubah brightness
- `WRITE_SETTINGS` - Menulis pengaturan sistem
- `ACCESS_SUPERUSER` - Root access

## Komponen Aplikasi

### 1. MainActivity
- Interface utama untuk kontrol manual
- Management permission
- Start/stop service

### 2. BrightnessService
- Background service
- Floating widget management  
- Persistent notification

### 3. BrightnessController
- Core logic untuk kontrol brightness
- Root access ke file sistem
- Validation dan error handling

### 4. RootUtils
- Utility untuk menjalankan command dengan root
- File system access
- Command execution

### 5. BootReceiver
- Auto start service saat boot
- Check SharedPreferences settings

## Settings

- **Auto Start on Boot**: Enable/disable auto start
- **Show Notification**: Enable/disable persistent notification

## Build Instructions

1. Clone repository
2. Open dengan Android Studio
3. Sync Gradle
4. Build APK: `./gradlew assembleRelease`
5. Install ke device yang sudah root

## Troubleshooting

### Root Access Issues
- Pastikan device sudah di-root dengan benar
- Install SuperUser app (Magisk, SuperSU, dll)
- Grant root permission untuk aplikasi

### Overlay Permission
- Android 6.0+: Settings > Apps > Special access > Display over other apps
- Enable untuk "Brightness Control"

### Service Tidak Start
- Check notification permission
- Check battery optimization (disable untuk app ini)
- Pastikan auto-start permission enabled

### Brightness Tidak Berubah
- Verify file system path: `/sys/class/leds/lcd-backlight/`
- Check file permissions dengan root shell
- Test manual: `echo 2047 > /sys/class/leds/lcd-backlight/brightness`

## Development Notes

- Target SDK: Android 10 (API 29)
- Min SDK: Android 5.0 (API 21)
- Build Tools: Gradle 7.0.4
- Language: Java

## Warning

⚠️ **Aplikasi ini memerlukan root access dan memodifikasi file sistem level rendah. Gunakan dengan hati-hati dan pastikan Anda memahami risiko yang terlibat.**

## License

Open source - Free to use and modify.
