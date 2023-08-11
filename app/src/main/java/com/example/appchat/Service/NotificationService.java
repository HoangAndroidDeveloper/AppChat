package com.example.appchat.Service;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.appchat.Activity.Main;
import com.example.appchat.Object.MyNotification;
import com.example.appchat.R;
import com.example.appchat.variable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        createChannelId();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Notification/"+variable.account.getId());
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
         return START_STICKY;
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

    private void createChannelId()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            CharSequence name = "NotificationMessage";
            String description = "Message";
            int importance = NotificationManager.IMPORTANCE_MAX;
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(Main.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

}
