package com.ajayasija.bluetoothsender.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.ajayasija.bluetoothsender.listener.ConnectionStatusListener;

import java.io.IOException;
import java.util.UUID;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;

    private final String NAME="BLUETOOTH_SERVICE";
    private final String MY_UUID="30096f4b-6403-4fc5-a439-b3575ed64fed";
    private final ConnectionStatusListener listener;

    public AcceptThread(BluetoothAdapter bluetoothAdapter,ConnectionStatusListener listener) {
        // Use a temporary object that is later assigned to mmServerSocket
        // because mmServerSocket is final.
        this.listener=listener;
        BluetoothServerSocket tmp = null;
        try {
            // MY_UUID is the app's UUID string, also used by the client code.
            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, UUID.fromString(MY_UUID));
        } catch (IOException e) {
            Log.e("ioexception", "Socket's listen() method failed", e);
        }
        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e("error_acception", "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                // A connection was accepted. Perform work associated with
                // the connection in a separate thread.

                try {
                    listener.onSucces(socket);
                    mmServerSocket.close();
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
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e("error_closing", "Could not close the connect socket", e);
        }
    }
}
