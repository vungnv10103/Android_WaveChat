package com.vungnv.chatapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vungnv.chatapp.R;
import com.vungnv.chatapp.models.ChatMessageModel;

import java.util.Base64;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessageModel> chatMessage;
    private Context context;
    private final Bitmap receiverImageProfile;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public ChatAdapter(Context context, List<ChatMessageModel> chatMessage, Bitmap receiverImageProfile, String senderId) {
        this.context = context;
        this.chatMessage = chatMessage;
        this.receiverImageProfile = receiverImageProfile;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View viewSent = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_send_message, parent, false);
            return new SentMessageViewHolder(viewSent);
        } else {
            View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_received_message, parent, false);
            return new ReceivedMessageViewHolder(viewReceived);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessage.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessage.get(position), receiverImageProfile);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }

    }


    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDateTime;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDateTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "sent", Toast.LENGTH_SHORT).show();
            });
        }

        void setData(ChatMessageModel chat) {
            tvMessage.setText(chat.message);
            tvDateTime.setText(chat.dateTime);
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvDateTime;
        ImageView imgProfile;


        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDateTime = itemView.findViewById(R.id.tvTime);

            itemView.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "received", Toast.LENGTH_SHORT).show();
            });
        }

        void setData(ChatMessageModel chat, Bitmap receivedImageProfile) {
            imgProfile.setImageBitmap(receivedImageProfile);
            tvMessage.setText(chat.message);
            tvDateTime.setText(chat.dateTime);
        }
    }

    @SuppressLint("NewApi")
    private Bitmap getImageProfile(String encodeImage) {
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

}
