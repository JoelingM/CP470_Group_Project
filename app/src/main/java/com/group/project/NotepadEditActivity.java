package com.group.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.group.project.noteDB.NoteDatabaseHelper;
import com.group.project.noteModel.Note;

import java.util.Date;

/**
 * Activity that allows the user to write a new note.
 *
 * @author Brendan Whelan
 */
public class NotepadEditActivity extends AppCompatActivity {

    private EditText editNoteText;
    private EditText editTitleText;
    private Note temp;
    public static final String NOTE_EXTRA_KEY = "note_id";


    /**
     * Called on activity creation.
     *
     * Checks the intent for a note, if one is found then that note is being edited, otherwise create new note.
     *
     * @param savedInstanceState Used to check for any passed in notes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad_edit);

        editNoteText = findViewById(R.id.note_input);
        editTitleText = findViewById(R.id.note_title);
        if(getIntent().getExtras()!=null){
            long id = getIntent().getExtras().getLong(NOTE_EXTRA_KEY,0);

            NoteDatabaseHelper dbHelper = new NoteDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+ NoteDatabaseHelper.TABLE_NAME +" WHERE _id = " + id, null);

            cursor.moveToNext();
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            long date = cursor.getLong(3);
            temp = new Note(title,content,date);
            temp.setId(id);
            cursor.close();
        }else{
            temp = new Note();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.save_note){
            onSaveNote();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when save note selected.
     *
     * Creates a note object, adding it to the database. If editing an existing note, the note is instead updated in the database.
     */
    private void onSaveNote() {
        String text=editNoteText.getText().toString();
        String title=editTitleText.getText().toString();
        if(!title.isEmpty()){
            long date = new Date().getTime(); //Get date

            //Get database
            NoteDatabaseHelper dbHelper = new NoteDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(NoteDatabaseHelper.KEY_TITLE, title);
            values.put(NoteDatabaseHelper.KEY_CONTENT, text);
            values.put(NoteDatabaseHelper.KEY_DATE, date);
            //new note
            if(temp.getId() == -1){
                //Add the note to the database
                long newRowId = db.insert(NoteDatabaseHelper.TABLE_NAME, null, values);
                temp.setId(newRowId);
                Log.i("NotepadEditActivity", "New Note added to database!");
            }else{
                db.update(NoteDatabaseHelper.TABLE_NAME, values, "_id="+temp.getId(), null);
                Log.i("NotepadEditActivity", "Existing Note edited in database!");
            }

            finish();
        }else{
            Snackbar.make(findViewById(R.id.content),R.string.bw_not_empty, Snackbar.LENGTH_SHORT).show();
        }
    }
}
