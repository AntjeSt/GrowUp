package com.example.growup.control.plant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.growup.R;
import com.example.growup.control.plant.PlantController;
import com.example.growup.control.plant.PlantControllerFactory;
import com.example.growup.plantModel.Plant;

import java.util.ArrayList;

public class WateringAlertReceiver extends BroadcastReceiver {
    public static final int NOTIFICATIONID = 100;
    String severalPlantsToWater = "Your plants need water.";
    String content;

    @Override
    public void onReceive(Context context, Intent intent) {

        PlantController plantController = PlantControllerFactory.producePlantController(context);
        ArrayList<Plant> plantsDueToday = plantController.getPlantsWaterToday();

        if (plantsDueToday != null && plantsDueToday.size() >= 1) {
            if (plantsDueToday.size() > 1){
                content = severalPlantsToWater;
            }
            else {
                content = plantsDueToday.get(0).getPlantName() + " needs to get watered.";
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "WateringReminder");
            builder.setContentTitle("Watering Notification");
            builder.setContentText(content);
            builder.setSmallIcon(R.drawable.ic_plant);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setAutoCancel(true);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIFICATIONID, builder.build());
        }
        else {
            System.out.println("no plants to Water");
        }

    }





}
