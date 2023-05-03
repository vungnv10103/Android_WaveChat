package com.vungnv.chatapp.activities.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.util.concurrent.TimeUnit;

public class PhoneNumberLoginActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button btnGetOTP;
    private EditText edPhone;
    private ProgressBar progressBar;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId;
    private PreferenceManager preferenceManager;
    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_login);

        init();

        toolbar.setNavigationIcon(R.drawable.icon_arrow_back_white);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        btnGetOTP.setOnClickListener(v -> {
            btnGetOTP.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            String phone = edPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
                return;
            }
            sendOTP("+84" + phone);


        });
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        toolbar = findViewById(R.id.toolBar);
        edPhone = findViewById(R.id.edPhone);
        btnGetOTP = findViewById(R.id.btnGetOTP);
        progressBar = findViewById(R.id.progressBar);
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
                                isStart = true;
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.w("Log", "onVerificationFailed", e);
                                isStart = false;
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
//                                super.onCodeSent(s, forceResendingToken);
                                Log.d("Log", "onCodeSent:" + verificationId);
                                Log.d("Log", "token:" + token);

                                // Save verification ID and resending token so we can use them later
                                mVerificationId = verificationId;
                                mResendToken = token;
                                isStart = true;
                                preferenceManager.putString(Constants.KEY_VERIFICATION_ID, verificationId);
                                startActivity(new Intent(PhoneNumberLoginActivity.this, VerifyPhoneNumberActivity.class));
                                finishAffinity();
                            }
                        })
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
}