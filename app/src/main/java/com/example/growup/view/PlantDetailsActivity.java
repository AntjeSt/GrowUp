package com.example.growup.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

public class PlantDetailsActivity extends AppCompatActivity {

    DateManager dateManager;
    DatePickerDialog datePickerDialog;
    public static final String EXTRA_PLANT = "com.example.growup.plant";
    Plant plant;
    PlantController plantController;
    BottomNavigationView bottomNavigationView;
    ImageView backView;
    Button saveButton;
    Button deleteButton;
    Bitmap plantImage;
    TextView lastWateredView;

    String lastWatered;

    ActivityResultLauncher<Intent> activityResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);
        plantController = PlantControllerFactory.producePlantController(getApplicationContext());
        dateManager = DateManagerFactory.produceDateManager();

        plant = (Plant) getIntent().getSerializableExtra(EXTRA_PLANT);

        backView = findViewById(R.id.back);
        saveButton = findViewById(R.id.saveChangesButton);
        deleteButton = findViewById(R.id.deleteButton);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.myplantsTab);


        TextView plantNameView  = findViewById(R.id.plantNameRv);
        plantNameView.setText(plant.getPlantName());
        TextView plantTypeView = findViewById(R.id.plantType);
        plantTypeView.setText(plant.getPlantType());
        TextView plantLocationView = findViewById(R.id.location);
        plantLocationView.setText(plant.getLocation());
        TextView waterFrequencyView = findViewById(R.id.waterFrequency);
        waterFrequencyView.setText(String.valueOf(plant.getWaterFrequency()));
        lastWateredView = findViewById(R.id.lastWateredDate);
        lastWateredView.setText(plant.getLastWatered());
        ImageView plantImageView = findViewById(R.id.deviceImageRv);
        Bitmap bitmap = plantController.retrievePlantImage(plant);
        if (bitmap != null){
        plantImageView.setImageBitmap(bitmap);
        }
        else{
            plantImageView.setImageResource(R.drawable.ic_camera);
        }

        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlantDetailsActivity.this, PlantListActivity.class);
                startActivity(intent);
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bundle bundle = result.getData().getExtras();
                    plantImage = (Bitmap) bundle.get("data");
                    plantImageView.setImageBitmap(plantImage);
                }
            }
        });

        plantImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null){
                    activityResultLauncher.launch(intent);
                }
                else {
                    Toast.makeText(PlantDetailsActivity.this, "You don't have an app to support this action",Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plantController.deletePlant(plant);
                Toast.makeText(PlantDetailsActivity.this, "Deleted plant successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PlantDetailsActivity.this, PlantListActivity.class);
                startActivity(intent);
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String plantName = plantNameView.getText().toString() ;
                String plantType = plantTypeView.getText().toString();
                String location = plantLocationView.getText().toString();
                int waterFrequency;
                if (waterFrequencyView.length() !=0){
                    waterFrequency = Integer.parseInt(waterFrequencyView.getText().toString());}
                else {waterFrequency = 0;}
                lastWatered = lastWateredView.getText().toString();
                System.out.println("save:" + lastWatered);
                if (validUserInputs(plantName, plantType, location, waterFrequency)) {
                    Plant updatedPlant = plantController.updatePlant(plant.getPlantID(), plantName, plantType, location, waterFrequency, lastWatered);
                    if (updatedPlant != null) {
                        if (plantImage != null){
                            plantController.savePlantImage(updatedPlant, plantImage);}
                        else if (bitmap != null) {plantController.savePlantImage(updatedPlant, bitmap);}
                        Toast.makeText(PlantDetailsActivity.this, "Your plant was updated succesfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PlantDetailsActivity.this, PlantListActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(PlantDetailsActivity.this, "Plant name already exists.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

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
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month+1;
                String date = dateManager.convertDateString(day, month, year);
                lastWateredView.setText(date);
                lastWatered = date;
            }
        };
        datePickerDialog = dateManager.getDatePickerDialog(dateSetListener, PlantDetailsActivity.this);
    }

    public void openDatePicker(View view) {
        this.datePickerDialog.show();
    }

    public boolean validUserInputs(String plantName, String plantType, String location, int waterFrequency){
        if (plantName.length() ==0){
            Toast.makeText(PlantDetailsActivity.this,"Please enter a name for your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        if (plantType.length() ==0){
            Toast.makeText(PlantDetailsActivity.this,"Please enter the type of your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        if (location.length() ==0){
            Toast.makeText(PlantDetailsActivity.this,"Please enter your plants location e.g. living room.",Toast.LENGTH_SHORT).show();
            return false; }
        if (waterFrequency == 0){
            Toast.makeText(PlantDetailsActivity.this,"Please enter how often you want to water your plant.",Toast.LENGTH_SHORT).show();
            return false; }
        return true;
    }
}