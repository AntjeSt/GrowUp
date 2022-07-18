package com.example.growup.plantModel;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Plant implements Serializable {

    private long plantID;
    private String plantName;
    private String plantType;
    private String location;
    private int waterFrequency;
    private String lastWatered;
    private String nextWater;
    private String imagePath;

    public Plant(String plantName, String plantType, String location, int waterFrequency, String lastWatered){
        this.plantName = plantName;
        this.plantType = plantType;
        this.location = location;
        this.waterFrequency = waterFrequency; //handling 0<x
        this.lastWatered = lastWatered; // "15-05-2022"
        setLastWateredNextWater(lastWatered);
        setPlantID();
    }

    public Plant(long plantID, String plantName, String plantType, String location, int waterFrequency, String lastWatered){
        this.plantName = plantName;
        this.plantType = plantType;
        this.location = location;
        this.waterFrequency = waterFrequency; //handling 0<x
        this.lastWatered = lastWatered; // "15-05-2022"
        setLastWateredNextWater(lastWatered);
        this.plantID = plantID;
    }

    private void setPlantID() {
        long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
        this.plantID = number;
    }

    public long getPlantID() {
        return this.plantID;
    }


    public String getPlantName(){
        return this.plantName;
    }

    public void setPlantName(String plantName){
        this.plantName = plantName;
    }


    public String getPlantType(){
        return this.plantType;
    }


    public void setPlantType(String plantType) {
        this.plantType = plantType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getWaterFrequency() {
        return waterFrequency;
    }

    public void setWaterFrequency(int waterFrequency) {
        this.waterFrequency = waterFrequency;
    }

    public String getLastWatered() {
        return lastWatered;
    }

    public void setLastWateredNextWater(String lastWatered) {
        this.lastWatered = lastWatered;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calender = Calendar.getInstance();
        try {
            calender.setTime(sdf.parse(this.lastWatered));
        }catch (ParseException e) {
            e.printStackTrace();
        }

        calender.add(Calendar.DAY_OF_MONTH, this.waterFrequency);
        this.nextWater = sdf.format(calender.getTime());
    }

    public String getNextWater(){
        return nextWater;
    }

    public String getImagePath() {
        return imagePath;
    }

     // when taking a picture
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    @Override
    public String toString() {
        return "Plant{" +
                "plantID='" + plantID + '\'' +
                "plantName='" + plantName + '\'' +
                ", plantType='" + plantType + '\'' +
                ", location='" + location + '\'' +
                ", waterFrequency=" + waterFrequency +
                ", lastWatered='" + lastWatered + '\'' +
                ", nextWater='" + nextWater + '\'' +
                ", imagePath='" + imagePath + '\'' +'}' + "\n" ;
    }


}
