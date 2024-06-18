package com.example.soulswipe;

import static android.widget.AdapterView.*;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageRef;
    private ImageView imageView;
    Map<String, Object> user;
    MaterialAutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapter;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText nameEdt = findViewById(R.id.nameEdt);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        user = new HashMap<>();


        autoCompleteTextView = findViewById(R.id.genderEdt);

        String[] items = {"Male", "Female"};

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);

        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnClickListener((view)->{
            if (!TextUtils.isEmpty(autoCompleteTextView.getText().toString())) {
                adapter.getFilter().filter(null);
            }
        });

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = (String) parent.getItemAtPosition(position);
            user.put("gender", selectedItem);
            autoCompleteTextView.clearFocus();
        });

        imageView = findViewById(R.id.imageView3);

        storageRef = FirebaseStorage.getInstance().getReference();

        Button chooseImageButton = findViewById(R.id.chooseButton);
        TextView nameTxt = findViewById(R.id.nameTxt);
        EditText birthdateEdt = findViewById(R.id.birthdateEdt);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(year1, month1, dayOfMonth);
            Date birthdate = calendar1.getTime();

            user.put("birthdate", new Timestamp(birthdate));
            String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            birthdateEdt.setText(selectedDate);
        }, year, month, day);

        birthdateEdt.setOnClickListener(view->{
            datePickerDialog.show();
        });

        Button submitBtn = findViewById(R.id.submitBtn);
        Intent intent = new Intent(this,MainActivity.class);
        submitBtn.setOnClickListener(view -> {
            String name = nameEdt.getText().toString();

            user.put("name", nameEdt.getText().toString());

            SharedPreferences sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if(user.get("gender") == "Female"){
                editor.putString("interest", "Male");
            }else{
                editor.putString("interest", "Female");
            }

            editor.apply();


            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(user,SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        boolean createAccount = this.getIntent().getBooleanExtra("create",false);
                        if(createAccount){
                            startActivity(intent);
                        }
                        finish();

                    })
                    .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));
        });
        chooseImageButton.setOnClickListener(view -> openGallery());

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("TAG", "DocumentSnapshot data: " + document.getData());
                            Glide.with(this)
                                    .load("https://firebasestorage.googleapis.com/v0/b/soulswipe-e882e.appspot.com/o/images%2F" +
                                            document.get("picture").toString() + "?alt=media")
                                    .into(imageView);
                            nameEdt.setText(document.getString("name"));
                            String gender = document.getString("gender");
                            Timestamp birthdate = document.getTimestamp("birthdate");
                            nameTxt.setText(document.getString("name"));
                            if(gender != null){
                                autoCompleteTextView.setText(gender);
                            }

                            if(birthdate != null){
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                String selectedDate = dateFormat.format(birthdate.toDate());
                                birthdateEdt.setText(selectedDate);
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                });


    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();

            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        long imageName = System.currentTimeMillis();
        StorageReference imageRef = storageRef.child("images/" + imageName);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        Glide.with(this)
            .load(imageUri)
            .into(imageView);

        uploadTask.addOnFailureListener(exception -> {
            Log.e("TAG", "Upload failed: " + exception.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            Log.d("TAG", "Upload successful");
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                Log.d("TAG", "Image URL: " + imageUrl);
                user.put("picture", imageName);
            });
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        autoCompleteTextView.setAdapter(adapter);
    }
}