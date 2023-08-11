package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.Activity.MessageActivity;
import com.example.appchat.Object.Friend;
import com.example.appchat.Object.Message;
import com.example.appchat.R;
import com.example.appchat.databinding.ActivityMessageBinding;
import com.example.appchat.databinding.ItMessageBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MessageAD extends RecyclerView.Adapter<MessageAD.MessHolder>
{
   Message message;
   Context context;
   Dialog dialog;
   ActivityMessageBinding messageBinding;
   public static int positionIcon = 0; // để xác định xem tin nhắn số mấy thả icon
   public static List<Integer> LSelectMess = new ArrayList<>();

   public static boolean checkSelect = false;
    public MessageAD(Message message, Context context, Dialog dialog
            , ActivityMessageBinding messageBinding) {
        this.message = message;
        this.context = context;
        this.dialog = dialog;
        this.messageBinding = messageBinding;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void loadMessage(Message message)
    {
        this.message = message;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public MessHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ItMessageBinding mess = ItMessageBinding.inflate(LayoutInflater.from(parent.getContext())
                ,parent,false);
        return new MessHolder(mess);
    }

    @Override
    public void onBindViewHolder(@NonNull MessHolder holder
            , @SuppressLint("RecyclerView") int position)
    {
        if(checkSelect)
        {
           CheckBox radioButton = holder.messageBinding.btSelectMess;
           radioButton.setVisibility(View.VISIBLE);
           radioButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(radioButton.isChecked())
                   {
                       LSelectMess.add(position);
                   }
                   else
                   {
                       for(int i = 0;i<LSelectMess.size();i++)
                       {
                           if(LSelectMess.get(i) == position)
                           {
                               LSelectMess.remove(i);
                               break;
                           }
                       }
                   }
               }
           });
        }
        else
        {
            holder.messageBinding.btSelectMess.setVisibility(View.GONE);
        }
        List<Message.TN> Ltn = message.getMessage();
        Message.TN tn = Ltn.get(position);
        String tnDi = tn.getTndi(), tnDen = tn.getTnden();
        String timeDen = tn.getTimeden(), timeDi = tn.getTimedi();
        Friend friend = message.getFriend();
        String avt  = friend.getAvt();
        String emojiDen = tn.getEmojiden();
        String emojiDi = tn.getEmojidi();
        if(tnDi == null && tnDen == null)
        {
            holder.messageBinding.layoutDi.setVisibility(View.GONE);
            holder.messageBinding.layoutDen.setVisibility(View.GONE);
            holder.messageBinding.cardAvtDen.setVisibility(View.GONE);
            holder.messageBinding.cardEmojiDen.setVisibility(View.GONE);
            holder.messageBinding.cardEmojiDi.setVisibility(View.GONE);
        }
        else
        if(tnDi == null)
        {
            holder.messageBinding.layoutParent.setVisibility(View.VISIBLE);
                if (avt == null) {
                    Glide.with(context).load(R.drawable.img_girl_1).centerCrop()
                            .into(holder.messageBinding.avtDen);
                } else {
                    Glide.with(context).load(avt).centerCrop()
                            .into(holder.messageBinding.avtDen);
                }
                holder.messageBinding.layoutDi.setVisibility(View.GONE);
                holder.messageBinding.cardAvtDen.setVisibility(View.VISIBLE);
                holder.messageBinding.layoutDen.setVisibility(View.VISIBLE);
                holder.messageBinding.tnDen.setText(tnDen);
                holder.messageBinding.cardEmojiDen.setVisibility(View.VISIBLE);
                holder.messageBinding.cardEmojiDi.setVisibility(View.GONE);
                holder.messageBinding.timeDen.setText(timeDen);
                if (emojiDen != null) {
                    Glide.with(context).load(emojiDen).centerCrop().into(holder.messageBinding.emoji);
                } else {
                    holder.messageBinding.cardEmojiDen.setVisibility(View.GONE);
                }
        }
        else
        if(tnDen == null)
        {
               holder.messageBinding.layoutParent.setVisibility(View.VISIBLE);
                holder.messageBinding.tnDi.setText(tnDi);
                holder.messageBinding.cardAvtDen.setVisibility(View.GONE);
                holder.messageBinding.cardEmojiDen.setVisibility(View.GONE);
                holder.messageBinding.layoutDen.setVisibility(View.GONE);
                holder.messageBinding.layoutDi.setVisibility(View.VISIBLE);
                holder.messageBinding.cardEmojiDi.setVisibility(View.VISIBLE);
                holder.messageBinding.timeDi.setText(timeDi);
                if (emojiDi != null) {
                    Glide.with(context).load(emojiDi).centerCrop().into(holder.messageBinding.emojiDi);
                } else {
                    holder.messageBinding.cardEmojiDi.setVisibility(View.GONE);
                }
        }


       holder.messageBinding.layoutDen.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               positionIcon = position;
               setDialog(tnDen);
               selectMessage(holder);
               return true;
           }
       });
    }


    @Override
    public int getItemCount()
    {
        if(message != null)
        {
            if(message.getMessage() != null)
            {
                return message.getMessage().size();
            }
        }
        return 0;
    }

   public void setDialog(String tnDen)
   {
       TextView messageDialog = dialog.findViewById(R.id.message_dialog);
       messageDialog.setText(tnDen);
       dialog.show();
   }
    @SuppressLint("NotifyDataSetChanged")
    public void selectMessage(MessHolder holder) // click vào nút chọn  nhiều tin nhắn
    {
        LinearLayout select = dialog.findViewById(R.id.bt_select);
        select.setOnClickListener(v -> {
            dialog.dismiss();
            MessageActivity.HideLayout(messageBinding);
            checkSelect = true;
            notifyDataSetChanged();
        });

    }

    public static class MessHolder extends RecyclerView.ViewHolder{

        ItMessageBinding messageBinding;
        public MessHolder(@NonNull ItMessageBinding itemView) {
            super(itemView.getRoot());
            messageBinding = itemView;
        }
    }
}
