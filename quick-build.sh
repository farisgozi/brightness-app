#!/bin/bash

# Quick Build Script for Brightness Control App

echo "🚀 Quick Build for Brightness Control App"
echo "========================================"

# Simple build options
echo ""
echo "Choose build method:"
echo "1. 🐳 Docker Build (Recommended - No Android SDK needed)"
echo "2. 🔧 Local Build (Requires Android SDK)"
echo "3. ⚙️  Setup Android SDK first"
echo ""

read -p "Enter choice [1-3]: " choice

case $choice in
    1)
        echo "🐳 Building with Docker..."
        echo ""
        
        # Check Docker
        if ! command -v docker &> /dev/null; then
            echo "❌ Docker not installed!"
            echo "Install Docker: sudo apt install docker.io"
            exit 1
        fi
        
        # Create optimized Dockerfile for quick build
        cat > Dockerfile.quick << 'EOF'
FROM gradle:7.4.2-jdk11

USER root

# Install Android SDK
ENV ANDROID_HOME /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

RUN apt-get update && apt-get install -y wget unzip && \
    mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    cd /tmp && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    mv cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm commandlinetools-linux-8512546_latest.zip

# Install required SDK components
RUN yes | ${ANDROID_HOME}/cmdline-tools/latest/bin/sdkmanager \
    "platforms;android-30" \
    "build-tools;30.0.3" \
    "platform-tools"

WORKDIR /project
COPY . .

# Build
RUN chmod +x gradlew
RUN ./gradlew clean assembleDebug --no-daemon --stacktrace
EOF

        echo "📦 Building Docker image and APK..."
        docker build -f Dockerfile.quick -t brightness-quick-build .
        
        if [ $? -eq 0 ]; then
            echo "✅ Build successful!"
            
            # Extract APK from container
            echo "📱 Extracting APK..."
            docker run --rm -v "$(pwd)/build-output:/output" brightness-quick-build sh -c "cp -r app/build/outputs/apk /output/"
            
            if [ -f "build-output/apk/debug/app-debug.apk" ]; then
                echo "🎉 APK created: build-output/apk/debug/app-debug.apk"
                echo ""
                echo "📱 To install:"
                echo "   adb install build-output/apk/debug/app-debug.apk"
            else
                echo "❌ APK not found in output"
            fi
        else
            echo "❌ Docker build failed!"
        fi
        ;;
        
    2)
        echo "🔧 Local build..."
        
        if [ -z "$ANDROID_HOME" ]; then
            echo "❌ ANDROID_HOME not set!"
            echo ""
            echo "Set it manually:"
            echo "export ANDROID_HOME=/path/to/android-sdk"
            echo "export PATH=\$PATH:\$ANDROID_HOME/platform-tools"
            echo ""
            echo "Or run option 3 to setup SDK automatically"
            exit 1
        fi
        
        echo "✅ Using Android SDK: $ANDROID_HOME"
        echo "🧹 Cleaning..."
        ./gradlew clean
        
        echo "🔨 Building debug APK..."
        ./gradlew assembleDebug
        
        if [ $? -eq 0 ]; then
            echo "✅ Build successful!"
            echo "📱 APK: app/build/outputs/apk/debug/app-debug.apk"
            echo ""
            echo "📱 To install:"
            echo "   adb install app/build/outputs/apk/debug/app-debug.apk"
        else
            echo "❌ Build failed!"
        fi
        ;;
        
    3)
        echo "⚙️ Setting up Android SDK..."
        
        if [ ! -f "setup-sdk.sh" ]; then
            echo "❌ setup-sdk.sh not found!"
            exit 1
        fi
        
        chmod +x setup-sdk.sh
        ./setup-sdk.sh
        
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ SDK setup completed!"
            echo "Now run this script again and choose option 2"
        fi
        ;;
        
    *)
        echo "❌ Invalid choice!"
        exit 1
        ;;
esac

echo ""
echo "🎯 Next steps after successful build:"
echo "1. Enable USB debugging on your rooted Android device"
echo "2. Connect device via USB"
echo "3. Run: adb install [apk-file]"
echo "4. Grant root permissions when prompted"
echo "5. Enable overlay and write settings permissions"
