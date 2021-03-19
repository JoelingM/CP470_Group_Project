package com.group.project;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

/**
 * An object to represent an Event
 *
 * @author Aaron Exley
 */
public class aeEvent implements Parcelable {

    private String name;
    private String semester;
    private String date;
    private String time;
    private String endDate;
    private String endTime;
    private String type;
    private String className;
    private String desc;
    private long id;
    private int repeat;
    private int currentSteps;
    private int totalSteps;
    private int notificationId;

    /**
     * An object to represent an Event
     * @param id The id of the event
     * @param name The name of the event
     * @param semester The name of the semester for the event
     * @param date The start date of the event
     * @param time The start time of the event
     * @param endDate The end date of the event
     * @param endTime The end time of the event
     * @param type The type of the event
     * @param className The class name for the event
     * @param desc The description for the event
     * @param repeat Does the event repeat weekly
     * @param currentSteps The current steps for the event
     * @param totalSteps The total steps for the event
     * @param notificationId The id of the notification for the event
     */
    public aeEvent(long id, String name, String semester, String date, String time,
                 String endDate, String endTime, String type, String className, String desc, int repeat, int currentSteps, int totalSteps, int notificationId) {

        this.name = name;
        this.semester = semester;
        this.date = date;
        this.time = time;
        this.endDate = endDate;
        this.endTime = endTime;
        this.type = type;
        this.className = className;
        this.desc = desc;
        this.repeat = repeat;
        this.id = id;
        this.currentSteps = currentSteps;
        this.totalSteps = totalSteps;
        this.notificationId = notificationId;

    }

    public void setNotificationId(int id) {
        this.notificationId = id;
    }

    public int getNotificationId() {
        return notificationId;
    }

    @NonNull
    @Override
    public String toString() {
        String repeatText = "Repeat: " + ((repeat == 0) ? "False" : "True");
        String steps = (totalSteps > 1) ? currentSteps + " / " + totalSteps : "";
        return String.format("%s\n%s, %s\n%s, %s -> %s, %s\n%s, %s\n%s\n%s", name, semester, className, date, time, endDate, endTime, type, repeatText, desc, steps);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(semester);
        parcel.writeString(date);
        parcel.writeString(time);
        parcel.writeString(endDate);
        parcel.writeString(endTime);
        parcel.writeString(type);
        parcel.writeString(className);
        parcel.writeString(desc);
        parcel.writeInt(repeat);
        parcel.writeInt(currentSteps);
        parcel.writeInt(totalSteps);
    }

    public static final Parcelable.Creator<aeEvent> CREATOR = new Parcelable.Creator<aeEvent>() {
        public aeEvent createFromParcel(Parcel in) {
            return new aeEvent(in);
        }

        public aeEvent[] newArray(int size) {
            return new aeEvent[size];
        }
    };

    private aeEvent(Parcel in) {

        this.id = in.readLong();
        this.name = in.readString();
        this.semester = in.readString();
        this.date = in.readString();
        this.time = in.readString();
        this.endDate = in.readString();
        this.endTime = in.readString();
        this.type = in.readString();
        this.className = in.readString();
        this.desc = in.readString();
        this.repeat = in.readInt();
        this.currentSteps = in.readInt();
        this.totalSteps = in.readInt();

    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public void setCurrentSteps(int currentSteps) {
        this.currentSteps = currentSteps;
    }

    public void addCurrentSteps(int currentSteps) {
        this.currentSteps += currentSteps;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public String getClassName() {
        return className;
    }

    public String getDesc() {
        return desc;
    }

    public boolean getRepeat() {
        return repeat == 1;
    }
}
