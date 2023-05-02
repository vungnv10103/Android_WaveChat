package com.vungnv.chatapp.activities.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.vungnv.chatapp.activities.login.LoginActivity;
import com.vungnv.chatapp.models.UserModel;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.HashPassword;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class SignupActivity extends AppCompatActivity {
    private LinearLayout layoutRegisterForm, layoutVerify;
    private CardView layoutImage;
    private ImageView imgProfile;
    private ProgressBar progressBar, progressBarVerify;
    private EditText edName, edPhone, edEmail, edPassword, edConfirmPass;
    private EditText edOTP1, edOTP2, edOTP3, edOTP4, edOTP5, edOTP6;
    private Button btnRegister, btnVerify;
    private TextView textAddImage, tvLogin, tvResendOTP;
    private UserModel userModel;
    private String encodeImage = "";

    private PreferenceManager preferenceManager;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String mVerificationId, mPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        init();
        animationInput();

        layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

        btnRegister.setOnClickListener(v -> {
            loading(true);
            String name = edName.getText().toString().trim();
            String phone = edPhone.getText().toString().trim();
            String email = edEmail.getText().toString().trim();
            String pass = edPassword.getText().toString().trim();
            String confirmPass = edConfirmPass.getText().toString().trim();

            boolean check = isValidForm(name, phone, email, pass, confirmPass);
            if (check) {
                String newPass = null;
                try {
                    newPass = HashPassword.hashPassword(confirmPass);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                HashMap<String, Object> user = new HashMap<>();
                user.put(Constants.KEY_EMAIL, email);
                user.put(Constants.KEY_IMAGE, encodeImage);
                user.put(Constants.KEY_PASSWORD, newPass);
                user.put(Constants.KEY_NAME, name);
                user.put(Constants.KEY_PHONE, "+84" + phone);
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USER).add(user).addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_EMAIL, email);
                    preferenceManager.putString(Constants.KEY_IMAGE, encodeImage);
                    preferenceManager.putString(Constants.KEY_PASSWORD, confirmPass);
                    preferenceManager.putString(Constants.KEY_NAME, name);
                    sendOTP("+84" + phone);

                }).addOnFailureListener(e -> {
                    loading(false);
                    showToast(e.getMessage());
                });
            }

        });
        btnVerify.setOnClickListener(v -> {
            progressBarVerify.setVisibility(View.VISIBLE);
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
                        btnVerify.setVisibility(View.VISIBLE);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();


                    } else {
                        Toast.makeText(SignupActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
        tvLogin.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void init() {
        layoutRegisterForm = findViewById(R.id.layoutRegisterForm);
        layoutVerify = findViewById(R.id.layoutVerify);
        layoutImage = findViewById(R.id.layoutImage);
        imgProfile = findViewById(R.id.imgProfile);
        edName = findViewById(R.id.edName);
        edPhone = findViewById(R.id.edPhone);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPass);
        edConfirmPass = findViewById(R.id.edPassConfirm);
        btnRegister = findViewById(R.id.btnRegister);
        textAddImage = findViewById(R.id.textAddImage);
        tvLogin = findViewById(R.id.tvLogin);
        tvResendOTP = findViewById(R.id.tvResendOTP);
        progressBar = findViewById(R.id.progressBar);
        progressBarVerify = findViewById(R.id.progressBarVerify);
        btnVerify = findViewById(R.id.btnVerify);
        edOTP1 = findViewById(R.id.inputCode1);
        edOTP2 = findViewById(R.id.inputCode2);
        edOTP3 = findViewById(R.id.inputCode3);
        edOTP4 = findViewById(R.id.inputCode4);
        edOTP5 = findViewById(R.id.inputCode5);
        edOTP6 = findViewById(R.id.inputCode6);
        preferenceManager = new PreferenceManager(getApplicationContext());
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String encodeImage(@NonNull Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imgProfile.setImageBitmap(bitmap);
                            textAddImage.setVisibility(View.GONE);
                            encodeImage = encodeImage(bitmap);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isValidForm(String name, String phone, String email, String pass, String confirmPass) {
        if (encodeImage.isEmpty()) {
            showToast("Chưa chọn ảnh");
            return false;
        } else if (name.isEmpty()) {
            showToast("Điền tên");
            return false;
        } else if (phone.isEmpty()) {
            showToast("Điền SĐT");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Email không hợp lệ");
            return false;
        } else if (pass.isEmpty()) {
            showToast("Điền pass");
            return false;
        } else if (confirmPass.isEmpty()) {
            showToast("Điền xác nhận pass");
            return false;
        } else if (!pass.toLowerCase(Locale.ROOT).equals(confirmPass.toLowerCase())) {
            showToast("2 pass phải giống nhau");
            return false;
        }
        return true;
    }

    private void loading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setVisibility(View.VISIBLE);
        }
    }

    private void animationView() {
        layoutRegisterForm.setVisibility(View.GONE);
        layoutVerify.setVisibility(View.VISIBLE);
        edOTP1.requestFocus();
        // auto show keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    public void toggleKeyboard(@NonNull Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();

        if (view != null) {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            } else {
                imm.showSoftInput(view, 0);
            }
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
                    toggleKeyboard(SignupActivity.this);
                }
            }
        });
    }
}