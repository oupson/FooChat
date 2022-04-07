package fr.foo.foochat.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.foo.foochat.R;

public class ConversationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }
}