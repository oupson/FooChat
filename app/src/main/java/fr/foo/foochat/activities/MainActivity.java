package fr.foo.foochat.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import fr.foo.foochat.BuildConfig;
import fr.foo.foochat.R;
import fr.foo.foochat.databinding.ActivityMainBinding;
import fr.foo.foochat.services.BluetoothListenerService;

public class MainActivity extends AppCompatActivity {
    private static final int BLUETOOTH_REQUEST_CODE = 404;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createNotificationChannels();

        Context context = getApplicationContext();
        Intent intent = new Intent(this, BluetoothListenerService.class); // Build the intent for the service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION}, BLUETOOTH_REQUEST_CODE);
        }

        binding.addConversationFloatingActionButton.setOnClickListener((v) -> startActivity(new Intent(this, DeviceDiscoverActivity.class)));
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    BuildConfig.APPLICATION_ID + ".foreground_service",
                    getString(R.string.channel_foreground_service_name),
                    NotificationManager.IMPORTANCE_NONE
            );
            channel.setDescription(getString(R.string.channel_foreground_service_description));
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_REQUEST_CODE); // TODO
            }
        }
    }
}