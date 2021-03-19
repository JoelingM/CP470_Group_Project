package com.group.project.noteDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * A database helper used to create a database for storing information relevant to Note objects
 *
 * @author Brendan Whelan
 */
public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Notes.db";
    private static int VERSION_NUM = 1;

    public static final String TABLE_NAME = "notes";
    private static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "message";
    public static final String KEY_DATE = "date";

    private static final String SQL_CREATE_ENTRIES =
            "create table "+ TABLE_NAME + "(" + KEY_ID + " integer primary key autoincrement," +
                    KEY_TITLE + " text not null," +  KEY_CONTENT + " text," + KEY_DATE + " long)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public NoteDatabaseHelper(Context ctx){
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
