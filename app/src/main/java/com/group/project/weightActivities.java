package com.group.project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class weightActivities extends AppCompatActivity {

    EditText currentWeight;
    EditText goalWeight;
    private boolean createGoal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_activities);
        currentWeight = findViewById(R.id.editText3);
        goalWeight = findViewById(R.id.editText4);
        SharedPreferences sharedPref = getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        //currentWeight.setText(sharedPref.getString("CurrentWeight", ""));
        //goalWeight.setText(sharedPref.getString("GoalWeight", ""));
    }

    /**
     * saves the current weight into a shared preference
     * @param view current view
     * @return returns the string value for the current weight
     */
    public String SaveCurrentWeight(View view) {
        currentWeight = findViewById(R.id.editText3);
        String weight = currentWeight.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("CurrentWeight", weight);
        editor.commit();
        Toast.makeText(this, "Current weight of : " + weight + " has successfully been saved. ", Toast.LENGTH_SHORT).show();
        currentWeight.getText().clear();
        weight = currentWeight.getText().toString();
        return weight;
    }

    /**
     * saves the goal weight to a shared preference and saves the current weight as the origin weight.
     * @param view
     */
    public void SaveGoalWeight(View view) {
        goalWeight = findViewById(R.id.editText4);
        String weight = goalWeight.getText().toString();

        SharedPreferences sharedPref = getSharedPreferences("ab_Weights_Accessing", Context.MODE_PRIVATE);
        String currentweight = sharedPref.getString("CurrentWeight", "");
        View parentLayout = findViewById(android.R.id.content);
        if (currentweight == "") {

            Snackbar.make(parentLayout, "Cannot create a goal without knowing your current weight.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if(weight.isEmpty()){
            Snackbar.make(parentLayout, "Cannot create a goal without knowing your goal weight.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {

                float current = Integer.parseInt(currentweight);
                float goal = Integer.parseInt(weight);


                if (goal > current && goal != 999 ) { // 99 for unit testing

                    AlertDialog.Builder builder = new AlertDialog.Builder(weightActivities.this);
                    builder.setTitle(R.string.ab_AlertDialog);
                    // Add the buttons
                    // Get the layout inflater
                    LayoutInflater inflater = getLayoutInflater();

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    final View layout = inflater.inflate(R.layout.ab_custom_dialogue, null);
                    TextView comment = layout.findViewById(R.id.Comments);
                    comment.setText("");
                    builder.setView(layout)
                            // Add action buttons
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    createGoal(sharedPref);
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialogue = builder.create();
                    dialogue.show();

                } else {
                    createGoal(sharedPref);
                }

        }

    }

    /**
     * puts the goal values into the shared preferences
     * @param sharedPref
     */
    public void createGoal(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        String currentweight = sharedPref.getString("CurrentWeight", "");
        String weight = goalWeight.getText().toString();
        goalWeight.getText().clear();
        View parentLayout = findViewById(android.R.id.content);
        editor.putString("GoalWeight", weight);
        editor.putString("OriginWeight", currentweight);
        editor.commit();
        Snackbar.make(parentLayout, "Successfully created goal with starting weight: " + currentweight + " and goal weight: " + weight + ".", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void GoBackToMain(View view) {
        finish();
    }

    /**
     * goes to the goal value
     * @param view current view
     */
    public void GoToGoals(View view) {
        Intent intent = new Intent(weightActivities.this, weight_Goals.class);
        startActivity(intent);
    }
}
