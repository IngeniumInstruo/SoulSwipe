package com.example.soulswipe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

<<<<<<< HEAD
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
=======
import androidx.fragment.app.Fragment;

>>>>>>> origin/main
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
<<<<<<< HEAD
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private TextInputLayout textInputLayout;
    public ProfileFragment() {
=======

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
        // Required empty public constructor
>>>>>>> origin/main
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

<<<<<<< HEAD
    @Override
    public void onResume() {
        super.onResume();
        textInputLayout.clearFocus();
    }

=======
>>>>>>> origin/main
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Button logoutBtn = view.findViewById(R.id.logoutBtn);
<<<<<<< HEAD
        Button editBtn = view.findViewById(R.id.editBtn);
        TextView nametxt = view.findViewById(R.id.nameTxt);
        ImageView userImg = view.findViewById(R.id.userImg);
        String authUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(authUid)
        .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                    Glide.with(requireContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                document.get("picture").toString() + "?alt=media")
                        .into(userImg);
                    nametxt.setText(document.getString("name"));
                } else {
                    Log.d("TAG", "No such document");
                }
            } else {
                Log.d("TAG", "get failed with ", task.getException());
            }
        });

        textInputLayout = view.findViewById(R.id.textInputLayout1);
=======
        TextInputLayout textInputLayout = view.findViewById(R.id.textInputLayout1);
>>>>>>> origin/main
        @SuppressLint("WrongViewCast") MaterialAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoCompleteTextView1);

        String[] items = {"Male", "Female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);

        autoCompleteTextView.setAdapter(adapter);

        SharedPreferences sharedPref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        logoutBtn.setOnClickListener((View v)->{
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext(), SignInActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

<<<<<<< HEAD
        editBtn.setOnClickListener((View v)->{
            Intent intent = new Intent(requireContext(), CreateAccountActivity.class);
            startActivity(intent);
        });

        autoCompleteTextView.setOnClickListener((v)->{
            if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
                adapter.getFilter().filter(null);
            }
        });

        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            textInputLayout.clearFocus();
            editor.putString("interest", selectedItem);
            editor.apply();
            CardStackAdapter.getInstance(new ArrayList<>(),requireContext()).updateInterest(selectedItem);

            autoCompleteTextView.clearFocus();


        });

=======

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                textInputLayout.clearFocus();
                editor.putString("interest", selectedItem);
                editor.apply();
                CardFragment.getInstance().updateInterest();
            }
        });

>>>>>>> origin/main
        return view;
    }
}