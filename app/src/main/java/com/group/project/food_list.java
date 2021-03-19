package com.group.project;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class food_list extends AppCompatActivity {
    private ArrayList<String> mArray;
    private final String ACTIVITY_NAME = "ChatWindow";
    private SQLiteDatabase db;
    foodDatabaseHelper helper;
    Cursor cursor;
    FoodAdapter fdAdapter;
    boolean LargeExists;
    private AdapterView.OnItemClickListener listener;
    ContentValues cval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);




        ListView messagesView  = findViewById(R.id.FoodListView);
        mArray = new ArrayList<>();
        fdAdapter = new FoodAdapter(this);
        messagesView.setAdapter(fdAdapter);

        helper= new foodDatabaseHelper(this);
        db = helper.getWritableDatabase();

        cursor = db.rawQuery("SELECT * from " + foodDatabaseHelper.TABLE_NAME + ";", new String[]{});


        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            mArray.add(cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Food)));
            Log.i("Food_list",cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Food) ));
            cursor.moveToNext();
        }
        FrameLayout fl = findViewById(R.id.foodFrameLayout);
        if (fl == null ){
            LargeExists = false;
        }else {
            LargeExists = true;
        }
        fdAdapter.notifyDataSetChanged();
        messagesView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cursor = db.rawQuery("SELECT * from " + foodDatabaseHelper.TABLE_NAME + ";", new String[]{});
                Bundle bundle = new Bundle();
                bundle.putInt("Position", position);
                bundle.putLong("FL_id", id);

                cursor.moveToFirst();
                while (! cursor.isAfterLast() &&  Long.parseLong(cursor.getString(cursor.getColumnIndex("_id"))) != id  ) {
                    cursor.moveToNext();
                }
                String foodName= cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Food));
                String Calories= cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Calories));
                String Fat= cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Fat));
                String Description= cursor.getString(cursor.getColumnIndex(foodDatabaseHelper.KEY_Desc));
                bundle.putString("FL_name", foodName);
                bundle.putString("FL_cals", Calories);
                bundle.putString("FL_fat", Fat);
                bundle.putString("FL_desc", Description);
                if (LargeExists){

                    foodFragment foodFrag = new foodFragment(food_list.this   );
                    foodFrag.setArguments(bundle);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.foodFrameLayout, foodFrag) // replace flContainer
                            .addToBackStack(null)
                            .commit();
                }else {
                    foodFragment foodFrag = new foodFragment();
                    foodFrag.setArguments(bundle);
                    Intent intent = new Intent(food_list.this, food_details.class);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 10 );


                }
            }
        });


    Log.i("i","i");
    }

    /**
     * fires intent to the addFood.java
     * @link addFood.java
     * @param view current view
     */
    public void addFood(View view) {
        Intent intent = new Intent(food_list.this , addFood.class);
        startActivityForResult(intent, 10);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("food_list", mArray.toString());
    }


    /**
     * Food adapter to connect arrayList to listview
     */
    @SuppressWarnings("NullableProblems")
    class FoodAdapter extends ArrayAdapter<String> {
        /**
         *
         * @param ctx current context
         */
        private FoodAdapter(Context ctx) {
            super(ctx, 0);
        }

        /**
         *
         * @return arrayLists size
         */
        public int getCount() {
            return mArray.size();
        }

        /**
         *
         * @param position
         * @return items at position
         */
        public String getItem(int position) {
            return mArray.get(position);
        }

        /**
         *
         * @param position
         * @return the id of the item at positioning
         */
        public long getItemId(int position){
            cursor = db.rawQuery("SELECT * from " + "FoodItems" + ";", new String[]{});
            cursor.moveToPosition(position);
            if (!cursor.isAfterLast()) {
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                Log.i("ChatWindow", "id = " + id);
                return Long.parseLong(id);
            }
            return 0;
        }

        /**
         *
         * @param position position of the item
         * @param convertView
         * @param parent parent of the frame layout
         * @return inflate the frame for a specific food item
         */
        @SuppressWarnings("UnusedAssignment")
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = food_list.this.getLayoutInflater();
            View result = inflater.inflate(R.layout.fooditem, null);

            TextView message = result.findViewById(R.id.footItemView);
            message.setText(getItem(position)); // get the string at position
            return result;


        }
    }


    /**
     * adds the food item to the database
     * @param requestCode the code that was sent out from this activity
     * @param resultCode the code that was sent back from the other activity
     * @param data the data to be sent back from another activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == food_list.RESULT_OK) {

                String nameString = data.getStringExtra("ab_food_Name");
                String caloriesString = data.getStringExtra("ab_food_Calories");
                String fatString = data.getStringExtra("ab_food_Fat");
                String descriptionString = data.getStringExtra("ab_food_Desc");

                mArray.add(nameString);
                cval = new ContentValues();

                cval.put(foodDatabaseHelper.KEY_Food, nameString);
                cval.put(foodDatabaseHelper.KEY_Calories, caloriesString);
                cval.put(foodDatabaseHelper.KEY_Fat, fatString);
                cval.put(foodDatabaseHelper.KEY_Desc, descriptionString);
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate= df.format(c);
                cval.put(foodDatabaseHelper.Key_Date, formattedDate.toString());
                dbInteract dbi = new dbInteract();
                dbi.execute("Insert");
                fdAdapter.notifyDataSetChanged();

            }else if (resultCode == -99){
                String idPassed = data.getStringExtra("FL_id");
                dbInteract dbi = new dbInteract();
                dbi.execute("Delete", idPassed);
                int position = data.getIntExtra("Position",-1);
                if (position != -1 ) {
                    mArray.remove(position);
                    fdAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.close();
        cursor.close();
        db.close();

    }

    /**
     * removes an item from the food list , also closes details fragment
     * @param position int value for the food item to delete
     * @param id id of the food item to delete
     * @param frag the fragment to remove
     */
    public void removeMessage(int position, String id, Fragment frag){
        dbInteract dbi = new dbInteract();
        dbi.execute("Delete", id);

        mArray.remove(position);
        fdAdapter.notifyDataSetChanged();
        getSupportFragmentManager()
                .beginTransaction()
                .remove(frag)
                .addToBackStack(null)
                .commit();


    }
    public class dbInteract extends AsyncTask<String, Void, String>{
        /**
         * an async task that gets sent one of two statements , either a delete or select statement to be run in background
         * @param body
         * @return ""
         */
        @Override
        protected String doInBackground(String... body) {

            if (body[0] == "Delete" ){
                Cursor  cs = DeleteStatement(body[1]);
            }else if (body[0] == "Insert"){
                InsertStatement();
            }

            return "";
        }
    }

    /**
     * The Delete statement
     * @param id id of the food item to delete
     * @return null
     */
    protected  Cursor DeleteStatement(String id){
        db.delete("FoodItems","_id = " + id, null );
        return null;
    }

    /**
     *the insert statement for a food item
     * @return null
     */
    protected  Cursor InsertStatement(){

        db.insert(foodDatabaseHelper.TABLE_NAME, "NullPlaceHolder", cval);
        return null;
    }
}
