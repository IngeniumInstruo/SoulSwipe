package com.example.soulswipe;

import android.annotation.SuppressLint;
<<<<<<< HEAD
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
=======
import android.os.Bundle;
import android.util.Log;
>>>>>>> origin/main
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
<<<<<<< HEAD
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
=======
import androidx.appcompat.app.AppCompatActivity;
>>>>>>> origin/main
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint({"MissingInflatedId", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.custom_actionbar, null);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(actionBarView);
=======
        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
>>>>>>> origin/main

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));


        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (item.getItemId() == R.id.navigation_item_1) {
                        viewPager.setCurrentItem(0, true);
                        return true;
                    } else if (item.getItemId() == R.id.navigation_item_2) {
                        viewPager.setCurrentItem(1, true);
                        return true;
                    } else if (item.getItemId() == R.id.navigation_item_3) {
                        viewPager.setCurrentItem(2, true);
                        return true;
                    } else {
                        return false;
                    }
                });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
<<<<<<< HEAD

=======
                // Update BottomNavigationView item selection
>>>>>>> origin/main
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }
<<<<<<< HEAD

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CardStackAdapter.getInstance(new ArrayList<>(),this).clearList();
        MatchAdapter.getInstance(new ArrayList<>(),this).clearList();
        ConversationAdapter.getInstance(new ArrayList<>(),this).clearList();
        MessageAdapter.getInstance(new ArrayList<>(),this).clearList();
    }
=======
>>>>>>> origin/main
}