package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Activity.MessageActivity;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.Message;
import com.example.appchat.R;
import com.example.appchat.databinding.ItemUserChatBinding;

import java.util.ArrayList;
import java.util.List;

public class UserChat extends RecyclerView.Adapter<UserChat.UViewHolder>
{

    private final Context context;
    private List<Message> LMessage;
    private List<Message> LMessage2;
    public UserChat(Context context, List<Message> LMessage) {
        this.context = context;
        this.LMessage = LMessage;
        this.LMessage2 = LMessage;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void LoadUserChat(List<Message> LMessage)
    {
        this.LMessage = LMessage;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserChatBinding chatBinding = ItemUserChatBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UViewHolder(chatBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UViewHolder holder, int position) {

        if (LMessage != null) {
            if (LMessage.size() > 0)
            {
                Message message = LMessage.get(position);
                List<Message.TN> LTN = message.getMessage();
                Message.TN TN = LTN.get(LTN.size() - 1);
                Friend friend = message.getFriend();
                String avt = friend.getAvt();
                String name = friend.getName();
                holder.chatBinding.shimmerView.hideShimmer();
                if (avt == null)
                    Glide.with(context).load(R.drawable.img_girl_1).centerCrop().into(holder.chatBinding.avtUser);
                else {
                    Glide.with(context).load(avt).centerCrop().into(holder.chatBinding.avtUser);
                }
                holder.chatBinding.avtUser.setBackgroundColor(Color.WHITE);
                if (TN.getTnden() == null) {
                    holder.chatBinding.messageEnd.setText(TN.getTndi());
                } else {
                    holder.chatBinding.messageEnd.setText(TN.getTnden());
                }
                holder.chatBinding.messageEnd.setBackgroundColor(Color.WHITE);
                holder.chatBinding.nameChat.setText(name);
                holder.chatBinding.nameChat.setBackgroundColor(Color.WHITE);
                holder.chatBinding.avtUser.setOnClickListener(v -> {
                    Intent it = new Intent(context, MessageActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("Friend", friend);
                    it.putExtra("bundle", bundle);
                    context.startActivity(it);
                });
            }

        }
        if (LMessage == null )
        {
            holder.chatBinding.shimmerView.startShimmer();
            holder.chatBinding.messageEnd.setBackgroundColor(context.getResources().getColor(R.color.load_color));
            holder.chatBinding.nameChat.setBackgroundColor(context.getResources().getColor(R.color.load_color));
            holder.chatBinding.avtUser.setBackgroundColor(context.getResources().getColor(R.color.load_color));
        }
    }

    @Override
    public int getItemCount() {
        if(LMessage != null )
        {
            if(LMessage.size() > 0)
             return LMessage.size();
        }
        return 5;
    }



    public static class UViewHolder extends RecyclerView.ViewHolder {
              ItemUserChatBinding chatBinding;
        public UViewHolder(@NonNull ItemUserChatBinding itemView) {

            super(itemView.getRoot());
            this.chatBinding = itemView;
        }
    }
}
