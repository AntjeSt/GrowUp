package com.example.growup.control.plant;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.growup.plantModel.Plant;
import com.example.growup.plantModel.PlantDatabase;
import com.example.growup.plantModel.PlantDatabaseFactory;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

class PlantControllerImpl implements PlantController {

    private PlantDatabase plantDatabase;
    private Context ctx;
    private DateManager dateManager;


    PlantControllerImpl(Context ctx){
        this.ctx = ctx;
        plantDatabase = PlantDatabaseFactory.producePlantDatabase(ctx);
        dateManager = DateManagerFactory.produceDateManager();
      }

    // testing purposes
    PlantControllerImpl(){
        dateManager = DateManagerFactory.produceDateManager();
        plantDatabase = PlantDatabaseFactory.producePlantDatabase();
    }

    @Override
    public ArrayList<Plant> getOrderedPlantList() {
        ArrayList<Plant> plants = plantDatabase.getAllPlants();
        if (plants == null ) {
            return plants;}
        else {
            Plant[] plantsArray = plants.toArray(new Plant[plants.size()]);
            Plant[] sortedPlantsArray = sortByDate(plantsArray);
            ArrayList<Plant> sortedPlants = new ArrayList<>(Arrays.asList(sortedPlantsArray));

        return sortedPlants;}
    }

    public Plant[] sortByDate(Plant[] plantsArray){
            Plant plant;
            for(int i= 0; i <plantsArray.length; i++) {
                for (int j=0; j < plantsArray.length - 1; j++) {
                    if (dateManager.firstDateLater(plantsArray[j].getNextWater(), plantsArray[j+1].getNextWater())) {
                        plant = plantsArray[j];
                        plantsArray[j] = plantsArray[j + 1];
                        plantsArray[j + 1] = plant;
                    }
                }
            }
            return plantsArray;
        }


    @Override
    public Plant savePlant(String plantName, String plantType, String location, int waterFrequency, String lastWatered) {
        Plant plant = new Plant (plantName, plantType, location, waterFrequency, lastWatered);
        if (plantDatabase.savePlantToList(plant) == 0){
            return plant;
        };
        return null;
    }

    @Override
    public Plant updatePlant(long plantID, String plantName, String plantType, String location, int waterFrequency, String lastWatered) {
        Plant plant = new Plant (plantID, plantName, plantType, location, waterFrequency, lastWatered);
        if (plantDatabase.updatePlantToList(plant) == 0){
            return plant;
        }
        return null;

    }

    @Override
    public void deletePlant(Plant plant) {
        plantDatabase.deletePlant(plant);
    }

    @Override
    public void updateWateringDates(Plant plant, String watered){
        plantDatabase.updateWateringInfo(plant, watered);
    }

    @Override
    public void savePlantImage(Plant plant, Bitmap plantImage) {
        plantDatabase.saveImage(plant, plantImage);
    }

    @Override
    public Bitmap retrievePlantImage(Plant plant) {
        Bitmap bitmap = plantDatabase.retrieveImage(plant);
        return bitmap;
    }

    @Override
    public ArrayList<Plant> getPlantsWaterToday() {
        ArrayList<Plant> plants = plantDatabase.getAllPlants();
        ArrayList<Plant> plantsToWater = new ArrayList<>();

        for (Plant plant: plants){
            if (dateManager.indicateDueDate(plant.getNextWater()))
            {plantsToWater.add(plant);
            }
        }

        return plantsToWater;
    }

    @Override
    public void setAndSynchronizePlantList(byte[] remotePlantListArray) {
        ArrayList<Plant> remotePlantList = convertByteArrayToArrayList(remotePlantListArray);
        ArrayList<Plant> ownPlantList = getOrderedPlantList();
        ArrayList<Plant> synchronizedPlantList = getSynchronizedPlantList(ownPlantList,remotePlantList);
        System.out.println("synchronized PlantLists");
        plantDatabase.exchangePlantList(synchronizedPlantList);
    }

    public ArrayList<Plant> getSynchronizedPlantList (ArrayList<Plant> ownPlantList, ArrayList<Plant> remotePlantList){
        ArrayList<Plant> synchronizedPlants = new ArrayList<>();
        ArrayList<Plant> ownAddedPlants = new ArrayList<>();
        ArrayList<Plant> remoteAddedPlants = new ArrayList<>();


        for(int i= 0; i < ownPlantList.size(); i++){
            for (int j = 0; j < remotePlantList.size(); j++){
                if (ownPlantList.get(i).getPlantID() == remotePlantList.get(j).getPlantID()){
                     if(!dateManager.firstDateLater(ownPlantList.get(i).getLastWatered(), remotePlantList.get(j).getLastWatered())){
                        ownPlantList.get(i).setLastWateredNextWater(remotePlantList.get(j).getLastWatered());
                     }

                    synchronizedPlants.add(ownPlantList.get(i));
                    ownAddedPlants.add(ownPlantList.get(i));
                    remoteAddedPlants.add(remotePlantList.get(j));



                     break;
                }
            }
        }

        ownPlantList.removeAll(ownAddedPlants);
        remotePlantList.removeAll(remoteAddedPlants);

        if (ownPlantList.size() > 0) {
            synchronizedPlants.addAll(ownPlantList);
        }

        if(remotePlantList.size() > 0) {
            for (Plant plant : remotePlantList) {
                plant.setImagePath(null);
                synchronizedPlants.add(plant);
            }
        }

        return synchronizedPlants;
    }


    public ArrayList<Plant> convertByteArrayToArrayList(byte[] remoteByteArray){
        ByteArrayInputStream bais;
        ObjectInputStream ois = null;
        ArrayList<Plant> plants = null;
        Object obj = null;

          try {
            bais = new ByteArrayInputStream(remoteByteArray);
            ois = new ObjectInputStream(bais);
            obj = ois.readObject();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                    plants = (ArrayList<Plant>) obj;
                    System.out.println("fromBTArray:" + plants.toString());

                    return plants;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return plants;


    }


}
