package com.example.growup.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.growup.R;
import com.example.growup.control.plant.DateManager;
import com.example.growup.control.plant.DateManagerFactory;
import com.example.growup.control.plant.PlantController;
import com.example.growup.control.plant.PlantControllerFactory;
import com.example.growup.plantModel.Plant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class AddPlantActivity extends AppCompatActivity {

    PlantController plantController;
    DateManager dateManager;
    DatePickerDialog datePickerDialog;
    final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

    String plantName;
    String plantType;
    String location;
    int waterFrequency;
    String lastWatered;
    Bitmap plantImage;


    TextView lastWateredInput;
    BottomNavigationView bottomNavigationView;
    EditText waterFrequencyInput;
    EditText plantNameInput;
    EditText plantTypeInput;
    EditText locationInput;
    Button saveButton;

    ImageView plantImageView;
    ActivityResultLauncher <Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null){
                Bundle bundle = result.getData().getExtras();
                plantImage = (Bitmap) bundle.get("data");
                plantImageView.setImageBitmap(plantImage);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dateManager = DateManagerFactory.produceDateManager();
        System.out.println("dateManger1:" + dateManager);
        plantController = PlantControllerFactory.producePlantController(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plant);
        plantNameInput = findViewById(R.id.plantNameRv);
        plantTypeInput = findViewById(R.id.plantType);
        locationInput = findViewById(R.id.location);
        waterFrequencyInput = findViewById(R.id.waterFrequency);
        saveButton = findViewById(R.id.saveButton);
        plantImageView = findViewById(R.id.deviceImageRv);

        saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        plantName = plantNameInput.getText().toString();
                        plantType = plantTypeInput.getText().toString();
                        location = locationInput.getText().toString();
                        if (waterFrequencyInput.length() !=0){
                        waterFrequency = Integer.parseInt(waterFrequencyInput.getText().toString());}
                        else {waterFrequency = 0;}
                        if (validUserInputs(plantName, plantType, location, waterFrequency)) {
                           Plant savedPlant = plantController.savePlant(plantName, plantType, location, waterFrequency, lastWatered);
                              if (savedPlant != null) {
                                  if (plantImage != null){
                               plantController.savePlantImage(savedPlant, plantImage);}
                               Toast.makeText(AddPlantActivity.this, getResources().getString(R.string.addedplantsuccessfullyToast), Toast.LENGTH_SHORT).show();
                               Intent intent = new Intent(AddPlantActivity.this, PlantListActivity.class);
                               startActivity(intent);
                            }
                           else{
                               Toast.makeText(AddPlantActivity.this, "Plant name already exists.", Toast.LENGTH_SHORT).show();
                           }

                        }

                    }
        });

        plantImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null){
                activityResultLauncher.launch(intent);
            }
            else {
                Toast.makeText(AddPlantActivity.this, "You don't have an app to support this action",Toast.LENGTH_SHORT).show();
            }
            }
        });
        System.out.println("dateManger2:" + dateManager);
        initDatePicker();
        lastWateredInput= findViewById(R.id.lastWateredDate);
        lastWateredInput.setText(dateManager.getTodaysDate());
        lastWatered = dateManager.getTodaysDate();

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.addplantTab);

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
                        startActivity(new Intent(getApplicationContext(),BluetoothActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.addplantTab:
                        return true;

                }
                return false;
            }
        });
    }


    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = dateManager.convertDateString(day, month, year);
                lastWateredInput.setText(date);
                lastWatered = date;
            }
        };

        datePickerDialog = dateManager.getDatePickerDialog(dateSetListener, AddPlantActivity.this);
    }

    public void openDatePicker(View view) {
        this.datePickerDialog.show();
    }

    // validate UserInput on save
    public boolean validUserInputs(String plantName, String plantType, String location, int waterFrequency){
        if (plantName.length() ==0){
            Toast.makeText(AddPlantActivity.this,"Please enter a name for your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        if (plantType.length() ==0){
            Toast.makeText(AddPlantActivity.this,"Please enter the type of your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        if (location.length() ==0){
            Toast.makeText(AddPlantActivity.this,"Please enter your plants location e.g. living room.",Toast.LENGTH_SHORT).show();
            return false; }
        if (waterFrequency == 0){
            Toast.makeText(AddPlantActivity.this,"Please enter how often you want to water your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        return true;
    }
}