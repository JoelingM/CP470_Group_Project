package com.group.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.group.project.noteAdapters.NotesAdapter;
import com.group.project.noteDB.NoteDatabaseHelper;
import com.group.project.noteModel.Note;
import com.group.project.noteUtility.NoteEventListener;
import com.group.project.noteUtility.NotesViewFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.group.project.NotepadEditActivity.NOTE_EXTRA_KEY;

/**
 * The Main Activity used to display and interact with written notes.
 *
 * @author Brendan Whelan
 */
public class NotepadMainActivity extends AppCompatActivity implements NoteEventListener {

    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NotesAdapter adapter;
    private SQLiteDatabase db;
    private ProgressBar progBar;
    private Boolean firstStart = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Database
        NoteDatabaseHelper dbHelper = new NoteDatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        setContentView(R.layout.activity_notepad_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //init recycler
        recyclerView = findViewById(R.id.notes_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progBar = findViewById(R.id.prog_bar);
        progBar.setVisibility(View.VISIBLE);
        //Initialize new note button
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> onAddNewNote());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bw_toolbar, menu);
        return true;
    }

    /**
     * Called when menu opened.
     *
     * Used to display information about the app.
     *
     * @param item The menu item selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NotepadMainActivity.this);
        if (item.getItemId() == R.id.About_Item) {
            LayoutInflater inflater = getLayoutInflater();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            @SuppressLint("InflateParams") final View layout = inflater.inflate(R.layout.bw_about_dialogue, null);
            TextView comment = layout.findViewById(R.id.Comments);
            comment.setText(R.string.bw_comments);
            builder.setTitle("Version 1.0 by Brendan Whelan");
            builder.setView(layout)
                    // Add action buttons
                    .setPositiveButton(R.string.ok, (dialog, id) -> {
                    })
                    .setNegativeButton(R.string.cancel, (dialog, id) -> dialog.cancel());
            AlertDialog dialogue = builder.create();
            dialogue.show();
        }
        return super.onOptionsItemSelected(item);

    }

    /**
     * Called when the list of notes needs to be updated.
     *
     * Uses a cursor to pull the notes from the database and fills the RecyclerView with the item
     */
    private void loadNotes() {
        this.notes=new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + NoteDatabaseHelper.TABLE_NAME, null);

        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String content = cursor.getString(2);
            long date = cursor.getLong(3);
            Note note = new Note(title,content,date);
            note.setId(id);
            notes.add(note);
        }

        cursor.close();
        this.adapter=new NotesAdapter(this, this.notes);
        //Set listener to adapter

        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
    }

    /**
     * Called when the new note FAB is pressed
     *
     * Opens the NotepadEditActivity
     */
    private void onAddNewNote() {
        //Start the edit note activity
        startActivity(new Intent(this,NotepadEditActivity.class));
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Load notes from database
        if(firstStart){
            firstStart = false;
            new NoteQuery().execute();
        }else {
            loadNotes();
        }
    }

    /**
     * Called when a note item is pressed.
     *
     * Opens a fragment to display the note's title and text
     *
     * @param note The note to be displayed
     */
    @Override
    public void onNoteClick(Note note) {
        NotesViewFragment frag = new NotesViewFragment();

        Bundle bundle = new Bundle();
        bundle.putString("title", note.getNoteTitle());
        bundle.putString("content", note.getNoteText());

        frag.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                frag).commit();
    }

    /**
     * Called when a note is long pressed
     *
     * Opens a dialog to Delete or Edit a note.
     *
     * @param note The note that was long pressed
     */
    @Override
    public void onNoteLongClick(Note note) {
        new AlertDialog.Builder(this)
                .setTitle("Note Options")
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Delete", (dialogInterface, i) -> {
                    db.execSQL("DELETE FROM " + NoteDatabaseHelper.TABLE_NAME + " WHERE _id="+note.getId());
                    Toast.makeText(this, R.string.bw_note_delete, Toast.LENGTH_SHORT).show();
                    loadNotes(); // Refreshes list
                })
                .setNegativeButton("Edit", (dialogInterface, i) -> EditNote(note))
                .create()
                .show();
    }

    /**
     * Called when user chooses to edit a note.
     *
     * Moves to the NotepadEditActivity, passing in the note pressed.
     *
     * @param note The note to be edited.
     */
    private void EditNote(Note note){
        Intent edit=new Intent(this,NotepadEditActivity.class);
        edit.putExtra(NOTE_EXTRA_KEY, note.getId());
        startActivity(edit);
    }

    private void SetProgress(int value){
        progBar.setVisibility(View.VISIBLE);
        progBar.setProgress(value);
    }

    private void FinishSync(){
        adapter=new NotesAdapter(this, notes);
        //Set listener to adapter
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * AsyncTask called when the notes are loaded, allowing it to be loaded in the background thread
     */
    @SuppressLint("StaticFieldLeak")
    public class NoteQuery extends AsyncTask<Void, Integer, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            notes=new ArrayList<>();

            Cursor cursor = db.rawQuery("SELECT * FROM " + NoteDatabaseHelper.TABLE_NAME, null);

            float length = cursor.getCount();
            float count = 0;
            while(cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String content = cursor.getString(2);
                long date = cursor.getLong(3);
                Note note = new Note(title,content,date);
                note.setId(id);
                notes.add(note);
                count += 1;
                publishProgress((int) ((count/length)*100));
                if(count != length) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            cursor.close();

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("AsyncNote", "Progress: " + values[0]);
            SetProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progBar.setVisibility(View.INVISIBLE);
            FinishSync();
        }
    }

}
