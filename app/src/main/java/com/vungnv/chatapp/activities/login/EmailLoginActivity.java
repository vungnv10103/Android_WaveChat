package com.vungnv.chatapp.activities.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vungnv.chatapp.MainActivity;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.signup.SignupActivity;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.HashPassword;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.security.NoSuchAlgorithmException;

public class EmailLoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText edEmail, edPass;
    private TextView tvRegister;
    private Button btnLogin;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        init();
        edEmail.setText("nguyen@gmail.com");
        edPass.setText("vung123");

        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        btnLogin.setOnClickListener(v -> {
            btnLogin.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String email = edEmail.getText().toString().trim();
            String pass = edPass.getText().toString().trim();
            if (isValidLogin(email, pass)) {
                login(email, pass);
            }

        });
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(EmailLoginActivity.this, SignupActivity.class));
        });
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        toolbar = findViewById(R.id.toolBar);
        edEmail = findViewById(R.id.edEmail);
        edPass = findViewById(R.id.edPass);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        tvRegister = findViewById(R.id.tvRegister);

    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidLogin(String email, String pass) {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ");
            return false;
        } else if (pass.isEmpty()) {
            showToast("Vui lòng điền password");
            return false;
        }
        return true;
    }

    private void login(String email, String pass) {
        String newPass = null;
        try {
            newPass = HashPassword.hashPassword(pass);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .whereEqualTo(Constants.KEY_PASSWORD, newPass)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_USER_ID, snapshot.getId());
                            preferenceManager.putString(Constants.KEY_NAME, snapshot.getString(Constants.KEY_NAME));
                            preferenceManager.putString(Constants.KEY_IMAGE, snapshot.getString(Constants.KEY_IMAGE));
                            Intent intent = new Intent(EmailLoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            btnLogin.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            showToast("Đăng nhập thất bại");
                        }
                    }
                });
    }
}