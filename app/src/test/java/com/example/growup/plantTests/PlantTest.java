package com.example.growup.plantTests;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import com.example.growup.plantModel.Plant;

public class PlantTest {
    @Test
    public void setWateringDates() {
        Plant plant = new Plant("Bob", "Bonsai", "Balkon", 7, "15-05-2022");
        String plantDetails = plant.toString();
        System.out.println(plantDetails);
        assertEquals("22-05-2022",plant.getNextWater());

    }
}