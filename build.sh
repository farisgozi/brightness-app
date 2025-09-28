#!/bin/bash

# Brightness Control App Build Script

echo "==================================="
echo "Brightness Control App Build Script"
echo "==================================="

# Auto-detect Android SDK or use manual setup
if [ -z "$ANDROID_HOME" ]; then
    echo "ANDROID_HOME not set. Attempting auto-detection..."
    
    # Common Android SDK paths
    POSSIBLE_PATHS=(
        "$HOME/Android/Sdk"
        "$HOME/android-sdk"
        "/opt/android-sdk"
        "/usr/local/android-sdk"
        "$HOME/.local/share/Android/Sdk"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -d "$path" ]; then
            export ANDROID_HOME="$path"
            echo "Found Android SDK at: $ANDROID_HOME"
            break
        fi
    done
    
    if [ -z "$ANDROID_HOME" ]; then
        echo ""
        echo "❌ Android SDK not found automatically!"
        echo ""
        echo "📋 Manual setup options:"
        echo "1. Install Android Studio (includes SDK)"
        echo "2. Download command line tools from: https://developer.android.com/studio#command-tools"
        echo "3. Set ANDROID_HOME manually:"
        echo "   export ANDROID_HOME=/path/to/android/sdk"
        echo "   export PATH=\$PATH:\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/tools"
        echo ""
        echo "🔧 Quick alternative - Build with Docker (if available):"
        echo "   ./docker-build.sh"
        echo ""
        read -p "Do you want to continue without Android SDK? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            exit 1
        fi
        echo "⚠️  Continuing without Android SDK - build may fail"
        ANDROID_HOME="/tmp/fake-android-home"
    fi
fi

echo "Android SDK: $ANDROID_HOME"

# Clean previous builds
echo "Cleaning previous builds..."
./gradlew clean

# Build debug APK
echo "Building debug APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Debug APK built successfully!"
    echo "📍 Location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed!"
    exit 1
fi

# Build release APK
echo "Building release APK..."
./gradlew assembleRelease

if [ $? -eq 0 ]; then
    echo "✅ Release APK built successfully!"
    echo "📍 Location: app/build/outputs/apk/release/app-release-unsigned.apk"
else
    echo "❌ Release build failed!"
    exit 1
fi

echo ""
echo "==================================="
echo "Build completed successfully!"
echo "==================================="
echo ""
echo "To install debug APK:"
echo "adb install app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "To install release APK (unsigned):"
echo "adb install app/build/outputs/apk/release/app-release-unsigned.apk"
echo ""
echo "⚠️  Remember: This app requires root access!"
echo "Make sure your device is rooted before installing."
