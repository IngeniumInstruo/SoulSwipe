package com.example.soulswipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder>{
    private List<ConversationModel> conversationList;
    private Context context;
    private static ConversationAdapter instance;
    private HashMap<String, Integer> positionMap = new HashMap<>();
    private FirebaseFirestore db;

    public ConversationAdapter(List<ConversationModel> conversation, Context context){
        db = FirebaseFirestore.getInstance();
        this.context = context;
        conversationList = conversation;
        notifyDataSetChanged();
    }
    public static synchronized ConversationAdapter getInstance(List<ConversationModel> conversationList, Context context){
        if(instance == null){
            instance = new ConversationAdapter(conversationList, context);
        }
        return instance;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView conversationImg;
        private TextView titleConversation,messageConversation;
        private LinearLayout conversationLayout;

        public ViewHolder(View view) {
            super(view);
            conversationImg = view.findViewById(R.id.conversationImg);
            titleConversation = view.findViewById(R.id.title_conversation);
            messageConversation  = view.findViewById(R.id.message_conversation);
            conversationLayout = view.findViewById(R.id.conversation_layout);
        }
    }

    @NonNull
    @Override
    public ConversationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);

        return new ConversationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationAdapter.ViewHolder holder, int position) {
        ConversationModel conversation = conversationList.get(position);
        String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String,String> uidMap = Helper.getInstance().compareUid(authUid,conversation.getMatchedUser().getUid());

        Glide.with(context)
        .load(conversation.getImage())
        .into(holder.conversationImg);
        holder.titleConversation.setText(conversation.getUser());
        holder.titleConversation.setTypeface(null,Typeface.BOLD);
        Map<String, Object> updateConversation = new HashMap<>();

        if(Objects.equals(conversation.getSender(), authUid)){
            holder.messageConversation.setText("You: "+conversation.getLastMessage());
        }else{
            holder.messageConversation.setText(conversation.getLastMessage());
        }
        if(Objects.equals(uidMap.get("user1"), authUid)){
            if(conversation.getUser1Viewed() == false){
                updateConversation.put("user1Viewed", true);
                holder.messageConversation.setTypeface(null,Typeface.BOLD);
            }else{
                holder.messageConversation.setTypeface(null,Typeface.NORMAL);
            }
        }else{
            if(conversation.getUser2Viewed() == false){
                updateConversation.put("user2Viewed", true);
                holder.messageConversation.setTypeface(null,Typeface.BOLD);
            }else{
                holder.messageConversation.setTypeface(null,Typeface.NORMAL);
            }
        }

        holder.conversationLayout.setOnClickListener((view)->{
            db.collection("conversations").document(uidMap.get("objectUid"))
                    .set(updateConversation, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("user",conversation.getMatchedUser());
            context.startActivity(intent);
        });
    }

    public void addItem(ConversationModel conversation){
        positionMap.put(conversation.getUid(),conversationList.size());
        conversationList.add(conversation);
        notifyDataSetChanged();
    }

    public void updateItem(String uid,String lastMessage, Boolean user1Viewed, Boolean user2Viewed, String sender){
        int position = positionMap.get(uid);
        conversationList.get(position).setLastMessage(lastMessage);
        conversationList.get(position).setUser1Viewed(user1Viewed);
        conversationList.get(position).setUser2Viewed(user2Viewed);
        conversationList.get(position).setSender(sender);
        notifyItemChanged(position);
    }

    public void clearList(){
        instance = null;
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }
}
