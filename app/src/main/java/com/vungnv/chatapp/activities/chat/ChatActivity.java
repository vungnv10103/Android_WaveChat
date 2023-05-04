package com.vungnv.chatapp.activities.chat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.vungnv.chatapp.R;
import com.vungnv.chatapp.models.UserModel2;
import com.vungnv.chatapp.utils.Constants;

public class ChatActivity extends AppCompatActivity {
    private UserModel2 receivedUser;
    private TextView tvNameUser;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        imgBack.setOnClickListener(v -> onBackPressed());

        loadReceivedDetail();
    }

    private void init() {
        tvNameUser = findViewById(R.id.tvNameUser);
        imgBack = findViewById(R.id.imgBack);
    }

    private void loadReceivedDetail() {
        receivedUser = (UserModel2) getIntent().getSerializableExtra(Constants.KEY_USER);
        tvNameUser.setText(receivedUser.name);
    }

}