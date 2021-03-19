package com.group.project;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import static com.group.project.aeCalenderDatabaseHelper.getAllSemesters;
import static com.group.project.aeCalenderDatabaseHelper.getAllSemestersIds;
import static com.group.project.aeCalenderDatabaseHelper.updateClass;

/**
 * An Activity to create or update a class
 *
 * @author Aaron Exley
 */
public class CreateClass extends AppCompatActivity {

    private SQLiteDatabase db;
    private boolean newEvent;
    private long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(this);
        db = cdHelper.getWritableDatabase();

        newEvent = true;
        Intent intent = getIntent();

        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button deleteButton = findViewById(R.id.deleteButton);

        EditText classNameText = findViewById(R.id.class_name);
        EditText locationText = findViewById(R.id.class_location);
        Spinner semesterSpinner = findViewById(R.id.semester_spinner);

        EditText startTimePicker = findViewById(R.id.startTimePicker);
        startTimePicker.setInputType(InputType.TYPE_NULL);
        EditText endTimePicker = findViewById(R.id.endTimePicker);
        endTimePicker.setInputType(InputType.TYPE_NULL);

        ArrayList<String> semesters = getAllSemesters(db);
        ArrayList<Integer> semestersIds = getAllSemestersIds(db);
        semesterSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, semesters));

        // If there are no semesters abort
        if (semesters.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ae_error_semester);
            builder.setPositiveButton(R.string.ae_ok, (DialogInterface dialog, int id) -> finish());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        ToggleButton[] days = new ToggleButton[7];

        days[0] = findViewById(R.id.toggleMon);
        days[1] = findViewById(R.id.toggleTue);
        days[2] = findViewById(R.id.toggleWed);
        days[3] = findViewById(R.id.toggleThu);
        days[4] = findViewById(R.id.toggleFri);
        days[5] = findViewById(R.id.toggleSat);
        days[6] = findViewById(R.id.toggleSun);

        // If there was a class passed in fill in the ui with its contents
        if (intent.hasExtra("class")) {

            newEvent = false;
            deleteButton.setVisibility(View.VISIBLE);

            aeClass aeClass = intent.getParcelableExtra("class");

            System.out.println(Arrays.toString(aeClass.getDates()));

            id = aeClass.getId();
            classNameText.setText(aeClass.getName());
            locationText.setText(aeClass.getLocation());
            semesterSpinner.setSelection(semesters.indexOf(aeClass.getSemester()));
            startTimePicker.setText(aeClass.getStartTime());
            endTimePicker.setText(aeClass.getEndTime());

            String[] dates = aeClass.getDates();

            for (int i = 0; i < 7; i++) {

                if (Arrays.asList(dates).contains(days[i].getText().toString())) {
                    days[i].setChecked(true);
                }

            }
        } else {
            semesterSpinner.setSelection(intent.getIntExtra("semester", 0));
        }

        // Set date picker popup
        startTimePicker.setOnClickListener((View v) -> {
            Calendar cTime = Calendar.getInstance();
            int hour = cTime.get(Calendar.HOUR_OF_DAY);
            int minute = cTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(this, (TimePicker view, int selectedHour, int selectedMinute) -> {
                String timeString = selectedHour + ":" + selectedMinute;
                startTimePicker.setText(timeString);
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        // Set time picker popup
        endTimePicker.setOnClickListener((View v) -> {
            Calendar cTime = Calendar.getInstance();
            int hour = cTime.get(Calendar.HOUR_OF_DAY);
            int minute = cTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(this, (TimePicker view, int selectedHour, int selectedMinute) -> {
                String timeString = selectedHour + ":" + selectedMinute;
                endTimePicker.setText(timeString);
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });


        // Set save button
        saveButton.setOnClickListener((View v) -> {

            String name = classNameText.getText().toString();
            String location = locationText.getText().toString();
            String semester = semesterSpinner.getSelectedItem().toString();

            String startTime = startTimePicker.getText().toString();
            String endTime = endTimePicker.getText().toString();

            // Create day string
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 7; i++) {
                builder.append(days[i].isChecked() ? days[i].getText().toString() : "");
                builder.append(" ");
            }
            String dayText = builder.toString();

            int semesterId = semestersIds.get(semesters.indexOf(semester));

            // Create the new class
            aeClass aeClass = new aeClass(id, name, location, semester, dayText.trim().split(" "), startTime, endTime);

            if (newEvent) {
                // If new add the event to the db
                id = aeCalenderDatabaseHelper.addClass(db, name, location, semesterId, dayText, startTime, endTime);
                aeClass.setId(id);
            } else {
                // Otherwise update it in the db
                updateClass(db, id, aeClass, semesterId);
            }

            // Set the result to what was changed and finish the activity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Update", true);
            resultIntent.putExtra("Class", aeClass);
            resultIntent.putExtra("new", newEvent);
            if (!newEvent) {
                resultIntent.putExtra("position", intent.getIntExtra("position", 0));
            }
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());

        deleteButton.setOnClickListener((View v) -> {

            // Ask if the user is sure
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ae_are_you_sure_to_delete_event);
            builder.setPositiveButton(R.string.ae_ok, (DialogInterface dialog, int id) -> {

                // Set the status to delete the class and finished the activity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("Delete", true);
                resultIntent.putExtra("position", intent.getIntExtra("position", 0));
                setResult(RESULT_OK, resultIntent);
                finish();
            });
            builder.setNegativeButton(R.string.ae_no, (DialogInterface dialog, int id) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();

        });
    }

    /**
     * Close the db when done
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
