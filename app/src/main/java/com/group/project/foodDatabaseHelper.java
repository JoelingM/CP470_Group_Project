package com.group.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class foodDatabaseHelper extends SQLiteOpenHelper {


    private static String DATABASE_NAME = "Food.db";
    private static int VERSION_NUM = 12 ;

    public static String Key_Date = "Date";
    public static String TABLE_NAME = "FoodItems";
    public static String KEY_Desc = "Description";
    public static String KEY_Fat = "Fat";
    public static String KEY_Calories = "Calories";
    public static String KEY_Food = "Food_Name";
    public static String KEY_ID = "_id";

    private static String DATABASE_CREATE = "create table " + TABLE_NAME + "(" + KEY_ID + " integer primary key autoincrement," + KEY_Food + " text not null,"+KEY_Calories+" text ,"+ KEY_Fat+" text , "+ KEY_Desc +" text , "+ Key_Date+" text );";


    /**
     * This is creating a database helper
     * @param ctx context
     */
    public foodDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    /**
     *
     * @param db SQLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);


    }

    /**
     *  upgrades the database
     * @param db
     * @param oldVersion; int for the old version of the database
     * @param newVersion; int for new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        Log.i("foodDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }
}


