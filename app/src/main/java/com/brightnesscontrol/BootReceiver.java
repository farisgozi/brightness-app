package main.java.com.brightnesscontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";
    private static final String PREFS_NAME = "BrightnessControlPrefs";
    private static final String KEY_AUTO_START = "auto_start_enabled";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Boot completed received");
        
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action) || 
            Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action) ||
            "android.intent.action.QUICKBOOT_POWERON".equals(action)) {
            
            // Check if auto-start is enabled
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            boolean autoStartEnabled = prefs.getBoolean(KEY_AUTO_START, true); // Default enabled
            
            if (autoStartEnabled) {
                startBrightnessService(context);
            }
        }
    }
    
    private void startBrightnessService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, BrightnessService.class);
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            
            Log.d(TAG, "Brightness service started on boot");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start service on boot: " + e.getMessage());
        }
    }
}
