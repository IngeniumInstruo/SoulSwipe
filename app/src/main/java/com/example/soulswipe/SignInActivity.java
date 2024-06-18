package com.example.soulswipe;

import android.annotation.SuppressLint;
import android.content.Intent;
<<<<<<< HEAD
import android.net.Uri;
=======
>>>>>>> origin/main
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
=======
>>>>>>> origin/main

public class SignInActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("232016844867-8jg59hgji2blu6pt327q60paojcqg763.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        findViewById(R.id.googleBtn).setOnClickListener(view -> signIn());
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if(currentUser != null){
            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user != null) {
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNewUser) {
<<<<<<< HEAD
                                if (user != null) {
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                                    String name = user.getDisplayName();

                                    HashMap<String, Object> googleUser = new HashMap<>();
                                    googleUser.put("name",name);
                                    googleUser.put("picture","blank.webp");
                                    googleUser.put("gender","Male");

                                    db.collection("users").document(user.getUid())
                                            .set(googleUser,SetOptions.merge())
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("TAG", "DocumentSnapshot successfully written!");
                                                Intent createAccountIntent = new Intent(this,CreateAccountActivity.class);
                                                startActivity(createAccountIntent);
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Log.w("TAG", "Error adding document", e));

                                }

                            } else {
                                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                                startActivity(mainActivityIntent);
=======
                                Intent createAccountIntent = new Intent(this,CreateAccountActivity.class);
                                startActivity(createAccountIntent);
                                finish();
                            } else {
                                Intent mainActivityIntent = new Intent(this, MainActivity.class);
                                startActivity(mainActivityIntent);
                                finish();
>>>>>>> origin/main
                            }
                        }

                    } else {
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }

}