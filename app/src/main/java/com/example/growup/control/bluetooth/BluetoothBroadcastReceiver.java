package com.example.growup.control.bluetooth;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.growup.view.BluetoothActivity;

import java.util.ArrayList;

public class BluetoothBroadcastReceiver extends BroadcastReceiver {
    ArrayList<BluetoothDevice> devices = new ArrayList<>();
    BluetoothActivity bluetoothActivity;

    public BluetoothBroadcastReceiver(BluetoothActivity bluetoothActivity){
        this.bluetoothActivity = bluetoothActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            bluetoothActivity.setTextScanning();

        }
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            devices.add(device);  // MAC address
        }
        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            bluetoothActivity.setDeviceList(devices);
            bluetoothActivity.setTextFinished();
        }
    }


}
