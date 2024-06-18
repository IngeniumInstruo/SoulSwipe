package com.example.soulswipe;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.ViewHolder>{
    private List<CardModel> matchList;
    private Context context;
    private static MatchAdapter instance;

    public MatchAdapter(List<CardModel> matches, Context context){
        this.context = context;
        matchList = matches;
        notifyDataSetChanged();
    }
    public static synchronized MatchAdapter getInstance(List<CardModel> matchList, Context context){
        if(instance == null){
            instance = new MatchAdapter(matchList, context);
        }
        return instance;
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView matchImg;
        private TextView nameTxt;
        public ViewHolder(View view) {
            super(view);
            matchImg = view.findViewById(R.id.matchImg);
            nameTxt = view.findViewById(R.id.nameTxt);
        }
    }

    @NonNull
    @Override
    public MatchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchAdapter.ViewHolder holder, int position) {
        CardModel match = matchList.get(position);
        holder.nameTxt.setText(match.getTitle());
        Glide.with(context)
            .load(match.getImage())
            .into(holder.matchImg);
        holder.matchImg.setOnClickListener((view)->{
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("user",match);
            context.startActivity(intent);
        });
    }

    public void addItem(CardModel match){
        matchList.add(match);
        notifyDataSetChanged();
    }

    public void clearList(){
        instance = null;
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

}
