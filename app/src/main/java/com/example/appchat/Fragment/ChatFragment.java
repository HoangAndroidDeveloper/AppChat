package com.example.appchat.Fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.appchat.Adapter.UserChat;
import com.example.appchat.Object.Message;
import com.example.appchat.Object.User;
import com.example.appchat.databinding.FragmentChatBinding;
import com.example.appchat.variable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment
{
    private DatabaseReference getLAcc;
    private List<Message> LMessage;
    private List<Message> LMessage2;
    FragmentChatBinding chatBinding;
    Context context;
    UserChat userChat;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        chatBinding = FragmentChatBinding.inflate(inflater);
        assert container != null;
        context = container.getContext();
        if (context != null)
        {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
            userChat = new UserChat(context, LMessage);
            chatBinding.rUserChat.setLayoutManager(layoutManager);
            chatBinding.rUserChat.setAdapter(userChat);
            getLMessage();
            chatBinding.btSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return false;
                }
            });
        }
        return chatBinding.getRoot();

    }

    private void search(String txt) {

        if(txt.trim().isEmpty())
        {
            LMessage = LMessage2;
        }
        else
        {
            List<Message> messages = new ArrayList<>();
            for(Message message : LMessage2)
            {
                String name = message.getFriend().getName();
                if(name.toLowerCase().contains(txt.toLowerCase()))
                {
                    messages.add(message);
                }

            }
            LMessage = messages;

        }
        userChat.LoadUserChat(LMessage);

    }


    public void getLMessage()
    {
        LMessage = new ArrayList<>();
        getLAcc = FirebaseDatabase.getInstance()
                .getReference("LMessage/"+ variable.account.getId());
        getLAcc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                LMessage.clear();
                for(DataSnapshot sn:snapshot.getChildren())
                {
                    Message message = sn.getValue(Message.class);
                    LMessage.add(message);
                }
                Handler handler = new Handler();
                LMessage2 = LMessage;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userChat.LoadUserChat(LMessage);
                    }
                },1400);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

}