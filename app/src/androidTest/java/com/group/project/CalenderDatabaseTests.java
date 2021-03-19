package com.group.project;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.group.project.ui.reminders.EventsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CalenderDatabaseTests {

    @Mock
    Context mockContext;

    @Mock
    aeCalenderDatabaseHelper helper;

    @Mock
    SQLiteDatabase Mdb;

    @Before
    public void launch() {

        ActivityScenario.launch(EventList.class);

        mockContext = ApplicationProvider.getApplicationContext();

        helper = new aeCalenderDatabaseHelper(mockContext);

        Mdb = helper.getWritableDatabase();

    }

    @Rule
    public ActivityTestRule<EventList> eRule = new ActivityTestRule<>(EventList.class);


    @Test
    public void testClickSwitch_CP470_AE_TC_014() {

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        onData(anything()).inAdapterView(withId(R.id.eventList)).atPosition(0).check(matches(isDisplayed()));

    }

}
