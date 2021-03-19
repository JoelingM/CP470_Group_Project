package com.group.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

import junit.framework.TestSuite;
import org.mockito.Mock;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
@RunWith(AndroidJUnit4.class)
@LargeTest
public class weightActivitiesTest {

    @Mock
    Context context;

    @Mock
    SharedPreferences prefs;

    @Mock
    SharedPreferences.Editor edit;
    @Before
    public void launchActivity() {
        ActivityScenario.launch(weightActivities.class );
    }

    @Test
    public void CP470_AB_TC001() {
        onView(withId(R.id.editText3))
                .perform(typeText(""));
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        String firstInput = "167";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());
        String input = "Hello";
        onView(withId(R.id.editText3))
                .perform(typeText(input));
        onView(withId(R.id.button3)).perform(click());
        String temp = prefs.getString("CurrentWeight","Please Set Your Current Weight");
        Log.i("i", temp);
        assertEquals(temp,"");

    }
    @Test
    public void CP470_AB_TC002() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        String firstInput = "167";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());
        String input = "-167";
        onView(withId(R.id.editText3))
                .perform(typeText(input));
        onView(withId(R.id.button3)).perform(click());


        String temp = prefs.getString("CurrentWeight","Please Set Your Current Weight");
        Log.i("i", temp);
        assertEquals(temp,firstInput);

    }
    @Test
    public void CP470_AB_TC003() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);

        String firstInput = "267";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());

        firstInput = "267";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());


        String goal = "167";
        onView(withId(R.id.editText4))
                .perform(typeText(goal));
        onView(withId(R.id.button4)).perform(click());
        String input = "Hello";
        onView(withId(R.id.editText4))
                .perform(typeText(input));
        onView(withId(R.id.button4)).perform(click());

        String goalW = prefs.getString("GoalWeight","No Goal Weight Set");
        String originW = prefs.getString("OriginWeight","No Origin Weight Set");

        assertEquals(originW,firstInput);
        assertEquals(goalW,"167");

    }
    @Test
    public void CP470_AB_TC004() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);

        String firstInput = "267";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());

        firstInput = "267";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());


        String goal = "167";
        onView(withId(R.id.editText4))
                .perform(typeText(goal));
        onView(withId(R.id.button4)).perform(click());
        String input = "-167";
        onView(withId(R.id.editText4))
                .perform(typeText(input));
        onView(withId(R.id.button4)).perform(click());

        String goalW = prefs.getString("GoalWeight","No Goal Weight Set");
        String originW = prefs.getString("OriginWeight","No Origin Weight Set");

        assertEquals(originW,firstInput);
        assertEquals(goalW,"167");

    }
    @Test
    public void CP470_AB_TC005() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);

        String firstInput = "167";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());

        firstInput = "167";
        onView(withId(R.id.editText3))
                .perform(typeText(firstInput));
        onView(withId(R.id.button3)).perform(click());


        String goal = "167";
        onView(withId(R.id.editText4))
                .perform(typeText(goal));
        onView(withId(R.id.button4)).perform(click());
        String input = "-167";
        onView(withId(R.id.editText4))
                .perform(typeText(input));
        onView(withId(R.id.button4)).perform(click());

        String goalW = prefs.getString("GoalWeight","No Goal Weight Set");
        String originW = prefs.getString("OriginWeight","No Origin Weight Set");

        assertEquals(originW,firstInput);
        assertEquals(goalW,"167");

    }
}