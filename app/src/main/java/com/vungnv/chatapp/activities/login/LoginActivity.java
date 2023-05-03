package com.vungnv.chatapp.activities.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vungnv.chatapp.MainActivity;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.signup.SignupActivity;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import org.checkerframework.checker.units.qual.C;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private Button btnGetOTP, btnVerify;
    private TextView tvRegister;
    private ImageView logWithFacebook, logWithGoogle;
    private EditText edPhone, edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private ProgressBar progressBar;
    private LinearLayout layoutInputPhone, layoutInputOTP;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId, mPhone;

    private PreferenceManager preferenceManager;
    int checkLogin = 0;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private static final int REQUEST_CODE = 1000;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        animationInput();
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

        btnGetOTP.setOnClickListener(v -> {
            checkLogin++;
            String phone = edPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter your phone", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            btnGetOTP.setVisibility(View.INVISIBLE);
            mPhone = "+84" + phone;
            sendOTP(mPhone);

        });

        btnVerify.setOnClickListener(v -> {
            String verificationCode = edOTP1.getText().toString() + edOTP2.getText().toString() + edOTP3.getText().toString() +
                    edOTP4.getText().toString() + edOTP5.getText().toString() + edOTP6.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
//                        addDataToFireStore();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            preferenceManager.putString(Constants.KEY_PHONE, mPhone);
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finishAffinity();
                        }

                    } else {
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
        logWithGoogle.setOnClickListener(v -> {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE);
        });
        logWithFacebook.setOnClickListener(v -> {
            oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                        @Override
                        public void onSuccess(BeginSignInResult result) {
                            try {
                                startIntentSenderForResult(
                                        result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                        null, 0, 0, 0);
                                finishAffinity();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(Constants.TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // No saved credentials found. Launch the One Tap sign-up flow, or
                            // do nothing and continue presenting the signed-out UI.
                            Log.d(Constants.TAG, e.getLocalizedMessage());
                        }
                    });
        });

    }

    private void init() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        oneTapClient = Identity.getSignInClient(this);
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
        logWithGoogle = findViewById(R.id.logWithGoogle);
        logWithFacebook = findViewById(R.id.logWithFacebook);
    }

    private void addDataToFireStore() {
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

    private void animationView() {
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
                } else {
                    hideKeyboard(LoginActivity.this);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        Bundle bundle = getIntent().getBundleExtra("data");
        boolean isShow;
        if (bundle != null) {
            isShow = bundle.getBoolean("isShowMsg");
        } else {
            isShow = true;
        }
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        } else {
            if (isShow && checkLogin == 0) {
                Toast.makeText(this, "Phiên đăng nhâp hết hạn", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleGoogleSignInResult(task);
            try {
//                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
//                String idToken = credential.getGoogleIdToken();
//                String username = credential.getId();
//                String password = credential.getPassword();
//                if (idToken != null) {
//                    // Got an ID token from Google. Use it to authenticate
//                    // with your backend.
//                    Log.d(Constants.TAG, "Got ID token.");
//                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
//                    mAuth.signInWithCredential(firebaseCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(Constants.TAG, "signInWithCredential:success");
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                finishAffinity();
//                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
//
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.w(Constants.TAG, "signInWithCredential:failure", task.getException());
//                            }
//                        }
//                    });
//                } else if (password != null) {
//                    // Got a saved username and password. Use them to authenticate
//                    // with your backend.
//                    Log.d(Constants.TAG, "Got password.");
//                }

                task.getResult(ApiException.class);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finishAffinity();

            }
            catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    // Define a helper method to handle Google Sign-In result
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, create AuthCredential using Google ID token
            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

            // Authenticate with Firebase using the AuthCredential
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // Sign in failed, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(Constants.TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    // Define a helper method to update the UI after successful sign-in
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // User is signed in, do something
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finishAffinity();
        } else {
            // User is signed out, do something else
            Toast.makeText(this, "Login Fail", Toast.LENGTH_SHORT).show();
        }
    }

}