package com.group.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class weight_Goals extends AppCompatActivity {
    TextView originText;
    TextView  currentText;
    TextView goalText;
    String originString;
    String currentString;
    String goalString;
    float origin;
    float goalWeight;
    float current;

    ProgressBar pbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight__goals);
        foodDatabaseHelper    helper= new foodDatabaseHelper(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate= df.format(c);
        Cursor cursor = db.rawQuery("SELECT * from " + foodDatabaseHelper.TABLE_NAME + " WHERE Date LIKE '"+formattedDate+"' ;", new String[]{});
        float cals = 0;
        float fats = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String Calories = cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Calories));
            String Fat= cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Fat));
            cals = cals + Float.parseFloat(Calories);
            fats = fats + Float.parseFloat(Fat);
            cursor.moveToNext();

        }
        TextView tv3 = findViewById(R.id.textView3);
        TextView tv5 = findViewById(R.id.textView5);
        tv3.setText(""+cals+"g");
        tv5.setText(""+fats+"g");
        getProgress();


    }

    /**
     * gets the int value of the progress bar
     * @return the int value of the progress bar
     */
    public int getProgress(){
        SharedPreferences sharedPref =  getSharedPreferences("ab_Weights_Accessing",  Context.MODE_PRIVATE);

        goalString = sharedPref.getString("GoalWeight","No Goal Weight Set");
        currentString = sharedPref.getString("CurrentWeight","Please Set Your Current Weight");
        originString = sharedPref.getString("OriginWeight","No Origin Weight Set");

        originText = findViewById(R.id.originWeightText);
        goalText = findViewById(R.id.goalText);
        pbar = findViewById(R.id.pBar);
        currentText = findViewById(R.id.currentWText);

        originText.setText(originString);
        goalText.setText(goalString);
        currentText.setText(currentString);




        int progress = 0 ;
        try {
            origin = Float.parseFloat(originString);
            current = Float.parseFloat(currentString);
            goalWeight = Float.parseFloat(goalString);



            if (origin < goalWeight) {
                progress = Math.round(normalize(origin, goalWeight, current) * 100);
            } else {
                progress = 100 - Math.round(normalize(goalWeight, origin, current) * 100);
            }
            Log.i("Weight_Goals", "" + progress);
            pbar.setProgress(progress, true);
        }catch(Exception e){
            pbar.setProgress(0 );
        }
        return progress;
    }

    /**
     * normalize the probress to be between 0 and 100
     * @param min the min value to be used to in normalizing , typically origin weight
     * @param max the max value to be used in normalizing, typically  the max weight
     * @param current the value to be normalized
     * @return
     */
    protected float normalize(float min , float max, float current){
        return(current -min )/ (max -min);
    }
}
