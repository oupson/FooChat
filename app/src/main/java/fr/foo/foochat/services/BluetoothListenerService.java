package fr.foo.foochat.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.UUID;

import fr.foo.foochat.BuildConfig;
import fr.foo.foochat.R;
import fr.foo.foochat.activities.ConnectThread;
import fr.foo.foochat.activities.MainActivity;

public class BluetoothListenerService extends Service {
    private static final String TAG = "BluetoothLService";
    public static final String NAME = BuildConfig.APPLICATION_ID;
    public static final UUID UUID = java.util.UUID.fromString("e250dce6-9f6f-461d-8005-e24a435992b4");
    private static final int NOTIFICATION_ID = 507;

    private static class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private final BluetoothAdapter bluetoothAdapter;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    Log.d(TAG, "new conn");
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }

    }

    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        public BluetoothListenerService getService() {
            return BluetoothListenerService.this;
        }
    }

    private AcceptThread acceptThread;

    public BluetoothListenerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0); // TODO FIX
        Notification notification = new NotificationCompat.Builder(this.getBaseContext(), BuildConfig.APPLICATION_ID + ".foreground_service")
                .setContentTitle("notificationTitle") // TODO
                .setContentText("input") // TODO
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(NOTIFICATION_ID, notification);

        acceptThread = new AcceptThread();
        acceptThread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        try {
            acceptThread.cancel();
            acceptThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // TODO CALLBACK
    public void connectToDevice(BluetoothDevice device) {
        ConnectThread thread = new ConnectThread(acceptThread.bluetoothAdapter, device);

        thread.start();
    }
}