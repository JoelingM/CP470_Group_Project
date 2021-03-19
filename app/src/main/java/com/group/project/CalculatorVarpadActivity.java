package com.group.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalculatorVarpadActivity extends Fragment {

    public CalculatorActivity CA;
    public TextView equTV;
    public TextView ansTV;
    public LinearLayout keyLayout;

    /**
     *
     * @param calcActivity - The activity the fragment is appearing in
     */
    public CalculatorVarpadActivity(CalculatorActivity calcActivity){
        super();
        this.equTV = calcActivity.findViewById(R.id.equationTV);
        this.ansTV = calcActivity.findViewById(R.id.answerTV);
        this.CA = calcActivity;
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View varpadFrag = inflater.inflate(R.layout.activity_calculator_varpad, null);
        Bundle bundleVar = getArguments();

        Button rightButton = varpadFrag.findViewById(R.id.rightButton);
        rightButton.setOnClickListener((View v) -> {

            CA.switchToKeyFrag();

        });

        Button thetaButton = varpadFrag.findViewById(R.id.thetaButton);
        thetaButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "\u0398"); });

        Button phiButton = varpadFrag.findViewById(R.id.phiButton);
        phiButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "\u03D5"); });

        Button xButton = varpadFrag.findViewById(R.id.xButton);
        xButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "X"); });

        Button yButton = varpadFrag.findViewById(R.id.yButton);
        yButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "Y"); });

        Button zButton = varpadFrag.findViewById(R.id.zButton);
        zButton.setOnClickListener((View v) -> { equTV.setText(equTV.getText().toString() + "Z"); });
        return varpadFrag;
    }
}
