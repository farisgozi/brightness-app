package main.java.com.brightnesscontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "BrightnessControlPrefs";
    private static final String KEY_AUTO_START = "auto_start_enabled";
    private static final String KEY_SHOW_NOTIFICATION = "show_notification";
    
    private Switch switchAutoStart;
    private Switch switchShowNotification;
    private TextView tvAppVersion;
    private SharedPreferences prefs;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        initViews();
        loadSettings();
        setupListeners();
    }
    
    private void initViews() {
        switchAutoStart = findViewById(R.id.switchAutoStart);
        switchShowNotification = findViewById(R.id.switchShowNotification);
        tvAppVersion = findViewById(R.id.tvAppVersion);
        
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        
        // Set app version
        try {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            tvAppVersion.setText("Version " + version);
        } catch (Exception e) {
            tvAppVersion.setText("Version 1.0");
        }
    }
    
    private void loadSettings() {
        switchAutoStart.setChecked(prefs.getBoolean(KEY_AUTO_START, true));
        switchShowNotification.setChecked(prefs.getBoolean(KEY_SHOW_NOTIFICATION, true));
    }
    
    private void setupListeners() {
        switchAutoStart.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_AUTO_START, isChecked).apply();
        });
        
        switchShowNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_SHOW_NOTIFICATION, isChecked).apply();
        });
    }
}
