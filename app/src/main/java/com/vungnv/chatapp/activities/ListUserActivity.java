package com.vungnv.chatapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.chat.ChatActivity;
import com.vungnv.chatapp.adapters.UserAdapter;
import com.vungnv.chatapp.listeners.IUserListener;
import com.vungnv.chatapp.models.UserModel2;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ListUserActivity extends AppCompatActivity implements IUserListener {
    private ImageView imgBack;
    private TextView tvGetDataError;
    private ProgressBar progressBar;
    private RecyclerView rcvListUser;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_user);
        init();
        imgBack.setOnClickListener(v -> onBackPressed());
        getListUser();

    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        imgBack = findViewById(R.id.imgBack);
        rcvListUser = findViewById(R.id.rcvListUser);
        tvGetDataError = findViewById(R.id.tvGetDataError);
    }

    private void getListUser() {
        loading(true);
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        loading(false);
                        String currentUserID = preferenceManager.getString(Constants.KEY_USER_ID);
                        if (task.isSuccessful() && task.getResult() != null) {
                            List<UserModel2> listUser = new ArrayList<>();
                            for (QueryDocumentSnapshot data : task.getResult()) {
                                if (currentUserID.equals(data.getId())) {
                                    continue;
                                }
                                UserModel2 user = new UserModel2();
                                user.id = data.getId();
                                user.name = data.getString(Constants.KEY_NAME);
                                user.email = data.getString(Constants.KEY_EMAIL);
                                user.image = data.getString(Constants.KEY_IMAGE);
                                user.phoneNumber = data.getString(Constants.KEY_PHONE);
                                user.token = data.getString(Constants.KEY_FCM_TOKEN);

                                Log.d(Constants.TAG, "user: " + user);
                                listUser.add(user);
                            }
                            if (listUser.size() > 0) {
                                UserAdapter userAdapter = new UserAdapter(ListUserActivity.this, listUser, ListUserActivity.this);
                                rcvListUser.setAdapter(userAdapter);
                                rcvListUser.setVisibility(View.VISIBLE);
                            } else {
                                tvGetDataError.setText("Không có dữ liệu");
                                tvGetDataError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            tvGetDataError.setText("Lỗi lấy data");
                            tvGetDataError.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rcvListUser.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            rcvListUser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUserClicked(UserModel2 user) {
        Intent intent = new Intent(ListUserActivity.this, ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}