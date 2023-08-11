package com.example.appchat.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import com.example.appchat.Service.NotificationService;
import com.example.appchat.databinding.ActivityCallBinding;

public class CallActivity extends AppCompatActivity {

    ActivityCallBinding callBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callBinding = ActivityCallBinding.inflate(getLayoutInflater());
        setContentView(callBinding.getRoot());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onDestroy() {
        startNoService();
        super.onDestroy();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startNoService()
    {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        boolean check = pm.isIgnoringBatteryOptimizations(getPackageName());
        if(check)
            startService(new Intent(this, NotificationService.class));
    }
}