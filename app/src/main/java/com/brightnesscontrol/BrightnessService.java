package main.java.com.brightnesscontrol;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

public class BrightnessService extends Service {
    private static final String TAG = "BrightnessService";
    private static final String CHANNEL_ID = "BrightnessServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    
    private WindowManager windowManager;
    private View floatingView;
    private View collapsedView;
    private View expandedView;
    private RootUtils rootUtils;
    private BrightnessController brightnessController;
    private SeekBar seekBarFloating;
    private TextView tvFloatingBrightness;
    private boolean isExpanded = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service created");
        
        rootUtils = new RootUtils();
        brightnessController = new BrightnessController(rootUtils);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        createNotificationChannel();
        createFloatingView();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");
        
        startForeground(NOTIFICATION_ID, createNotification());
        
        return START_STICKY; // Service akan restart otomatis jika dibunuh sistem
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service destroyed");
        
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Brightness Control Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background service for brightness control");
            channel.setShowBadge(false);
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );
        
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Brightness Control")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.ic_brightness)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    private void createFloatingView() {
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_brightness, null);
        
        collapsedView = floatingView.findViewById(R.id.collapsedView);
        expandedView = floatingView.findViewById(R.id.expandedView);
        seekBarFloating = floatingView.findViewById(R.id.seekBarFloating);
        tvFloatingBrightness = floatingView.findViewById(R.id.tvFloatingBrightness);
        ImageView imgCollapsed = floatingView.findViewById(R.id.imgCollapsed);
        
        // Setup WindowManager parameters
        int layoutFlag;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutFlag = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutFlag = WindowManager.LayoutParams.TYPE_PHONE;
        }
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 100;
        
        // Add touch listener untuk drag
        floatingView.setOnTouchListener(new FloatingViewTouchListener());
        
        // Setup collapsed view click listener
        collapsedView.setOnClickListener(v -> toggleView());
        
        // Setup seekbar
        setupSeekBar();
        
        // Add view to window
        windowManager.addView(floatingView, params);
        
        // Initialize brightness info
        updateBrightnessInfo();
    }
    
    private void setupSeekBar() {
        int maxBrightness = brightnessController.getMaxBrightness();
        int currentBrightness = brightnessController.getCurrentBrightness();
        
        if (maxBrightness > 0) {
            seekBarFloating.setMax(maxBrightness);
            if (currentBrightness >= 0) {
                seekBarFloating.setProgress(currentBrightness);
                tvFloatingBrightness.setText(String.valueOf(currentBrightness));
            }
        }
        
        seekBarFloating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    setBrightness(progress);
                    tvFloatingBrightness.setText(String.valueOf(progress));
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void toggleView() {
        if (isExpanded) {
            collapsedView.setVisibility(View.VISIBLE);
            expandedView.setVisibility(View.GONE);
            isExpanded = false;
        } else {
            collapsedView.setVisibility(View.GONE);
            expandedView.setVisibility(View.VISIBLE);
            isExpanded = true;
            updateBrightnessInfo();
        }
    }
    
    private void setBrightness(int brightness) {
        new Thread(() -> brightnessController.setBrightness(brightness)).start();
    }
    
    private void updateBrightnessInfo() {
        new Thread(() -> {
            int currentBrightness = brightnessController.getCurrentBrightness();
            if (currentBrightness >= 0) {
                runOnUiThread(() -> {
                    seekBarFloating.setProgress(currentBrightness);
                    tvFloatingBrightness.setText(String.valueOf(currentBrightness));
                });
            }
        }).start();
    }
    
    private void runOnUiThread(Runnable runnable) {
        floatingView.post(runnable);
    }
    
    // Touch listener untuk drag floating view
    private class FloatingViewTouchListener implements View.OnTouchListener {
        private int initialX, initialY;
        private float initialTouchX, initialTouchY;
        
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = ((WindowManager.LayoutParams) floatingView.getLayoutParams()).x;
                    initialY = ((WindowManager.LayoutParams) floatingView.getLayoutParams()).y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                    
                case MotionEvent.ACTION_MOVE:
                    WindowManager.LayoutParams params = (WindowManager.LayoutParams) floatingView.getLayoutParams();
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(floatingView, params);
                    return true;
                    
                case MotionEvent.ACTION_UP:
                    return true;
            }
            return false;
        }
    }
}
