package com.example.appchat.Adapter;

import static com.example.appchat.R.drawable.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Fragment.FriendFragment;
import com.example.appchat.Object.Account;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.User;
import com.example.appchat.R;
import com.example.appchat.databinding.ItDxFriendBinding;
import com.example.appchat.variable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

public class DeXuat_AD extends RecyclerView.Adapter<DeXuat_AD.DX_Holder>
{
    Context context;
    List<User> LUser;
    List<User> LUser2;
    @SuppressLint("NotifyDataSetChanged")
    public void load (List<User> LUser)
    {
        this.LUser = LUser;
        notifyDataSetChanged();
    }

    public DeXuat_AD(Context context, List<User> LUser) {
        this.context = context;
        this.LUser = LUser;
        LUser2 = LUser;
    }

    @NonNull
    @Override
    public DX_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItDxFriendBinding dxFriendBinding = ItDxFriendBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new DX_Holder(dxFriendBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DX_Holder holder, int position)
    {
        User user = LUser.get(position);
        long id = user.getId();
        String name = user.getName();
        String avt = user.getAvatar();
        if(avt != null)
        Glide.with(context).load(avt).centerCrop().into(holder.dxFriendBinding.avt);
        else
         Glide.with(context).load(img_girl_1).centerCrop().into(holder.dxFriendBinding.avt);
        holder.dxFriendBinding.name.setText(name);
        holder.dxFriendBinding.btAddFriend.setOnClickListener(v -> {
            Account ac = variable.account;
            long idAc = ac.getId();
            User userRequest = variable.user;
            String nameAc = userRequest.getName();
            String avtAc = userRequest.getAvatar();
            DatabaseReference putFriend = FirebaseDatabase.getInstance()
                    .getReference("RequestFriend/"+id+"/"+variable.account.getId());
            Friend friend = new Friend(idAc,nameAc,avtAc);
             putFriend.setValue(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {

                     Glide.with(context).load(img_add_friend_complete).centerCrop()
                             .into(holder.dxFriendBinding.imgAddFriend);
                     User user = new User();
                     user.setId(id);
                     user.setName(name);
                     DatabaseReference put = FirebaseDatabase.getInstance().getReference("MyRequest/"+variable.account.getId()+"/"+id);
                     put.setValue(user); // đưa id user mà bạn đã gửi lời mời, để k hiện thị user đó lên gợi ý nữa
                     User user1 = user;
                     user1.setId(idAc);
                     DatabaseReference put1 = FirebaseDatabase.getInstance().getReference("MyRequest/"+id+"/"+idAc);
                     put1.setValue(user1);
                 }
             });

        });
    }

    @Override
    public int getItemCount()
    {
        if(LUser != null)
        {
            return LUser.size();
        }
        return 0;
    }



    public static class DX_Holder extends RecyclerView.ViewHolder
    {
        ItDxFriendBinding dxFriendBinding;
        public DX_Holder(@NonNull ItDxFriendBinding itemView) {
            super(itemView.getRoot());
            dxFriendBinding = itemView;
        }
    }
}
