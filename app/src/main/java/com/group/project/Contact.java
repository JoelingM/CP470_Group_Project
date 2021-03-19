package com.group.project;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to hold Contact information.
 */
public class Contact implements Parcelable, Comparable<Contact> {
    private long id;
    private String name;
    private Bitmap icon;
    private List<String> phoneNumbers;

    /**
     * A Parcelable Creator for Contact.
     */
    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        /**
         * Create a Contact from a Parcel.
         * @param in The Parcel which contains the Contact details.
         * @return
         * {@inheritDoc}
         */
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        /**
         * Create an array of Contact.
         * @param size
         * @return
         */
        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    /**
     * A Constructor for the Contact.
     * @param id The database ID of the Contact. -1 default.
     * @param name The name of the Contact.
     * @param icon The icon of the Contact. null default.
     * @param phoneNumbers The list of phone numbers for the Contact.
     */
    public Contact(long id, String name, Bitmap icon, Iterable<String> phoneNumbers) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.phoneNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            this.phoneNumbers.add(phoneNumber);
        }
    }

    /**
     * A Constructor for the Contact.
     * @param name The name of the Contact.
     * @param icon The icon of the Contact. null default.
     * @param phoneNumbers The list of phone numbers for the Contact.
     */
    public Contact(String name, Bitmap icon, Iterable<String> phoneNumbers) {
        this.id = -1;
        this.name = name;
        this.icon = icon;
        this.phoneNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            this.phoneNumbers.add(phoneNumber);
        }
    }

    /**
     * A constructor for a Parcel representing the Contact.
     * @param in The Parcel containing the Contact's information.
     */
    public Contact(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.icon = in.readParcelable((getClass().getClassLoader()));
        this.phoneNumbers = new ArrayList<>();
        in.readStringList(this.phoneNumbers);
    }

    /**
     * Get the Contact ID.
     * @return The Contact ID. -1 if does not exist.
     */
    public long getID() { return this.id; }

    /**
     * Set the Contact ID.
     * @param id The new ID.
     */
    public void setID(long id) { this.id = id; }

    /**
     * Get the Contact name.
     * @return The Contact name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set the Contact name.
     * @param name The new name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the Contact icon.
     * @return The Contact icon. null is default.
     */
    public Bitmap getIcon() {
        return icon;
    }

    /**
     * Set the Contact icon.
     * @param icon The new icon.
     */
    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    /**
     * Get the Contact phone numbers.
     * @return The Contact phone numbers.
     */
    public List<String> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    /**
     * Set the Contact phone numbers.
     * @param phoneNumbers The new phone numbers.
     */
    public void setPhoneNumbers(Iterable<String> phoneNumbers) {
        this.phoneNumbers = new ArrayList<>();
        for (String phoneNumber : phoneNumbers) {
            this.phoneNumbers.add(phoneNumber);
        }
    }

    /**
     * Required method for Parcelable interface.
     * @return 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Write the description of the Contact to a Parcel.
     * @param parcel The Parcel to write to.
     * @param flags Default flags.
     */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(this.id);
        parcel.writeString(this.name);
        parcel.writeParcelable(this.icon, flags);
        parcel.writeStringList(this.phoneNumbers);
    }

    /**
     * Compare this Contact to another Contact. Required for the Comparable interface.
     * @param other The other Contact.
     * @return <0 if this Contact is less than the other; 0 if equal; >0 if this Contact is greater than the other.
     */
    @Override
    public int compareTo(Contact other) {
        int cmpResult = this.name.compareTo(other.name);
        int i = 0;
        while (cmpResult == 0 && i < this.phoneNumbers.size() && i < other.phoneNumbers.size()) {
            cmpResult = this.phoneNumbers.get(i).compareTo(other.phoneNumbers.get(i));
        }
        return cmpResult;
    }

}
