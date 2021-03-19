package com.group.project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * A Class to hold the class info
 *
 * @author Aaron Exley
 */
public class aeClass implements Parcelable {

    private long id;
    private String name;
    private String semester;
    private String location;
    private String startTime;
    private String endTime;
    private String[] dates;

    /**
     * A Class to hold the class info
     * @param id The id of the class
     * @param name The name of the class
     * @param location The location of the class
     * @param semester The name of the semester of the class
     * @param dates The dates for the class
     * @param startTime The start time for the class
     * @param endTime The end time for the class
     */
    public aeClass(long id, String name, String location,  String semester, String[] dates, String startTime, String endTime) {

        this.id = id;
        this.name = name;
        this.semester = semester;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dates = dates;
    }

    /**
     * Turns the class to a string
     * @return A string in the form
     *
     * Name
     * Semester, location
     * date
     * startTime-endTime
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String date : dates) {
            builder.append(date);
            builder.append(" ");
        }
        String dateText = builder.toString();
        return String.format("%s\n%s, %s\n%s\n%s-%s", name, semester, location, dateText, startTime, endTime);
    }

    ///// Parcelable Stuff //////
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(semester);
        parcel.writeString(location);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeStringArray(dates);
    }

    public static final Parcelable.Creator<aeClass> CREATOR = new Parcelable.Creator<aeClass>() {
        public aeClass createFromParcel(Parcel in) {
            return new aeClass(in);
        }

        public aeClass[] newArray(int size) {
            return new aeClass[size];
        }
    };

    private aeClass(Parcel in) {

        this.id = in.readLong();
        this.name = in.readString();
        this.semester = in.readString();
        this.location = in.readString();
        this.startTime = in.readString();
        this.endTime = in.readString();
        this.dates = in.createStringArray();

    }

    /////// Setters and getters /////////
    public void setId(long id) {
        this.id = id;
    }

    public void setTime(String startTime, String endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }

    public String getLocation() {
        return location;
    }

    public String[] getDates() {
        return dates;
    }
}
