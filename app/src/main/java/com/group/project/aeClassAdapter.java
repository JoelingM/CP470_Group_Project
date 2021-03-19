package com.group.project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * An Array adapter for class objects
 *
 * @author Aaron Exley
 */
public class aeClassAdapter extends ArrayAdapter<String> {

    private ArrayList<aeClass> classes;
    private Context ctx;

    public aeClassAdapter(Context ctx) {
        super(ctx, 0);
        classes = new ArrayList<>();
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return classes.size();
    }

    @Override
    public String getItem(int position) {
        return classes.get(position).toString();
    }

    @NonNull
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();

        View result = inflater.inflate(R.layout.class_list_element, parent, false);

        TextView eventDetails = result.findViewById(R.id.class_name);

        eventDetails.setText(getItem(position));

        return result;
    }

    public void setClasses(ArrayList<aeClass> aeClass) {
        this.classes = aeClass;
    }

    public aeClass getItemAtPos(int i) {
        return this.classes.get(i);
    }

    @Override
    public long getItemId(int position) {
        return classes.get(position).getId();
    }

    public void addClass(aeClass aeclass) {
        this.classes.add(aeclass);
    }

    public void setClass(aeClass aeclass, int i) {
        this.classes.set(i, aeclass);
    }

    public void removeClass(int i) {
        this.classes.remove(i);
    }
}
