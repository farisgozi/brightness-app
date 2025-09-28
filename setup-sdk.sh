#!/bin/bash

# Android SDK Setup Script for Brightness Control App

echo "============================================"
echo "Android SDK Setup for Brightness Control"
echo "============================================"

# Set SDK location
SDK_DIR="$HOME/android-sdk"
TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip"

echo "Installing Android SDK to: $SDK_DIR"

# Create SDK directory
mkdir -p "$SDK_DIR"
cd "$SDK_DIR"

# Download command line tools
echo "📥 Downloading Android SDK command line tools..."
wget -O cmdline-tools.zip "$TOOLS_URL"

if [ $? -ne 0 ]; then
    echo "❌ Failed to download Android SDK tools"
    echo "Please check your internet connection"
    exit 1
fi

# Extract tools
echo "📦 Extracting tools..."
unzip -q cmdline-tools.zip
rm cmdline-tools.zip

# Move to correct location
mkdir -p cmdline-tools/latest
mv cmdline-tools/* cmdline-tools/latest/ 2>/dev/null
rmdir cmdline-tools/bin cmdline-tools/lib 2>/dev/null || true

# Set environment variables
export ANDROID_HOME="$SDK_DIR"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

echo "🔧 Installing required SDK packages..."

# Accept licenses and install required packages
yes | sdkmanager --sdk_root="$ANDROID_HOME" \
    "platforms;android-30" \
    "build-tools;30.0.3" \
    "platform-tools" \
    "tools"

if [ $? -eq 0 ]; then
    echo "✅ Android SDK installed successfully!"
    
    # Add to profile
    echo "" >> ~/.bashrc
    echo "# Android SDK" >> ~/.bashrc
    echo "export ANDROID_HOME=\"$SDK_DIR\"" >> ~/.bashrc
    echo "export PATH=\"\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools\"" >> ~/.bashrc
    
    echo ""
    echo "📋 Environment variables added to ~/.bashrc"
    echo "Run: source ~/.bashrc  (or restart terminal)"
    echo ""
    echo "🎯 Now you can run: ./build.sh"
    
else
    echo "❌ SDK installation failed"
    exit 1
fi

echo ""
echo "============================================"
echo "Setup completed!"
echo "============================================"
