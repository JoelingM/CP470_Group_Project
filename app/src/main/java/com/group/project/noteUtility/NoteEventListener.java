package com.group.project.noteUtility;

import com.group.project.noteModel.Note;

public interface NoteEventListener {
    void onNoteClick(Note note);
    void onNoteLongClick(Note note);
}
