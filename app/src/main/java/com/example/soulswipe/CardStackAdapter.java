package com.example.soulswipe;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<CardModel> items;
    private Context context;
    private static CardStackAdapter instance;

    public static synchronized CardStackAdapter getInstance(List<CardModel> items, Context context){
        if(instance == null){
            instance = new CardStackAdapter(items,context);
        }
        return instance;
    }

    public CardStackAdapter(List<CardModel> items, Context context) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardModel item = items.get(position);
        holder.title.setText(item.getTitle());
        Glide.with(context)
            .load(item.getImage())
            .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateInterest(String interest){
        items = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("gender", interest).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (!document.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                items.add(new CardModel(
                                        document.getString("name"),
                                        "https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                                document.get("picture").toString() + "?alt=media",document.getId()));
                            }
                        }
                        notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void clearList(){
        instance = null;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            image = view.findViewById(R.id.image);
        }
    }
}
