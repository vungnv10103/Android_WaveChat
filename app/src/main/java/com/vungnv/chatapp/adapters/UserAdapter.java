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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vungnv.chatapp.R;
import com.vungnv.chatapp.listeners.IUserListener;
import com.vungnv.chatapp.models.UserModel2;

import java.util.Base64;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static List<UserModel2> listUser;
    private Context context;
    private static IUserListener userListener;

    public UserAdapter(Context context, List<UserModel2> listUser, IUserListener userListener) {
        this.context = context;
        UserAdapter.listUser = listUser;
        UserAdapter.userListener = userListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_countainer_user, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel2 user = listUser.get(position);
        holder.tvName.setText(user.name);
//        holder.tvLatestMsg.setText(user.latestMsg);
        holder.tvLatestMsg.setText(user.email);
        holder.imgProfile.setImageBitmap(getImageProfile(user.image));


    }

    @Override
    public int getItemCount() {
        if (listUser.size() != 0) {
            return listUser.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLatestMsg;
        ImageView imgProfile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvNameUser);
            tvLatestMsg = itemView.findViewById(R.id.tvLatestMsg);


            itemView.setOnClickListener(v -> {
                userListener.onUserClicked(listUser.get(getAdapterPosition()));
            });
        }
    }

    @SuppressLint("NewApi")
    private Bitmap getImageProfile(String encodeImage) {
        byte[] bytes = Base64.getDecoder().decode(encodeImage);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
