# Troubleshooting Guide - Brightness Control App

## Root Access Issues

### Problem: "Root access required" message
**Solution:**
1. Pastikan device sudah di-root dengan benar
2. Install Magisk atau SuperSU
3. Buka terminal/adb shell dan test: `su`
4. Jika gagal, re-root device Anda

### Problem: SuperUser prompt tidak muncul
**Solution:**
1. Buka Magisk Manager/SuperSU
2. Check apakah ada request pending
3. Grant permission secara manual
4. Restart aplikasi

## Permission Issues

### Problem: Overlay permission denied
**Solution Android 10:**
1. Settings → Apps → Special app access
2. Display over other apps → Brightness Control → Allow
3. Atau: Settings → Apps → Brightness Control → Permissions

### Problem: Write settings permission denied
**Solution:**
1. Settings → Apps → Special app access
2. Modify system settings → Brightness Control → Allow

## Brightness Control Issues

### Problem: Brightness tidak berubah
**Diagnosis:**
```bash
# Test via ADB/Terminal
adb shell
su
cat /sys/class/leds/lcd-backlight/max_brightness
cat /sys/class/leds/lcd-backlight/brightness
echo 2047 > /sys/class/leds/lcd-backlight/brightness
```

**Solutions:**
1. **Path salah**: Check path brightness file untuk device Anda
2. **Permission denied**: 
   ```bash
   chmod 666 /sys/class/leds/lcd-backlight/brightness
   ```
3. **SELinux blocking**: 
   ```bash
   setenforce 0  # Temporary disable
   ```

### Problem: Max brightness bukan 2047
**Solution:**
```bash
# Check actual max brightness
cat /sys/class/leds/lcd-backlight/max_brightness
```
Edit `BrightnessController.java` dan ubah default value sesuai device Anda.

## Service Issues

### Problem: Service tidak start otomatis saat boot
**Solution:**
1. Check battery optimization: Settings → Battery → Battery optimization
2. Set Brightness Control ke "Don't optimize"
3. Check auto-start permission: Settings → Apps → Autostart → Enable

### Problem: Floating widget tidak muncul
**Solution:**
1. Restart service: Stop → Start
2. Check overlay permission
3. Reboot device
4. Check notification: Service harus running

## Device Specific Issues

### Oppo A5s / ColorOS Issues
**Additional steps:**
1. Settings → Security → Device managers → Enable untuk SuperUser
2. Settings → Privacy → Special app access → Device admin apps
3. Phone Manager → Permissions → Autostart → Enable untuk Brightness Control

### Ancient OS / ROM GSI Issues
**Known issues:**
1. Beberapa path brightness berbeda
2. SELinux policy lebih strict
3. Init.d script mungkin diperlukan untuk persistent root

**Solutions:**
```bash
# Alternative brightness paths to try:
/sys/class/leds/lcd-backlight/brightness
/sys/class/backlight/panel0-backlight/brightness  
/sys/devices/platform/soc/soc:qcom,dsi-display@0/backlight/panel0-backlight/brightness
```

## Performance Issues

### Problem: Battery drain
**Solution:**
1. Disable notification jika tidak perlu
2. Adjust service priority
3. Check wake locks: Settings → Battery → Battery usage

### Problem: App lag atau crash
**Solution:**
1. Check logcat: `adb logcat | grep BrightnessControl`
2. Free up RAM
3. Restart service
4. Reinstall APK

## Build Issues

### Problem: Gradle build failed
**Solution:**
```bash
./gradlew clean
./gradlew assembleDebug --stacktrace
```

### Problem: Android SDK not found
**Solution:**
```bash
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/platform-tools
```

## Advanced Debugging

### Enable verbose logging
Edit `MainActivity.java`:
```java
private static final boolean DEBUG = true;
```

### ADB logcat filtering
```bash
adb logcat | grep -E "(BrightnessControl|RootUtils|BrightnessController)"
```

### Manual testing brightness paths
```bash
# Find all possible brightness files
find /sys -name "*brightness*" 2>/dev/null

# Test write access
echo 1000 > /sys/class/leds/lcd-backlight/brightness
cat /sys/class/leds/lcd-backlight/brightness
```

## Contact & Support

Jika masalah masih berlanjut:
1. Check logcat output
2. Verify device compatibility
3. Test dengan device/ROM lain
4. Report issue dengan detail device info

**Device Info yang dibutuhkan:**
- Device model
- Android version
- ROM type (Stock/Custom/GSI)
- Root method (Magisk/SuperSU)
- Kernel version
