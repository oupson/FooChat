package fr.oupson.foochat.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import fr.oupson.foochat.BuildConfig;
import fr.oupson.foochat.adapters.MessageAdapter;
import fr.oupson.foochat.database.AppDatabase;
import fr.oupson.foochat.database.Message;
import fr.oupson.foochat.databinding.ActivityConversationBinding;
import fr.oupson.foochat.services.BluetoothListenerService;

public class ConversationActivity extends AppCompatActivity {
    public static final String CONVERSATION_ID = BuildConfig.APPLICATION_ID + ".conv_id";
    private static final String TAG = "ConversationActivity";
    private ActivityConversationBinding binding;
    private BluetoothAdapter bluetoothAdapter;

    private final List<Message> messageList = new ArrayList<>();
    private final MessageAdapter adapter = new MessageAdapter(messageList, (msg) -> {
    });

    String macAddress;
    BluetoothListenerService service;
    boolean bound;

    private final ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");

            BluetoothListenerService.LocalBinder binder = (BluetoothListenerService.LocalBinder) service;
            ConversationActivity.this.service = binder.getService();

            if (!(ConversationActivity.this.service.getClients().containsKey(macAddress)
                    && ConversationActivity.this.service.getClients().get(macAddress).isAlive())) {
                ConversationActivity.this.service.connectToDevice(bluetoothAdapter.getRemoteDevice(macAddress));
            }
            bound = true;
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConversationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.topAppBar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.messagesRecyclerView.setAdapter(adapter);
        binding.messagesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = AppDatabase.getInstance(this);
        macAddress = getIntent().getStringExtra(CONVERSATION_ID);

        db.convDao().getConv(macAddress).observe(this, (conv) ->
                getSupportActionBar().setTitle(conv.titreConv)
        );

        db.msgDao().getAllObservable(macAddress).observe(this, (msgList) -> {
            messageList.clear();
            messageList.addAll(msgList);
            adapter.notifyDataSetChanged();
            binding.messagesRecyclerView.scrollToPosition(msgList.size() - 1);
        });


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent intent = new Intent(this, BluetoothListenerService.class);
        bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);

        binding.sendImageButton.setOnClickListener((view) -> {
            service.getClients().get(macAddress).sendMessage(binding.messageInputLayout.getEditText().getText().toString());
            binding.messageInputLayout.getEditText().setText(null);
        });
    }
}