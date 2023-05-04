package com.vungnv.chatapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.kongzue.dialogx.dialogs.FullScreenDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.utils.Constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

public class LikeFragment extends Fragment {
    private Button btnAddName, btnAddEmail, btnTest;

    public LikeFragment() {
    }

    public static LikeFragment newInstance() {
        return new LikeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_like, container, false);

        init(root);
        generateEmail();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        btnAddName.setOnClickListener(v -> {
//            byte[] array = new byte[7]; // length is bounded by 7
//            new Random().nextBytes(array);
//            String generatedString = new String(array, StandardCharsets.UTF_8);
//            showToast(generatedString);
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            StringBuilder buffer = new StringBuilder(targetStringLength);
            for (int i = 0; i < targetStringLength; i++) {
                int randomLimitedInt = leftLimit + (int)
                        (random.nextFloat() * (rightLimit - leftLimit + 1));
                buffer.append((char) randomLimitedInt);
            }
            String generatedString = buffer.toString();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(generatedString)
                    .build();
            if (currentUser == null){
                return;
            }
            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("success");

                            }
                        }
                    });
        });
        btnAddEmail.setOnClickListener(v -> {
            if (currentUser == null){
                return;
            }
            currentUser.updateEmail("vanvung123az@gmail.com")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                showToast("User email address updated.");
                                Log.d(Constants.TAG, "User email address updated.");
                            } else {
                                showToast("Error, " + Objects.requireNonNull(task.getException()).getMessage());
                                Log.d(Constants.TAG, Objects.requireNonNull(task.getException()).getMessage());
                            }
                        }
                    });
        });
        btnTest.setOnClickListener(v -> {
            FullScreenDialog.show(new OnBindView<FullScreenDialog>(R.layout.bottom_sheet_add_to_cart) {
                @Override
                public void onBind(FullScreenDialog dialog, View v) {
                    //View childView = v.findViewById(resId)...
                }
            });
        });
        return root;

    }

    private void init(View root) {
        btnAddName = root.findViewById(R.id.btnAddName);
        btnAddEmail = root.findViewById(R.id.btnAddEmail);
        btnTest = root.findViewById(R.id.btnTest);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private String generateEmail() {
        StringBuilder emailAddress = new StringBuilder();
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        while (emailAddress.length() < 5) {
            int character = (int) (Math.random() * 26);
            emailAddress.append(alphabet.substring(character, character + 1));
            emailAddress.append(Integer.valueOf((int) (Math.random() * 99)).toString());
            emailAddress.append("@gmail.com");
        }
//        showToast(emailAddress.toString());
        return emailAddress.toString();
    }

}