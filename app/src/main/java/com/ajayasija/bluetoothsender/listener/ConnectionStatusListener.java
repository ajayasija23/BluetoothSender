package com.ajayasija.bluetoothsender.listener;

import android.bluetooth.BluetoothSocket;

public interface ConnectionStatusListener {
    void onError();
    void onSucces(BluetoothSocket name);
}
