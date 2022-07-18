package com.example.growup.control.plant;

import android.content.Context;

public class PlantControllerFactory {

    public static PlantController producePlantController (Context ctx){
        return new PlantControllerImpl(ctx);
    }

    public PlantController produceTestPlantController(){
        return new PlantControllerImpl();
    }
}
