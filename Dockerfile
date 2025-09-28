FROM openjdk:11-jdk-slim

# Install required packages
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Set Android SDK environment
ENV ANDROID_HOME /opt/android-sdk
ENV ANDROID_SDK_ROOT ${ANDROID_HOME}
ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools/30.0.3

# Download and install Android SDK command line tools
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    cd ${ANDROID_HOME} && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip && \
    unzip commandlinetools-linux-8512546_latest.zip && \
    mv cmdline-tools latest && \
    mv latest cmdline-tools/ && \
    rm commandlinetools-linux-8512546_latest.zip

# Accept licenses and install required packages
RUN yes | sdkmanager --sdk_root=${ANDROID_HOME} \
    "platforms;android-30" \
    "build-tools;30.0.3" \
    "platform-tools" \
    "extras;android;m2repository" \
    "extras;google;m2repository"

WORKDIR /app

# Copy gradle wrapper and build files first (for caching)
COPY gradle/ gradle/
COPY gradlew .
COPY gradle.properties .
COPY settings.gradle .
COPY build.gradle .
COPY app/build.gradle app/

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY . .

# Set proper JAVA_OPTS
ENV JAVA_OPTS="-Xmx2048m -XX:MaxMetaspaceSize=512m"

# Build the APK
RUN ./gradlew clean assembleDebug --no-daemon --stacktrace
