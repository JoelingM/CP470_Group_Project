package com.group.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.Thread.sleep;

public class CalculatorEquationsActivity extends Fragment {

    public CalculatorActivity CA;
    public TextView equTV;
    public TextView ansTV;
    public ListView eqLV;
    public equationAdapter eqAdapter;
    public ProgressBar progBar;
    private ArrayList<String> equationList = new ArrayList<String>();


    /**
     *
     * @param calcActivity
     */
    public CalculatorEquationsActivity(CalculatorActivity calcActivity){
        super();
        this.equTV = calcActivity.findViewById(R.id.equationTV);
        this.ansTV = calcActivity.findViewById(R.id.answerTV);
        this.progBar = calcActivity.findViewById(R.id.progBar);
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

        View eqFrag = inflater.inflate(R.layout.activity_calculator_equations, null);
        Bundle bundleVar = getArguments();



        Button backButton = eqFrag.findViewById(R.id.backButton);
        backButton.setOnClickListener((View v) -> {
            CA.switchToKeyFrag();
        });



        eqLV = eqFrag.findViewById(R.id.equationsListView);
        eqAdapter = new equationAdapter(getActivity());
        eqLV.setAdapter(eqAdapter);
        new fillListView().execute();


        eqLV.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {
            equTV.setText(equationList.get(position));
        });

        Button deleteButton = eqFrag.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener((View v) -> {
            String delEquation = equTV.getText().toString();

            //Get preferences file reference
            SharedPreferences prefs = null;
            if (getActivity() != null) {
                prefs = getActivity().getSharedPreferences("equationPrefs", Context.MODE_PRIVATE);
            }

            //get file editor reference
            SharedPreferences.Editor edit = prefs.edit();

            //Equations to delete from
            String delEquations = prefs.getString("equationPrefs", "0");

            //Replace equation in string
            delEquations = delEquations.replace("," + delEquation + ",", ",");
            edit.putString("equationPrefs", delEquations);
            edit.apply();

            //Update list
            equationList = new ArrayList<String>(Arrays.asList(delEquations.split(",")));
            eqAdapter.notifyDataSetChanged();

        });
        return eqFrag;
    }

    public class fillListView extends AsyncTask<String, Integer, String> {
        /**
         * {@inheritDoc}
         * @param progs
         */
        @Override
        protected void onProgressUpdate(Integer ... progs){
            progBar.setProgress(progs[0]);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progBar.setVisibility(View.VISIBLE);
            progBar.setProgress(0);
        }

        /**
         * {@inheritDoc}
         * @param a
         */
        @Override
        protected void onPostExecute(String a){
            progBar.setVisibility(View.INVISIBLE);
            String equations[] = a.split(",");
            equationList = new ArrayList<String>(Arrays.asList(equations));

            eqAdapter.notifyDataSetChanged();


        }

        /**
         *
         * @param args
         * @return
         */
        protected String doInBackground(String ... args){
            try{
                sleep(50);
                publishProgress(20);
            } catch (Exception e){ }

            //Get preferences file reference
            SharedPreferences prefs = null;
            if (getActivity() != null) {
                prefs = getActivity().getSharedPreferences("equationPrefs", Context.MODE_PRIVATE);
                try{
                    sleep(50);
                    publishProgress(40);
                } catch (Exception e){ }
            }

            try{
                sleep(50);
                publishProgress(60);
            } catch (Exception e){ }

            //get file editor reference
            SharedPreferences.Editor edit = prefs.edit();

            try{
                sleep(50);
                publishProgress(80);
            } catch (Exception e){ }

            //Get last used email
            String equations = prefs.getString("equationPrefs", "0");

            try{
                sleep(50);
                publishProgress(100);
            } catch (Exception e){ }

            return equations;
        }
    }


    private class equationAdapter extends ArrayAdapter<String> {
        public equationAdapter(Context ctx) {
            super(ctx, 0);
        }

        /**
         *
         * @return - The number of items in the equation list
         */
        public int getCount() {
            return equationList.size();
        }

        /**
         *
         * @param position - the position in the equation list of the selected item
         * @return - the item at the position in the equation list
         */
        public String getItem(int position) {
            return equationList.get(position);
        }

        /**
         *
         * @param position
         * @param convertView
         * @param parent
         * @return - The view of the textbox with the equation.
         */
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View result = inflater.inflate(R.layout.activity_textbox, null);

            TextView equation= (TextView) result.findViewById(R.id.textView);
            equation.setText(getItem(position)); // get the string at position
            return result;

        }

    }

}


