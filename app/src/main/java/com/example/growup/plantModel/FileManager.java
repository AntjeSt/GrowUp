package com.example.growup.plantModel;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public interface FileManager {

    /**
     * save the plat arraylist to the file
     *
     * @param plants arraylist from Plantdatabase
     */
    void savePlantListToFile (ArrayList<Plant> plants);

    /**
     * get all plants
     *
     * @return the Arraylist of saved plants in the users file
     */
    ArrayList<Plant> getPlantListFromFile();


    /**
     * saves the given bitmap to a png file, filename: plantID
     * @param plantImageID of the current plant
     * @param plantImage as bitmap to save
     * @return path to ImageFile
     */
    String saveImageToFile (String plantImageID, Bitmap plantImage);

    /**
     * saves the given bitmap to a png file, filename: plantID
     * @param imagePath of the current plant to find ImageFile
     * @return Bitmap after convertion from png file
     */
    Bitmap retrieveImage (String imagePath);


    /**
     * deletes the Image to the deleted plant
     * @param imagePath of the current plant to find ImageFile
     */
    void deletePlantImage(String imagePath);
}
