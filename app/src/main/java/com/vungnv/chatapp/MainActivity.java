package com.vungnv.chatapp;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vungnv.chatapp.ui.HomeFragment;
import com.vungnv.chatapp.ui.LikeFragment;
import com.vungnv.chatapp.ui.NotificationFragment;
import com.vungnv.chatapp.ui.ProfileFragment;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.createNotification;
import com.vungnv.chatapp.utils.createNotificationChannel;

public class MainActivity extends AppCompatActivity {
    private final createNotificationChannel notification = new createNotificationChannel();
    private final createNotification mNotification = new createNotification();
    private LinearLayout homeLayout, likeLayout, notificationLayout, profileLayout;
    private ImageView homeImage, likeImage, notificationImage, profileImage;
    private TextView tvHome, tvLike, tvNotification, tvProfile;

    private int selectedTab = 1;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        GoogleSignInAccount acc = GoogleSignIn.getLastSignedInAccount(this);
        if (acc != null) {
            String personName = acc.getDisplayName();
            String personEmail = acc.getEmail();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                notification.createNotificationChannel1(MainActivity.this);
//                mNotification.mCreateNotification(MainActivity.this, "Email: " + personEmail, "Name: " + personName);
//            }
        }

        replaceFragment(new HomeFragment());
        replaceAnimation(homeLayout);


        homeLayout.setOnClickListener(v -> {
            if (selectedTab != 1) {
                replaceFragment(new HomeFragment());
                replaceAnimation(homeLayout);
                selectedTab = 1;
            }

        });
        likeLayout.setOnClickListener(v -> {
            if (selectedTab != 2) {
                replaceFragment(new LikeFragment());
                replaceAnimation(likeLayout);
                selectedTab = 2;
            }

        });
        notificationLayout.setOnClickListener(v -> {
            if (selectedTab != 3) {
                replaceFragment(new NotificationFragment());
                replaceAnimation(notificationLayout);
                selectedTab = 3;
            }

        });
        profileLayout.setOnClickListener(v -> {
            if (selectedTab != 4) {
                replaceAnimation(profileLayout);
                replaceFragment(new ProfileFragment());
                selectedTab = 4;
            }
        });
    }

    private void init() {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        homeLayout = findViewById(R.id.homeLayout);
        likeLayout = findViewById(R.id.likeLayout);
        notificationLayout = findViewById(R.id.notificationLayout);
        profileLayout = findViewById(R.id.profileLayout);

        homeImage = findViewById(R.id.homeImage);
        likeImage = findViewById(R.id.likeImage);
        notificationImage = findViewById(R.id.notificationImage);
        profileImage = findViewById(R.id.profileImage);

        tvHome = findViewById(R.id.homeTv);
        tvLike = findViewById(R.id.likeTv);
        tvNotification = findViewById(R.id.notificationTv);
        tvProfile = findViewById(R.id.profileTv);
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragment, null)
                .commit();
    }

    private void replaceAnimation(LinearLayout layout) {
        if (layout == profileLayout) {
            homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            likeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            profileLayout.setBackgroundResource(R.drawable.round_back_profile_100);

            tvHome.setVisibility(View.GONE);
            tvLike.setVisibility(View.GONE);
            tvNotification.setVisibility(View.GONE);
            tvProfile.setVisibility(View.VISIBLE);

            homeImage.setImageResource(R.drawable.home_icon);
            likeImage.setImageResource(R.drawable.like_icon);
            notificationImage.setImageResource(R.drawable.notification_icon);
            profileImage.setImageResource(R.drawable.profile_selected_icon);


        } else if (layout == notificationLayout) {
            homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            likeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            notificationLayout.setBackgroundResource(R.drawable.round_back_notification_100);
            profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            tvHome.setVisibility(View.GONE);
            tvLike.setVisibility(View.GONE);
            tvNotification.setVisibility(View.VISIBLE);
            tvProfile.setVisibility(View.GONE);

            homeImage.setImageResource(R.drawable.home_icon);
            likeImage.setImageResource(R.drawable.like_icon);
            notificationImage.setImageResource(R.drawable.notification_selected_icon);
            profileImage.setImageResource(R.drawable.profile_icon);
        } else if (layout == likeLayout) {
            homeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            likeLayout.setBackgroundResource(R.drawable.round_back_like_100);
            notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            tvHome.setVisibility(View.GONE);
            tvLike.setVisibility(View.VISIBLE);
            tvNotification.setVisibility(View.GONE);
            tvProfile.setVisibility(View.GONE);

            homeImage.setImageResource(R.drawable.home_icon);
            likeImage.setImageResource(R.drawable.like_selected_icon);
            notificationImage.setImageResource(R.drawable.notification_icon);
            profileImage.setImageResource(R.drawable.profile_icon);
        } else if (layout == homeLayout) {
            homeLayout.setBackgroundResource(R.drawable.round_back_home_100);
            likeLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            notificationLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            profileLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));

            tvHome.setVisibility(View.VISIBLE);
            tvLike.setVisibility(View.GONE);
            tvNotification.setVisibility(View.GONE);
            tvProfile.setVisibility(View.GONE);

            homeImage.setImageResource(R.drawable.home_selected_icon);
            likeImage.setImageResource(R.drawable.like_icon);
            notificationImage.setImageResource(R.drawable.notification_icon);
            profileImage.setImageResource(R.drawable.profile_icon);
        }
        // Animation
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.0f, 1f, 1f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        scaleAnimation.setDuration(200);
        scaleAnimation.setFillAfter(true);
        layout.startAnimation(scaleAnimation);

    }
}