package com.example.appchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.appchat.Fragment.ChatFragment;
import com.example.appchat.Fragment.FriendFragment;
import com.example.appchat.Fragment.MyFragment;

import com.example.appchat.Object.MyNotification;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.Service.NotificationService;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.MainBinding;
import com.example.appchat.variable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Main extends AppCompatActivity
{

    private static int isBack = 0;
    private static int positionTab = 0;
    public  static DatabaseReference reference;
    MainBinding mainBinding;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainBinding = MainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        getUser();
        reference = FirebaseDatabase.getInstance()
                .getReference("Notification/"+variable.account.getId());
        getNotification();
        checkService();
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        mainBinding.bottomNavigationView.setOnNavigationItemSelectedListener(item ->
        {
            int id = item.getItemId();
              if(id == R.id.mMessage)
              {
                  if(positionTab != 0)
                  {
                      positionTab = 0;
                      ChatFragment chatFragment = new ChatFragment();
                      openFragment(chatFragment);
                  }
              }
              else if (id == R.id.mProfile)
              {
                  if(positionTab != 1)
                  {
                      positionTab = 1;
                      MyFragment myFragment = new MyFragment();
                      openFragment(myFragment);
                  }
              }
              else {
                  if(positionTab != 2) {
                      positionTab = 2;
                      FriendFragment friendFragment = new FriendFragment();
                      openFragment(friendFragment);
                  }
              }
            return true;
        });
    }

     // check cấp quyền chạy ngầm
    @SuppressLint("BatteryLife")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkService()
    {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        String packageName = getPackageName();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        { // cấp quyền chạy ngầm
            Intent it = new Intent();
            if (!pm.isIgnoringBatteryOptimizations(packageName))
            {
                it.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                it.setData(Uri.parse("package:" + packageName));
                startActivityForResult(it,100);
            }
        }
    }

    public void openFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout,fragment);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onBackPressed() {
        isBack ++;
        Toast.makeText(this,"Nhấn 2 lần để thoát",Toast.LENGTH_SHORT).show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isBack = 0;
            }
        },2000);
        if(isBack == 2)
        {
            isBack = 0;
            finishAffinity();
            super.onBackPressed();
        }

    }
    public void getUser() // lấy thông tin của user đăng nhập
    {
        DatabaseReference get = FirebaseDatabase.getInstance()
                .getReference("LUser/"+ variable.account.getId());
        get.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                variable.user = snapshot.getValue(User.class);
                openFragment(new ChatFragment());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            boolean check = pm.isIgnoringBatteryOptimizations(getPackageName());
            if(!check)
            {
                Toast.makeText(Main.this,"Nếu không cho phép bạn sẽ không nhận được thông báo từ ứng dụng",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void getNotification()
    {
        createChannelId();
        reference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                MyNotification myNotification = snapshot.getValue(MyNotification.class);
                reference.setValue(null);
                if(myNotification != null)
                    putNotification(myNotification);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    public static final String CHANNEL_ID = "ID";
    private void createChannelId()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "NotificationMessage";
            String description = "Message";
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
    public void putNotification( MyNotification notificationMess)
    {
        String name = notificationMess.getName();
        String avt = notificationMess.getAvt();
        String mess = notificationMess.getMessage();
        @SuppressLint("RemoteViewLayout")
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.custom_notification);
        notificationLayout.setTextViewText(R.id.name_notification,name);
        notificationLayout.setTextViewText(R.id.MessNotification,mess);
        if(avt == null)
            notificationLayout.setImageViewResource(R.id.image_notification,R.drawable.img_girl_1);
        else
            notificationLayout.setImageViewUri(R.id.image_notification, Uri.parse(avt));

        Notification notification = new NotificationCompat.Builder(this, Main.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(notificationLayout)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())

                .build();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManagerCompat.from(this).notify((int) new Date().getTime(), notification);

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