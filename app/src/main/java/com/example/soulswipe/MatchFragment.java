package com.example.soulswipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MatchFragment extends Fragment {
    private List<CardModel> matchList = new ArrayList<>();
    private List<ConversationModel> conversationList = new ArrayList<>();

    public MatchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_match, container, false);
        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false));
        MatchAdapter matchAdapter = MatchAdapter.getInstance(matchList, getContext());
        recyclerView.setAdapter(matchAdapter);
        db.collection("matches").where(Filter.and(Filter.equalTo("user1Like",true),Filter.equalTo("user2Like",true),Filter.or(Filter.equalTo("user1",userUid),Filter.equalTo("user2",userUid))))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            QueryDocumentSnapshot document = dc.getDocument();
                            String matchedUid = document.getString("user1");
                            if (matchedUid.equals(userUid)) {
                                matchedUid = document.getString("user2");
                            }
                            db.collection("users").document(matchedUid).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document1 = task1.getResult();
                                    if (document1.exists()) {
                                        Log.d("TAG", "DocumentSnapshot data: " + document1.getData());
                                        matchAdapter.addItem(new CardModel(
                                                document1.getString("name"),
                                                "https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                                        document1.get("picture").toString() + "?alt=media", document1.getId()));
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task1.getException());
                                }
                            });
                        }
                    }
                });

        RecyclerView conversationRecycler = view.findViewById(R.id.conversationRecycler);
        conversationRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        ConversationAdapter conversationAdapter = ConversationAdapter.getInstance(conversationList, getContext());
        conversationRecycler.setAdapter(conversationAdapter);

        db.collection("conversations").where(Filter.and(Filter.notEqualTo("lastMessage",null),Filter.or(Filter.equalTo("user1",userUid),Filter.equalTo("user2",userUid))))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("TAG", "Listen failed.", e);
                        return;
                    }

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if(dc.getType() == DocumentChange.Type.ADDED) {
                            QueryDocumentSnapshot document = dc.getDocument();
                            String matchedUid = document.getString("user1");
                            if (matchedUid.equals(userUid)) {
                                matchedUid = document.getString("user2");
                            }
                            db.collection("users").document(matchedUid).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    DocumentSnapshot document1 = task1.getResult();
                                    if (document1.exists()) {
                                        Log.d("TAG", "DocumentSnapshot data: " + document1.getData());
                                        CardModel matchedUser = new CardModel(
                                                document1.getString("name"),
                                                "https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                                        document1.get("picture").toString() + "?alt=media", document1.getId());

                                        conversationAdapter.addItem(new ConversationModel(
                                                document1.getString("name"),
                                                document.getString("lastMessage"),
                                                document.getTimestamp("timestamp"), "https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                                document1.get("picture").toString() + "?alt=media",
                                                matchedUser,document.getId(),
                                                document.getString("sender"),
                                                document.getBoolean("user1Viewed"),
                                                document.getBoolean("user2Viewed")));
                                    }
                                } else {
                                    Log.d("TAG", "get failed with ", task1.getException());
                                }
                            });
                        }
                        else if (dc.getType() == DocumentChange.Type.MODIFIED) {
                            QueryDocumentSnapshot document = dc.getDocument();
                            conversationAdapter.updateItem(document.getId(),document.getString("lastMessage"),document.getBoolean("user1Viewed"),document.getBoolean("user2Viewed"),document.getString("sender"));
                        }
                    }
                });

        return view;
    }

}