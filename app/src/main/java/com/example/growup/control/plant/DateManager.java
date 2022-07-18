package com.example.growup.control.plant;

import android.app.DatePickerDialog;
import android.content.Context;

public interface DateManager {

    /**
     * converts the date to String - purpose todays Date -> to String
     * @param day - day context: todays Date
     * @param month - month context: todays Date
     * @param year - year context: todays Date
     * @return date as String
     */
    String convertDateString(int day, int month, int year);

    /**
     * gets current Date as a string
     * @return date as String
     */
    String getTodaysDate();

    /**
     * opens the datePickerdialog when adding a plant/ setting lastWatered date
     * @param dateSetListener to know when user picked a date
     * @param ctx - Activitycontext
     * @return datePickerDialog for user to choose a date
     */

    DatePickerDialog getDatePickerDialog(DatePickerDialog.OnDateSetListener dateSetListener, Context ctx);

    /**
     * returns true if the Wateringdate is today or in the past
     * @param plantsWateringDate - date of a plant for next watering
     */
    boolean indicateDueDate(String plantsWateringDate);

    /**
     * compares to String dates returns true if date1 is the same or later than date2
     * @param date1
     * @param date2
     */
    boolean firstDateLater (String date1, String date2);
}
