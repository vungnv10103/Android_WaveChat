package com.vungnv.chatapp.activities.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vungnv.chatapp.R;

public class EmailLoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edEmail, edPass;
    private Button btnLogin;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        init();

        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        btnLogin.setOnClickListener(v -> {
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String email = edEmail.getText().toString().trim();
            String pass = edPass.getText().toString().trim();
            if(isValidLogin(email, pass)){
                login();
            }

        });
    }

    private void init() {
        toolbar = findViewById(R.id.toolBar);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidLogin(String email, String pass){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("Email không hợp lệ");
            return false;
        } else if(pass.isEmpty()){
            showToast("Vui lòng điền password");
            return false;
        }
        return true;
    }
    private void login(){

    }
}