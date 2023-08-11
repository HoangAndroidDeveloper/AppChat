package com.example.appchat.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.appchat.Adapter.EmojiAdapter;
import com.example.appchat.Adapter.MessageAD;
import com.example.appchat.Object.Account;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.Message;
import com.example.appchat.Object.MyNotification;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.Service.NotificationService;
import com.example.appchat.checkNetwork;
import com.example.appchat.databinding.ActivityMessageBinding;
import com.example.appchat.variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class MessageActivity extends AppCompatActivity
{
    ActivityMessageBinding messageBinding;
    public static String [] LEmoji;
    Message message , message2 ; // tin nhắn
    Friend friend;
    MessageAD messageAD ; // message adapter
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        messageBinding = ActivityMessageBinding.inflate(getLayoutInflater());
        setContentView(messageBinding.getRoot());
        setDialog();
        checkNetwork.context = this;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetwork check = new checkNetwork();
        registerReceiver(check,intentFilter);
        messageBinding.btBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Intent it = getIntent();
        Bundle bd = it.getBundleExtra("bundle");
        friend = (Friend) bd.getSerializable("Friend"); // lấy friend để set thông tin user
        if(friend.getAvt() != null)
        {
            Glide.with(this).load(friend.getAvt()).centerCrop().into(messageBinding.avt);
        }
        else
        {
            Glide.with(this).load(R.drawable.img_girl_1).centerCrop().into(messageBinding.avt);
        }
        // set message adapter
        messageBinding.rMessage.setLayoutManager(new LinearLayoutManager(this));
        messageAD = new MessageAD(message,this,dialog, messageBinding);
        messageBinding.rMessage.setAdapter(messageAD);
        getMess(friend);
        User user = variable.user;
        Friend friend1 = new Friend(variable.account.getId(),user.getName(),user.getAvatar());
        getMess2(friend.getId());
        messageBinding.nameMessage.setText(friend.getName());

        // click bt call
        messageBinding.btCall.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MessageActivity.this, CallActivity.class);
                startActivity(it);
            }
        });
        LEmoji = getResources().getStringArray(R.array.LEmoji); // lấy ds emoji
        // click vào nút nhắn tin
        messageBinding.btSend.setOnClickListener(v ->
        {
            String tnDi = messageBinding.ipMessage.getText().toString();
            messageBinding.ipMessage.setText("");
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            String timeDi = simpleDateFormat.format(new Date());
            sendMessage(tnDi,friend1,timeDi);
            putNotification(friend1, tnDi,timeDi);
        });
        messageBinding.btClose.setOnClickListener(v -> {
           HideLayout();
        });
        // click nút delete
        messageBinding.btDelete.setOnClickListener(v -> {
            HideLayout();
            for(int i = 0;i<MessageAD.LSelectMess.size();i++)
            {
                Message.TN tn = message.getMessage().get(MessageAD.LSelectMess.get(i));
                tn.setTndi(null);
                tn.setTnden(null);
            }
            MessageAD.LSelectMess.clear();
            DatabaseReference putMess = FirebaseDatabase.getInstance()
                    .getReference("LMessage/"+friend1.getId()+"/"+friend.getId());
            putMess.setValue(message);
        });
    }
    @SuppressLint("NotifyDataSetChanged")
    public void HideLayout()
    {
        messageBinding.layoutBottom.setVisibility(View.GONE);
        messageBinding.layoutClose.setVisibility(View.GONE);
        messageBinding.layoutSend.setVisibility(View.VISIBLE);
        messageBinding.layoutIF.setVisibility(View.VISIBLE);
        MessageAD.checkSelect = false;
        messageAD.notifyDataSetChanged();
    }

    private void putNotification(Friend friend, String tnDi,String time)
    {
        MyNotification notification = new MyNotification(tnDi,time,friend.getAvt(),friend.getName());
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Notification/"+this.friend.getId());
        reference.setValue(notification);
    }

    public void sendMessage(String tnDi, Friend friend2, String time) // gửi các tin nhắn lên firebase
    {
        Account ac = variable.account;
        long id = ac.getId();
        long id2 = friend.getId();
        putMess(id,id2,friend,message,tnDi
                ,null,null,time,null,null);
        putMess(id2,id,friend2,message2,null
                ,tnDi,time,null,null,null);
    }
    public void getMess(Friend friend)//  tin nhắn
    {
        DatabaseReference putMess = FirebaseDatabase.getInstance()
                .getReference("LMessage/"+ variable.account.getId()+"/"+friend.getId());
        putMess.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                message = snapshot.getValue(Message.class);
                getAvt();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    public void getMess2(long id) // lấy tin nhắn của user nhận
    {
        DatabaseReference putMess = FirebaseDatabase.getInstance()
                .getReference("LMessage/"+id+"/"+variable.account.getId());
        putMess.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                message2 = snapshot.getValue(Message.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }
    public void putMess(long idSend, long idReceive, Friend friend,Message message
            , String tnDi, String tnDen, String timeDen, String timeDi,String iconDen, String iConDi)
    {
        DatabaseReference putMess = FirebaseDatabase.getInstance()
                .getReference("LMessage/"+idSend+"/"+idReceive);
        if(message == null)
        {
            message = new Message();
        }
        message.setFriend(friend);
        if(message.getMessage() == null)
        {
            message.setMessage(new ArrayList<>());
        }
        Message.TN tn = new Message.TN(tnDen,tnDi,timeDen,timeDi,iconDen,iConDi);
        message.getMessage().add(tn);
        putMess.setValue(message);
    }

    public void getAvt() // lấy avatar của người nhận
    {
        if (message != null) {
            Friend friend = this.friend;
            long id = friend.getId();
            DatabaseReference get = FirebaseDatabase.getInstance().getReference("LUser/" + id);
            get.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if(user != null)
                    {
                        if (user.getAvatar() != null)
                        {
                            String avt = user.getAvatar();
                            if(!avt.equals(friend.getAvt()))
                            {
                                friend.setAvt(avt);
                                DatabaseReference putAvt = FirebaseDatabase.getInstance()
                                        .getReference("Friend/" + variable.account.getId() + "/" + id);
                                putAvt.setValue(friend);
                                Glide.with(MessageActivity.this).load(avt).centerCrop()
                                        .into(messageBinding.avt);
                            }
                        }
                    }
                    messageBinding.rMessage.smoothScrollToPosition(message.getMessage().size()-1);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                           messageAD.loadMessage(message);
                        }
                    },1000);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    EmojiAdapter.interfaceEmoji click = new EmojiAdapter.interfaceEmoji()
    {
        @Override
        public void click(int position)
        {
            int positionIcon = MessageAD.positionIcon;
            long id = variable.account.getId();
            DatabaseReference putIcon = FirebaseDatabase.getInstance()
                    .getReference("LMessage/"+id+"/"+friend.getId());
            Message.TN tn = message.getMessage().get(positionIcon);
            tn.setEmojiden(LEmoji[position]);
            putIcon.setValue(message);
            DatabaseReference putIcon2 = FirebaseDatabase.getInstance()
                    .getReference("LMessage/"+friend.getId()+"/"+id);
            Message.TN tn2 = message2.getMessage().get(positionIcon);
            tn2.setEmojidi(LEmoji[position]);
            putIcon2.setValue(message2).addOnCompleteListener(task ->
            {
                if(task.isComplete())
                 Toast.makeText(MessageActivity.this,"put success",Toast.LENGTH_SHORT).show();
            });
            dialog.dismiss();
        }
    };

    public Dialog dialog;

        public void setDialog()
        {
            dialog = new Dialog(MessageActivity.this);
            dialog.setContentView(R.layout.emoji_dialog);
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            RecyclerView rEmoji = dialog.findViewById(R.id.r_Emoji);
            rEmoji.setLayoutManager(new LinearLayoutManager(MessageActivity.this
                    ,LinearLayoutManager.HORIZONTAL,false));
            EmojiAdapter emojiAdapter = new EmojiAdapter(MessageActivity.LEmoji
                    ,MessageActivity.this,click);
            rEmoji.setAdapter(emojiAdapter);
        }

    public static void HideLayout(ActivityMessageBinding messageBinding) // ẩn hiện các chức năng khi click vào icon trong dialog
    {
        messageBinding.layoutBottom.setVisibility(View.VISIBLE);
        messageBinding.layoutClose.setVisibility(View.VISIBLE);
        messageBinding.layoutSend.setVisibility(View.GONE);
        messageBinding.layoutIF.setVisibility(View.GONE);
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