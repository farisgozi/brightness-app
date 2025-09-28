#!/bin/bash

# Docker-based build for Brightness Control App
# This script uses Docker to build the APK without requiring local Android SDK

echo "==========================================="
echo "Brightness Control App - Docker Build"
echo "==========================================="

# Check if Docker is available
if ! command -v docker &> /dev/null; then
    echo "❌ Docker not found!"
    echo "Please install Docker to use this build method"
    echo "Alternative: Install Android SDK and use ./build.sh"
    exit 1
fi

echo "✅ Docker found"

# Create Dockerfile for Android build
cat > Dockerfile << 'EOF'
FROM openjdk:11-jdk

# Install Android SDK
ENV ANDROID_HOME /opt/android-sdk
ENV PATH ${PATH}:${ANDROID_HOME}/tools:${ANDROID_HOME}/platform-tools

RUN apt-get update && apt-get install -y wget unzip

# Download and install Android SDK command line tools
RUN mkdir -p ${ANDROID_HOME} && \
    cd ${ANDROID_HOME} && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    rm commandlinetools-linux-8512546_latest.zip

# Accept licenses and install required packages
RUN yes | ${ANDROID_HOME}/cmdline-tools/bin/sdkmanager --sdk_root=${ANDROID_HOME} \
    "platforms;android-30" \
    "build-tools;30.0.3" \
    "platform-tools"

WORKDIR /app
COPY . .

# Make gradlew executable
RUN chmod +x ./gradlew

# Build the APK
CMD ["./gradlew", "assembleDebug", "assembleRelease"]
EOF

echo "📦 Building Docker image..."
docker build -t brightness-control-builder .

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

echo "🔨 Building APK with Docker..."
docker run --rm -v $(pwd)/app/build:/app/app/build brightness-control-builder

if [ $? -eq 0 ]; then
    echo "✅ Build completed successfully!"
    echo "📍 APK locations:"
    find app/build/outputs/apk -name "*.apk" 2>/dev/null || echo "No APK files found"
else
    echo "❌ Build failed!"
    exit 1
fi

# Cleanup
rm -f Dockerfile

echo ""
echo "==========================================="
echo "Docker build completed!"
echo "==========================================="
