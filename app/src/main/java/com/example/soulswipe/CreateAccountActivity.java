package com.example.soulswipe;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private StorageReference storageRef;
    private ImageView imageView;
    Map<String, Object> user;

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

        TextInputLayout textInputLayout = findViewById(R.id.textInputLayout);
        @SuppressLint("WrongViewCast") MaterialAutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoCompleteTextView);

        String[] items = {"Male", "Female"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items);

        // Set the adapter to the AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter);

        // Set a listener to get the selected item
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = (String) parent.getItemAtPosition(position);
                user.put("gender", selectedItem);
            }
        });


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date birthdate = calendar.getTime();

                user.put("birthdate", new Timestamp(birthdate));
            }
        }, year, month, day);


        imageView = findViewById(R.id.imageView3);

        storageRef = FirebaseStorage.getInstance().getReference();

        Button chooseImageButton = findViewById(R.id.chooseButton);
        Button birthdateButton = findViewById(R.id.birthdateBtn);
        birthdateButton.setOnClickListener(view->{
            datePickerDialog.show();
        });
        Button submitBtn = findViewById(R.id.submitBtn);
        Intent intent = new Intent(this,MainActivity.class);
        submitBtn.setOnClickListener(view -> {
            user.put("name", nameEdt.getText().toString());

            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("TAG", "DocumentSnapshot successfully written!");

                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("TAG", "Error adding document", e);
                        }
                    });
        });
        chooseImageButton.setOnClickListener(view -> openGallery());
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
        user.put("picture", imageName);
        StorageReference imageRef = storageRef.child("images/" + imageName);
        UploadTask uploadTask = imageRef.putFile(imageUri);

        Glide.with(this)
            .load(imageUri)
            .into(imageView);

        uploadTask.addOnFailureListener(exception -> {
            // Handle unsuccessful uploads
            Log.e("TAG", "Upload failed: " + exception.getMessage());
        }).addOnSuccessListener(taskSnapshot -> {
            // Handle successful uploads on complete
            Log.d("TAG", "Upload successful");
            // Get the download URL for the image
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                // Do something with the image URL (e.g., save it to a database)
                Log.d("TAG", "Image URL: " + imageUrl);
            });
        });
    }
}