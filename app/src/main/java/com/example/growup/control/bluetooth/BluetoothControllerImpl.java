package com.example.growup.control.bluetooth;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.growup.control.plant.PlantController;
import com.example.growup.control.plant.PlantControllerFactory;
import com.example.growup.plantModel.Plant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

class BluetoothControllerImpl implements BluetoothController {

    Context context;
    SocketManager socketManager;
    Handler handler;
    PlantController plantController;

    BluetoothControllerImpl(Context context, Handler mainThreadHandler) {
        this.context = context;
        this.handler = mainThreadHandler;
        this.plantController = new PlantControllerFactory().producePlantController(context);
    }


    @Override

    public void startAcceptThread() {
        System.out.println("startAcceptThread:" + handler);
        socketManager = new SocketManager(handler);
        socketManager.startAcceptThread();
        setState(socketManager.getState());
    }


    @Override
    public void sendPlantListAsByteArray() {
        ArrayList<Plant> plantList = plantController.getOrderedPlantList();
        if (plantList != null) {
            socketManager.write(plantListToByteArray(plantList));
            System.out.println("send data");
        }
    }

    public byte[] plantListToByteArray(ArrayList<Plant> plantList) {
        byte[] plantlistByteArray;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(plantList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plantlistByteArray = baos.toByteArray();
        return plantlistByteArray;
    }

    @Override
    public void startConnectThread(BluetoothDevice currentDevice) {
        socketManager = new SocketManager(handler, currentDevice);
        socketManager.startConnectThread();
    }

    public synchronized void setState(int state) {
        int state1 = socketManager.getState();
        handler.obtainMessage(state1).sendToTarget();
    }

}
