package com.group.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.group.project.aeCalenderDatabaseHelper.updateEventProgress;

/**
 * An ArrayAdapter for event objects
 *
 * @author Aaron Exley
 */
public class aeEventAdapter extends ArrayAdapter<String> {

    private ArrayList<aeEvent> events;
    private Context ctx;

    public aeEventAdapter(Context ctx) {
        super(ctx, 0);
        events = new ArrayList<>();
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return events.size();
    }

    @Override
    public String getItem(int position) {
        return events.get(position).toString();
    }

    @NonNull
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();

        View result = inflater.inflate(R.layout.event_list_element, parent, false);

        TextView eventDetails = result.findViewById(R.id.eventDetails);
        Switch complete = result.findViewById(R.id.eventFinished);
        ProgressBar progress = result.findViewById(R.id.eventProgress);


        complete.setOnCheckedChangeListener((CompoundButton compoundButton, boolean b) -> {

            aeEvent event = getItemAtPos(position);
            aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(ctx);
            SQLiteDatabase db = cdHelper.getWritableDatabase();

            if (b) {

                if (event.getCurrentSteps() < event.getTotalSteps()) {
                    event.addCurrentSteps(1);
                    updateEventProgress(db, event.getId(), event.getCurrentSteps());

                    if (event.getCurrentSteps() == event.getTotalSteps()) {
                        complete.setChecked(true);
                    } else {
                        complete.setChecked(false);
                    }

                    progress.setProgress( (int) ((event.getCurrentSteps() / (double)event.getTotalSteps()) * 100) );

                    notifyDataSetChanged();

                    Snackbar.make(result, "Completed Progress!", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                } else {
                    complete.setChecked(true);
                }
            }

            db.close();
        });

        aeEvent event = getItemAtPos(position);
        progress.setProgress( (int) ((event.getCurrentSteps() / (double)event.getTotalSteps()) * 100) );
        eventDetails.setText(getItem(position));
        return result;
    }

    public aeEvent getItemAtPos(int i) {
        return this.events.get(i);
    }

    @Override
    public long getItemId(int position) {
        return events.get(position).getId();
    }

    public void setEvents(ArrayList<aeEvent> events) {
        this.events = events;
    }

    public void addEvent(aeEvent event) {
        this.events.add(event);
    }

    public void setEvent(aeEvent event, int i) {
        this.events.set(i, event);
    }

    public void removeEvent(int i) {
        this.events.remove(i);
    }


}