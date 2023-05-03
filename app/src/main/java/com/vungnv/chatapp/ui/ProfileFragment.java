package com.vungnv.chatapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.login.LoginActivity;
import com.vungnv.chatapp.activities.login.OptionsLoginActivity;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private Button btnLogout;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private SignInClient oneTapClient;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        init(root);

        btnLogout.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        logOut();
                    }
                });
            } else {
                oneTapClient.signOut();
                FirebaseAuth.getInstance().signOut();
                logOut();
            }
        });

        return root;
    }

    private void init(View root) {
        btnLogout = root.findViewById(R.id.btnLogout);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(requireActivity(), gso);
        oneTapClient = Identity.getSignInClient(requireActivity());

    }

    private void logOut() {
        Intent intent = new Intent(getContext(), OptionsLoginActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("isShowMsg", false);
        intent.putExtra("data", bundle);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}