package com.example.growup.plantDatabaseTest;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import com.example.growup.plantModel.FileManager;
import com.example.growup.plantModel.FileManagerFactory;
import com.example.growup.plantModel.Plant;
import com.example.growup.plantModel.PlantDatabase;
import com.example.growup.plantModel.PlantDatabaseFactory;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PlantDatabaseTest {

    PlantDatabase plantDatabase;
    FileManager fileManager;
    Plant plant1Bob;
    Plant plant2Bob;


    @Before
    public void setUp(){
        fileManager = Mockito.mock(FileManager.class);
        plantDatabase = PlantDatabaseFactory.producePlantDatabase(fileManager);
        plant1Bob = new Plant("Bob", "Bonsai", "Balkon", 7, "15-05-2022");
        plant2Bob = new Plant("Bob", "Bonsai", "Balkon", 7, "18-05-2022");
     }


@Test
    public void savingPlantTest(){
    ArrayList<Plant> plants = new ArrayList<>();
    when(fileManager.getPlantListFromFile()).thenReturn(plants);
    plantDatabase.savePlantToList(plant1Bob);
    plants.add(plant1Bob);
    assertEquals(plants,plantDatabase.getPlants());
    }


@Test
    public void duplicatedNameTest(){
    ArrayList<Plant> plants = new ArrayList<>();
    when(fileManager.getPlantListFromFile()).thenReturn(plants);
    plantDatabase.savePlantToList(plant1Bob);
    int success = plantDatabase.savePlantToList(plant2Bob);
    assertEquals(-1,success);
}

@Test
    public void updateWateringInfoTest(){
    ArrayList<Plant> plants = new ArrayList<>();
    plants.add(plant1Bob);
    when(fileManager.getPlantListFromFile()).thenReturn(plants);
    plantDatabase.getAllPlants();
    plantDatabase.updateWateringInfo(plant1Bob, "25-05-2022");
    assertEquals("25-05-2022",plant1Bob.getLastWatered());
}
}
