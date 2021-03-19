package com.group.project.noteAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group.project.R;
import com.group.project.noteModel.Note;
import com.group.project.noteUtility.NoteEventListener;
import com.group.project.noteUtility.NoteUtilities;

import java.util.ArrayList;

/**
 * Adapter class used for displaying Notes in a recycler view
 *
 * @author Brendan Whelan
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder>{

    private Context context;
    private ArrayList<Note> notes;
    private NoteEventListener listener;

    public NotesAdapter(Context context, ArrayList<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View noteView = LayoutInflater.from(context).inflate(R.layout.note_layout, parent, false);
        return new NoteHolder(noteView);
    }

    /**
     * Binds the adapter to the holder at a position
     *
     * @param holder The holder to bind the adapter to
     * @param position The position to bind the holder at
     */
    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note note = getNote(position);
        if(note!=null){
            holder.noteText.setText(note.getNoteTitle());
            holder.noteDate.setText(NoteUtilities.dateFromLong(note.getNoteDate()));

            //Add note press
            holder.itemView.setOnClickListener(view -> listener.onNoteClick(note));

            //Add long press
            holder.itemView.setOnLongClickListener(view -> {
                listener.onNoteLongClick(note);
                return false;
            });
        }

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private Note getNote(int position){
        return notes.get(position);
    }
    class NoteHolder extends RecyclerView.ViewHolder{

        TextView noteText, noteDate;

        private NoteHolder(@NonNull View itemView) {
            super(itemView);
            noteDate=itemView.findViewById(R.id.note_date);
            noteText=itemView.findViewById(R.id.note_text);
        }
    }

    public void setListener(NoteEventListener listener) {
        this.listener = listener;
    }
}
