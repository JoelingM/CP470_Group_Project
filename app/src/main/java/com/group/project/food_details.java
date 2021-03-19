package com.group.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class food_details extends AppCompatActivity {

    String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        foodFragment ffrag = new foodFragment();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ffrag.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.foodLayout, ffrag)
                .addToBackStack(null)
                .commit();


    }
}
