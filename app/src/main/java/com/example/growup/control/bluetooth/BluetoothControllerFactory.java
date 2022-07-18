package com.example.growup.control.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

public class BluetoothControllerFactory {
    public static BluetoothController produceBluetootController (Context context, Handler mainThreadHandler){
        return new BluetoothControllerImpl(context, mainThreadHandler);
    }

}
