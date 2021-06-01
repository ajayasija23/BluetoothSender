package com.ajayasija.bluetoothsender.threads;

import android.bluetooth.BluetoothSocket;
import android.os.Message;
import android.util.Log;

import com.ajayasija.bluetoothsender.listener.MsgStatusListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TransferThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final OutputStream mmOutStream;
    private final MsgStatusListener listener;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public TransferThread(BluetoothSocket socket,MsgStatusListener listener) {
        this.listener=listener;
        mmSocket = socket;
        OutputStream tmpOut = null;
        try {
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            Log.e("exception", "Error occurred when creating output stream", e);
        }
        mmOutStream = tmpOut;
    }


    // Call this from the main activity to send data to the remote device.
    public void write(String bytes) {
        try {
            mmOutStream.write(bytes.getBytes());
            listener.onMsgSuccess();
        } catch (IOException e) {
            listener.onMsgFailure();
            Log.e("error_sending", "Error occurred when sending data", e);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("close_error", "Could not close the connect socket", e);
        }
    }
}