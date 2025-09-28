#!/data/data/com.termux/files/usr/bin/bash
# Build script untuk Termux (Android)

echo "Installing required packages..."
pkg install openjdk-17 gradle wget unzip

echo "Setting up Android SDK in Termux..."
export ANDROID_HOME=$HOME/android-sdk
mkdir -p $ANDROID_HOME

echo "Building APK..."
gradle assembleDebug

echo "APK ready at: app/build/outputs/apk/debug/"
