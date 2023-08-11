package com.example.appchat.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.appchat.MainActivity;
import com.example.appchat.Object.Account;
import com.example.appchat.R;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.ActivityChangePassBinding;
import com.example.appchat.variable;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChangePass extends AppCompatActivity {

    ActivityChangePassBinding changePassBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePassBinding = ActivityChangePassBinding.inflate(getLayoutInflater());
        setContentView(changePassBinding.getRoot());
//
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        changePassBinding.backIpCode.setOnClickListener(v -> {
            startActivity(new Intent(ChangePass.this, MainActivity.class));
            finishAffinity();
        });
        Button btChangePass = changePassBinding.btChangePass;
        changePassBinding.pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String txt = Objects.requireNonNull(changePassBinding.passConfirm.getText()).toString();
                if(s.toString().isEmpty() || txt.isEmpty())
                {
                    btChangePass.setEnabled(false);
                    btChangePass.setBackgroundColor(getResources().getColor(R.color.view_color));
                }
                else
                {
                    btChangePass.setEnabled(true);
                    btChangePass.setBackgroundColor(getResources().getColor(R.color.blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        changePassBinding.passConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = Objects.requireNonNull(changePassBinding.pass.getText()).toString();
                if(s.toString().isEmpty() || txt.isEmpty())
                {
                    btChangePass.setEnabled(false);
                    btChangePass.setBackgroundColor(getResources().getColor(R.color.view_color));
                }
                else
                {
                    btChangePass.setEnabled(true);
                    btChangePass.setBackgroundColor(getResources().getColor(R.color.blue));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // click vào nút đổi pass
        btChangePass.setOnClickListener(v -> {
              String pass1 = changePassBinding.pass.getText().toString().trim();
              String pass2 = changePassBinding.passConfirm.getText().toString().trim();
              if(pass1.equals(variable.account.getPassword()))
              {
                  changePass(pass2);
              }
              else
              {
                  Toast.makeText(ChangePass.this,"Mật khẩu cũ sai",Toast.LENGTH_SHORT).show();
              }
        });
    }
    public void changePass(String pass) // đổi mật khẩu
    {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.load_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Account ac = variable.account;
        if(ac != null) {
            ac.setPassword(pass);
            DatabaseReference change = FirebaseDatabase.getInstance()
                    .getReference("LAccount/" + ac.getId());
            change.setValue(ac).addOnCompleteListener(task -> {
                dialog.dismiss();
                startActivity(new Intent(ChangePass.this,MainActivity.class));
                Toast.makeText(ChangePass.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
            });
        }
    }

}