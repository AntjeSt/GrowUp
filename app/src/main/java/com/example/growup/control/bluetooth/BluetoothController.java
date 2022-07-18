package com.example.growup.control.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BluetoothController {

    /**
     * starts the Thread that listens for incoming enquiries to connect to the device (Serversocket)
     */
    void startAcceptThread();


    /**
     * starts the acceptThread, tries to connect to a serverSocket
     */
    void startConnectThread(BluetoothDevice currentDevice);

    /**
     * send the plant data to remote user
     */
    void sendPlantListAsByteArray();

}
