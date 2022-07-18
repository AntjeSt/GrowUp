package com.example.growup.view;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;

import static org.hamcrest.Matchers.not;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.growup.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AddPlantActivityTest {

    @Rule
    public ActivityScenarioRule<AddPlantActivity> activityRule =
            new ActivityScenarioRule<>(AddPlantActivity.class);


    @Test
    public void navigationBarNavigatesThroughTabs(){
        onView(withId(R.id.bluetoothTab)).perform(click());
        onView(withId(R.id.bluetoothHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.addplantTab)).perform(click());
        onView(withId(R.id.toolbarTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.myplantsTab)).perform(click());
        onView(withId(R.id.myPlantsHeader)).check(matches(isDisplayed()));
    }


    @Test
    public void saveAPlantAddsPlantAndNavigatesToPlantList() {
        onView(withId(R.id.plantNameRv)).perform(click(),typeText("Dori"));
        onView(withId(R.id.plantType)).perform(click(),typeText("Bonsai"));
        onView(withId(R.id.location)).perform(click(),typeText("Balcony"));
        onView(withId(R.id.waterFrequency)).perform(click(),typeText("3"),closeSoftKeyboard());
        onView(withId(R.id.saveButton)).perform(click());
        onView(withId(R.id.myPlantsHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_plants)).check(matches(hasDescendant(withText("Dori"))));
    }

    @Test
    public void toastAppearsAfterAddingPlant(){
       // onView(withText(R.string.addedplantsuccessfullyToast)).inRoot(withDecorView(not(getWindow().getDecorView()))).check(matches(isDisplayed()));

    }

    @After
    public void deletePlantAfterTest(){
        onView(withId(R.id.myplantsTab)).perform(click());
        onView(withId(R.id.rv_plants)).check(matches(hasDescendant(withText("Dori")))).perform(click());
        onView(withId(R.id.plantDetailsHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.deleteButton)).perform(click());
        onView(withId(R.id.myPlantsHeader)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_plants)).check(matches(hasDescendant(not(withText("Dori")))));
    }
}