package com.example.growup.plantModel;

import android.content.Context;

public class PlantDatabaseFactory {

    public static PlantDatabase producePlantDatabase (Context ctx){
        PlantDatabase plantDatabase = PlantDatabaseImpl.getPlantDatabaseInstance(ctx);
        return plantDatabase;
    }

    //testing purposes
    public static PlantDatabase producePlantDatabase (FileManager fileManager){
        return new PlantDatabaseImpl(fileManager);
    }

    public static PlantDatabase producePlantDatabase (){
        return new PlantDatabaseImpl();
    }
}
