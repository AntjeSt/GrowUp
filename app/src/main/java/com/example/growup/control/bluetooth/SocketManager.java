package com.example.growup.control.bluetooth;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class SocketManager {

    //Context context;
    Handler handler;
    BluetoothDevice currentDevice;
    BluetoothDevice connectedDevice;
    ConnectThread connectThread;
    AcceptThread acceptThread;
    ExchangeThread exchangeThread;

    private static final UUID MY_UUID = UUID.fromString("eebfc475-6710-4be4-ad63-ad43bcdf9d10");
    public static final String NAME = "GrowUp";

    public static final int STATE_CONNECTING = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_CONNECTEDDEVICE = 10;
    public static final int STATE_EXCHANGE = 2;
    public static final int STATE_FINISHED = 3;
    public static final int STATE_CONNECTING_FAILED = -1;
    public static final int STATE_RECEIVED_LIST = 20;
    public static final int STATE_EXCHANGE_FAILED = -2;


    private int state;

    public SocketManager(Handler mainThreadHandler, BluetoothDevice btDevice) {
        this.handler = mainThreadHandler;
        this.currentDevice = btDevice;
    }

    public SocketManager( Handler mainThreadHandler) {
        this.handler = mainThreadHandler;
    }

    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public int getState() {
        return state;
    }

    public void startConnectThread() {
       connectThread = new ConnectThread(currentDevice);
        connectThread.start();
        System.out.println("connectThread started");

    }

    public void startAcceptThread(){
        acceptThread = new AcceptThread();
        acceptThread.start();
        System.out.println("Accept Thread started");

    }

    public synchronized void connect(BluetoothSocket socket){

        exchangeThread = new ExchangeThread(socket);
        exchangeThread.start();

    }

    private class AcceptThread extends Thread {

            private final BluetoothServerSocket serverSocket;

            public AcceptThread() {
                // Use a temporary object that is later assigned to ServerSocket
                // because ServerSocket is final.
                BluetoothServerSocket tmp = null;

                try {
                    // MY_UUID is the app's UUID string, also used by the client code.
                    tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
                } catch (IOException e) {
                    Log.e(TAG, "Socket's listen() method failed", e);
                }
                serverSocket = tmp;
            }

            public void run() {
                BluetoothSocket socket = null;
                // Keep listening until exception occurs or a socket is returned.
                while (true) {
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        Log.e(TAG, "Socket's accept() method failed", e);
                        break;
                    }



                    if (socket != null) {

                        connectedDevice = socket.getRemoteDevice();
                        Message msg = new Message();
                        msg.obj = connectedDevice.getName();
                        msg.what = 10;
                        handler.sendMessage(msg);

                        connect(socket);
                        try {
                            serverSocket.close();
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

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device1) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            device = device1;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.

                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                handler.obtainMessage(0).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            socket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect();

            } catch (IOException connectException) {

                handler.obtainMessage(-1).sendToTarget();
                // Unable to connect; close the socket and return.
                try {
                    socket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            handler.obtainMessage(1).sendToTarget();

            connectedDevice = socket.getRemoteDevice();
            Message msg = new Message();
            msg.obj = connectedDevice.getName();
            msg.what = 10;
            handler.sendMessage(msg);

            connect(socket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ExchangeThread extends Thread {

        private final BluetoothSocket socket;
        private final InputStream ips;
        private final OutputStream ops;
        private byte[] buffer; //buffer store for the stream

        public ExchangeThread(BluetoothSocket bluetoothSocket) {
            System.out.println("ExchangeThread started");
            socket = bluetoothSocket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            ips = tmpIn;
            ops = tmpOut;
        }

        public void run() {
            buffer = new byte[1024];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    System.out.println("reading");
                    // Read from the InputStream.
                    numBytes = ips.read(buffer);
                    // Send the obtained bytes to the UI activity.
                    Message readMsg = handler.obtainMessage(20, numBytes,-1, buffer);
                    readMsg.sendToTarget();

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                ops.write(bytes);

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                handler.obtainMessage(-2).sendToTarget();
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

    public void write(byte [] out){
        ExchangeThread tmp;
        Log.d(TAG, "copy ExchangeThread");
        exchangeThread.write(out);
    }


}




