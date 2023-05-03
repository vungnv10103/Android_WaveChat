package com.vungnv.chatapp.activities.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vungnv.chatapp.MainActivity;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.utils.Constants;

public class OptionsLoginActivity extends AppCompatActivity {

    private Button btnLWEmail, btnLWPhone, btnLWGoogle, btnLWFacebook;
    private int checkLogin = 0;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_login);
        init();
        btnLWGoogle.setOnClickListener(v -> {
            Intent signInIntent = gsc.getSignInIntent();
            startActivityForResult(signInIntent, Constants.REQUEST_CODE);
        });
        btnLWEmail.setOnClickListener(v -> {
            checkLogin++;
            startActivity(new Intent(OptionsLoginActivity.this, EmailLoginActivity.class));
        });
        btnLWPhone.setOnClickListener(v -> {
            checkLogin++;
            startActivity(new Intent(OptionsLoginActivity.this, PhoneNumberLoginActivity.class));
        });
    }

    private void init() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        btnLWEmail = findViewById(R.id.btnSignInWithEmail);
        btnLWPhone = findViewById(R.id.btnSignInWithPhoneNumber);
        btnLWGoogle = findViewById(R.id.btnSignInWithGoogle);
        btnLWFacebook = findViewById(R.id.btnSignInWithFaceBook);
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
            startActivity(new Intent(OptionsLoginActivity.this, MainActivity.class));
            finishAffinity();
        } else {
            if (isShow && checkLogin == 0) {
                Toast.makeText(this, "Phiên đăng nhâp hết hạn", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            startActivity(new Intent(OptionsLoginActivity.this, MainActivity.class));
            finishAffinity();
        } else {
            Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleGoogleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, create AuthCredential using Google ID token
            if (account != null) {
                String idToken = account.getIdToken();
                if (idToken != null) {
                    AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
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
                                        Toast.makeText(OptionsLoginActivity.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                } else {
                    Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(Constants.TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleGoogleSignInResult(task);
            try {
                task.getResult(ApiException.class);
                finish();
                startActivity(new Intent(OptionsLoginActivity.this, MainActivity.class));
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
}