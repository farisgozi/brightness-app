#!/bin/bash

# Brightness Control App Install Script

echo "======================================="
echo "Brightness Control App Install Script"
echo "======================================="

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "Error: ADB not found"
    echo "Please install Android SDK Platform Tools"
    exit 1
fi

# Check if device is connected
echo "Checking device connection..."
adb devices

DEVICE_COUNT=$(adb devices | grep -v "List of devices" | grep -v "^$" | wc -l)

if [ $DEVICE_COUNT -eq 0 ]; then
    echo "❌ No devices connected!"
    echo "Please connect your Android device and enable USB debugging"
    exit 1
fi

echo "✅ Device(s) connected: $DEVICE_COUNT"

# Check APK files
DEBUG_APK="app/build/outputs/apk/debug/app-debug.apk"
RELEASE_APK="app/build/outputs/apk/release/app-release-unsigned.apk"

if [ -f "$DEBUG_APK" ]; then
    echo "📱 Installing debug APK..."
    adb install -r "$DEBUG_APK"
    
    if [ $? -eq 0 ]; then
        echo "✅ Debug APK installed successfully!"
    else
        echo "❌ Failed to install debug APK"
        exit 1
    fi
    
elif [ -f "$RELEASE_APK" ]; then
    echo "📱 Installing release APK..."
    adb install -r "$RELEASE_APK"
    
    if [ $? -eq 0 ]; then
        echo "✅ Release APK installed successfully!"
    else
        echo "❌ Failed to install release APK"
        exit 1
    fi
    
else
    echo "❌ No APK files found!"
    echo "Please run build.sh first to build the APK"
    exit 1
fi

echo ""
echo "======================================="
echo "Installation completed!"
echo "======================================="
echo ""
echo "📋 Next steps:"
echo "1. Open the Brightness Control app on your device"
echo "2. Grant SuperUser/Root permission when prompted"
echo "3. Enable 'Display over other apps' permission"
echo "4. Enable 'Modify system settings' permission"
echo "5. Start the brightness service"
echo ""
echo "⚠️  Important notes:"
echo "- Device must be rooted"
echo "- App needs system-level permissions"
echo "- Tested on Oppo A5s with Ancient OS (Android 10)"
echo "- Max brightness value: 2047"
