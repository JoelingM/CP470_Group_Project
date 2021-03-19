package com.group.project.noteModel;

/**
 * A note object used to store the relevant information of a note
 *
 * @author Brendan Whelan
 */
public class Note {
    private long id = -1;

    private String noteText;

    private long noteDate;

    private String noteTitle;

    public Note(){
    }

    public Note(String noteTitle, String noteText, long noteDate) {
        this.noteTitle = noteTitle;
        this.noteText = noteText;
        this.noteDate = noteDate;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteText) {
        this.noteText = noteTitle;
    }

    public long getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(long noteDate) {
        this.noteDate = noteDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
