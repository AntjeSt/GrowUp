package com.example.growup.control.plant;

import android.app.DatePickerDialog;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class DateManagerImpl implements DateManager {

    @Override
    public String convertDateString(int day, int month, int year) {
        String month1 = "0";
        if (month < 10){
            month1 = month1 + month;
        }
        else {month1 = String.valueOf(month);}
        String day1 = "0";
        if (day <10){
            day1 = day1 + day;
        }
        else {day1 = String.valueOf(day);}
        String date = day1 + "-" +  month1 + "-" + year;
        System.out.println(date);
        return date;
    }

    @Override
    public String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH) +1;
        int year = cal.get(Calendar.YEAR);

        return convertDateString(day, month, year);
    }

    @Override
    public DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener, Context ctx){

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month = month + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(ctx,dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        return datePickerDialog;
    }

    @Override
    public boolean indicateDueDate(String plantsWateringdate) {
        String todaysDate = getTodaysDate();
        boolean later = firstDateLater(plantsWateringdate,todaysDate);
        if (later == true){
            return false;
        }
        return true;
    }

    @Override
    public boolean firstDateLater (String date1, String date2){
        String[] date1splitted = date1.split("-");
        String[] date2splitted = date2.split("-");
        int[] date1Int = new int[3];
        int[] date2Int = new int[3];
        int i;
        for (i = 0; i < 3; i++){
            date1Int[i] = Integer.parseInt(date1splitted[i]);
            date2Int[i] = Integer.parseInt(date2splitted[i]);
        }

        if (date2Int[2] <= date1Int[2] && date2Int[1] < date1Int[1]){
            return true;
        }
        else if (date2Int[2] <= date1Int[2] && date2Int[1] == date1Int[1]){
            if (date2Int[0] <= date1Int[0]){ return true; }
        }
        return false;
    }



    // maybe useful somewhere?
    public Date convertStringToDate(String nextWatering){
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy");
        Date date = null;
        try {
            date = format.parse(nextWatering);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
