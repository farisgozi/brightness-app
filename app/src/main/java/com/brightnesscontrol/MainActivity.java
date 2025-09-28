package com.brightnesscontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;
    private static final int WRITE_SETTINGS_PERMISSION_REQ_CODE = 2;
    
    private Button btnStartService, btnStopService, btnSettings;
    private SeekBar seekBarBrightness;
    private TextView tvBrightnessValue, tvMaxBrightness, tvCurrentBrightness;
    private RootUtils rootUtils;
    private BrightnessController brightnessController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initUtils();
        checkPermissions();
        setupListeners();
        updateBrightnessInfo();
    }

    private void initViews() {
        btnStartService = findViewById(R.id.btnStartService);
        btnStopService = findViewById(R.id.btnStopService);
        btnSettings = findViewById(R.id.btnSettings);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        tvBrightnessValue = findViewById(R.id.tvBrightnessValue);
        tvMaxBrightness = findViewById(R.id.tvMaxBrightness);
        tvCurrentBrightness = findViewById(R.id.tvCurrentBrightness);
    }

    private void initUtils() {
        rootUtils = new RootUtils();
        brightnessController = new BrightnessController(rootUtils);
    }

    private void checkPermissions() {
        // Check overlay permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                requestOverlayPermission();
            }
        }

        // Check write settings permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestWriteSettingsPermission();
            }
        }

        // Check root access
        checkRootAccess();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestWriteSettingsPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, WRITE_SETTINGS_PERMISSION_REQ_CODE);
    }

    private void checkRootAccess() {
        new Thread(() -> {
            boolean hasRoot = rootUtils.isRooted();
            runOnUiThread(() -> {
                if (!hasRoot) {
                    Toast.makeText(this, "Root access required! Please root your device.", 
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Root access detected!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void setupListeners() {
        btnStartService.setOnClickListener(v -> startBrightnessService());
        btnStopService.setOnClickListener(v -> stopBrightnessService());
        btnSettings.setOnClickListener(v -> openSettings());

        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setBrightness(progress);
                    tvBrightnessValue.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void startBrightnessService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Overlay permission required!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent serviceIntent = new Intent(this, BrightnessService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        Toast.makeText(this, "Brightness service started", Toast.LENGTH_SHORT).show();
    }

    private void stopBrightnessService() {
        Intent serviceIntent = new Intent(this, BrightnessService.class);
        stopService(serviceIntent);
        Toast.makeText(this, "Brightness service stopped", Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void setBrightness(int brightness) {
        new Thread(() -> {
            boolean success = brightnessController.setBrightness(brightness);
            runOnUiThread(() -> {
                if (success) {
                    tvCurrentBrightness.setText("Current: " + brightness);
                } else {
                    Toast.makeText(this, "Failed to set brightness", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void updateBrightnessInfo() {
        new Thread(() -> {
            int maxBrightness = brightnessController.getMaxBrightness();
            int currentBrightness = brightnessController.getCurrentBrightness();
            
            runOnUiThread(() -> {
                if (maxBrightness > 0) {
                    tvMaxBrightness.setText("Max: " + maxBrightness);
                    seekBarBrightness.setMax(maxBrightness);
                    
                    if (currentBrightness >= 0) {
                        seekBarBrightness.setProgress(currentBrightness);
                        tvBrightnessValue.setText(String.valueOf(currentBrightness));
                        tvCurrentBrightness.setText("Current: " + currentBrightness);
                    }
                } else {
                    Toast.makeText(this, "Cannot access brightness files. Check root access.", 
                            Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Overlay permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == WRITE_SETTINGS_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(this)) {
                    Toast.makeText(this, "Write settings permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Write settings permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBrightnessInfo();
    }
}
