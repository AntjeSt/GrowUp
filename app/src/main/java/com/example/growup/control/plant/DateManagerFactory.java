package com.example.growup.control.plant;

import android.content.Context;

public class DateManagerFactory {

    public static DateManager produceDateManager (){
        return new DateManagerImpl();
    }
}
