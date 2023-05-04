package com.vungnv.chatapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kongzue.dialogx.dialogs.FullScreenDialog;
import com.kongzue.dialogx.interfaces.OnBindView;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.ListUserActivity;
import com.vungnv.chatapp.activities.login.OptionsLoginActivity;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import org.checkerframework.checker.units.qual.C;

import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private ImageView imgProfile, imgLogout;
    private PreferenceManager preferenceManager;
    private TextView tvName;
    private FloatingActionButton fabAddUser;

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
                    showToast("add information");
                } else {
//                Toast.makeText(getContext(), "name: " + null, Toast.LENGTH_SHORT).show();
                    showToast("done");
                }
            } else {
                showToast("email null");
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setDefaultData();
        }

        imgLogout.setOnClickListener(v -> {
            logOut();
        });
        fabAddUser.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ListUserActivity.class));
        });
        return root;
    }

    private void init(View root) {
        preferenceManager = new PreferenceManager(requireContext());
        imgProfile = root.findViewById(R.id.imgProfile);
        imgLogout = root.findViewById(R.id.imgLogout);
        tvName = root.findViewById(R.id.tvNameUser);
        fabAddUser = root.findViewById(R.id.fabAddUser);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDefaultData() {
        if (preferenceManager.checkDataDefaultExits()) {  // login with email default
            tvName.setText(preferenceManager.getString(Constants.KEY_NAME));
            byte[] bytes = Base64.getDecoder().decode(preferenceManager.getString(Constants.KEY_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            imgProfile.setImageBitmap(bitmap);
        } else {  // login with providers
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                tvName.setText(user.getDisplayName());
            }
        }

    }


    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void logOut() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                firebaseFirestore.collection(Constants.KEY_COLLECTION_USER)
                        .document(preferenceManager.getString(Constants.KEY_USER_ID));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    Intent intent = new Intent(getContext(), OptionsLoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isShowMsg", false);
                    intent.putExtra("data", bundle);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    showToast("unable to logout");
                });

    }
}