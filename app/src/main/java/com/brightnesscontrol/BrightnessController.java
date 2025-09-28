package main.java.com.brightnesscontrol;

import android.util.Log;

public class BrightnessController {
    private static final String TAG = "BrightnessController";
    private static final String BRIGHTNESS_PATH = "/sys/class/leds/lcd-backlight/brightness";
    private static final String MAX_BRIGHTNESS_PATH = "/sys/class/leds/lcd-backlight/max_brightness";
    
    private RootUtils rootUtils;
    private int maxBrightness = -1;
    
    public BrightnessController(RootUtils rootUtils) {
        this.rootUtils = rootUtils;
        initMaxBrightness();
    }
    
    private void initMaxBrightness() {
        try {
            String maxBrightnessStr = rootUtils.readFile(MAX_BRIGHTNESS_PATH);
            if (!maxBrightnessStr.isEmpty()) {
                maxBrightness = Integer.parseInt(maxBrightnessStr.trim());
                Log.d(TAG, "Max brightness: " + maxBrightness);
            } else {
                Log.e(TAG, "Could not read max brightness file");
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing max brightness: " + e.getMessage());
            maxBrightness = 2047; // Default fallback for your device
        }
    }
    
    public int getMaxBrightness() {
        if (maxBrightness == -1) {
            initMaxBrightness();
        }
        return maxBrightness;
    }
    
    public int getCurrentBrightness() {
        try {
            String brightnessStr = rootUtils.readFile(BRIGHTNESS_PATH);
            if (!brightnessStr.isEmpty()) {
                return Integer.parseInt(brightnessStr.trim());
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing current brightness: " + e.getMessage());
        }
        return -1;
    }
    
    public boolean setBrightness(int brightness) {
        if (brightness < 0 || brightness > getMaxBrightness()) {
            Log.e(TAG, "Invalid brightness value: " + brightness);
            return false;
        }
        
        try {
            // Ensure the file is writable
            rootUtils.makeFileWritable(BRIGHTNESS_PATH);
            
            // Write the brightness value
            boolean success = rootUtils.writeFile(BRIGHTNESS_PATH, String.valueOf(brightness));
            
            if (success) {
                Log.d(TAG, "Brightness set to: " + brightness);
            } else {
                Log.e(TAG, "Failed to set brightness");
            }
            
            return success;
        } catch (Exception e) {
            Log.e(TAG, "Error setting brightness: " + e.getMessage());
            return false;
        }
    }
    
    public boolean increaseBrightness(int step) {
        int current = getCurrentBrightness();
        if (current >= 0) {
            int newBrightness = Math.min(current + step, getMaxBrightness());
            return setBrightness(newBrightness);
        }
        return false;
    }
    
    public boolean decreaseBrightness(int step) {
        int current = getCurrentBrightness();
        if (current >= 0) {
            int newBrightness = Math.max(current - step, 0);
            return setBrightness(newBrightness);
        }
        return false;
    }
    
    public boolean isBrightnessFileAccessible() {
        return rootUtils.fileExists(BRIGHTNESS_PATH) && rootUtils.fileExists(MAX_BRIGHTNESS_PATH);
    }
    
    public float getBrightnessPercentage() {
        int current = getCurrentBrightness();
        int max = getMaxBrightness();
        if (current >= 0 && max > 0) {
            return (float) current / max * 100;
        }
        return -1;
    }
    
    public boolean setBrightnessPercentage(float percentage) {
        if (percentage < 0 || percentage > 100) {
            return false;
        }
        
        int max = getMaxBrightness();
        if (max > 0) {
            int brightness = Math.round(max * percentage / 100);
            return setBrightness(brightness);
        }
        return false;
    }
}
