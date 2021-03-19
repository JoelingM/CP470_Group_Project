package com.group.project.noteUtility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A class of utility functions for working with Notes.
 *
 * @author Brendan Whelan
 */
public class NoteUtilities {

    /**
     * Converts a long to a DateFormat
     * @param time The current time as a long
     * @return Returns the appropriate Date
     */
    public static String dateFromLong(long time){
        DateFormat format = new SimpleDateFormat("EEE, yyyy MMM dd 'at' hh:mm aaa", Locale.CANADA);
        return format.format(new Date(time));
    }
}
