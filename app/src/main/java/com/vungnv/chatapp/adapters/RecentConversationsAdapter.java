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
import com.vungnv.chatapp.listeners.IConversionListener;
import com.vungnv.chatapp.models.ChatMessageModel;
import com.vungnv.chatapp.models.UserModel2;

import java.util.Base64;
import java.util.List;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.RecentConversationsViewHolder> {
    private Context context;
    private static List<ChatMessageModel> listChat;
    private static IConversionListener iConversionListener;

    public RecentConversationsAdapter(Context context, List<ChatMessageModel> listChat, IConversionListener iConversionListener) {
        this.context = context;
        RecentConversationsAdapter.listChat = listChat;
        RecentConversationsAdapter.iConversionListener = iConversionListener;
    }

    @NonNull
    @Override
    public RecentConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewReceived = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countainer_recent_conversation, parent, false);
        return new RecentConversationsAdapter.RecentConversationsViewHolder(viewReceived);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationsViewHolder holder, int position) {
        holder.setData(listChat.get(position));
    }

    @Override
    public int getItemCount() {
        return listChat.size();
    }


    static class RecentConversationsViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRecentConversion;
        ImageView imgProfile;


        public RecentConversationsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvNameUser);
            tvRecentConversion = itemView.findViewById(R.id.tvLatestMsg);

            itemView.setOnClickListener(v -> {
                ChatMessageModel model = listChat.get(getAdapterPosition());
                UserModel2 user = new UserModel2();
                user.id = model.conversionId;
                user.name = model.conversionName;
                user.image = model.conversionImage;
                iConversionListener.onConversionClicked(user);
            });
        }

        void setData(ChatMessageModel chat) {
            imgProfile.setImageBitmap(getImageProfile(chat.conversionImage));
            tvName.setText(chat.conversionName);
            tvRecentConversion.setText(chat.message);
        }
    }


    @SuppressLint("NewApi")
    private static Bitmap getImageProfile(String encodeImage) {
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
