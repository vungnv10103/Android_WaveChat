package com.vungnv.chatapp.activities.chat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.vungnv.chatapp.R;
import com.vungnv.chatapp.adapters.ChatAdapter;
import com.vungnv.chatapp.models.ChatMessageModel;
import com.vungnv.chatapp.models.UserModel2;
import com.vungnv.chatapp.utils.Constants;
import com.vungnv.chatapp.utils.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private UserModel2 receivedUser;
    private TextView tvNameUser;
    private ImageView imgBack;
    private ProgressBar progressBar;
    private FrameLayout layoutSend;
    private ChatAdapter chatAdapter;
    private FirebaseFirestore firebaseFirestore;
    private PreferenceManager preferenceManager;
    private List<ChatMessageModel> listChat;
    private RecyclerView rcvChat;
    private EditText inputMessage;
    private String conversationId = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        imgBack.setOnClickListener(v -> onBackPressed());

        loadReceivedDetail();
        listenMessage();
        setData();
        layoutSend.setOnClickListener(v -> sendMessage());

    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        listChat = new ArrayList<>();
        imgBack = findViewById(R.id.imgBack);
        tvNameUser = findViewById(R.id.tvNameUser);
        rcvChat = findViewById(R.id.rcvChat);
        firebaseFirestore = FirebaseFirestore.getInstance();
        inputMessage = findViewById(R.id.inputMessage);
        layoutSend = findViewById(R.id.layoutSend);
        progressBar = findViewById(R.id.progressBar);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setData() {
        chatAdapter = new ChatAdapter(ChatActivity.this,
                listChat, getImageProfile(receivedUser.image),
                preferenceManager.getString(Constants.KEY_USER_ID));
        rcvChat.setAdapter(chatAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void sendMessage() {
        String msgContent = inputMessage.getText().toString().trim();
        if (msgContent.length() == 0) {
            return;
        }
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVED_ID, receivedUser.id);
        message.put(Constants.KEY_MESSAGE, msgContent);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT).add(message);
        if (conversationId != null) {
            Toast.makeText(this, "update", Toast.LENGTH_SHORT).show();
            updateConversation(msgContent);
        } else {
            Toast.makeText(this, "add", Toast.LENGTH_SHORT).show();
            HashMap<String, Object> conversation = new HashMap<>();
            conversation.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversation.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_NAME));
            conversation.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_IMAGE));
            conversation.put(Constants.KEY_RECEIVED_ID, receivedUser.id);
            conversation.put(Constants.KEY_RECEIVED_NAME, receivedUser.name);
            conversation.put(Constants.KEY_RECEIVED_IMAGE, receivedUser.image);
            conversation.put(Constants.KEY_LATEST_MESSAGE, msgContent);
            conversation.put(Constants.KEY_TIMESTAMP, new Date());

            addConversation(conversation);
        }
        inputMessage.setText(null);
    }

    private void listenMessage() {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVED_ID, receivedUser.id)
                .addSnapshotListener(eventListener);
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receivedUser.id)
                .whereEqualTo(Constants.KEY_RECEIVED_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);


    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = listChat.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessageModel messageModel = new ChatMessageModel();
                    messageModel.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    messageModel.receivedId = documentChange.getDocument().getString(Constants.KEY_RECEIVED_ID);
                    messageModel.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    messageModel.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    messageModel.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);

                    listChat.add(messageModel);
                }
            }
            Collections.sort(listChat, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(listChat.size(), listChat.size());
                rcvChat.smoothScrollToPosition(listChat.size() - 1);

            }
            rcvChat.setVisibility(View.VISIBLE);
        } else {
            rcvChat.setVisibility(View.GONE);
            if (conversationId == null) {
                checkForConversation();
            }
        }
    };

    private void loadReceivedDetail() {
        receivedUser = (UserModel2) getIntent().getSerializableExtra(Constants.KEY_USER);
        tvNameUser.setText(receivedUser.name);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Bitmap getImageProfile(String encodeImage) {
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConversation(HashMap<String, Object> conversation) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .add(conversation)
                .addOnSuccessListener(documentReference -> {
                    conversationId = documentReference.getId();
                });
    }

    private void checkForConversation() {
        if (listChat.size() != 0) {
            checkForConversationRemotely(preferenceManager.getString(Constants.KEY_USER_ID), receivedUser.id);
            checkForConversationRemotely(receivedUser.id, preferenceManager.getString(Constants.KEY_USER_ID));
        }
    }

    private void updateConversation(String message) {
        DocumentReference documentReference = firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION).document(conversationId);
        documentReference.update(
                Constants.KEY_LATEST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date()
        );
    }

    private void checkForConversationRemotely(String senderId, String receivedId) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVED_ID, receivedId)
                .get()
                .addOnCompleteListener(conversationCompleteListener);

    }

    private final OnCompleteListener<QuerySnapshot> conversationCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
            conversationId = snapshot.getId();
        }
    };

}