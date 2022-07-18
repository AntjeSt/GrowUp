package com.example.growup.view;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;
import static androidx.core.app.ActivityCompat.startActivityForResult;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.growup.R;
import com.example.growup.control.bluetooth.BluetoothBroadcastReceiver;
import com.example.growup.control.bluetooth.BluetoothController;
import com.example.growup.control.bluetooth.BluetoothControllerFactory;
import com.example.growup.control.bluetooth.SocketManager;
import com.example.growup.control.plant.PlantController;
import com.example.growup.control.plant.PlantControllerFactory;
import com.example.growup.plantModel.Plant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Set;


public class BluetoothActivity extends AppCompatActivity implements DiscoveredDevicesAdapter.ItemClickListener{

    private static final int MESSAGE_STATE_CHANGED = 0;


    BottomNavigationView bottomNavigationView;
    Boolean isTouched = false;
    Button searchButton;
    CompoundButton switchButton;
    Button sendPlantsButton;
    BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    TextView bondedDevice;
    ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<>() ;
    RecyclerView discoveredDevicesReyclerview;
    DiscoveredDevicesAdapter discoveredDevicesAdapter;
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice currentDevice;
    Button deviceButton;
    BluetoothController bluetoothController;
    PlantController plantController;
    boolean connected = false;


    Handler mainThreadHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case -1:
                    setState("Failed");
                    break;
                case 0:
                    setState("Connecting...");
                    break;
                case 1:
                    setState("Connected");
                    connected = true;
                    break;
                case 10:
                    setTextBonded((String) message.obj);
                case 2:
                    setState("Exchange");
                    break;
                case 20:
                    setState("Reading");
                    setAndSynchronizePlantList((byte[]) message.obj);
                case 3:
                    setState("Finished");
                    break;
            }
            return false;
        }
    });



    private void setState(String s) {
      if (deviceButton != null){
        deviceButton.setText(s);}
    }

    private void setAndSynchronizePlantList (byte[] remoteplantListArray){
        plantController = PlantControllerFactory.producePlantController(getApplicationContext());
        plantController.setAndSynchronizePlantList(remoteplantListArray);
    }

    private void setTextBonded(String s) {
      bondedDevice.setText(s);
      sendPlantsButton.setVisibility(View.VISIBLE);
    }


    ActivityResultLauncher<Intent> requestBluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                bluetoothController.startAcceptThread();
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
                requestDiscoverable.launch(discoverableIntent);
            }
            else {
                switchButton.setChecked(false);
            }
        }
    });

    ActivityResultLauncher<Intent> requestDiscoverable = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Log.d(TAG, "Discoverable was granted");
            }
        }
    });

    ActivityResultLauncher<String[]> bluetoothPermissionRequest =
            registerForActivityResult(new ActivityResultContracts
                            .RequestMultiplePermissions(), result -> {
                        Boolean connectGranted = result.getOrDefault(
                                Manifest.permission.BLUETOOTH_CONNECT, false);
                        Boolean scanGranted = result.getOrDefault(
                                Manifest.permission.BLUETOOTH_SCAN,false);
                        Boolean advertiseGranted = result.getOrDefault(
                                Manifest.permission.BLUETOOTH_ADVERTISE,false);
                        Boolean fineGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION,false);
                        Boolean coarseGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_COARSE_LOCATION,false);
                        if (connectGranted != null && scanGranted != null && scanGranted && advertiseGranted != null && advertiseGranted && fineGranted != null && fineGranted && coarseGranted != null && coarseGranted) {
                            System.out.println("granted all");

                        }else {
                            Toast.makeText(this, "You will not be able to exchange plant data",Toast.LENGTH_SHORT);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver(BluetoothActivity.this);
        bluetoothController = BluetoothControllerFactory.produceBluetootController(BluetoothActivity.this, mainThreadHandler);
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();

        if (bluetoothAdapter == null) {
            Toast.makeText(BluetoothActivity.this,"Apparently your device doesn't support Bluetooth",Toast.LENGTH_SHORT);
        }

        bottomNavigationView= findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.bluetoothTab);

        bondedDevice = findViewById(R.id.bondedDevice);
        switchButton = findViewById(R.id.btSwitch);
        searchButton = findViewById(R.id.searchDevices);
        sendPlantsButton = findViewById(R.id.sendData);
        sendPlantsButton.setVisibility(View.GONE);


        switchButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isTouched = true;
                return false;

            }
        });

        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (isTouched && isChecked) {
                    isTouched = false;

                    bluetoothPermissionRequest.launch(new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    });

                    Intent btIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    requestBluetooth.launch(btIntent);


                            /*  / get paired devices
                                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                                if (pairedDevices.size() > 0) {
                                    // There are paired devices. Get the name and address of each paired device.
                                    for (BluetoothDevice device : pairedDevices) {
                                        String deviceName = device.getName();
                                        String deviceHardwareAddress = device.getAddress(); // MAC address
                                    }
                                }*/
                }

                    else if (isTouched && !isChecked) {
                    bluetoothAdapter.disable();
                    }
                }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                    searchButton.setText("SCAN FOR DEVICES");
                    System.out.println("canceled discovery");

                }

                bluetoothAdapter.startDiscovery();

                IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(bluetoothBroadcastReceiver, discoverDevicesIntent);

                IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                registerReceiver(bluetoothBroadcastReceiver, filterStart);

                IntentFilter filterFinish = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(bluetoothBroadcastReceiver, filterFinish);

            }
        });

        sendPlantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bluetoothController.sendPlantListAsByteArray();
            }
        });

        //item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myplantsTab:
                        startActivity(new Intent(getApplicationContext(),PlantListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.bluetoothTab:
                        return true;
                    case R.id.addplantTab:
                        startActivity(new Intent(getApplicationContext(),AddPlantActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

    }

/*    private boolean hasBluetoothPermissions () {
        if (checkAndroidVersion() == 1) {
            if (ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("has all permissions");
            return true;
            }
            else {
                System.out.println("not all permissions");}
        }
        else {
            if (
            ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(BluetoothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
            }
        }
        return false;
    }*/

/*    private int checkAndroidVersion (){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            return 1;
        } else {
            return 0;
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //needs to be disabled for navigationtest to work
        unregisterReceiver(bluetoothBroadcastReceiver);
    }

    @Override
    public void onButtonClick(Button connectBtn, int position) {
        deviceButton = connectBtn;
        String s = deviceButton.getText().toString();
        if (s.equals("connect")){
        currentDevice = discoveredDevicesAdapter.getItem(position);
        bluetoothController.startConnectThread(currentDevice);}
    }


    public void setDeviceList(ArrayList<BluetoothDevice> bluetoothDeviceArraylist) {
        this.discoveredDevices = bluetoothDeviceArraylist;
        discoveredDevicesReyclerview = findViewById(R.id.discoveredRv);
        discoveredDevicesReyclerview.setLayoutManager(new LinearLayoutManager(this));
        discoveredDevicesAdapter = new DiscoveredDevicesAdapter(this, discoveredDevices);
        discoveredDevicesAdapter.setClickListener(this);
        discoveredDevicesReyclerview.setAdapter(discoveredDevicesAdapter);

    }

    public void setTextScanning() {
        searchButton.setText("SCANNING...");
    }

    public void setTextFinished() {
        searchButton.setText("SCAN FOR DEVICES");

    }
}