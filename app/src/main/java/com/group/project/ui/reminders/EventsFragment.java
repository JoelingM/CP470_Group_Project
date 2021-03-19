package com.group.project.ui.reminders;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.group.project.CreateEvent;
import com.group.project.R;
import com.group.project.aeCalenderDatabaseHelper;
import com.group.project.aeCalenderDatabaseHelper.ExecGetEvents.AsyncResponseEvent;
import com.group.project.aeEvent;
import com.group.project.aeEventAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.group.project.aeCalenderDatabaseHelper.getEvents;
import static com.group.project.aeCalenderDatabaseHelper.removeEvent;

/**
 * A fragment to show the Events
 *
 * @author Aaron Exley
 */
public class EventsFragment extends Fragment implements AsyncResponseEvent {

    private aeEventAdapter ea;
    private SQLiteDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reminders, container, false);

        ListView lv = root.findViewById(R.id.eventList);
        FloatingActionButton fab = root.findViewById(R.id.fab);

        ea = new aeEventAdapter(getActivity());
        lv.setAdapter(ea);

        aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(getActivity());
        db = cdHelper.getWritableDatabase();

        Bundle bundle = getArguments();

        String date = null;
        if (bundle != null) {
            date = bundle.getString("date");
        }

        getEvents(this, db, date);




        fab.setOnClickListener((View view) -> {
            Intent intent = new Intent(getActivity(), CreateEvent.class);

            startActivityForResult(intent, 2);
        });

        lv.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {

            Intent intent = new Intent(getActivity(), CreateEvent.class);

            intent.putExtra("event", ((aeEventAdapter)adapterView.getAdapter()).getItemAtPos(i));
            intent.putExtra("position", i);

            startActivityForResult(intent, 2);

        });

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

            boolean update = data.getBooleanExtra("Update", false);
            boolean delete = data.getBooleanExtra("Delete", false);

            if (update) {
                // If the update flag is set there was a change
                aeEvent event = data.getParcelableExtra("event");

                boolean newEvent = data.getBooleanExtra("new", false);

                if (newEvent) {
                    // If the event is new add it to the list
                    ea.addEvent(event);
                    message = getString(R.string.ae_event_created);
                } else {
                    // Otherwise update it in the list
                    int i = data.getIntExtra("position", 0);

                    ea.setEvent(event, i);
                    message = getString(R.string.ae_event_updated);
                }


            } else if (delete) {
                // If the delete flag is set delete it from the db and list
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