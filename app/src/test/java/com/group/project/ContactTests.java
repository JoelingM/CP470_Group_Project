package com.group.project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class ContactTests {
    Context context;
    SQLiteDatabase database;
    ContactSQLHelper helper;

    @Before
    public void setUp() {
        // Create a fresh Contact, ContactSQLHelper, and database.
        this.context = ApplicationProvider.getApplicationContext();
        this.helper = new ContactSQLHelper(this.context);
        this.database = this.helper.getWritableDatabase();
        this.helper.onUpgrade(this.database, this.database.getVersion(), this.database.getVersion() + 1);
    }

    @Test
    public void CP470_NC_TC_001() {
        ContactSQLHelper.GetContactsTask queryTask = this.helper.new GetContactsTask(null, null, null);
        List<Contact> contacts = queryTask.doInBackground();

        assertEquals(contacts.size(), 0);
    }

    @Test
    public void CP470_NC_TC_002() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact contact = insertTask.doInBackground();

        assertNotEquals(contact.getID(), -1);
        assertEquals(contact.getName(), "Alice");
        assertNull(contact.getIcon());
        assertEquals(contact.getPhoneNumbers().size(), 1);
        assertEquals(contact.getPhoneNumbers().get(0), "1111111111");
    }

    @Test
    public void CP470_NC_TC_003() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        insertTask.doInBackground();

        ContactSQLHelper.GetContactsTask queryTask = this.helper.new GetContactsTask(null, null, null);
        List<Contact> contacts = queryTask.doInBackground();

        assertEquals(contacts.size(), 1);
        Contact contact = contacts.get(0);
        assertNotEquals(contact.getID(), -1);
        assertEquals(contact.getName(), "Alice");
        assertNull(contact.getIcon());
        assertEquals(contact.getPhoneNumbers().size(), 1);
        assertEquals(contact.getPhoneNumbers().get(0), "1111111111");
    }

    @Test
    public void CP470_NC_TC_004() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact updateContact = insertTask.doInBackground();
        long initialID = updateContact.getID();

        int iconWidth = 100; int iconHeight = 100;
        Bitmap icon = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        for (int r = 0; r < iconWidth; r++)
            for (int c = 0; c < iconHeight; c++)
                icon.setPixel(c, r, 0x00000000);
        updateContact.setIcon(icon);

        ContactSQLHelper.SaveContactsTask saveTask = this.helper.new SaveContactsTask(null, null, updateContact, null);
        Contact contact = saveTask.doInBackground();

        assertEquals(contact.getID(), initialID);
        assertEquals(contact.getName(), "Alice");
        assertEquals(contact.getPhoneNumbers().size(), 1);
        assertEquals(contact.getPhoneNumbers().get(0), "1111111111");
        assertNotNull(contact.getIcon());
        assert(icon.sameAs(contact.getIcon()));
    }

    @Test
    public void CP470_NC_TC_005() {
        int iconWidth = 100; int iconHeight = 100;

        Bitmap insertIcon = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        for (int r = 0; r < iconWidth; r++)
            for (int c = 0; c < iconHeight; c++)
                insertIcon.setPixel(c, r, 0x00000000);
        Contact insertContact = new Contact("Alice", insertIcon, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact updateContact = insertTask.doInBackground();
        long initialID = updateContact.getID();

        Bitmap updateIcon = Bitmap.createBitmap(iconWidth, iconHeight, Bitmap.Config.ARGB_8888);
        for (int r = 0; r < iconWidth; r++)
            for (int c = 0; c < iconHeight; c++)
                updateIcon.setPixel(c, r, 0xFFFFFFFF);
        updateContact.setIcon(updateIcon);

        ContactSQLHelper.SaveContactsTask saveTask = this.helper.new SaveContactsTask(null, null, updateContact, null);
        Contact contact = saveTask.doInBackground();

        assertEquals(contact.getID(), initialID);
        assertEquals(contact.getName(), "Alice");
        assertEquals(contact.getPhoneNumbers().size(), 1);
        assertEquals(contact.getPhoneNumbers().get(0), "1111111111");
        assertNotNull(contact.getIcon());
        assert(!insertIcon.sameAs(contact.getIcon()));
        assert(updateIcon.sameAs(contact.getIcon()));
    }

    @Test
    public void CP470_NC_TC_006() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact updateContact = insertTask.doInBackground();

        updateContact.getPhoneNumbers().add("2222222222");
        ContactSQLHelper.SaveContactsTask saveTask = this.helper.new SaveContactsTask(null, null, updateContact, null);
        Contact contact = saveTask.doInBackground();

        assertNotEquals(contact.getID(), -1);
        assertEquals(contact.getName(), "Alice");
        assertNull(contact.getIcon());
        assertEquals(contact.getPhoneNumbers().size(), 2);
        assertEquals(contact.getPhoneNumbers().get(0), "1111111111");
        assertEquals(contact.getPhoneNumbers().get(1), "2222222222");
    }

    @Test
    public void CP470_NC_TC_007() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111", "2222222222")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact updateContact = insertTask.doInBackground();

        updateContact.getPhoneNumbers().remove("1111111111");
        ContactSQLHelper.SaveContactsTask saveTask = this.helper.new SaveContactsTask(null, null, updateContact, null);
        Contact contact = saveTask.doInBackground();

        assertNotEquals(contact.getID(), -1);
        assertEquals(contact.getName(), "Alice");
        assertNull(contact.getIcon());
        assertEquals(contact.getPhoneNumbers().size(), 1);
        assertEquals(contact.getPhoneNumbers().get(0), "2222222222");
    }

    @Test
    public void CP470_NC_TC_008() {
        Contact insertContact = new Contact("Alice", null, new ArrayList<>(Arrays.asList("1111111111")));
        ContactSQLHelper.SaveContactsTask insertTask = this.helper.new SaveContactsTask(null, null, insertContact, null);
        Contact updateContact = insertTask.doInBackground();

        updateContact.getPhoneNumbers().remove("1111111111");
        ContactSQLHelper.DeleteContactsTask deleteTask = this.helper.new DeleteContactsTask(null, null, updateContact, null);
        deleteTask.doInBackground();

        ContactSQLHelper.GetContactsTask queryTask = this.helper.new GetContactsTask(null, null, null);
        List<Contact> contacts = queryTask.doInBackground();

        assertEquals(contacts.size(), 0);
    }
}
