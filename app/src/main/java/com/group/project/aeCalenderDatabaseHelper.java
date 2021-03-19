package com.group.project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * A helper class to create and interact with the database
 *
 * @author Aaron
 */
@SuppressLint("SimpleDateFormat")
public class aeCalenderDatabaseHelper extends SQLiteOpenHelper {

    private static final int VERSION_NUM = 1;
    private static final String DATABASE_NAME = "calenderDB";

    ////////////////// EVENTS //////////////////////
    public static final String EVENT_TABLE = "events";
    public static final String E_KEY_ID = "_id";
    public static final String E_KEY_NAME = "name";
    public static final String E_KEY_SEMESTER = "semester";
    public static final String E_KEY_DATE = "date";
    public static final String E_KEY_TIME = "time";
    public static final String E_KEY_END_DATE = "endDate";
    public static final String E_KEY_END_TIME = "endTime";
    public static final String E_KEY_TYPE = "type";
    public static final String E_KEY_CLASS = "class";
    public static final String E_KEY_DESC = "description";
    public static final String E_KEY_REPEAT = "repeat";
    public static final String E_KEY_TOTAL_STEPS = "tSteps";
    public static final String E_KEY_CURRENT_STEPS = "steps";
    public static final String E_KEY_NOTIFICATION_ID = "notificationId";

    ////////////////// CLASSES //////////////////////
    public static final String CLASS_TABLE = "classes";
    public static final String C_KEY_ID = "_id";
    public static final String C_KEY_NAME = "name";
    public static final String C_KEY_LOCATION = "location";
    public static final String C_KEY_START_TIME = "startTime";
    public static final String C_KEY_END_TIME = "endTime";
    public static final String C_KEY_DATES = "dates";
    public static final String C_KEY_SEMESTER = "semester";
    public static final String C_KEY_NOTIFICATION_ID = "notificationId";

    ////////////////// SEMESTERS //////////////////////
    public static final String SEMESTERS_TABLE = "semester";
    public static final String S_KEY_ID = "_id";
    public static final String S_KEY_NAME = "name";
    public static final String S_KEY_DATE_START = "dateStart";
    public static final String S_KEY_DATE_END = "dateEnd";

    ////////////////// TYPES //////////////////////
    public static final String TYPES_TABLE = "types";
    public static final String T_KEY_ID = "_id";
    public static final String T_KEY_NAME = "name";

    private static final String EVENT_TABLE_CREATE = "CREATE TABLE "
            + EVENT_TABLE + "("
            + E_KEY_ID + " integer primary key autoincrement, "
            + E_KEY_NAME + " text not null, "
            + E_KEY_SEMESTER + " INTEGER, "
            + E_KEY_DATE + " text, "
            + E_KEY_TIME + " text, "
            + E_KEY_END_DATE + " text, "
            + E_KEY_END_TIME + " text, "
            + E_KEY_TYPE + " INTEGER not null, "
            + E_KEY_CLASS + " INTEGER, "
            + E_KEY_DESC + " text, "
            + E_KEY_REPEAT + " INTEGER, "
            + E_KEY_TOTAL_STEPS + " INTEGER, "
            + E_KEY_CURRENT_STEPS + " INTEGER, "
            + E_KEY_NOTIFICATION_ID + " INTEGER, "
            + "FOREIGN KEY(" + E_KEY_SEMESTER + ") REFERENCES " + SEMESTERS_TABLE + "(" + S_KEY_ID + "), "
            + "FOREIGN KEY(" + E_KEY_TYPE + ") REFERENCES " + TYPES_TABLE + "(" + T_KEY_ID + "), "
            + "FOREIGN KEY(" + E_KEY_CLASS + ") REFERENCES " + CLASS_TABLE + "(" + C_KEY_ID + ")"
            +");";

    private static final String CLASS_TABLE_CREATE = "CREATE TABLE "
            + CLASS_TABLE + "("
            + C_KEY_ID + " integer primary key autoincrement, "
            + C_KEY_NAME + " text not null, "
            + C_KEY_LOCATION + " text, "
            + C_KEY_DATES + " text, "
            + C_KEY_START_TIME + " text, "
            + C_KEY_END_TIME + " text, "
            + C_KEY_NOTIFICATION_ID + " INTEGER, "
            + C_KEY_SEMESTER + " INTEGER not null, "
            + "FOREIGN KEY(" + C_KEY_SEMESTER + ") REFERENCES " + SEMESTERS_TABLE + "(" + S_KEY_ID + ")"
            +");";

    private static final String SEMESTER_TABLE_CREATE = "CREATE TABLE "
            + SEMESTERS_TABLE + "("
            + S_KEY_ID + " integer primary key autoincrement, "
            + S_KEY_NAME + " text not null, "
            + S_KEY_DATE_START + " text not null, "
            + S_KEY_DATE_END + " text not null"
            +");";

    private static final String TYPES_TABLE_CREATE = "CREATE TABLE "
            + TYPES_TABLE + "("
            + T_KEY_ID + " integer primary key autoincrement, "
            + T_KEY_NAME + " text not null"
            +");";

    private static final String[] TYPES = { "Midterm", "Final Exam", "Test", "Assignment", "Reminder" };

    public aeCalenderDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TYPES_TABLE_CREATE);
        db.execSQL(SEMESTER_TABLE_CREATE);
        db.execSQL(CLASS_TABLE_CREATE);
        db.execSQL(EVENT_TABLE_CREATE);

        for (String type: TYPES) {
            addType(db, type);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS " + TYPES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SEMESTERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CLASS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_TABLE);

        onCreate(db);

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Add a new event to the Database
     * @param db The db to add the event to
     * @param name The name of the event
     * @param semesterId The id of the semester the event is in
     * @param date The start date of the event
     * @param time The start time of the event
     * @param endDate The end date of the event
     * @param endTime The end time of the event
     * @param typeId The id of the type the event is associated with
     * @param classId The id of the class the event is associated with
     * @param desc The description of the event
     * @param repeat does the event repeat weekly
     * @param currentSteps how many steps is the event at
     * @param totalSteps whats the total steps the event has
     * @param notificationId the id of the notification for the event
     * @return The id from the database of the created event
     */
    public static long addEvent(SQLiteDatabase db, String name, int semesterId, String date, String time,
                                String endDate, String endTime, int typeId, int classId, String desc, boolean repeat,
                                int currentSteps, int totalSteps, int notificationId){

        ContentValues cValues = new ContentValues();
        cValues.put(E_KEY_NAME, name);
        cValues.put(E_KEY_SEMESTER, semesterId);
        cValues.put(E_KEY_DATE, date);
        cValues.put(E_KEY_TIME, time);
        cValues.put(E_KEY_END_DATE, endDate);
        cValues.put(E_KEY_END_TIME, endTime);
        cValues.put(E_KEY_TYPE, typeId);
        cValues.put(E_KEY_CLASS, classId);
        cValues.put(E_KEY_DESC, desc);
        cValues.put(E_KEY_REPEAT, repeat ? 1 : 0);
        cValues.put(E_KEY_CURRENT_STEPS, currentSteps);
        cValues.put(E_KEY_TOTAL_STEPS, totalSteps);
        cValues.put(E_KEY_NOTIFICATION_ID, notificationId);


        return db.insert(EVENT_TABLE, "", cValues);

    }

    /**
     * Updates an event in the database
     * @param db The db that the event is in
     * @param id The id of the event to update
     * @param event The updated event
     */
    public static void updateEvent(SQLiteDatabase db, long id, aeEvent event, long semesterId, long classId, long typeId) {

        ContentValues cValues = new ContentValues();

        cValues.put(E_KEY_NAME, event.getName());
        cValues.put(E_KEY_SEMESTER, semesterId);
        cValues.put(E_KEY_DATE, event.getDate());
        cValues.put(E_KEY_TIME, event.getTime());
        cValues.put(E_KEY_END_DATE, event.getEndDate());
        cValues.put(E_KEY_END_TIME, event.getEndTime());
        cValues.put(E_KEY_TYPE, typeId);
        cValues.put(E_KEY_CLASS, classId);
        cValues.put(E_KEY_DESC, event.getDesc());
        cValues.put(E_KEY_REPEAT, event.getRepeat() ? 1 : 0);
        cValues.put(E_KEY_NOTIFICATION_ID, event.getNotificationId());

        db.update(EVENT_TABLE, cValues, E_KEY_ID + "=" + id , null);
    }

    /**
     * Updates a class in the db
     * @param db The db that the class is in
     * @param id The id of the class to update
     * @param aeClass The updated class
     */
    public static void updateClass(SQLiteDatabase db, long id, aeClass aeClass, long semesterId) {

        ContentValues cValues = new ContentValues();

        StringBuilder builder = new StringBuilder();
        for (String date : aeClass.getDates()) {
            builder.append(date);
            builder.append(" ");
        }
        String dateText = builder.toString();

        cValues.put(C_KEY_NAME, aeClass.getName());
        cValues.put(C_KEY_SEMESTER, semesterId);
        cValues.put(C_KEY_START_TIME, aeClass.getStartTime());
        cValues.put(C_KEY_END_TIME, aeClass.getEndTime());
        cValues.put(C_KEY_LOCATION, aeClass.getLocation());
        cValues.put(C_KEY_DATES, dateText);

        db.update(CLASS_TABLE, cValues, C_KEY_ID + "=" + id , null);
    }

    /**
     * Updated the number of steps in the db for an event
     * @param db The cb that contains the event
     * @param id The id of the event to update
     * @param progress The new progress
     */
    public static void updateEventProgress(SQLiteDatabase db, long id,  int progress) {

        ContentValues cValues = new ContentValues();

        cValues.put(E_KEY_CURRENT_STEPS, progress);

        db.update(EVENT_TABLE, cValues, E_KEY_ID + "=" + id , null);
    }

    /**
     * Removes an event from the database
     * @param db The db that contains the event
     * @param id The id of the event to delete
     */
    public static void removeEvent(SQLiteDatabase db, long id) {
        db.delete(EVENT_TABLE, E_KEY_ID + "=" + id, null);
    }

    /**
     * Add a new type to the db
     * @param db The db to add the type to
     * @param name The name of the type
     */
    public static void addType(SQLiteDatabase db, String name) {

        ContentValues cValues = new ContentValues();
        cValues.put(T_KEY_NAME, name);

        db.insert(TYPES_TABLE, "", cValues);
    }

    /**
     * Gets all the id's of the types in the database
     * @param db The db that has the types
     * @return An ArrayList with all of the types in the order they appear
     */
    public static ArrayList<Integer> getAllTypesIds(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + T_KEY_ID + " FROM " + TYPES_TABLE, null);

        ArrayList<Integer> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getInt(0));
            c.moveToNext();
        }
        c.close();

        return types;

    }

    /**
     * Gets all the types from the database
     * @param db The db that has the types
     * @return An ArrayList with all of the types in the order they appear
     */
    public static ArrayList<String> getAllTypes(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + T_KEY_NAME + " FROM " + TYPES_TABLE, null);

        ArrayList<String> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return types;

    }

    /**
     * Gets all of id's of the semesters from the database
     * @param db The db that has the semesters
     * @return An ArrayList that contains all the id's of the semesters in the order they appear
     */
    public static ArrayList<Integer> getAllSemestersIds(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + S_KEY_ID + " FROM " + SEMESTERS_TABLE, null);

        ArrayList<Integer> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getInt(0));
            c.moveToNext();
        }
        c.close();

        return types;
    }

    /**
     * Gets all of the names of the semesters in the database
     * @param db The db that has the semesters
     * @return An ArrayList that contains all the names of the semesters in the order they appear
     */
    public static ArrayList<String> getAllSemesters(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + S_KEY_NAME + " FROM " + SEMESTERS_TABLE, null);

        ArrayList<String> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return types;
    }

    /**
     * Gets all of the class Id's from the db
     * @param db The db that has the classes
     * @return An ArrayList of the id's of the classes in the order they appear
     */
    public static ArrayList<Integer> getAllClassNamesIds(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + C_KEY_ID + " FROM " + CLASS_TABLE, null);

        ArrayList<Integer> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getInt(0));
            c.moveToNext();
        }
        c.close();

        return types;
    }

    /**
     * Gets all the class names from the db
     * @param db The db that has the classes
     * @return An arrayList of the name's of the classes in the order they appear
     */
    public static ArrayList<String> getAllClassNames(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT " + C_KEY_NAME + " FROM " + CLASS_TABLE, null);

        ArrayList<String> types = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            types.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return types;
    }

    /**
     * Adds a new semester to the database
     * @param db The db to add the semester to
     * @param name The name of the semester
     * @param startDate The start date of the semester
     * @param endDate The end date of the semester
     *
     * @return id of semester inserted
     */
    public static long addSemester(SQLiteDatabase db, String name, String startDate, String endDate) {

        ContentValues cValues = new ContentValues();
        cValues.put(S_KEY_NAME, name);
        cValues.put(S_KEY_DATE_START, startDate);
        cValues.put(S_KEY_DATE_END, endDate);

        return db.insert(SEMESTERS_TABLE, "", cValues);
    }

    /**
     * Removes a semester from the database
     * @param db The db to remove the semester from
     * @param id The id of the semester
     */
    public static void removeSemester(SQLiteDatabase db, int id) {
        db.delete(SEMESTERS_TABLE, S_KEY_ID + "=" + id, null);
    }

    /**
     * Adds a new class the the database
     * @param db The db to add the class to
     * @param name The name of the class
     * @param location The location of the class
     * @param semesterId The id of the semester associated with the class
     * @param dates The string of dates the class is on, separated by spaces, eg (MON TUE ...)
     * @param startTime The start time of the class
     * @param endTime The end time of the class
     * @return The id of the created class
     */
    public static long addClass(SQLiteDatabase db, String name, String location, int semesterId, String dates, String startTime, String endTime) {

        ContentValues cValues = new ContentValues();
        cValues.put(C_KEY_NAME, name);
        cValues.put(C_KEY_SEMESTER, semesterId);
        cValues.put(C_KEY_LOCATION, location);
        cValues.put(C_KEY_DATES, dates);
        cValues.put(C_KEY_START_TIME, startTime);
        cValues.put(C_KEY_END_TIME, endTime);

        return db.insert(CLASS_TABLE, "", cValues);
    }

    /**
     * Removes a class from the database
     * @param db The db to remove the class from
     * @param id The id of the class to remove
     */
    public static void removeClass(SQLiteDatabase db, long id) {
        db.delete(CLASS_TABLE, C_KEY_ID + "=" + id, null);
    }

    /**
     * Gets the events on the given date
     * The results are returned in the processFinish function in the AsyncResponseEvent interface
     * @see ExecGetEvents.AsyncResponseEvent
     * @param resultClass The class that will get the response, Must implement AsyncResponseEvent
     * @param db The db to get the events from
     * @param date The date to filter by, can be null
     */
    public static void getEvents(ExecGetEvents.AsyncResponseEvent resultClass, SQLiteDatabase db, String date) {
        (new ExecGetEvents(resultClass, db)).execute(date);
    }

    /**
     * Gets the classes for a specific semester
     * The results are returned in the processFinish function in the AsyncResponseClass interface
     * @param resultClass The class that will get the response, Must implement AsyncResponseClass
     * @param db The db to get the class from
     * @param semesterId The semester to filter by
     */
    public static void getClasses(ExecGetClasses.AsyncResponseClass resultClass, SQLiteDatabase db, int semesterId) {
        (new ExecGetClasses(resultClass, db)).execute(semesterId);
    }

    /**
     * An Async Task to retrieve the events
     */
    public static class ExecGetEvents extends AsyncTask<String, Void, ArrayList<aeEvent>> {

        private AsyncResponseEvent resultClass;
        private SQLiteDatabase db;

        /**
         * An Async Task to retrieve the events
         * @param resultClass The class for the results
         * @param db The db to get the events from
         */
        public ExecGetEvents(AsyncResponseEvent resultClass, SQLiteDatabase db) {
            super();
            this.resultClass = resultClass;
            this.db = db;
        }

        /**
         * An Interface that must be implemented to get the results
         */
        public interface AsyncResponseEvent {
            void processFinish(ArrayList<aeEvent> events);
        }

        /**
         * Calls processFinished in the AsyncResponseEvent
         * for the given class with the results
         * @param events The results from the db
         */
        @Override
        protected void onPostExecute(ArrayList<aeEvent> events) {

            if (resultClass != null) {
                resultClass.processFinish(events);
            }
        }

        /**
         * Gets the events from the db
         * @param strings The date at index 0
         * @return An ArrayList of events
         */
        @Override
        protected ArrayList<aeEvent> doInBackground(String... strings) {

            ArrayList<aeEvent> events = new ArrayList<>();

            String whereClaus = "";
            String date = null;

            if (strings.length > 0) {
                date = strings[0];
            }

            if (date != null) {
                String[] dates = date.split("/");
                whereClaus = " WHERE " + E_KEY_DATE + "=\"" + dates[0] + "/" + dates[1] + "/" + dates[2] + "\""
                + " OR " + E_KEY_END_DATE + "=\"" + dates[0] + "/" + dates[1] + "/" + dates[2] + "\"";
            }

            Cursor c = db.rawQuery("SELECT * FROM " + EVENT_TABLE + whereClaus, null);

            c.moveToFirst();
            while(!c.isAfterLast()) {
                int id = c.getInt(c.getColumnIndex(E_KEY_ID));
                String name = c.getString(c.getColumnIndex(E_KEY_NAME));
                int semesterId = c.getInt(c.getColumnIndex(E_KEY_CLASS));
                String eDate = c.getString(c.getColumnIndex(E_KEY_DATE));
                String time = c.getString(c.getColumnIndex(E_KEY_TIME));
                String endDate = c.getString(c.getColumnIndex(E_KEY_END_DATE));
                String endTime = c.getString(c.getColumnIndex(E_KEY_END_TIME));
                int typeId = c.getInt(c.getColumnIndex(E_KEY_ID));
                int classId = c.getInt(c.getColumnIndex(E_KEY_CLASS));
                String desc = c.getString(c.getColumnIndex(E_KEY_DESC));
                int repeat = c.getInt(c.getColumnIndex(E_KEY_REPEAT));
                int currentSteps = c.getInt(c.getColumnIndex(E_KEY_CURRENT_STEPS));
                int totalSteps = c.getInt(c.getColumnIndex(E_KEY_TOTAL_STEPS));
                int notificationID = c.getInt(c.getColumnIndex(E_KEY_NOTIFICATION_ID));

                Cursor c2 = db.rawQuery("SELECT " + S_KEY_NAME + " FROM " + SEMESTERS_TABLE + " WHERE " + S_KEY_ID + "=" + semesterId, null);
                Cursor c3 = db.rawQuery("SELECT " + T_KEY_NAME + " FROM " + TYPES_TABLE + " WHERE " + T_KEY_ID + "=" + typeId, null);
                Cursor c4 = db.rawQuery("SELECT " + C_KEY_NAME + " FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + classId, null);

                String semester = "";
                String type = "";
                String className = "";
                if (c2.moveToFirst()) {
                    semester = c2.getString(c2.getColumnIndex(S_KEY_NAME));
                }
                if (c3.moveToFirst()) {
                    type = c3.getString(c3.getColumnIndex(T_KEY_NAME));
                }
                if (c4.moveToFirst()) {
                    className = c4.getString(c4.getColumnIndex(C_KEY_NAME));
                }

                c2.close();
                c3.close();
                c4.close();

                aeEvent event = new aeEvent(id, name, semester, eDate, time, endDate, endTime, type, className, desc, repeat, currentSteps, totalSteps, notificationID);

                events.add(event);
                c.moveToNext();
            }

            c.close();

            return events;
        }
    }

    /**
     * An Async Task to retrieve the classes
     */
    public static class ExecGetClasses extends AsyncTask<Integer, Void, ArrayList<aeClass>> {

        private AsyncResponseClass resultClass;
        private SQLiteDatabase db;

        /**
         * An Async Task to retrieve the classes
         * @param resultClass The classes that needs the results
         * @param db The db to get the classes form
         */
        public ExecGetClasses(AsyncResponseClass resultClass,  SQLiteDatabase db) {
            super();
            this.resultClass = resultClass;
            this.db = db;
        }

        /**
         * An Interface that must be implemented to get the results
         */
        public interface AsyncResponseClass {
            void processFinish(ArrayList<aeClass> classes);
        }

        /**
         * Calls processFinished in the AsyncResponseClass
         * for the given class with the results
         * @param classes The results from the db
         */
        @Override
        protected void onPostExecute(ArrayList<aeClass> classes) {

            if (resultClass != null) {
                resultClass.processFinish(classes);
            }
        }

        /**
         * Gets the classes from the db
         * @param ints The id of the semester at index 0
         * @return An ArrayList of classes
         */
        @Override
        protected ArrayList<aeClass> doInBackground(Integer... ints) {

            ArrayList<aeClass> classes = new ArrayList<>();

            String whereClause = "";

            if (ints.length > 0) {
                whereClause = " WHERE " + C_KEY_SEMESTER + "=" + ints[0];
            }

            Cursor c = db.rawQuery("SELECT * FROM " + CLASS_TABLE + whereClause, null);

            c.moveToFirst();
            while(!c.isAfterLast()) {
                int id = c.getInt(c.getColumnIndex(C_KEY_ID));
                String name = c.getString(c.getColumnIndex(C_KEY_NAME));
                int semesterId = c.getInt(c.getColumnIndex(C_KEY_SEMESTER));
                String location = c.getString(c.getColumnIndex(C_KEY_LOCATION));
                String dates = c.getString(c.getColumnIndex(C_KEY_DATES));
                String startTime = c.getString(c.getColumnIndex(C_KEY_START_TIME));
                String endTme = c.getString(c.getColumnIndex(C_KEY_START_TIME));

                Cursor c2 = db.rawQuery("SELECT " + S_KEY_NAME + " FROM " + SEMESTERS_TABLE + " WHERE " + S_KEY_ID + "=" + semesterId, null);

                String semester = "";
                if (c2.moveToFirst()) {
                    semester = c2.getString(c2.getColumnIndex(S_KEY_NAME));
                }

                c2.close();

                aeClass aeClass = new aeClass(id, name, semester, location, dates.split(","), startTime, endTme);

                classes.add(aeClass);
                c.moveToNext();
            }

            c.close();

            return classes;
        }
    }

}
