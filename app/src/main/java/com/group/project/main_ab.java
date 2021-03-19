package com.group.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class main_ab extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ab_toolbar, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(main_ab.this);
        switch (item.getItemId()) {
            case R.id.About_Item:
                LayoutInflater inflater = getLayoutInflater();

                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                final View layout = inflater.inflate(R.layout.ab_custom_dialogue, null);
                TextView comment = layout.findViewById(R.id.Comments);
                comment.setText(R.string.ab_comments);
                builder.setTitle("Version 1.0 by Austin Bursey");
                builder.setView(layout)
                        // Add action buttons
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog dialogue = builder.create();
                dialogue.show();
                break;

            case R.id.back_Item:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }
    public void goToFoods(View view) {
        Intent intent = new Intent(main_ab.this , food_list.class);
        startActivity(intent);
    }
    public void goToWeights(View view) {
        Intent intent = new Intent(main_ab.this , weightActivities.class);
        startActivity(intent);
    }
    public void GoToGoals(View view) {
        Intent intent = new Intent(main_ab.this , weight_Goals.class);
        startActivity(intent);
    }
}
