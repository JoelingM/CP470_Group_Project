package com.group.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalculatorKeypadActivity extends Fragment {

    public CalculatorActivity CA;
    public TextView equTV;
    public TextView ansTV;
    public LinearLayout keyLayout;

    /**
     * Constructor
     * @param calcActivity the activity that the fragment is being passed into
     */
    public CalculatorKeypadActivity(CalculatorActivity calcActivity){
        super();
        this.equTV = calcActivity.findViewById(R.id.equationTV);
        this.ansTV = calcActivity.findViewById(R.id.answerTV);
        this.CA = calcActivity;
    }


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return keypadFrag - the view that the fragment uses
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View keypadFrag = inflater.inflate(R.layout.activity_calculator_keypad, null);
        Bundle bundleVar = getArguments();

        Button leftButton = keypadFrag.findViewById(R.id.leftButton);
        leftButton.setOnClickListener((View v) -> {
            CA.switchToVarFrag();
        });

        Button rightButton = keypadFrag.findViewById(R.id.rightButton);
        rightButton.setOnClickListener((View v) -> {
            CA.switchToEqFrag();
        });

        Button saveButton = keypadFrag.findViewById(R.id.saveButton);
        saveButton.setOnClickListener((View v) -> {

            //Get preferences file reference
            SharedPreferences prefs = null;
            if (getActivity() != null) {
                prefs = getActivity().getSharedPreferences("equationPrefs", Context.MODE_PRIVATE);
            }

            //get file editor reference
            SharedPreferences.Editor edit = prefs.edit();

            //Get last used email
            String equations = prefs.getString("equationPrefs", "");

            edit.putString("equationPrefs", equations + equTV.getText().toString() + ",");
            edit.apply();

            CharSequence text = "Equation Saved";
            int duration = Toast.LENGTH_SHORT; //= Toast.LENGTH_LONG if Off
            Toast toast = Toast.makeText(getActivity(), text, duration); //this is the ListActivity
            toast.show(); //display your message box

        });

        //Numbers 0-9 and decimal
        Button oneButton = keypadFrag.findViewById(R.id.num1Button);
        oneButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "1"); });
        Button twoButton = keypadFrag.findViewById(R.id.num2Button);
        twoButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "2"); });
        Button threeButton = keypadFrag.findViewById(R.id.num3Button);
        threeButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "3"); });

        Button fourButton = keypadFrag.findViewById(R.id.num4Button);
        fourButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "4"); });
        Button fiveButton = keypadFrag.findViewById(R.id.num5Button);
        fiveButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "5"); });
        Button sixButton = keypadFrag.findViewById(R.id.num6Button);
        sixButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "6"); });

        Button sevenButton = keypadFrag.findViewById(R.id.num7Button);
        sevenButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "7"); });
        Button eightButton = keypadFrag.findViewById(R.id.num8Button);
        eightButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "8"); });
        Button nineButton = keypadFrag.findViewById(R.id.num9Button);
        nineButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "9"); });

        Button zeroButton = keypadFrag.findViewById(R.id.num0Button);
        zeroButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "0"); });
        Button decButton = keypadFrag.findViewById(R.id.decButton);
        decButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "."); });

        //Basic math symbols
        Button sumButton = keypadFrag.findViewById(R.id.sumButton);
        sumButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "+"); });
        Button subButton = keypadFrag.findViewById(R.id.subButton);
        subButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "-"); });
        Button mulButton = keypadFrag.findViewById(R.id.mulButton);
        mulButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "*"); });
        Button divButton = keypadFrag.findViewById(R.id.divButton);
        divButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "/"); });

        //Brackets, and power
        Button LBButton = keypadFrag.findViewById(R.id.LBButton);
        LBButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "("); });
        Button RBButton = keypadFrag.findViewById(R.id.RBButton);
        RBButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + ")"); });
        Button powerButton = keypadFrag.findViewById(R.id.pwrButton);
        powerButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "^"); });

        Button deleteButton = keypadFrag.findViewById(R.id.delButton);
        deleteButton.setOnClickListener((View v) -> {
            if (equTV.getText().toString().length() > 0) {
                equTV.setText(equTV.getText().toString().substring(0, equTV.getText().toString().length() - 1));
            }
        });

        Button clearButton = keypadFrag.findViewById(R.id.clrButton);
        clearButton.setOnClickListener((View v) -> { equTV.setText(""); });


        Button eqButton = keypadFrag.findViewById(R.id.equButton);
        eqButton.setOnClickListener((View v) -> { ansTV.setText(CA.evaluate(equTV.getText().toString())); });

        return keypadFrag;
    }


}
