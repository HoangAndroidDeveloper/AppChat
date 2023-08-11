package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Activity.MessageActivity;
import com.example.appchat.Activity.Profile;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.Message;
import com.example.appchat.R;
import com.example.appchat.databinding.ItFriendBinding;

import java.util.ArrayList;
import java.util.List;

public class Friend_AD extends RecyclerView.Adapter<Friend_AD.FViewHolder> implements Filterable
{

    Context context;
    List<Friend> LFriend;
    List<Friend> LFriend2;

    public Friend_AD(Context context, List<Friend> LFriend) {
        this.context = context;
        this.LFriend = LFriend;LFriend2 = LFriend;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void LoadFriend(List<Friend> LFriend)
    {
        this.LFriend = LFriend;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public FViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItFriendBinding itFriendBinding  = ItFriendBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new FViewHolder(itFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FViewHolder holder, int position) {
           Friend friend = LFriend.get(position);
           String avt = friend.getAvt();
           String name = friend.getName();
           if(avt == null)
           Glide.with(context).load(R.drawable.img_girl_1).centerCrop().into(holder.itFriendBinding.avt);
           else
            Glide.with(context).load(avt).centerCrop().into(holder.itFriendBinding.avt);
           holder.itFriendBinding.name.setText(name);
           holder.itFriendBinding.btChat.setOnClickListener(v -> {
               Intent it = new Intent(context, MessageActivity.class);
               Bundle bundle = new Bundle();
               bundle.putSerializable("Friend", friend); // truyền friend qua để nhập thông tin cho user
               it.putExtra("bundle",bundle);
               context.startActivity(it);
           });
           holder.itFriendBinding.btProfile.setOnClickListener(v ->
           {
               Intent it = new Intent(context, Profile.class);
               Bundle bundle = new Bundle();
               bundle.putSerializable("friend",friend);
               it.putExtra("bundle",bundle);
               context.startActivity(it);
           });

    }

    @Override
    public int getItemCount()
    {
        if(LFriend != null)
        {
            return LFriend.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String txt = constraint.toString();
                if(txt.isEmpty())
                {
                    LFriend = LFriend2;
                }
                else
                {
                    List<Friend> friendList = new ArrayList<>();
                    for(Friend friend : LFriend2)
                    {
                        String name = friend.getName();
                        if(name.toLowerCase().contains(txt.toLowerCase()))
                        {
                            friendList.add(friend);
                        }
                        LFriend = friendList;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = LFriend;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                LFriend = (List<Friend>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class FViewHolder extends RecyclerView.ViewHolder {
        ItFriendBinding itFriendBinding;
        public FViewHolder(@NonNull ItFriendBinding itemView) {
            super(itemView.getRoot());
            itFriendBinding = itemView;
        }
    }
}
