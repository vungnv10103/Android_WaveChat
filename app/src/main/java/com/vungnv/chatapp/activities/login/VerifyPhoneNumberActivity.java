package com.vungnv.chatapp.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vungnv.chatapp.MainActivity;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.util.Objects;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken mResendToken;

    private EditText edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private Button btnVerify;
    private ProgressBar progressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_number);

        init();
//        edOTP1.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edOTP1, InputMethodManager.SHOW_IMPLICIT);
        animationInput();

        String mVerificationId = preferenceManager.getString(Constants.KEY_VERIFICATION_ID);
        btnVerify.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.GONE);
            String verificationCode = edOTP1.getText().toString() + edOTP2.getText().toString() + edOTP3.getText().toString() +
                    edOTP4.getText().toString() + edOTP5.getText().toString() + edOTP6.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
//                        addDataToFireStore();
                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(VerifyPhoneNumberActivity.this, MainActivity.class));
                        finishAffinity();


                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(VerifyPhoneNumberActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        progressBar = findViewById(R.id.progressBar);
        btnVerify = findViewById(R.id.btnVerify);
        edOTP1 = findViewById(R.id.inputCode1);
        edOTP2 = findViewById(R.id.inputCode2);
        edOTP3 = findViewById(R.id.inputCode3);
        edOTP4 = findViewById(R.id.inputCode4);
        edOTP5 = findViewById(R.id.inputCode5);
        edOTP6 = findViewById(R.id.inputCode6);
    }
    public void hideKeyboard(@NonNull Activity activity) {
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
                } else {
                    hideKeyboard(VerifyPhoneNumberActivity.this);
                }
            }
        });
    }
}