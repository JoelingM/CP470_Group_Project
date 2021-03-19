package com.group.project.ui.classes;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group.project.CreateClass;
import com.group.project.R;
import com.group.project.aeCalenderDatabaseHelper;
import com.group.project.aeCalenderDatabaseHelper.ExecGetClasses.AsyncResponseClass;
import com.group.project.aeClass;
import com.group.project.aeClassAdapter;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;
import static com.group.project.aeCalenderDatabaseHelper.addSemester;
import static com.group.project.aeCalenderDatabaseHelper.getAllSemesters;
import static com.group.project.aeCalenderDatabaseHelper.getAllSemestersIds;
import static com.group.project.aeCalenderDatabaseHelper.getClasses;
import static com.group.project.aeCalenderDatabaseHelper.removeClass;

/**
 * A fragment to show the classes
 *
 * @author Aaron Exley
 */
@SuppressLint("InflateParams")
public class ClassFragment extends Fragment implements AsyncResponseClass {

    private aeClassAdapter ca;
    private SQLiteDatabase db;
    private ArrayList<String> semesters;
    private Spinner semesterSpinner;
    private DatePickerDialog picker;
    private ArrayList<Integer> semesterIds;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_classes, container, false);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        ListView lv = root.findViewById(R.id.classList);
        semesterSpinner = root.findViewById(R.id.semesterPicker);
        Button createSemester = root.findViewById(R.id.createSemester);

        ca = new aeClassAdapter(getActivity());
        lv.setAdapter(ca);

        aeCalenderDatabaseHelper cdHelper = new aeCalenderDatabaseHelper(getActivity());
        db = cdHelper.getWritableDatabase();

        if (getActivity() != null) {
            semesters = getAllSemesters(db);
            semesterIds = getAllSemestersIds(db);
            semesterSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, semesters));
        }

        // Display different classes based on the selected semester
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                semesters = getAllSemesters(db);
                semesterIds = getAllSemestersIds(db);

                int semesterId = semesterIds.get(semesterSpinner.getSelectedItemPosition());

                getClasses(ClassFragment.this, db, semesterId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Set the click listener for create semester, shows an AlertDialog that allow
        // for a new semester to be created
        createSemester.setOnClickListener((View v) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.ae_add_sem);
            View layout = inflater.inflate(R.layout.create_semester, null);

            EditText datePicker = layout.findViewById(R.id.startDatePicker);
            datePicker.setInputType(InputType.TYPE_NULL);
            EditText endDatePicker = layout.findViewById(R.id.endDatePicker);
            endDatePicker.setInputType(InputType.TYPE_NULL);

            // Popup date picker
            datePicker.setOnClickListener((View view1) -> {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(getActivity(), (DatePicker view, int y, int m, int d) -> {
                    String dateString = d + "/" + (m + 1) + "/" + y;
                    datePicker.setText(dateString);
                }, year, month, day);
                picker.show();
            });

            // Popup datepicker
            endDatePicker.setOnClickListener((View view2) -> {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);

                picker = new DatePickerDialog(getActivity(), (DatePicker view, int y, int m, int d) -> {
                    String dateString = d + "/" + (m + 1) + "/" + y;
                    endDatePicker.setText(dateString);
                }, year, month, day);
                picker.show();
            });

            builder.setView(layout);
            builder.setPositiveButton(R.string.ae_ok, (DialogInterface dialog, int id) -> {

                EditText editText = layout.findViewById(R.id.semesterName);
                String name = editText.getText().toString();
                String startDate = datePicker.getText().toString();
                String endDate = datePicker.getText().toString();
                addSemester(db, name, startDate, endDate);
                semesters.add(name);
                semesterSpinner.setAdapter(new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_list_item_1, semesters));
            });
            builder.setNegativeButton(R.string.ae_no ,(DialogInterface dialog, int id) -> dialog.cancel());

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        if (semesterIds.size() != 0) {
            int semesterId = semesterIds.get(semesterSpinner.getSelectedItemPosition());
            getClasses(this, db, semesterId);
        }

        fab.setOnClickListener((View view) -> {
            Intent intent = new Intent(getActivity(), CreateClass.class);
            intent.putExtra("semester", semesterSpinner.getSelectedItemPosition());
            startActivityForResult(intent, 3);
        });

        lv.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {

            Intent intent = new Intent(getActivity(), CreateClass.class);

            intent.putExtra("class", ((aeClassAdapter)adapterView.getAdapter()).getItemAtPos(i));
            intent.putExtra("position", i);

            startActivityForResult(intent, 3);

        });


        return root;
    }

    /**
     * Is called when an Activity finishes that was opened with StartActivityForResult
     *
     * Checks if the update or delete flag was set and deletes or updates that class in the
     * db and list
     *
     * @param requestCode The code set in startActivityForResult
     * @param resultCode the result of the activity
     * @param data The data sent back
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 3 && resultCode == RESULT_OK) {
            String message = "";
            boolean update = data.getBooleanExtra("Update", false);
            boolean delete = data.getBooleanExtra("Delete", false);

            if (update) {
                aeClass aeclass = data.getParcelableExtra("Class");

                boolean newEvent = data.getBooleanExtra("new", false);

                // If the class is a new class add it to the list
                if (newEvent) {
                    ca.addClass(aeclass);
                    message = getString(R.string.ae_class_created);
                } else {
                    // If the class is not a new class update the value in the list
                    int i = data.getIntExtra("position", 0);

                    ca.setClass(aeclass, i);
                    message = getString(R.string.ae_class_updated);
                }

            } else if (delete) {
                // If the delete flag is set, delete the class from the db and list
                int i = data.getIntExtra("position", 0);
                long id = ca.getItemId(i);

                removeClass(db, id);
                ca.removeClass(i);

                message = getString(R.string.ae_class_deleted);
            }

            ca.notifyDataSetChanged();
            Toast t = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.BOTTOM, 0, 150);
            t.show();


        }

    }

    /**
     * Called by the async task when its finished getting the data
     * @see AsyncResponseClass
     *
     * @param classes The classes retrieved from the database
     */
    @Override
    public void processFinish(ArrayList<aeClass> classes) {
        ca.setClasses(classes);
        ca.notifyDataSetChanged();
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