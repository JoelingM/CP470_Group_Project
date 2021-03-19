package com.group.project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;

import static com.group.project.aeCalenderDatabaseHelper.CLASS_TABLE;
import static com.group.project.aeCalenderDatabaseHelper.C_KEY_ID;
import static com.group.project.aeCalenderDatabaseHelper.C_KEY_NAME;
import static com.group.project.aeCalenderDatabaseHelper.EVENT_TABLE;
import static com.group.project.aeCalenderDatabaseHelper.E_KEY_ID;
import static com.group.project.aeCalenderDatabaseHelper.E_KEY_NAME;
import static com.group.project.aeCalenderDatabaseHelper.SEMESTERS_TABLE;
import static com.group.project.aeCalenderDatabaseHelper.S_KEY_ID;
import static com.group.project.aeCalenderDatabaseHelper.S_KEY_NAME;
import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class CalenderTests {

    @Mock
    Context mockContext;

    @Mock
    SQLiteDatabase Mdb;

    aeCalenderDatabaseHelper helper;

    @Before
    public void setUp() {

        mockContext = ApplicationProvider.getApplicationContext();

        helper = new aeCalenderDatabaseHelper(mockContext);

        Mdb = helper.getWritableDatabase();
    }

    @Test
    public void testGetAllSemestersOnEmpty_CP470_AE_TC_014() {


        ArrayList<String> semesters = aeCalenderDatabaseHelper.getAllSemesters(Mdb);

        assertEquals(0, semesters.size());

    }

    @Test
    public void testCreateSemester_CP470_AE_TC_011() {
        long id = aeCalenderDatabaseHelper.addSemester(Mdb, "Test1", "01/04/2019", "01/05/2019");

        Cursor c = Mdb.rawQuery("SELECT * FROM " + SEMESTERS_TABLE + " WHERE " + S_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(S_KEY_NAME));

        assertEquals("Test1", name);
    }

    @Test
    public void testGetAllClassNamesAfterAdd_CP470_AE_TC_013() {

        aeCalenderDatabaseHelper.addSemester(Mdb, "Test1", "01/04/2019", "01/05/2019");

        ArrayList<String> semesters = aeCalenderDatabaseHelper.getAllSemesters(Mdb);

        assertEquals(1, semesters.size());

    }

    @Test
    public void testCreateClass_CP470_AE_TC_002() {

        long id = aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");

        Cursor c = Mdb.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(C_KEY_NAME));

        assertEquals("Test2", name);

    }

    @Test
    public void testCreateEvent_CP470_AE_TC_001() {

        long id = aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        Cursor c = Mdb.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + E_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(E_KEY_NAME));

        assertEquals("Test3", name);
    }


    @Test
    public void testDeleteEvent_CP_AE_TC_003() {

        long id = aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        Cursor c = Mdb.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + E_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(E_KEY_NAME));

        assertEquals("Test3", name);

        aeCalenderDatabaseHelper.removeEvent(Mdb, id);

        c = Mdb.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + E_KEY_ID + "=" + id, null);

        assertEquals(0, c.getCount());

    }

    @Test
    public void testDeleteClass_CP470_AE_TC_004() {

        long id = aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");

        Cursor c = Mdb.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(C_KEY_NAME));

        assertEquals("Test2", name);

        aeCalenderDatabaseHelper.removeClass(Mdb, id);

        c = Mdb.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + id, null);

        assertEquals(0, c.getCount());


    }

    @Test
    public void testUpdateEvent_CP470_AE_TC_005() {

        long id = aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        Cursor c = Mdb.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + E_KEY_ID + "=" + id, null);

        aeEvent event = new aeEvent(id, "Test5", "Somthing", "02/02/2019", "10:30",
                "03/02/2019", "11:20", "S", "S", "Test", 0, 0, 10, -1);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(E_KEY_NAME));

        assertEquals("Test3", name);

        aeCalenderDatabaseHelper.updateEvent(Mdb, id, event, 1, 1, 1);

        c = Mdb.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + E_KEY_ID + "=" + id, null);

        c.moveToFirst();
        name = c.getString(c.getColumnIndex(E_KEY_NAME));

        assertEquals("Test5", name);
    }

    @Test
    public void testUpdateClass_CP470_AE_TC_006() {

        long id = aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");

        aeClass aeClass = new aeClass(id, "Test5", "Somewhere", "Something", new String[] {"MON"}, "10:30", "11:30");

        Cursor c = Mdb.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + id, null);

        c.moveToFirst();
        String name = c.getString(c.getColumnIndex(C_KEY_NAME));

        assertEquals("Test2", name);

        aeCalenderDatabaseHelper.updateClass(Mdb, id, aeClass, 1);

        c = Mdb.rawQuery("SELECT * FROM " + CLASS_TABLE + " WHERE " + C_KEY_ID + "=" + id, null);

        c.moveToFirst();
        name = c.getString(c.getColumnIndex(C_KEY_NAME));

        assertEquals("Test5", name);
    }

    @Test
    public void testGetEveryEvent_CP470_AE_TC_007() {

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test1", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test2", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/03/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.ExecGetEvents getEvents = new aeCalenderDatabaseHelper.ExecGetEvents(null, Mdb);

        ArrayList<aeEvent> events = getEvents.doInBackground();

        assertEquals(3, events.size());

    }

    @Test
    public void testGetEveryClass_CP470_AE_TC_008() {

        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");
        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");
        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 2, "MON", "10:30", "11:30");

        aeCalenderDatabaseHelper.ExecGetClasses getClasses = new aeCalenderDatabaseHelper.ExecGetClasses(null, Mdb);

        ArrayList<aeClass> classes = getClasses.doInBackground();

        assertEquals(3, classes.size());

    }

    @Test
    public void testGetEveryEventWithDate_CP470_AE_TC_009() {
        aeCalenderDatabaseHelper.addEvent(Mdb, "Test1", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test2", 1, "02/02/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.addEvent(Mdb, "Test3", 1, "02/03/2019", "10:30",
                "03/02/2019", "11:20", 1, 1, "Test", false, 0, 10, -1);

        aeCalenderDatabaseHelper.ExecGetEvents getEvents = new aeCalenderDatabaseHelper.ExecGetEvents(null, Mdb);

        ArrayList<aeEvent> events = getEvents.doInBackground("02/02/2019");

        assertEquals(2, events.size());
    }

    @Test
    public void testGetEverySemesterWithSemester_CP_AE_TC_010() {

        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");
        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 1, "MON", "10:30", "11:30");
        aeCalenderDatabaseHelper.addClass(Mdb, "Test2", "Somewhere", 2, "MON", "10:30", "11:30");

        aeCalenderDatabaseHelper.ExecGetClasses getClasses = new aeCalenderDatabaseHelper.ExecGetClasses(null, Mdb);

        ArrayList<aeClass> classes = getClasses.doInBackground(1);

        assertEquals(2, classes.size());

    }




}