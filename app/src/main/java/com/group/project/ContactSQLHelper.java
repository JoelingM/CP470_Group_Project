package com.group.project;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class ContactSQLHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "contacts.db";
    public static final int DATABASE_VERSION = 5;

    public static final String TABLE_CONTACT = "CONTACT";
    public static final String CONTACT_ID = "_id";
    public static final String CONTACT_NAME = "name";
    public static final String CONTACT_CREATE = "CREATE TABLE " + TABLE_CONTACT + " (" + CONTACT_ID + " integer primary key autoincrement, " + CONTACT_NAME + " text NOT NULL);";

    public static final String TABLE_NUMBER = "NUMBER";
    public static final String NUMBER_ID = "_id";
    public static final String NUMBER_CONTACT_ID = "contact_id";
    public static final String NUMBER_NUMBER = "number";
    public static final String NUMBER_PRIORITY = "priority";
    public static final String NUMBER_CREATE = "CREATE TABLE " + TABLE_NUMBER + " (" + NUMBER_ID + " integer primary key autoincrement, " + NUMBER_CONTACT_ID + " integer NOT NULL, " + NUMBER_NUMBER + " text NOT NULL, " + NUMBER_PRIORITY + " integer NOT NULL, FOREIGN KEY (" + NUMBER_CONTACT_ID + ") REFERENCES " + TABLE_CONTACT + "(" + CONTACT_ID + ") ON DELETE CASCADE);";

    public static final String TABLE_ICON = "ICON";
    public static final String ICON_ID = "_id";
    public static final String ICON_CONTACT_ID = "contact_id";
    public static final String ICON_ICON = "icon";
    public static final String ICON_CREATE = "CREATE TABLE " + TABLE_ICON + " (" + ICON_ID + " integer primary key autoincrement, " + ICON_CONTACT_ID + " integer NOT NULL, " + ICON_ICON + " blob, FOREIGN KEY (" + ICON_CONTACT_ID + ") REFERENCES " + TABLE_CONTACT + " (" + CONTACT_ID + ") ON DELETE CASCADE);";

    /**
     * Constructor.
     * @param context
     */
    ContactSQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * {@inheritDoc}
     * @param db
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // Make sure the foreign keys are being obeyed.
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    /**
     * {@inheritDoc}
     * @param database
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create the database.
        database.execSQL(CONTACT_CREATE);
        database.execSQL(NUMBER_CREATE);
        database.execSQL(ICON_CREATE);
    }

    /**
     * {@inheritDoc}
     * @param database
     * @param prevVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, int prevVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ICON + ";");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NUMBER + ";");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT + ";");
        onCreate(database);
    }

    /**
     * Get all the Contacts from the database.
     */
    public class GetContactsTask extends AsyncTask<Void, Float, List<Contact>> {
        ProgressBar progressBar;
        List<Contact> writeContacts;
        RecyclerView.Adapter adapter;

        /**
         * Constructor.
         * @param progressBar The ProgressBar, if any, to update.
         * @param writeContacts The List of Contact to write the results to.
         * @param adapter The RecyclerView.Adapter to call on dataset changed.
         */
        GetContactsTask(ProgressBar progressBar, List<Contact> writeContacts, RecyclerView.Adapter adapter) {
            this.progressBar = progressBar;
            this.writeContacts = writeContacts;
            this.adapter = adapter;
        }

        /**
         * {@inheritDoc}
         * @param values
         */
        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            if (this.progressBar != null) {
                this.progressBar.setProgress(Math.round(values[0]));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (this.progressBar != null) {
                this.progressBar.setProgress(0);
                this.progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        }

        /**
         * {@inheritDoc}
         * @param contacts The returned List of Contact.
         */
        @Override
        protected void onPostExecute(List<Contact> contacts) {
            super.onPostExecute(contacts);

            if (this.progressBar != null) {
                this.progressBar.setVisibility(ProgressBar.INVISIBLE);
                this.progressBar.setProgress(0);
            }
            if (this.writeContacts != null) {
                this.writeContacts.clear();
                this.writeContacts.addAll(contacts);
            }
            if (this.adapter != null) {
                this.adapter.notifyDataSetChanged();
            }
        }

        /**
         * {@inheritDoc}
         * @param voids
         * @return The List of Contact
         */
        @Override
        protected List<Contact> doInBackground(Void... voids) {
            // Access the database.
            SQLiteDatabase database = ContactSQLHelper.this.getReadableDatabase();
            Cursor c = database.rawQuery(""
                    + "SELECT "
                    + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_ID + " AS contact_id, "
                    + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_NAME + " AS contact_name, "
                    + ContactSQLHelper.TABLE_NUMBER + "." + ContactSQLHelper.NUMBER_NUMBER + " AS number, "
                    + ContactSQLHelper.TABLE_ICON + "." + ContactSQLHelper.ICON_ICON + " AS icon "
                    + "FROM "
                    + ContactSQLHelper.TABLE_CONTACT + " INNER JOIN "
                    + ContactSQLHelper.TABLE_NUMBER + " ON " + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_ID + " = " + ContactSQLHelper.TABLE_NUMBER + "." + ContactSQLHelper.NUMBER_CONTACT_ID
                    + " LEFT OUTER JOIN " + ContactSQLHelper.TABLE_ICON + " ON " + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_ID + " = " + ContactSQLHelper.TABLE_ICON + "." + ContactSQLHelper.ICON_CONTACT_ID
                    + " ORDER BY " + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_NAME + " ASC, "
                    + ContactSQLHelper.TABLE_CONTACT + "." + ContactSQLHelper.CONTACT_ID + " ASC, "
                    + ContactSQLHelper.TABLE_NUMBER + "." + ContactSQLHelper.NUMBER_PRIORITY + " ASC;", null);
            c.moveToFirst();

            // Get the total number of rows to process.
            int numRows = c.getCount();
            int workDone = 0;
            this.publishProgress(workDone / Math.max(1, (float) numRows));

            // Get the information on the columns returned.
            int contactIdCol = c.getColumnIndex("contact_id");
            int contactNameCol = c.getColumnIndex("contact_name");
            int numberCol = c.getColumnIndex("number");
            int iconCol = c.getColumnIndex("icon");

            // Fill the Contacts list.
            List<Contact> contacts = new ArrayList<>();
            long curId = -1;
            String curName = "";
            List<String> curNumbers = new ArrayList<>();
            Bitmap curIcon = null;
            while (!c.isAfterLast()) {
                if (curId != c.getInt(contactIdCol)) {
                    if (curId != -1) {
                        contacts.add(new Contact(curId, curName, curIcon, curNumbers));
                    }
                    curId = c.getInt(contactIdCol);
                    curName = c.getString(contactNameCol);
                    curNumbers.clear();
                    try {
                        curIcon = BitmapFactory.decodeByteArray(c.getBlob(iconCol), 0, c.getBlob(iconCol).length);
                    } catch (Exception e) {
                        curIcon = null;
                    }
                }
                curNumbers.add(c.getString(numberCol));
                c.moveToNext();

                // Update the progress.
                this.publishProgress(++workDone / (float) numRows);
            }

            // Add the final Contact.
            if (curId != -1) {
                contacts.add(new Contact(curId, curName, curIcon, curNumbers));
            }

            // Close the connection and return the Contacts.
            c.close();
            database.close();
            return contacts;
        }
    }

    /**
     * Save a Contact to the database.
     */
    public class SaveContactsTask extends AsyncTask<Void, Float, Contact> {
        Activity finishOnCompleteActivity;
        Integer returnCode;
        Contact saveContact;
        ProgressBar progressBar;

        /**
         * Constructor.
         * @param finishOnCompleteActivity The Activity, if any, which should be finished when the AsyncTask is complete.
         * @param returnCode The return code, if any, to return on finish.
         * @param saveContact The Contact, if any, to update to.
         * @param progressBar The ProgressBar, if any, to update.
         */
        SaveContactsTask(Activity finishOnCompleteActivity, Integer returnCode, Contact saveContact, ProgressBar progressBar) {
            this.finishOnCompleteActivity = finishOnCompleteActivity;
            this.returnCode = returnCode;
            this.saveContact = saveContact;
            this.progressBar = progressBar;
        }

        /**
         * {@inheritDoc}
         * @param values
         */
        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            if (this.progressBar != null) {
                this.progressBar.setProgress(Math.round(values[0]));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (this.progressBar != null) {
                this.progressBar.setProgress(0);
                this.progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        }

        /**
         * {@inheritDoc}
         * @param contact The Contact saved to the database.
         */
        @Override
        protected void onPostExecute(Contact contact) {
            super.onPostExecute(contact);

            if (this.progressBar != null) {
                this.progressBar.setVisibility(ProgressBar.INVISIBLE);
                this.progressBar.setProgress(0);
            }
            if (this.finishOnCompleteActivity != null && this.returnCode != null) {
                this.finishOnCompleteActivity.finish();
            }
        }

        /**
         * {@inheritDoc}
         * @param voidArgs
         * @return The Contact.
         */
        @Override
        protected Contact doInBackground(Void... voidArgs) {
            // Access the database.
            SQLiteDatabase database = ContactSQLHelper.this.getReadableDatabase();

            // Depending on whether the Contact is new or old, add or update.
            if (this.saveContact.getID() == -1) {
                addContact(database, this.saveContact);
            } else {
                updateContact(database, this.saveContact);
            }

            // Close the connection.
            database.close();

            return this.saveContact;
        }

        /**
         * Add a Contact to the database.
         * @param database The database.
         * @param contact The Contact to add.
         * @throws SQLException
         */
        public void addContact(SQLiteDatabase database, Contact contact) throws SQLException {
            try {
                // Start the transaction.
                database.beginTransaction();

                // Calculate the number of rows to update or insert.
                int workDone = 0;
                int numRows = 1 + (contact.getIcon() == null ? 0 : 1) + contact.getPhoneNumbers().size();
                this.publishProgress(workDone / (float) numRows);
                try {
                    Thread.sleep((long) 1000 / Math.max(1, numRows));
                } catch (InterruptedException  e) {}

                // Insert the new Contact.
                ContentValues values = new ContentValues();
                values.put(ContactSQLHelper.CONTACT_NAME, contact.getName());
                contact.setID(database.insertOrThrow(ContactSQLHelper.TABLE_CONTACT, null, values));

                // Update the progress.
                this.publishProgress(++workDone / (float) numRows);
                try {
                    Thread.sleep((long) 1000 / Math.max(1, numRows));
                } catch (InterruptedException  e) {}

                // Insert the Contact's icon, if one exists.
                if (contact.getIcon() != null) {

                    values.clear();
                    values.put(ContactSQLHelper.ICON_CONTACT_ID, contact.getID());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    contact.getIcon().compress(Bitmap.CompressFormat.PNG, 0, bos);
                    values.put(ContactSQLHelper.ICON_ICON, bos.toByteArray());
                    database.insertOrThrow(ContactSQLHelper.TABLE_ICON, null, values);

                    // Update the progress.
                    this.publishProgress(++workDone / (float) numRows);
                    try {
                        Thread.sleep((long) 1000 / Math.max(1, numRows));
                    } catch (InterruptedException  e) {}
                }

                // Insert the phone numbers.
                for (int i = 0; i < contact.getPhoneNumbers().size(); i++) {
                    values.clear();
                    values.put(ContactSQLHelper.NUMBER_CONTACT_ID, contact.getID());
                    values.put(ContactSQLHelper.NUMBER_NUMBER, contact.getPhoneNumbers().get(i));
                    values.put(ContactSQLHelper.NUMBER_PRIORITY, i);
                    database.insertOrThrow(ContactSQLHelper.TABLE_NUMBER, null, values);

                    // Update the progress.
                    this.publishProgress(++workDone / (float) numRows);
                    try {
                        Thread.sleep((long) 1000 / Math.max(1, numRows));
                    } catch (InterruptedException  e) {}
                }

                // Complete the transaction.
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                // Change the Contact's ID back, because the transaction failed.
                contact.setID(-1);
                throw e;
            } finally {
                database.endTransaction();
            }
        }

        /**
         * Update a Contact in the database.
         * @param database The database.
         * @param contact The Contact to update.
         * @throws SQLException
         */
        public void updateContact(SQLiteDatabase database, Contact contact) throws SQLException {
            try {
                // Start the transaction.
                database.beginTransaction();

                // Calculate the number of rows to update or insert.
                int workDone = 0;
                int numRows = 2 + (contact.getIcon() == null ? 0 : 1) + contact.getPhoneNumbers().size();
                this.publishProgress(workDone / (float) numRows);
                try {
                    Thread.sleep((long) 1000 / Math.max(1, numRows));
                } catch (InterruptedException  e) {}

                // Update the Contact.
                ContentValues values = new ContentValues();
                values.put(ContactSQLHelper.CONTACT_NAME, contact.getName());
                database.update(ContactSQLHelper.TABLE_CONTACT, values,ContactSQLHelper.CONTACT_ID + "=?", new String[]{ Long.toString(contact.getID()) });

                // Update the progress.
                this.publishProgress(++workDone / (float) numRows);
                try {
                    Thread.sleep((long) 1000 / Math.max(1, numRows));
                } catch (InterruptedException  e) {}

                // Update or insert the Contact's icon.
                if (contact.getIcon() != null) {
                    Long iconId = null;
                    Cursor c = database.rawQuery("SELECT " + ContactSQLHelper.ICON_ID + " AS icon_id FROM " + ContactSQLHelper.TABLE_ICON + " WHERE " + ContactSQLHelper.ICON_CONTACT_ID + "=?;", new String[]{ Long.toString(contact.getID()) });
                    c.moveToFirst();
                    if (c.getCount() > 0) {
                        iconId = c.getLong(c.getColumnIndex("icon_id"));
                    }
                    c.close();

                    values.clear();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    contact.getIcon().compress(Bitmap.CompressFormat.PNG, 0, bos);
                    values.put(ContactSQLHelper.ICON_ICON, bos.toByteArray());

                    if (iconId != null) {
                        database.update(ContactSQLHelper.TABLE_ICON, values, ContactSQLHelper.ICON_CONTACT_ID + "=?", new String[] { Long.toString(contact.getID()) });
                    } else {
                        values.put(ContactSQLHelper.ICON_CONTACT_ID, contact.getID());
                        database.insertOrThrow(ContactSQLHelper.TABLE_ICON, null, values);
                    }

                    // Update the progress.
                    this.publishProgress(++workDone / (float) numRows);
                    try {
                        Thread.sleep((long) 1000 / Math.max(1, numRows));
                    } catch (InterruptedException  e) {}
                }

                // Delete all existing phone numbers.
                database.delete(ContactSQLHelper.TABLE_NUMBER, ContactSQLHelper.NUMBER_CONTACT_ID + "=?", new String[]{ Long.toString(contact.getID()) });

                // Update the progress.
                this.publishProgress(++workDone / (float) numRows);
                try {
                    Thread.sleep((long) 1000 / Math.max(1, numRows));
                } catch (InterruptedException  e) {}

                // Insert the new phone numbers.
                for (int i = 0; i < contact.getPhoneNumbers().size(); i++) {
                    values.clear();
                    values.put(ContactSQLHelper.NUMBER_CONTACT_ID, contact.getID());
                    values.put(ContactSQLHelper.NUMBER_NUMBER, contact.getPhoneNumbers().get(i));
                    values.put(ContactSQLHelper.NUMBER_PRIORITY, i);
                    database.insertOrThrow(ContactSQLHelper.TABLE_NUMBER, null, values);

                    // Update the progress.
                    this.publishProgress(++workDone / (float) numRows);
                }

                // Complete the transaction.
                database.setTransactionSuccessful();
            } catch (SQLException e) {
                // Change the Contact's ID back, because the transaction failed.
                contact.setID(-1);
                throw e;
            } finally {
                database.endTransaction();
            }
        }
    }

    /**
     * Delete a Contact from the database.
     */
    public class DeleteContactsTask extends AsyncTask<Void, Float, Void> {
        Activity finishOnCompleteActivity;
        Integer returnCode;
        Contact deleteContact;
        ProgressBar progressBar;

        /**
         * Constructor.
         * @param finishOnCompleteActivity The Activity, if any, which should be finished when the AsyncTask is complete.
         * @param returnCode The return code, if any, to return on finish.
         * @param deleteContact The Contact to delete.
         * @param progressBar The ProgressBar, if any, to update.
         */
        DeleteContactsTask(Activity finishOnCompleteActivity, Integer returnCode, Contact deleteContact, ProgressBar progressBar) {
            this.finishOnCompleteActivity = finishOnCompleteActivity;
            this.returnCode = returnCode;
            this.deleteContact = deleteContact;
            this.progressBar = progressBar;
        }

        /**
         * {@inheritDoc}
         * @param values
         */
        @Override
        protected void onProgressUpdate(Float... values) {
            super.onProgressUpdate(values);
            if (this.progressBar != null) {
                this.progressBar.setProgress(Math.round(values[0]));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (this.progressBar != null) {
                this.progressBar.setProgress(0);
                this.progressBar.setVisibility(ProgressBar.VISIBLE);
            }
        }

        /**
         * {@inheritDoc}
         * @param voidArg
         */
        @Override
        protected void onPostExecute(Void voidArg) {
            super.onPostExecute(voidArg);

            if (this.progressBar != null) {
                this.progressBar.setVisibility(ProgressBar.INVISIBLE);
                this.progressBar.setProgress(0);
            }
            if (this.finishOnCompleteActivity != null && this.returnCode != null) {
                this.finishOnCompleteActivity.finish();
            }
        }

        /**
         * {@inheritDoc}
         * @param voids
         */
        @Override
        protected Void doInBackground(Void... voids) {
            // Access the database.
            SQLiteDatabase database = ContactSQLHelper.this.getReadableDatabase();

            // Delete the contact and all references to it (via database's cascade).
            database.delete(ContactSQLHelper.TABLE_CONTACT, ContactSQLHelper.CONTACT_ID + "=?", new String[]{ Long.toString(deleteContact.getID()) });

            // Close the connection.
            database.close();

            return null;
        }
    }

}
