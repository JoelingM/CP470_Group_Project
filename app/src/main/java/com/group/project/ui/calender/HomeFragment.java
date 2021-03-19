package com.group.project.ui.calender;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.group.project.CreateEvent;
import com.group.project.EventList;
import com.group.project.R;
import com.group.project.aeCalenderDatabaseHelper;
import com.group.project.aeCalenderDatabaseHelper.ExecGetEvents.AsyncResponseEvent;
import com.group.project.aeEvent;
import com.group.project.aeEventAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.group.project.aeCalenderDatabaseHelper.getEvents;
import static com.group.project.aeCalenderDatabaseHelper.removeEvent;

/**
 * A Fragment used to show the Calender and events on different days
 *
 * @author Aaron Exley
 */
@SuppressLint("SimpleDateFormat")
public class HomeFragment extends Fragment implements AsyncResponseEvent {

    private aeEventAdapter ea;
    private String selectedDate;
    private SQLiteDatabase db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_calender, container, false);

        aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(getActivity());
        db = cdHelper.getWritableDatabase();

        Button moreButton = root.findViewById(R.id.more_btn);
        CalendarView cv = root.findViewById(R.id.calendarView);
        ListView lv = root.findViewById(R.id.eventDisplay);

        // Sets the event adapter for the list view
        ea = new aeEventAdapter(getActivity());
        lv.setAdapter(ea);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        // Sets the event listener for the show more button
        // Simple start activity, Can have result of delete or update
        moreButton.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), EventList.class);
            intent.putExtra("date", selectedDate);
            startActivity(intent);
        });

        // Grabs when the user selects a different date
        cv.setOnDateChangeListener((CalendarView calendarView, int year, int month, int day) -> {
            selectedDate = day + "/" + (month+1) + "/" + year;
            getEvents(this, db, day + "/" + (month+1) + "/" + year);
        });

        // Opens the item when the item is clicked
        lv.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {

            Intent sendDate = new Intent(getActivity(), CreateEvent.class);

            sendDate.putExtra("event", ((aeEventAdapter) adapterView.getAdapter()).getItemAtPos(i));
            sendDate.putExtra("position", i);

            startActivityForResult(sendDate, 2);

        });


        selectedDate = sdf.format(new Date(cv.getDate()));

        // Gets the events from the database to show in the list view
        // Since getEvents is async the results will show up in processFinish
        getEvents(this, db, sdf.format(new Date(cv.getDate())));

        return root;
    }

    /**
     * Is called when an Activity finishes that was opened with StartActivityForResult
     *
     * Checks if the update or delete flag was set and deletes or updates that event in the
     * db and list
     *
     * @param requestCode The code set in startActivityForResult
     * @param resultCode the result of the activity
     * @param data The data sent back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK) {

            String message = "";

            // Check the flags
            boolean update = data.getBooleanExtra("Update", false);
            boolean delete = data.getBooleanExtra("Delete", false);

            if (update) {
                aeEvent event = data.getParcelableExtra("event");

                String startDate = event.getDate();
                String endDate = event.getEndDate();

                boolean newEvent = data.getBooleanExtra("new", false);

                // If its a new event, create the event
                if (newEvent) {
                    ea.addEvent(event);
                    message = getString(R.string.ae_event_created);
                } else {
                    // Other wise update the event
                    int i = data.getIntExtra("position", 0);

                    ea.setEvent(event, i);
                    message = getString(R.string.ae_event_updated);

                    if (!(startDate.equals(selectedDate) || endDate.equals(selectedDate))) {
                        ea.removeEvent(i);
                    }

                }


            } else if (delete) {
                // If the delete flag is set delete the event
                int i = data.getIntExtra("position", 0);
                long id = ea.getItemId(i);

                removeEvent(db, id);
                ea.removeEvent(i);

                message = getString(R.string.ae_event_deleted);

            }

            ea.notifyDataSetChanged();

            if (getView() != null) {
                Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        }
    }

    /**
     * Called by the async task when its finished getting the data
     * @see AsyncResponseEvent
     *
     * @param events The events retrieved from the database
     */
    @Override
    public void processFinish(ArrayList<aeEvent> events) {
        ea.setEvents(events);
        ea.notifyDataSetChanged();
    }

    /**
     * Close the db when done
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
