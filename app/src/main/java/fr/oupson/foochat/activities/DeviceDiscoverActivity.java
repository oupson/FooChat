package fr.oupson.foochat.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import fr.oupson.foochat.adapters.BluetoothDeviceAdapter;
import fr.oupson.foochat.databinding.ActivityDeviceDiscoverBinding;
import fr.oupson.foochat.services.BluetoothListenerService;

public class DeviceDiscoverActivity extends AppCompatActivity {
    private static final String TAG = "DeviceDiscoverActivity";
    private static final int BLUETOOTH_REQUEST_CODE = 404;

    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;

    ActivityDeviceDiscoverBinding binding;

    BluetoothListenerService service;
    boolean bound;

    private final ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.d(TAG, "onServiceConnected");

            BluetoothListenerService.LocalBinder binder = (BluetoothListenerService.LocalBinder) service;
            DeviceDiscoverActivity.this.service = binder.getService();
            bound = true;
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.e(TAG, "onServiceDisconnected");
            bound = false;
        }
    };


    final List<BluetoothDeviceAdapter.Device> devices = new ArrayList<>();
    private final BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this.devices, (device) -> {
        Log.d(TAG, device.toString());


        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(device.getMacAddress());

        service.connectToDevice(remoteDevice);
    });

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
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "scan finished");
                binding.bluetoothSwipeRefreshLayout.setRefreshing(false);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeviceDiscoverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.deviceRecyclerView.setAdapter(adapter);
        binding.deviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_FINE_LOCATION}, BLUETOOTH_REQUEST_CODE);
        } else {
            setupBluetooth();
        }
    }

    @SuppressLint("MissingPermission")
    private void setupBluetooth() {
        pairedDevices = bluetoothAdapter.getBondedDevices();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);

        int requestCode = 1;
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivityForResult(discoverableIntent, requestCode);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        bluetoothAdapter.startDiscovery();
        binding.bluetoothSwipeRefreshLayout.setRefreshing(true);
        binding.bluetoothSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (!bluetoothAdapter.isDiscovering()) {
                int size = devices.size();
                devices.clear();
                adapter.notifyItemRangeRemoved(0, size);

                bluetoothAdapter.startDiscovery();
                binding.bluetoothSwipeRefreshLayout.setRefreshing(true);
            }
        });

        Intent intent = new Intent(this, BluetoothListenerService.class);
        bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            bluetoothAdapter.cancelDiscovery();
        }

        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == BLUETOOTH_REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, BLUETOOTH_REQUEST_CODE); // TODO
            } else {
                setupBluetooth();
            }
        }
    }
}