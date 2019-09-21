package com.luanta.testspeechui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewUserActivity extends AppCompatActivity {
    public static final String EXTRA_REPLY = "com.luanta.testspeechui.REPLY";
    private EditText mEditUserView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        mEditUserView = findViewById(R.id.editText_user);

        final Button button = findViewById(R.id.button_save_user);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyIntent = new Intent();

                if(TextUtils.isEmpty(mEditUserView.getText())) {
                    setResult(RESULT_CANCELED,replyIntent);
                }
                else {
                    String newName = mEditUserView.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, newName);
                    setResult(RESULT_OK,replyIntent);
                }
                finish();
            }
        });
    }
}
