package com.example.soulswipe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<MessageModel> messageList;
    private Context context;
    private String imageUrl;
    private static MessageAdapter instance;

    public MessageAdapter(List<MessageModel> messages, Context context){
        this.context = context;
        messageList = messages;
        notifyDataSetChanged();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public static synchronized MessageAdapter getInstance(List<MessageModel> messageList, Context context){
        if(instance == null){
            instance = new MessageAdapter(messageList, context);
        }
        return instance;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView left_message,right_message;
        private LinearLayout left_layout;
        private ImageView leftImg;
        public ViewHolder(View view) {
            super(view);
            left_message = view.findViewById(R.id.left_message);
            right_message = view.findViewById(R.id.right_message);
            left_layout = view.findViewById(R.id.left_layout);
            leftImg = view.findViewById(R.id.leftImg);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        MessageModel message = messageList.get(position);
        if (message.getSender().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            holder.right_message.setText(message.getMessage());
            holder.right_message.setVisibility(View.VISIBLE);
            holder.left_layout.setVisibility(View.GONE);
            holder.right_message.setBackgroundResource(R.drawable.right_message);
        } else {
            holder.left_message.setText(message.getMessage());
            holder.left_layout.setVisibility(View.VISIBLE);
                Glide.with(context)
                .load(imageUrl)
                .into(holder.leftImg);
            holder.right_message.setVisibility(View.GONE);
            holder.left_message.setBackgroundResource(R.drawable.left_message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void addItem(MessageModel message){
        messageList.add(message);
        messageList.sort(Comparator.comparing(MessageModel::getTimestamp));
        notifyDataSetChanged();
    }

    public void clearMessages(){
        messageList = new ArrayList<>();
    }

    public void clearList(){
        instance = null;
    }
}
