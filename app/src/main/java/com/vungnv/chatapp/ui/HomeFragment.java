package com.vungnv.chatapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.activities.ListUserActivity;
import com.vungnv.chatapp.activities.chat.ChatActivity;
import com.vungnv.chatapp.activities.login.OptionsLoginActivity;
import com.vungnv.chatapp.adapters.RecentConversationsAdapter;
import com.vungnv.chatapp.listeners.IConversionListener;
import com.vungnv.chatapp.models.ChatMessageModel;
import com.vungnv.chatapp.models.UserModel2;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment implements IConversionListener {
    private ImageView imgProfile, imgLogout;
    private PreferenceManager preferenceManager;
    private TextView tvName;
    private FloatingActionButton fabAddUser;
    private List<ChatMessageModel> listChat;
    private RecentConversationsAdapter recentConversionsAdapter;
    private FirebaseFirestore firebaseFirestore;
    private RecyclerView rcvListRecentConversion;
    private ProgressBar progressBar;

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
//                    showToast("add information");
                } else {
//                Toast.makeText(getContext(), "name: " + null, Toast.LENGTH_SHORT).show();
//                    showToast("done");
                }
            } else {
//                showToast("email null");
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
        listenerConversation();
        return root;
    }

    private void init(View root) {
        preferenceManager = new PreferenceManager(requireContext());
        imgProfile = root.findViewById(R.id.imgProfile);
        imgLogout = root.findViewById(R.id.imgLogout);
        tvName = root.findViewById(R.id.tvNameUser);
        fabAddUser = root.findViewById(R.id.fabAddUser);
        listChat = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        rcvListRecentConversion = root.findViewById(R.id.rcvListRecentConversion);
        progressBar = root.findViewById(R.id.progressBar);
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
        recentConversionsAdapter = new RecentConversationsAdapter(getContext(), listChat, this);
        rcvListRecentConversion.setAdapter(recentConversionsAdapter);

    }

    private void listenerConversation() {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_RECEIVED_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

    }

    private final EventListener<QuerySnapshot> eventListener = ((value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receivedId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    ChatMessageModel messageModel = new ChatMessageModel();
                    messageModel.senderId = senderId;
                    messageModel.receivedId = receivedId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {
                        messageModel.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVED_IMAGE);
                        messageModel.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVED_NAME);
                        messageModel.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    } else {
                        messageModel.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        messageModel.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        messageModel.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    messageModel.message = documentChange.getDocument().getString(Constants.KEY_LATEST_MESSAGE);
                    messageModel.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    listChat.add(messageModel);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    int sum = listChat.size();
                    for (int i = 0; i < sum; i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receivedId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                        if (listChat.get(i).senderId.equals(senderId) && listChat.get(i).receivedId.equals(receivedId)) {
                            listChat.get(i).message = documentChange.getDocument().getString(Constants.KEY_LATEST_MESSAGE);
                            listChat.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(listChat, ((o1, o2) -> o2.dateObject.compareTo(o1.dateObject)));
            recentConversionsAdapter.notifyDataSetChanged();
            rcvListRecentConversion.smoothScrollToPosition(0);
            rcvListRecentConversion.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    });


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

    @Override
    public void onConversionClicked(UserModel2 user) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
//        requireActivity().finish();
    }
}