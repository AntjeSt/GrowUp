package com.example.growup.control.plant;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.growup.plantModel.Plant;

import java.io.File;
import java.util.ArrayList;



public interface PlantController {
    /**
     * list all saved plants ordered by watering Date
     * @return the plants ordered by nextWateringDate
     */
    ArrayList<Plant> getOrderedPlantList();


    /**
     * save a plant + update View
     * @param plantName
     */
    Plant savePlant(String plantName, String plantType, String location, int waterFrequency, String lastWatered);

    /**
     * send/update new plant data, send to model
     * @param plantName
     * @param plantType
     * @param location
     * @param waterFrequency
     * @param lastWatered
     */
    Plant updatePlant(long PlantID, String plantName, String plantType, String location, int waterFrequency, String lastWatered);


    /**
     * delete a plant
     * @param plantName
     */
    void deletePlant(Plant plant);


    /**
     * update watering informations (watered and next watering depending on waterFrequency)
     * @param plant to update
     * @param  last watered from DatePicker
     */
    void updateWateringDates(Plant plant, String watered);

    /**
     * update watering informations (watered and next watering depending on waterFrequency)
     * @param plant of current activity
     * @param according plantImage as bitmap
     */
    void savePlantImage(Plant plant, Bitmap plantImage);

    /**
     * delegate file retrieving to the plantDatabase
     * @param plant of current tab
     * @param according plantImage as bitmap
     */
    Bitmap retrievePlantImage(Plant plant);

    /**
     * get all plants that need to get watered today
     * @return plants to water at current Date
     */
    ArrayList<Plant> getPlantsWaterToday();

    /**
     * compares the remote and own plantlists to save updated version
     * @param remoteplantListArray from BT Inpustream
     */
    void setAndSynchronizePlantList(byte[] remoteplantListArray);

}



