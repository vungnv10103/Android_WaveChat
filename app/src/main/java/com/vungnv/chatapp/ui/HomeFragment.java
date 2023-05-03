package com.vungnv.chatapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.utils.Constants;

public class HomeFragment extends Fragment {

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
        if (currentUser != null){
            Log.d(Constants.TAG, "data user: " + currentUser.getDisplayName() + "-" + currentUser.getUid() + "-" + currentUser.getPhoneNumber() +"-" + currentUser.getProviderId() +"-" + currentUser.getEmail() + "-" + currentUser.getPhotoUrl() + "-" +currentUser.getProviderData());
        }
        return root;
    }
    private void init(View root){

    }
    private void showToast(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}