package com.example.soulswipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends AppCompatActivity {
    private EditText messageEdt;
    private Button sendBtn;
    private List<MessageModel> messageList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private ListenerRegistration registration;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        CardModel user = (CardModel) getIntent().getSerializableExtra("user");

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_message_actionbar, null);
        ImageView message_img = actionBarView.findViewById(R.id.message_img);
        Glide.with(this)
            .load(user.getImage())
            .into(message_img);
        TextView message_name_txt =  actionBarView.findViewById(R.id.message_name_txt);
        message_name_txt.setText(user.getTitle());
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(actionBarView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        sendBtn = findViewById(R.id.sendButton);
        messageEdt = findViewById(R.id.messageEditText);

        String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        HashMap<String,String> uidMap = Helper.getInstance().compareUid(authUid,user.getUid());
        Map<String, Object> conversation = new HashMap<>();
        conversation.put("user1", uidMap.get("user1"));
        conversation.put("user2", uidMap.get("user2"));

        db.collection("conversations").document(uidMap.get("objectUid")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d("TAG", "No such document");
                        Map<String, Object> conversation = new HashMap<>();

                        conversation.put("timestamp", Timestamp.now());

                        db.collection("conversations").document(uidMap.get("objectUid"))
                        .set(conversation)
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

        Map<String, Object> message = new HashMap<>();
        message.put("sender", FirebaseAuth.getInstance().getCurrentUser().getUid());
        message.put("receiver", user.getUid());
        message.put("conversationId", uidMap.get("objectUid"));

        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        registration = db.collection("messages")
                .whereEqualTo("conversationId", uidMap.get("objectUid"))
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.w("TAG", "listen:error", e);
                        return;
                    }
                    messageAdapter = MessageAdapter.getInstance(messageList,getApplicationContext());
                    messageAdapter.setImageUrl(user.getImage());
                    recyclerView.setAdapter(messageAdapter);

                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                        if(dc.getType() == DocumentChange.Type.ADDED){
                            messageAdapter.addItem(new MessageModel(dc.getDocument().getString("sender"),dc.getDocument().getString("receiver"),dc.getDocument().getString("message"),dc.getDocument().getTimestamp("timestamp"),dc.getDocument().getString("conversationId")));
                            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                        }
                    }
                });

        sendBtn.setOnClickListener((view)->{
            message.put("message", messageEdt.getText().toString());
            message.put("timestamp",Timestamp.now());
            messageEdt.setText("");

            db.collection("messages")
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("TAG", "DocumentSnapshot written with ID: " + documentReference.getId());
                        conversation.put("lastMessage", message.get("message"));
                        conversation.put("timestamp", Timestamp.now());
                        conversation.put("sender", authUid);
                        if(authUid.equals(uidMap.get("user1"))){
                            conversation.put("user1Viewed", true);
                            conversation.put("user2Viewed", false);
                        }else{
                            conversation.put("user1Viewed", false);
                            conversation.put("user2Viewed", true);
                        }

                        db.collection("conversations").document(uidMap.get("objectUid"))
                                .set(conversation)
                                .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                                .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));
                    })
                    .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        registration.remove();
        MessageAdapter.getInstance(messageList,this).clearMessages();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                registration.remove();
                MessageAdapter.getInstance(messageList,this).clearMessages();
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}