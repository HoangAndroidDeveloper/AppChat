package com.example.appchat.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appchat.R;

public class EmojiAdapter extends RecyclerView.Adapter<EmojiAdapter.EHolder>
{
    String [] LEmoji ;
    Context context;
    interfaceEmoji Click;


    public interface interfaceEmoji {
        public  void click(int position);
    }
    public EmojiAdapter(String[] LEmoji, Context context, interfaceEmoji Click)
    {
        this.LEmoji = LEmoji;
        this.context = context;
        this.Click = Click;
    }

    @NonNull
    @Override
    public EHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emoji,parent,false);
        return new EHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EHolder holder, @SuppressLint("RecyclerView") int position)
    {
        Glide.with(context).load(LEmoji[position]).centerCrop().into(holder.emoji);
        holder.emoji.setOnClickListener(view ->
                Click.click(position)
        );
    }

    @Override
    public int getItemCount() {
        if(LEmoji != null)
         return LEmoji.length;
        return 0;
    }

    public static class EHolder extends RecyclerView.ViewHolder {
        ImageView emoji;
        public EHolder(@NonNull View itemView) {
            super(itemView);
            emoji = itemView.findViewById(R.id.emoji);
        }
    }
}
