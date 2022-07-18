package com.example.growup.plantModel;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


class PlantDatabaseImpl implements PlantDatabase{

    private static PlantDatabaseImpl instance;
    private static FileManager fileManager;
    private ArrayList<Plant> plants;
    private static Context ctx;



    public static PlantDatabase getPlantDatabaseInstance (Context ctx){
             if (PlantDatabaseImpl.instance == null){
            PlantDatabaseImpl.instance = new PlantDatabaseImpl();
        }
        PlantDatabaseImpl.ctx = ctx;
        fileManager = FileManagerFactory.produceFileManager(ctx);
        return PlantDatabaseImpl.instance;
    }

    PlantDatabaseImpl(){}

    // testing purposes
    PlantDatabaseImpl(FileManager fileManager){
        this.fileManager = fileManager;
    }


    @Override
    public int savePlantToList(Plant plant){
            plants = this.getAllPlants();
          if (plants == null){
            plants = new ArrayList<Plant>();
            plants.add(plant);
            fileManager.savePlantListToFile(plants);
            return 0;
        }
        else {
            if (duplicatePlantName(plant)) {
                return -1;
            } else {
                plants.add(plant);
                fileManager.savePlantListToFile(plants);
                return 0;
            }
        }
    }



    public boolean duplicatePlantName (Plant plant){
         for (Plant plant1: plants){
            if (plant.getPlantName().equals(plant1.getPlantName())){
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int updatePlantToList(Plant plant) {
        plants = this.getAllPlants();
        for (Plant plant1 : plants){
           if (plant.getPlantID() == plant1.getPlantID()){
                  plants.remove(plant1);
               break;
           }
        }

             if (duplicatePlantName(plant)) {
                return -1;
            } else {
                plants.add(plant);
                fileManager.savePlantListToFile(plants);
                return 0;
            }
    }


    @Override
    public void updateWateringInfo(Plant plant, String lastWatered) {
        plants = this.getAllPlants();
        for (Plant plant1 : plants) {
            if (plant.getPlantID()== (plant1.getPlantID())) {
                plant1.setLastWateredNextWater(lastWatered);
                fileManager.savePlantListToFile(plants);
            }
        }
    }

    @Override
    public void deletePlant(Plant plant) {
        plants = this.getAllPlants();
        for (Plant plant1 : plants){
            if (plant.getPlantID() == plant1.getPlantID()){
                plants.remove(plant1);
                deletePlantImage(plant);
                fileManager.savePlantListToFile(plants);
                break;
            }
        }
    }


    @Override
    public ArrayList<Plant> getAllPlants() {
        return fileManager.getPlantListFromFile();
    }

    @Override
    public ArrayList<Plant> getPlants(){
        return plants;
    }


    @Override
    public void saveImage(Plant plant, Bitmap plantImage) {
        long plantIDlong = plant.getPlantID();
        String plantID = Long.toString(plantIDlong);
        String imagePath = fileManager.saveImageToFile(plantID, plantImage);
        System.out.println("db.saveimage" + imagePath);
        plant.setImagePath(imagePath);
        updatePlantToList(plant);
    }

    @Override
    public Bitmap retrieveImage(Plant plant) {
        Bitmap bitmap = fileManager.retrieveImage(plant.getImagePath());
        return bitmap;
    }

    @Override
    public void exchangePlantList(ArrayList<Plant> synchronizedPlantList) {
        fileManager.savePlantListToFile(synchronizedPlantList);
    }


    public void deletePlantImage (Plant plant){
        fileManager.deletePlantImage(String.valueOf(plant.getPlantID()));
    };
}

