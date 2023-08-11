package com.example.appchat.Fragment;




import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appchat.Activity.Main;
import com.example.appchat.MainActivity;
import com.example.appchat.Object.Account;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.Service.NotificationService;
import com.example.appchat.databinding.FragmentMyBinding;
import com.example.appchat.variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;


public class MyFragment extends Fragment {

    FragmentMyBinding myBinding;
    Context context;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = container.getContext();
        myBinding = FragmentMyBinding.inflate(getLayoutInflater(), container, false);
        User us = variable.user;
        if(us.getAvatar() != null)
        Glide.with(context).load(us.getAvatar()).centerCrop().into(myBinding.avatar);
        else
        {
            Glide.with(context).load(R.drawable.img_girl_1).centerCrop().into(myBinding.avatar);
        }
        myBinding.name.setText(us.getName());
        myBinding.idUser.setText(us.getId()+"");
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
             SelectImage();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };
        myBinding.avatar.setOnClickListener(v -> TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedMessage("Hãy cấp quyền nếu bạn muốn sửa ảnh đại diện")
                .setPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE)
                .check());
        myBinding.btLogout.setOnClickListener(v -> {
            SharedPreferences perPreferences = context.
                    getSharedPreferences("HOANG_DEVICE",Context.MODE_PRIVATE);
            SharedPreferences.Editor logout = perPreferences.edit();
            logout.clear();
            logout.apply();
            startActivity(new Intent(context, MainActivity.class));
            context.stopService(new Intent(context, NotificationService.class));
        });
        return myBinding.getRoot();
    }
    public void SelectImage() // mở thư viện chọn ảnh
    {
        TedImagePicker.with(context).start(uri -> {
            if (variable.account != null) {
                long id = variable.account.getId();
                StorageReference putImage = FirebaseStorage.getInstance()
                        .getReference("Avatar/" + id);
                FirebaseApp.initializeApp(context); // có dòng này mới put ảnh lên đc, k có sẽ lỗi
                putImage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isComplete()) {
                            putImage.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                if (task.isComplete()) {
                                    Toast.makeText(context, "ọ", Toast.LENGTH_SHORT).show();
                                    DatabaseReference put = FirebaseDatabase.getInstance()
                                            .getReference("LUser/" + id);
                                    variable.user.setAvatar(String.valueOf(uri1));
                                    put.setValue(variable.user);
                                    Glide.with(context).load(uri1).centerCrop().into(myBinding.avatar);
                                }
                            });
                        }
                    }
                });
            }
        });
    }


}