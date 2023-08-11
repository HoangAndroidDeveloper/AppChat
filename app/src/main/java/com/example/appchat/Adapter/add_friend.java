package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.databinding.ItAddFriendBinding;
import com.example.appchat.variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class add_friend extends RecyclerView.Adapter<add_friend.AddHolder>
{
    Context context;
    List<Friend> LRequest;
    List<Friend> LRequest2;


    @SuppressLint("NotifyDataSetChanged")
    public void LoadRequest(List<Friend> LRequest)
    {
        this.LRequest = LRequest;
        notifyDataSetChanged();
    }

    public add_friend(Context context, List<Friend> LRequest) {
        this.context = context;
        this.LRequest = LRequest;
        LRequest2 = LRequest;
    }

    @NonNull
    @Override
    public AddHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ItAddFriendBinding itAddFriendBinding = ItAddFriendBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new AddHolder(itAddFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AddHolder holder, int position)
    {
        Friend friend = LRequest.get(position);
        String avt = friend.getAvt();
        String name = friend.getName();
        if(friend.getAvt() == null)
        {
            Glide.with(context).load(R.drawable.img_girl_1).centerCrop().into(holder.addFriendBinding.avt);
        }
        else
        {
            Glide.with(context).load(avt).centerCrop().into(holder.addFriendBinding.avt);
        }
        holder.addFriendBinding.name.setText(name);
        holder.addFriendBinding.btAccept.setOnClickListener(v -> {
                        LRequest.remove(position);
                        Delete(variable.account.getId(), friend.getId(), friend);
        });
    }

    @Override
    public int getItemCount()
    {
        if(LRequest != null) {
            return LRequest.size();
        }
        return 0;
    }




    public static class AddHolder extends RecyclerView.ViewHolder {
        ItAddFriendBinding addFriendBinding;
        public AddHolder(@NonNull ItAddFriendBinding itemView) {
            super(itemView.getRoot());
            addFriendBinding = itemView;
        }
    }
    public void Delete(long idAc,long id, Friend friend) // xóa các lời mời kết bạn khi đã xác nhận hoặc từ chối
    {
        User user = variable.user;
        Friend friend1 = new Friend(user.getId(),user.getName(), user.getAvatar());
        DatabaseReference put = FirebaseDatabase.getInstance()
                .getReference("RequestFriend/"+variable.account.getId());
        put.setValue(LRequest).addOnCompleteListener(task ->
        {
            DatabaseReference put12 = FirebaseDatabase.getInstance()
                    .getReference("Friend/"+idAc+"/"+id);
            put12.setValue(friend);
            DatabaseReference put1 = FirebaseDatabase.getInstance()
                    .getReference("Friend/"+id+"/"+idAc);
            put1.setValue(friend1);
        });
    }

}
