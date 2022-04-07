package fr.foo.foochat.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fr.foo.foochat.BuildConfig;
import fr.foo.foochat.R;

public class ConversationActivity extends AppCompatActivity {
    public static final String CONVERSATION_ID = BuildConfig.APPLICATION_ID + ".conv_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }
}