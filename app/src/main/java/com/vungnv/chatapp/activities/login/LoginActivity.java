package com.vungnv.chatapp.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vungnv.chatapp.MainActivity;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.signup.SignupActivity;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity{
    private Button btnGetOTP, btnVerify;
    private TextView tvRegister;
    private EditText edPhone, edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private ProgressBar progressBar;
    private LinearLayout layoutInputPhone, layoutInputOTP;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId, mPhone;

    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        animationInput();

        btnGetOTP.setOnClickListener(v -> {
            String phone = edPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            btnGetOTP.setVisibility(View.INVISIBLE);
            mPhone = "+84"+phone;
            sendOTP(mPhone);

        });

        btnVerify.setOnClickListener(v -> {
            String verificationCode = edOTP1.getText().toString()+edOTP2.getText().toString()+edOTP3.getText().toString() +
                    edOTP4.getText().toString() + edOTP5.getText().toString() + edOTP6.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
//                        addDataToFireStore();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            preferenceManager.putString(Constants.KEY_PHONE, mPhone);
                            startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                            finishAffinity();
                        }

                    }
                    else {
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        tvRegister.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }

        });

    }

    private void init() {
        tvRegister = findViewById(R.id.tvRegister);
        preferenceManager = new PreferenceManager(getApplicationContext());
        btnGetOTP = findViewById(R.id.btnGetOTP);
        layoutInputPhone = findViewById(R.id.layoutInputPhone);
        layoutInputOTP = findViewById(R.id.layoutInputOTP);
        edPhone = findViewById(R.id.edPhone);
        progressBar = findViewById(R.id.progressBar);
        btnVerify = findViewById(R.id.btnVerify);
        edOTP1 = findViewById(R.id.inputCode1);
        edOTP2 = findViewById(R.id.inputCode2);
        edOTP3 = findViewById(R.id.inputCode3);
        edOTP4 = findViewById(R.id.inputCode4);
        edOTP5 = findViewById(R.id.inputCode5);
        edOTP6 = findViewById(R.id.inputCode6);
    }
    private void addDataToFireStore(){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("first_name", "Nguyễn Văn");
        user.put("last_name", "Vữngg");
        firebaseFirestore.collection("users").add(user).addOnSuccessListener(documentReference -> {
            Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.d(Constants.TAG, "addDataToFireStore: " + e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendOTP(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                Log.d("Log", "onVerificationCompleted:" + phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.w("log", "onVerificationFailed", e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                                super.onCodeSent(s, forceResendingToken);
                                Log.d("Log", "onCodeSent:" + verificationId);
                                Log.d("Log", "token:" + token);

                                // Save verification ID and resending token so we can use them later
                                mVerificationId = verificationId;
                                mResendToken = token;
                                animationView();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    private void animationView(){
        progressBar.setVisibility(View.GONE);
        layoutInputPhone.setVisibility(View.GONE);
        btnGetOTP.setVisibility(View.GONE);
        layoutInputOTP.setVisibility(View.VISIBLE);
        btnVerify.setVisibility(View.VISIBLE);
        edOTP1.requestFocus();
    }

    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void animationInput() {
        edOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    edOTP2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    edOTP3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    edOTP1.requestFocus();
                }
            }
        });
        edOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    edOTP4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    edOTP2.requestFocus();
                }
            }
        });
        edOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    edOTP5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    edOTP3.requestFocus();
                }
            }
        });
        edOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    edOTP6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    edOTP4.requestFocus();
                }

            }
        });
        edOTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    edOTP5.requestFocus();
                }
                else {
                    hideKeyboard(LoginActivity.this);
                }
            }
        });
    }

}