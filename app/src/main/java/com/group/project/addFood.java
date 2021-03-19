package com.group.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class addFood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
    }

    /**
     * Returns to the 'main' of the foodList
     * @link food_list.java
     * @param view the current view
     */
    public void returnToMain(View view){
        EditText nameText = findViewById(R.id.Foodname);
        EditText caloriesText = findViewById(R.id.FoodCalories);
        EditText fatText = findViewById(R.id.FoodFat);
        EditText descriptionText = findViewById(R.id.description);
        String nameString = nameText.getText().toString();
        String caloriesString = caloriesText.getText().toString();
        String fatString = fatText.getText().toString();
        String descriptionString = descriptionText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("ab_food_Name", nameString );
        resultIntent.putExtra("ab_food_Calories",caloriesString );
        resultIntent.putExtra("ab_food_Fat",fatString );
        resultIntent.putExtra("ab_food_Desc",descriptionString );

        setResult(addFood.RESULT_OK, resultIntent);

        finish();
    }
}
