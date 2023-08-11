package com.example.appchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.MainActivity;
import com.example.appchat.MyFunction;
import com.example.appchat.Object.Account;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.ActivityIpCodeBinding;
import com.example.appchat.variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zyao89.view.zloading.ZLoadingDialog;
import com.zyao89.view.zloading.Z_TYPE;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class IpCode extends AppCompatActivity
{

    DatabaseReference LAccount , LUser;
     private  String  code;
    ActivityIpCodeBinding codeBinding ;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        codeBinding = ActivityIpCodeBinding.inflate(getLayoutInflater());
        setContentView(codeBinding.getRoot());
//
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        long idAcc = variable.account.getId();
        final User user = new User();
        user.setName(variable.user.getName());
        user.setId(idAcc);
        LAccount = FirebaseDatabase.getInstance("https://app-chat-da629-default-rtdb.firebaseio.com/").getReference("LAccount/"+idAcc);
        LUser = FirebaseDatabase.getInstance("https://app-chat-da629-default-rtdb.firebaseio.com/").getReference("LUser/"+idAcc);
        codeBinding.btXacNhan.setBackgroundColor(getColor(R.color.view_color));
        codeBinding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                     if(s.length() == 6)
                     {
                         codeBinding.btXacNhan.setEnabled(true);
                         codeBinding.btXacNhan.setBackgroundColor(getColor(R.color.blue));
                     }
                     else
                     {
                         codeBinding.btXacNhan.setEnabled(false);
                         codeBinding.btXacNhan.setBackgroundColor(getColor(R.color.view_color));
                     }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

         code = MyFunction.VerificationEmail(variable.account.getEmail());

        codeBinding.btXacNhan.setOnClickListener(v -> {
                if (codeBinding.pinView.getText().toString().equals(code)) {
                    Dialog dialog = new Dialog(IpCode.this);
                    dialog.setCancelable(false);
                    dialog.setContentView(R.layout.load_dialog);
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    LAccount.setValue(variable.account).addOnCompleteListener(task -> {
                        if (task.isComplete()) {
                            LUser.setValue(user).addOnCompleteListener(task1 -> {
                                if (task1.isComplete())
                                {
                                    dialog.dismiss();
                                    Toast.makeText(IpCode.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(IpCode.this, MainActivity.class));
                                    finishAffinity();
                                }
                            });
                        }
                    });

                } else
                {
                    Toast.makeText(IpCode.this, "Mã xác nhận chưa chính xác", Toast.LENGTH_SHORT).show();
                }
            });

        // click bt gửi lại
        countDownTime();
        codeBinding.sendTo.setOnClickListener(v -> {
           code = MyFunction.VerificationEmail(variable.account.getEmail());
           countDownTime();
        });
        codeBinding.backIpCode.setOnClickListener(v -> onBackPressed());
        }



    public void countDownTime()
    {
        TextView text = codeBinding.sendTo;
        text.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.default_color));
        CountDownTimer countDownTimer = new CountDownTimer(60000,1000)
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {

                text.setText("Gửi lại mã sau: "+(millisUntilFinished/1000));
            }

            @Override
            public void onFinish()
            {
               text.setText("Gửi lại");
               text.setTextColor(getResources().getColor(R.color.blue));
            }
        };
        countDownTimer.start();
    }


}