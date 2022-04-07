package fr.foo.foochat.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import fr.foo.foochat.database.AppDatabase;
import fr.foo.foochat.database.Conversation;
import fr.foo.foochat.database.Message;
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
    private BluetoothListenerService service;

    @SuppressLint("MissingPermission")
    public ConnectThread(BluetoothListenerService service, BluetoothAdapter adapter, BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.adapter = adapter;
        this.action = Action.Connect;
        this.service = service;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(BluetoothListenerService.UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public ConnectThread(BluetoothListenerService service, BluetoothAdapter adapter, BluetoothSocket socket) {
        this.mmDevice = socket.getRemoteDevice();
        this.adapter = adapter;
        this.mmSocket = socket;
        this.service = service;

        this.action = Action.Accept;
    }

    @SuppressLint("MissingPermission")
    public void run() {
        // Cancel discovery because it otherwise slows down the connection.
        this.adapter.cancelDiscovery();

        try {
            if (action == Action.Connect) {
                mmSocket.connect();
            }

            out = new BufferedOutputStream(mmSocket.getOutputStream());
            JSONObject hello = new JSONObject();
            hello.put("action", "hello");

            JSONObject helloData = new JSONObject();
            helloData.put("name", adapter.getName());
            hello.put("data", helloData);

            Log.d(TAG, hello.toString());

            out.write(hello.toString().getBytes(StandardCharsets.UTF_8));
            out.flush();

            InputStream in = mmSocket.getInputStream();

            int bytesRead;
            byte[] buffer = new byte[4096];
            do {
                bytesRead = in.read(buffer);

                String content = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                JSONObject object = new JSONObject(content);

                if (object.getString("action").equals("hello")) {
                    JSONObject o = object.getJSONObject("data");
                    AppDatabase.getInstance(null).convDao().insertConv(
                            new Conversation(
                                    mmSocket.getRemoteDevice().getAddress(),
                                    o.getString("name"),
                                    null
                            )
                    );
                } else if (object.getString("action").equals("message")) {
                    JSONObject o = object.getJSONObject("data");
                    AppDatabase.getInstance(null).msgDao().insertMessage(
                            new Message(
                                    System.currentTimeMillis(),
                                    o.getString("content"),
                                    Base64.decode(o.getString("image"), Base64.DEFAULT)
                            )
                    );
                }
            } while (true);

        } catch (Exception e) {
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }

            e.printStackTrace();
        }
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
