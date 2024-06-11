package com.example.soulswipe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardFragment extends Fragment {
    public CardFragment() {
    }
    private Context context;
    private CardStackView cardStackView;
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private ImageButton nopeBtn,likeBtn;
    private List<CardModel> items = new ArrayList<>();
    private static CardFragment cardFragment;
    FirebaseFirestore db;
    public static CardFragment getInstance(){
        if(cardFragment == null){
            cardFragment = new CardFragment();
        }
        return cardFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);

        db = FirebaseFirestore.getInstance();
        cardStackView = view.findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(context, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }

            @Override
            public void onCardSwiped(Direction direction) {
                if((direction == Direction.Right) && items.size() > manager.getTopPosition()-1){

                    String matchUid = FirebaseAuth.getInstance().getCurrentUser().getUid()+items.get(manager.getTopPosition()-1).getUid();

                    DocumentReference matchRef = db.collection("matches").document(items.get(manager.getTopPosition()-1).getUid()+FirebaseAuth.getInstance().getCurrentUser().getUid());
                    matchRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    matchRef.update("user2Like",true)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG", "DocumentSnapshot successfully updated!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error updating document", e);
                                                }
                                            });
                                } else {
                                    Map<String, Object> match = new HashMap<>();
                                    match.put("user1",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                    match.put("user2",items.get(manager.getTopPosition()-1).getUid());
                                    match.put("user1Like",true);
                                    match.put("user2Like",false);


                                    db.collection("matches").document(matchUid)
                                            .set(match)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("TAG", "Error adding document", e);
                                                }
                                            });
                                }
                            } else {
                                Log.d("TAG", "get failed with ", task.getException());
                            }
                        }
                    });



                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        adapter = new CardStackAdapter(items, context);
        manager.setStackFrom(StackFrom.Bottom);
        cardStackView.setLayoutManager(manager);
        cardStackView.setAdapter(adapter);


        SwipeAnimationSetting rightSetting = new SwipeAnimationSetting.Builder().setDirection(Direction.Right).build();
        SwipeAnimationSetting leftSetting = new SwipeAnimationSetting.Builder().setDirection(Direction.Left).build();

        nopeBtn = view.findViewById(R.id.nopeButton);
        nopeBtn.setOnClickListener((View v)->{
            manager.setSwipeAnimationSetting(leftSetting);
            cardStackView.swipe();
        });

        likeBtn = view.findViewById(R.id.likeButton);
        likeBtn.setOnClickListener((View v)->{
            manager.setSwipeAnimationSetting(rightSetting);
            cardStackView.swipe();
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        db = FirebaseFirestore.getInstance();
        createCards();
    }

    public void updateInterest(){
        items = new ArrayList<>();
        createCards();
    }

    private void createCards() {
        CollectionReference usersRef = db.collection("users");

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user",Context.MODE_PRIVATE);
        String interest = sharedPref.getString("interest", null);

        usersRef.whereEqualTo("gender", interest).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            items.add(new CardModel(
                                    document.getString("name"),
                                    "https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                            document.get("picture").toString() + "?alt=media",document.getId()));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.getException());
                    }
                });
    }
}