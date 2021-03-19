package com.group.project;

import android.content.Intent;
import android.os.Bundle;

import com.group.project.ui.reminders.EventsFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


/**
 * An activity to list the events
 *
 * @author Aaron Exley
 */
public class EventList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        Intent intent = getIntent();

        String date = intent.getStringExtra("date");


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        EventsFragment fragment = new EventsFragment();
        Bundle args = new Bundle();
        args.putString("date", date);
        fragment.setArguments(args);
        ft.add(R.id.eventFrame, fragment);
        ft.commit();

    }
}
