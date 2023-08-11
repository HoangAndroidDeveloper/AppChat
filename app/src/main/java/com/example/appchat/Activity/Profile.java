package com.example.appchat.Activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.appchat.Object.Friend;
import com.example.appchat.R;
import com.example.appchat.Service.NotificationService;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.ActivityProfileBinding;

public class Profile extends AppCompatActivity {

    ActivityProfileBinding profileBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        profileBinding.btBackProfile.setOnClickListener(v -> onBackPressed());
        Intent it = getIntent();
        Bundle bundle = it.getBundleExtra("bundle");
        Friend friend = (Friend) bundle.getSerializable("friend");
        String name = friend.getName();
        String avt = friend.getAvt();
        profileBinding.name.setText(name);
        if(avt == null)
        {
            Glide.with(this).load(R.drawable.img_girl_1).centerCrop().into(profileBinding.avt);
        }
        else
        {
            Glide.with(this).load(avt).centerCrop().into(profileBinding.avt);
        }
        profileBinding.btBack.setOnClickListener(v -> onBackPressed());
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