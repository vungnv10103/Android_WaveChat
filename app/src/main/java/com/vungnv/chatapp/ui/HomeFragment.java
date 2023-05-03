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

import java.util.Arrays;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private Button btnAddName, btnAddEmail, btnTest;


    public HomeFragment() {

    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        init(root);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
//            String name = Objects.equals(currentUser.getDisplayName(), "") ? "empty" : "done";
            String name = currentUser.getDisplayName();
            String phone = currentUser.getPhoneNumber();
            String email = currentUser.getEmail();
            if (name != null && phone != null && email != null) {
//                Toast.makeText(getContext(), "name: " + name.length(), Toast.LENGTH_SHORT).show();
                if (name.length() == 0 || phone.length() == 0 || email.length() == 0) {
                    Toast.makeText(getContext(), "add information", Toast.LENGTH_SHORT).show();
                } else {
//                Toast.makeText(getContext(), "name: " + null, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "email null", Toast.LENGTH_SHORT).show();
            }


//            if (name== null || phone == null || email == null){
//                Toast.makeText(getContext(), "add information", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(getContext(), "done", Toast.LENGTH_SHORT).show();
//            }
            Log.d(Constants.TAG, "data user: " + "name: " + name + " phone: " + phone + " email: " + email);
//            Log.d(Constants.TAG, "data user: " + currentUser.getDisplayName() + "-" + currentUser.getUid() + "-" + currentUser.getPhoneNumber() +"-" + currentUser.getProviderId() +"-" + currentUser.getEmail() + "-" + currentUser.getPhotoUrl() + "-" +currentUser.getProviderData());
        }
        btnAddName.setOnClickListener(v -> {
            String newName = "Vung";
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();
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
}