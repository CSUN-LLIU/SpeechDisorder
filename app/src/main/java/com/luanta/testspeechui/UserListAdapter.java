package com.luanta.testspeechui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.luanta.testspeechui.database.User;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserViewHolder> {
    private final LayoutInflater mInflater;
    private List<User> mUsers; // cached copy of users
    private static ClickListener clickListener;

    UserListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_user_item,parent,false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        if(mUsers != null){
            User current = mUsers.get(position);
            holder.userItemView.setText(current.getName());
        }
        else {
            // no data available yet
            holder.userItemView.setText("No user!");
        }

    }

    @Override
    public int getItemCount() {
        if(mUsers != null) {
            return mUsers.size();
        }
        else {
            return 0;
        }
    }

    void setUsers(List<User> users) {
        mUsers = users;
        notifyDataSetChanged();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{
        private final TextView userItemView;

        private UserViewHolder(View itemView) {
            super(itemView);
            userItemView = itemView.findViewById(R.id.textView_userItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickListener.onItemClick(view, getAdapterPosition());
                }
            });
        }
    }

    public User getUserAtPosition(int position){
        return mUsers.get(position);
    }

    public void setOnItemClickListener(ClickListener clickListener){
        UserListAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(View v, int position);
    }
}

