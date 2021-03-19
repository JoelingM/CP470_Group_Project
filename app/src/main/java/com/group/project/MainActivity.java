package com.group.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String myName = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(myName, "In onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set onClick listeners.
        findViewById(R.id.ab_btn).setOnClickListener((View v) -> {
            Log.d(myName, "In ab_btn.onClick()");
            startActivity(new Intent(this, main_ab.class   ));
        });
        findViewById(R.id.ae_btn).setOnClickListener((View v) -> {
            Log.d(myName, "In ae_btn.onClick()");
            Intent intent = new Intent(this, CalenderActivity.class);
            startActivity(intent);

        });
        findViewById(R.id.bw_btn).setOnClickListener((View v) -> {
            Log.d(myName, "In bw_btn.onClick()");
            Intent intent = new Intent(MainActivity.this, NotepadMainActivity.class);
            startActivityForResult(intent, 10);
        });
        findViewById(R.id.jm_btn).setOnClickListener((View v) -> {
            Log.d(myName, "In jm_btn.onClick()");
            startActivity(new Intent(this, CalculatorActivity.class));
        });
        findViewById(R.id.nc_btn).setOnClickListener((View v) -> {
            Log.d(myName, "In nc_btn.onClick()");
            startActivity(new Intent(this, ContactActivity.class));
        });
    }
}
