package fr.foo.foochat.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.foo.foochat.adapters.BluetoothDeviceAdapter;
import fr.foo.foochat.databinding.ActivityDeviceDiscoverBinding;

public class DeviceDiscoverActivity extends AppCompatActivity {
    private static final String TAG = "DeviceDiscoverActivity";
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    ActivityDeviceDiscoverBinding binding;


    List<BluetoothDeviceAdapter.Device> devices = new ArrayList<>();
    private final BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this.devices);

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, String.format("%s %s", deviceName, deviceHardwareAddress));
                if (deviceName != null) {
                    BluetoothDeviceAdapter.Device deviceToAdd = new BluetoothDeviceAdapter.Device(deviceName, deviceHardwareAddress);
                    if (!devices.contains(deviceToAdd)) {
                        DeviceDiscoverActivity.this.devices.add(deviceToAdd);
                        adapter.notifyItemInserted(DeviceDiscoverActivity.this.devices.size() - 1);
                    }
                }
            }
        }
    };


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceDiscoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.topAppBar);

        binding.deviceRecyclerView.setAdapter(adapter);
        binding.deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.d(TAG, String.format("paired %s : %s", deviceName, deviceHardwareAddress));

            }
        }

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        int requestCode = 1;
        Intent discoverableIntent =
                new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, requestCode);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }


        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }

}