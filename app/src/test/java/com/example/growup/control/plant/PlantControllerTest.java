package com.example.growup.control.plant;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.growup.plantModel.FileManager;
import com.example.growup.plantModel.Plant;
import com.example.growup.plantModel.PlantDatabase;
import com.example.growup.plantModel.PlantDatabaseFactory;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;



public class PlantControllerTest {

    private Plant plant1;
    private Plant plant1LatestDate;
    private Plant plant2;
    private Plant plant2LatestDate;
    private Plant plant3;
    private Plant plant4;
    PlantControllerImpl plantController;
    PlantDatabase plantDatabase;
    FileManager fileManager;

    @Before
    public void initPlants(){
        ArrayList<Plant> plants = new ArrayList<>();
        plant1 = new Plant("Bob", "Bonsai", "Balkon", 7, "15-05-2022");
        plant1LatestDate = plant1;
        plant1LatestDate.setLastWateredNextWater("17-05-2022");
        plant2 = new Plant("Alice", "Bonsai", "Balkon", 7, "15-05-2022");
        plant2LatestDate = plant2;
        plant2LatestDate.setLastWateredNextWater("17-05-2022");
        plant3 = new Plant("Carl", "Bonsai", "Balkon", 7, "18-05-2022");
        plant4 = new Plant("Heino", "Bonsai", "Balkon", 3, "16-05-2022");

    }

    @Before
    public void initPlantController(){
        plantController = new PlantControllerImpl();
    }

    @Before
    public void initPlantDatabase(){
        fileManager = mock(FileManager.class);
        plantDatabase = PlantDatabaseFactory.producePlantDatabase(fileManager);
    }

    @Test
    public void samePlantWithOlderWateringDateUpdated(){
        ArrayList<Plant> arraylist1 = new ArrayList<>();
        ArrayList<Plant> arraylist2 = new ArrayList<>();
        arraylist1.add(plant1);
        arraylist1.add(plant2LatestDate);
        arraylist2.add(plant2);
        arraylist2.add(plant1LatestDate);
        ArrayList<Plant> arraylistExpected = new ArrayList<>();
        arraylistExpected.add(plant1LatestDate);
        arraylistExpected.add(plant2LatestDate);
        ArrayList<Plant> synArraylist = plantController.getSynchronizedPlantList(arraylist1, arraylist2);
        assertEquals(arraylistExpected,synArraylist);

    }

    @Test
    public void addNewPlantInSynchronizedArraylist(){
        ArrayList<Plant> arraylist1 = new ArrayList<>();
        ArrayList<Plant> arraylist2 = new ArrayList<>();
        arraylist1.add(plant1);
        arraylist1.add(plant2);
        arraylist2.add(plant1);
        ArrayList<Plant> arraylist1Copy = new ArrayList<>(arraylist1);
        ArrayList<Plant> synArraylist = plantController.getSynchronizedPlantList(arraylist1Copy, arraylist2);
        assertEquals(arraylist1,synArraylist);
    }

    @Test
    public void samePlantlistsReturnSamePlantlist(){
        ArrayList<Plant> arraylist1 = new ArrayList<>();
        ArrayList<Plant> arraylist2 = new ArrayList<>();
        arraylist1.add(plant1);
        arraylist1.add(plant2);
        arraylist2.add(plant1);
        arraylist2.add(plant2);

        ArrayList<Plant> arraylist1Copy = new ArrayList<>(arraylist1);
        ArrayList<Plant> arraylist2Copy = new ArrayList<>(arraylist2);
        ArrayList<Plant> synArraylist = plantController.getSynchronizedPlantList(arraylist1Copy, arraylist2Copy);
        assertEquals(arraylist1,synArraylist);
        assertEquals(arraylist2,synArraylist);

    }

    @Test
    public void plantsOrderedAccordingToWateringDate(){
        ArrayList<Plant> plants = new ArrayList<>();
        plants.add(plant1);
        plants.add(plant2);
        plants.add(plant3);
        plants.add(plant4);
        ArrayList<Plant> expectedOrder = new ArrayList<>();
        expectedOrder.add(plant4);
        expectedOrder.add(plant1);
        expectedOrder.add(plant2);
        expectedOrder.add(plant3);
        when(fileManager.getPlantListFromFile()).thenReturn(plants);
        plants = plantController.getOrderedPlantList();
        assertEquals(expectedOrder, plants);
    }

    @Test
    public void sameMonthDifferentDay() {
        String date1 = "15-05-2022";
        String date2 = "16-05-2022";
        DateManagerImpl dateManager = new DateManagerImpl();
        boolean later = dateManager.firstDateLater(date1, date2);
        assertFalse(later);
    }


}