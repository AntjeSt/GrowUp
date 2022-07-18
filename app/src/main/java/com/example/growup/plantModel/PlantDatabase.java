package com.example.growup.plantModel;

import android.graphics.Bitmap;

import java.io.File;
import java.util.ArrayList;

public interface PlantDatabase {


    /**
     * new plant gets saved / check in advance if string name unique
     * @param plant
     */

    int savePlantToList(Plant plant);


    /**
     * exchanges the current saved plant with the changed plant if the users does alterations
     * @param plant - the plant with the updated changes
     */
    int updatePlantToList (Plant plant);

    /**
     * updates latest watering date and next to-be-watered date
     * @param plant current plant updated via checkbox
     * @param lastWatered
     */
    void updateWateringInfo(Plant plant, String lastWatered);

    /**
     * deletes plant with same plantID
     * @param plant of current view
     */
    void deletePlant(Plant plant);


    /** gives back all the plants from the file as arraylist
     * @return list with all plants from user
     */
    ArrayList<Plant> getAllPlants();

    /**fives back all the plants currently saved in the arraylist
     * @return list with all plants from user
     */
    ArrayList<Plant> getPlants();

    /**
     * gets passes information to FM -> saves the bitmap as png
     * @param plant holding the plantImage
     * @param plantImage from mediastore - file of the plant converted as png
     */
    void saveImage(Plant plant, Bitmap plantImage);

    /** retrieve the image of the plant to show in Imageview
     * @param plant holding the plantImage
     * @return Bitmap of the files image
     */

    Bitmap retrieveImage(Plant plant);

    /**
     * overrides the plants in the database with the updated version
     * @param synchronizedPlantList - list after synchronization with other user
     */
    void exchangePlantList(ArrayList<Plant> synchronizedPlantList);


}



