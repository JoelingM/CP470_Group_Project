package com.group.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import junit.framework.TestSuite;

import org.junit.Rule;
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
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
@RunWith(AndroidJUnit4.class)
public class weight_GoalsTest {
    @Mock
    Context context;

    @Mock
    SharedPreferences prefs;

    @Mock
    SharedPreferences.Editor edit;

    @Before
    public void launchActivity() {
        ActivityScenario.launch(weight_Goals.class );
    }
    @Rule
    public ActivityTestRule<weight_Goals> wAct = new ActivityTestRule<weight_Goals>(weight_Goals.class);
    @Test
    public void CP470_AB_TC006() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("GoalWeight", "100");
        editor.putString("OriginWeight", "0");
        editor.putString("CurrentWeight", "0");
        editor.commit();


        String originW = prefs.getString("OriginWeight","No Origin Weight Set");
        float val = Float.parseFloat(originW);
        weight_Goals wGoal = wAct.getActivity();
        int temp = wGoal.getProgress();
        assertEquals(temp, 0 );
        editor.putString("CurrentWeight", ""+(val +1 ));
        editor.commit();
        temp = wGoal.getProgress();
        assertEquals(temp, 1 );
        editor.putString("CurrentWeight", "0");
        editor.commit();
        temp = wGoal.getProgress();
        assertEquals(temp, 0 );
    }
    @Test
    public void CP470_AB_TC007() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("GoalWeight", "0");
        editor.putString("OriginWeight", "100");
        editor.putString("CurrentWeight", "100");
        editor.commit();


        String originW = prefs.getString("OriginWeight","No Origin Weight Set");
        float val = Float.parseFloat(originW);
        weight_Goals wGoal = wAct.getActivity();
        int temp = wGoal.getProgress();
        assertEquals(temp, 0 );
        editor.putString("CurrentWeight", ""+(val -1 ));
        editor.commit();
        temp = wGoal.getProgress();
        assertEquals(temp, 1 );
        editor.putString("CurrentWeight", "100");
        editor.commit();
        temp = wGoal.getProgress();
        assertEquals(temp, 0 );
    }
}