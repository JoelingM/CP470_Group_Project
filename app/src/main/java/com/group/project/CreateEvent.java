package com.group.project;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.group.project.ui.settings.SettingsFragment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import static com.group.project.aeCalenderDatabaseHelper.addEvent;
import static com.group.project.aeCalenderDatabaseHelper.getAllClassNames;
import static com.group.project.aeCalenderDatabaseHelper.getAllClassNamesIds;
import static com.group.project.aeCalenderDatabaseHelper.getAllSemesters;
import static com.group.project.aeCalenderDatabaseHelper.getAllSemestersIds;
import static com.group.project.aeCalenderDatabaseHelper.getAllTypes;
import static com.group.project.aeCalenderDatabaseHelper.getAllTypesIds;
import static com.group.project.aeCalenderDatabaseHelper.updateEvent;

@SuppressLint("SimpleDateFormat")
public class CreateEvent extends AppCompatActivity {

    private DatePickerDialog picker;
    private SQLiteDatabase db;
    private boolean newEvent;
    private long id;
    private int currentSteps = 0;
    private SharedPreferences sharedPreferences;
    private aeEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(this);
        db = cdHelper.getWritableDatabase();

        newEvent = true;

        Intent intent = getIntent();

        sharedPreferences = getSharedPreferences(SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE);

        EditText nameText = findViewById(R.id.EventName);
        EditText datePicker = findViewById(R.id.startDatePicker);
        datePicker.setInputType(InputType.TYPE_NULL);
        EditText timePicker = findViewById(R.id.startTimePicker);
        timePicker.setInputType(InputType.TYPE_NULL);

        EditText endDatePicker = findViewById(R.id.endDatePicker);
        endDatePicker.setInputType(InputType.TYPE_NULL);
        EditText endTimePicker = findViewById(R.id.endTimePicker);
        endTimePicker.setInputType(InputType.TYPE_NULL);

        Spinner typesSpinner = findViewById(R.id.typeSpinner);
        Spinner classSpinner = findViewById(R.id.classSpinner);
        Spinner semesterSpinner = findViewById(R.id.semesterSpinner);

        CheckBox repeatCheckbox = findViewById(R.id.repeatCheckbox);

        EditText descText = findViewById(R.id.desc);
        EditText stepsForEvent = findViewById(R.id.stepsForEvent);

        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        // Set the contents of the spinners
        ArrayList<String> types = getAllTypes(db);
        ArrayList<Integer> typeIds = getAllTypesIds(db);
        typesSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, types));

        ArrayList<String> classes = getAllClassNames(db);
        ArrayList<Integer> classesIds = getAllClassNamesIds(db);
        classSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, classes));

        ArrayList<String> semesters = getAllSemesters(db);
        ArrayList<Integer> semestersIds = getAllSemestersIds(db);
        semesterSpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, semesters));

        // If there are no classes or semesters abort
        if (classes.size() == 0 || semesters.size() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ae_error_class_and_semester);
            builder.setPositiveButton(R.string.ae_ok, (DialogInterface dialog, int id) -> finish());

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        // If an event was passes in fill in the ui elements to the contents
        if (intent.hasExtra("event")) {

            newEvent = false;
            deleteButton.setVisibility(View.VISIBLE);

            event = intent.getParcelableExtra("event");

            id = event.getId();
            currentSteps = event.getCurrentSteps();
            nameText.setText(event.getName());
            datePicker.setText(event.getDate());
            timePicker.setText(event.getTime());
            endDatePicker.setText(event.getEndDate());
            endTimePicker.setText(event.getEndTime());
            typesSpinner.setSelection(types.indexOf(event.getType()));
            classSpinner.setSelection(classes.indexOf(event.getClassName()));
            semesterSpinner.setSelection(semesters.indexOf(event.getSemester()));
            repeatCheckbox.setSelected(event.getRepeat());
            descText.setText(event.getDesc());

        }

        // Date picker popup
        datePicker.setOnClickListener((View v) -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
                String sd = String.valueOf(d);
                if (d < 10) {
                    sd = "0" + sd;
                }

                String ms = String.valueOf(m + 1);
                if (m + 1 < 10) {
                    ms = "0" + ms;
                }
                String dateString = sd + "/" + ms + "/" + y;
                datePicker.setText(dateString);
            }, year, month, day);
            picker.show();
        });

        // Time picker popup
        timePicker.setOnClickListener((View v) -> {
            Calendar cTime = Calendar.getInstance();
            int hour = cTime.get(Calendar.HOUR_OF_DAY);
            int minute = cTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(this, (TimePicker view, int selectedHour, int selectedMinute) -> {
                String timeString = selectedHour + ":" + ((selectedMinute < 10) ? "0" + selectedMinute: selectedMinute);
                timePicker.setText(timeString);
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        // Date picker popup
        endDatePicker.setOnClickListener((View v) -> {
            final Calendar cldr = Calendar.getInstance();
            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);

            picker = new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
                String sd = String.valueOf(d);
                if (d < 10) {
                    sd = "0" + sd;
                }

                String ms = String.valueOf(m + 1);
                if (m + 1 < 10) {
                    ms = "0" + ms;
                }
                String dateString = sd + "/" + ms + "/" + y;
                endDatePicker.setText(dateString);
            }, year, month, day);
            picker.show();
        });

        // Time picker popup
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

        // Save button
        saveButton.setOnClickListener((View v) -> {

            String name = nameText.getText().toString();
            String date = datePicker.getText().toString();
            String time = timePicker.getText().toString();

            String endDate = endDatePicker.getText().toString();
            String endTime = endTimePicker.getText().toString();

            String type = typesSpinner.getSelectedItem().toString();

            String className = classSpinner.getSelectedItem().toString();
            String semester = semesterSpinner.getSelectedItem().toString();

            int steps;
            try{
                steps = Integer.parseInt(stepsForEvent.getText().toString());
            } catch (NumberFormatException e) {
                steps = 0;
            }

            boolean repeat = repeatCheckbox.isChecked();
            String desc = descText.getText().toString();

            int semesterId = semestersIds.get(semesterSpinner.getSelectedItemPosition());
            int classId = classesIds.get(classSpinner.getSelectedItemPosition());
            int typeId = typeIds.get(typesSpinner.getSelectedItemPosition());

            boolean allowNotifications = sharedPreferences.getBoolean(SettingsFragment.ALLOW_PUSH_NOTIFICATIONS, true);
            int timeBefore = sharedPreferences.getInt(SettingsFragment.NOTIFICATION_TIME, 5);

            // Check if notifications is turned on
            int notificationId = -1;
            if (allowNotifications) {
                 DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
                 try {
                     if (date.length() == 1) {
                         date = "0" + date;
                     }
                     LocalDateTime dateObj = LocalDateTime.parse(date + "-" + time, format);
                     long timeInMilliseconds = dateObj.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli();

                     // Get the delay from now until the scheduled event
                     long delay = timeInMilliseconds - Calendar.getInstance().getTimeInMillis() - (timeBefore * 60000);

                     // Create the notification
                     Notification n = NotificationScheduler.getNotification(this, desc, name);
                     // Schedule the notification
                     notificationId = NotificationScheduler.scheduleNotification(this, n, delay ,repeat);
                 } catch (Exception e) {
                     Log.e("Create", "Oops");
                     e.printStackTrace();
                 }
            }

            // Create the event
            aeEvent event = new aeEvent(id, name, semester, date, time, endDate, endTime, type, className, desc, repeat ? 1 : 0, currentSteps, steps, notificationId);

            if (newEvent) {
                // If new add it to the db
                id = addEvent(db, name, semesterId, date, time, endDate, endTime, typeId, classId, desc, repeat, currentSteps, steps, notificationId);
                event.setId(id);
            } else {
                // Else update it
                updateEvent(db, id, event, semesterId, classId, typeId);
            }

            // Set the status and finish
            Intent resultIntent = new Intent();
            resultIntent.putExtra("Update", true);
            resultIntent.putExtra("new", newEvent);
            if (!newEvent) {
                resultIntent.putExtra("position", intent.getIntExtra("position", 0));
            }
            resultIntent.putExtra("event", event);
            setResult(RESULT_OK, resultIntent);

            finish();
        });

        cancelButton.setOnClickListener((View v) -> finish());

        deleteButton.setOnClickListener((View v) -> {

            // ask if the user is sure
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.ae_are_you_sure_to_delete_event);
            builder.setPositiveButton(R.string.ae_ok, (DialogInterface dialog, int id) -> {

                // Set status to delete and finish the activity
                NotificationScheduler.cancelNotification(this, event.getNotificationId());
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
