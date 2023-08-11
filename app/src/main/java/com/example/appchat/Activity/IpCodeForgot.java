package com.example.appchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appchat.MyFunction;
import com.example.appchat.Object.Account;
import com.example.appchat.R;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.ActivityIpCodeForgotBinding;
import com.example.appchat.variable;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class IpCodeForgot extends AppCompatActivity {
    String code;
    ActivityIpCodeForgotBinding forgotBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forgotBinding = ActivityIpCodeForgotBinding.inflate(getLayoutInflater());
        setContentView(forgotBinding.getRoot());
//



        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        Intent it = getIntent();
        String email = it.getStringExtra("email");
        countDownTime();
        code = MyFunction.VerificationEmail(email);
        // click nút back
        forgotBinding.backIpCode.setOnClickListener(v -> onBackPressed());

        //  click nút gửi lại mã
        forgotBinding.sendTo.setOnClickListener(v -> {
            countDownTime();
            code = MyFunction.VerificationEmail(email);
        });

        forgotBinding.btXacNhan.setBackgroundColor(getResources().getColor(R.color.view_color));
        forgotBinding.pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @SuppressLint("ResourceAsColor")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    forgotBinding.btXacNhan.setEnabled(true);
                    forgotBinding.btXacNhan.setBackgroundColor(getColor(R.color.blue));
                } else {
                    forgotBinding.btXacNhan.setEnabled(false);
                    forgotBinding.btXacNhan.setBackgroundColor(getColor(R.color.view_color));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // click bt xác nhận
        forgotBinding.btXacNhan.setOnClickListener(v -> {

            if (code.equals(Objects.requireNonNull(forgotBinding.pinView.getText()).toString())) {
                getPass(email);
            }
            else
            {
                Toast.makeText(IpCodeForgot.this,"Mã xác nhận sai",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void countDownTime() {
        TextView text = forgotBinding.sendTo;
        text.setTextColor(getResources().getColor(R.color.default_color));
        text.setEnabled(false);
        CountDownTimer countDownTimer = new CountDownTimer(59000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                text.setText("Gửi lại mã sau: " + (millisUntilFinished / 1000) + "s");
            }

            @Override
            public void onFinish() {
                text.setText("Gửi lại");
                text.setEnabled(true);
                text.setTextColor(getResources().getColor(R.color.blue));
            }
        };
        countDownTimer.start();
    }

    public void sendPass(String pass, String email) // gửi pass về email
    {
        String EmailSender = "trinhviethoang307@gmail.com";
        String password = "ehphxgljxybnsvvk", host = "smtp.gmail.com";
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailSender, password);
            }
        });
        Message mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            mimeMessage.setFrom(new InternetAddress(email));
            mimeMessage.setSubject("Mật kẩu của bạn được gửi từ App chat");
            mimeMessage.setText("Password của bạn là:" + pass);
            Thread thread = new Thread(() -> {
                try {
                    Transport.send(mimeMessage);
                } catch (MessagingException ignored) {
                }
            });
            thread.start();
        } catch (MessagingException ignored) {
        }
    }

    public void getPass(String email) {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.load_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        DatabaseReference get = FirebaseDatabase.getInstance()
                .getReference("LAccount");
        Query query = get.orderByChild("email").equalTo(email);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {

                Account ac = snapshot.getValue(Account.class);
                if(ac != null)
                {
                    variable.account = ac;
                    Intent intent = new Intent(IpCodeForgot.this,ChangePass.class);
                    startActivity(intent);
                    sendPass(ac.getPassword(),ac.getEmail());
                    dialog.dismiss();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}