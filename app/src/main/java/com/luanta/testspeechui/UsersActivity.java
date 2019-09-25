package com.luanta.testspeechui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luanta.testspeechui.database.User;
import com.luanta.testspeechui.database.UserViewModel;

import java.util.List;

public class UsersActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.luanta.testspeechui.REPLY";
    public static final int NEW_USER_ACTIVITY_REQUEST_CODE = 1;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        // Set up the RecyclerView.
        RecyclerView recyclerViewUsers = findViewById(R.id.recyclerview_users);
        final UserListAdapter adapterUsers = new UserListAdapter(this);
        recyclerViewUsers.setAdapter(adapterUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));

        // Set up the UserViewModel
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        // Get all the users from the database
        // and associate them to the adapter
        mUserViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(@Nullable final List<User> users) {
                // Update the cached copy of the users in the adapter
                adapterUsers.setUsers(users);
            }
        });

        // Floating action button setup
        FloatingActionButton fab = findViewById(R.id.fab_newUser);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UsersActivity.this,NewUserActivity.class);
                startActivityForResult(intent,NEW_USER_ACTIVITY_REQUEST_CODE);
            }
        });

        // Implement onItemClick() method of UserListAdapter.ClickListener interface
        adapterUsers.setOnItemClickListener(new UserListAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent replyIntent = new Intent();
                if(position != -1) {
                    User user = adapterUsers.getUserAtPosition(position);
                    replyIntent.putExtra(EXTRA_REPLY, user.getId());
//                Log.d("_onItemClick",user.getName());
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                else {
                    // Couldn't get user position, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                }
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_USER_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            User newUser = new User(data.getStringExtra(NewUserActivity.EXTRA_REPLY));
            mUserViewModel.insert(newUser);
        }
        else {
            Toast.makeText(
                 getApplicationContext(),
                 "Not save - new user is empty!",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
