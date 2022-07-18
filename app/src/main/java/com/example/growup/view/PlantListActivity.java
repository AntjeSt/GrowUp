package com.example.growup.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.growup.R;
import com.example.growup.control.plant.DateManager;
import com.example.growup.control.plant.DateManagerFactory;
import com.example.growup.control.plant.PlantController;
import com.example.growup.control.plant.PlantControllerFactory;
import com.example.growup.control.plant.WateringAlertReceiver;
import com.example.growup.plantModel.Plant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.Calendar;

public class PlantListActivity extends AppCompatActivity implements PlantListAdapter.ItemClickListener{

    AlarmManager alarmManager;
    DateManager dateManager;
    DatePickerDialog datePickerDialog;

    public static final String EXTRA_PLANT = "com.example.growup.plant";
    ArrayList<Plant> plants;
    PlantController plantController;
    BottomNavigationView bottomNavigationView;
    int currentPlantPosition;
    CheckBox checkBox;
    RecyclerView recyclerView;
    PlantListAdapter adapter;

    private static final String CHANNEL_ID = "WateringReminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plantlist);
        dateManager = DateManagerFactory.produceDateManager();
        plantController = PlantControllerFactory.producePlantController(getApplicationContext());
        plants = plantController.getOrderedPlantList();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.myplantsTab);
        initDatePicker();


        //item selected listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.myplantsTab:
                        return true;
                    case R.id.bluetoothTab:
                        startActivity(new Intent(getApplicationContext(),BluetoothActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.addplantTab:
                        startActivity(new Intent(getApplicationContext(),AddPlantActivity.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

        if (plants != null){
        recyclerView = findViewById(R.id.rv_plants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlantListAdapter(this, plants, plantController);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);}


       // enableRepeatingAlarm();  first try repeating alarm
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = dateManager.convertDateString(day, month, year);
                plantController.updateWateringDates(adapter.getItem(currentPlantPosition),date);
                plants = plantController.getOrderedPlantList();
                    RecyclerView recyclerView = findViewById(R.id.rv_plants);
                    recyclerView.setLayoutManager(new LinearLayoutManager(PlantListActivity.this));
                    adapter = new PlantListAdapter(PlantListActivity.this, plants, plantController);
                    adapter.setClickListener(PlantListActivity.this);
                    recyclerView.setAdapter(adapter);
                    checkBox.setChecked(false);
            }
        };
        datePickerDialog = dateManager.getDatePickerDialog(dateSetListener, PlantListActivity.this);
    }


    public void openDatePicker(View view, int position) {
        currentPlantPosition = position;
        this.datePickerDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(PlantListActivity.this, PlantDetailsActivity.class);
        Plant plant = adapter.getItem(position);
        intent.putExtra(EXTRA_PLANT, plant);
        startActivity(intent);
     }

    @Override
    public void onCheckBoxClick(CheckBox checkBox, int position) {

        openDatePicker(checkBox, position);
        this.checkBox = checkBox;
        Toast.makeText(PlantListActivity.this, "Checkbox clicked " + adapter.getItem(position).getPlantName(), Toast.LENGTH_SHORT).show();
    }

    public void enableRepeatingAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 25);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, WateringAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "wateringReminderChannel";
            String description = "channel to remind of watering ";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // set the plant alarm on Pause
    @Override
    protected void onPause() {
        super.onPause();
/*       createNotificationChannel();
        Intent intent = new Intent (PlantListActivity.this, WateringAlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(PlantListActivity.this,0,intent,PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long timeAtCreate = System.currentTimeMillis();
        long tenSeconds = 1000*20;

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtCreate + tenSeconds, pendingIntent);
        System.out.println("alarm set");*/
    }
}
