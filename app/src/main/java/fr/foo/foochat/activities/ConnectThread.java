package fr.foo.foochat.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import fr.foo.foochat.services.BluetoothListenerService;

public class ConnectThread extends Thread {
    public enum Action {
        Connect,
        Accept
    }

    private static final String TAG = "ConnectThread";
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private final BluetoothAdapter adapter;
    private final Action action;
    private OutputStream out;

    @SuppressLint("MissingPermission")
    public ConnectThread(BluetoothAdapter adapter, BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.adapter = adapter;
        this.action = Action.Connect;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(BluetoothListenerService.UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public ConnectThread(BluetoothAdapter adapter, BluetoothSocket socket) {
        this.mmDevice = socket.getRemoteDevice();
        this.adapter = adapter;
        this.mmSocket = socket;

        this.action = Action.Accept;
    }

    @SuppressLint("MissingPermission")
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        this.adapter.cancelDiscovery();

        if (action == Action.Connect) {
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
            }


            try {
                out = new BufferedOutputStream(mmSocket.getOutputStream());
                JSONObject hello = new JSONObject();
                hello.put("action", "hello");

                JSONObject helloData = new JSONObject();
                helloData.put("name", adapter.getName());
                hello.put("data", helloData);

                Log.d(TAG, hello.toString());

                out.write(hello.toString().getBytes(StandardCharsets.UTF_8));
                out.flush();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                out = new BufferedOutputStream(mmSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            InputStream in = mmSocket.getInputStream();

            int bytesRead;
            byte[] buffer = new byte[4096];
            do {
                bytesRead = in.read(buffer);

                String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                Log.d(TAG, content);
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "connected");
    }
    
    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}
