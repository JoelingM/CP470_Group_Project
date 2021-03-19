package com.group.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * food fragment that connects to go food_list when in landscape mode
 * @link food_list
 */
public class foodFragment extends Fragment {

    TextView nameText;
    TextView calText;
    TextView fatText;
    TextView descText;
    TextView idText;
    String name;
    String id;
    String cals;
    String fat;
    String desc;
    food_list fl;

    /**
     * emppty constructor
     */
    public foodFragment() {
        // Required empty public constructor
    }

    /**
     *
     * @param obj a food list object
     */
    public foodFragment(food_list obj){
        fl = obj;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("FL_name") && getArguments().containsKey("FL_cals")&& getArguments().containsKey("FL_fat")&& getArguments().containsKey("FL_desc") ){

            name = getArguments().getString("FL_name");
            cals = getArguments().getString("FL_cals");
            fat = getArguments().getString("FL_fat");
            desc = getArguments().getString("FL_desc");

            id = String.format("%d",getArguments().getLong("FL_id") );


        }else{
            Log.i("Message Fragment", "No Valid Arguments sent");
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        Button delBtn = view.findViewById(R.id.button7);
        if (delBtn != null) {
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // User clicked OK button
                    if (fl == null ) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("FL_id", id);
                        resultIntent.putExtra("Position", getArguments().getInt("Position"));
                        getActivity().setResult(-99, resultIntent);
                        getActivity().finish();
                    }else {
                        fl.removeMessage(getArguments().getInt("Position"), id , foodFragment.this);
                    }
                }
            });
        }
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        nameText = view.findViewById(R.id.Foodname);
        calText = view.findViewById(R.id.FoodCalories);
        fatText = view.findViewById(R.id.FoodFat);
        descText = view.findViewById(R.id.description);
        nameText.setText(name);
        calText.setText(cals);
        fatText.setText(fat);
        descText.setText(desc);
    }




}
