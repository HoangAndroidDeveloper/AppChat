package com.example.appchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.IpEmailBinding;

import java.util.Objects;

public class IpEmailForgot extends AppCompatActivity
{
    IpEmailBinding emailBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        emailBinding = IpEmailBinding.inflate(getLayoutInflater());
        setContentView(emailBinding.getRoot());
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        emailBinding.btSendCode.setOnClickListener(v -> {
            String email = Objects.requireNonNull(emailBinding.emailForgot.getText()).toString();
            if(!email.isEmpty())
            {
                Intent it = new Intent(IpEmailForgot.this, IpCodeForgot.class);
                it.putExtra("email",email);
                startActivity(it);
            }
            else
            {
                emailBinding.emailForgot.setError("Email rỗng");
            }
        });
        emailBinding.backIpMail.setOnClickListener(v -> onBackPressed());
    }
}