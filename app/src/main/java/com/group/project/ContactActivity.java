package com.group.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ContactActivity extends AppCompatActivity
                            implements ContactDetailFragment.ContactDetailFragmentRemovedListener {

    private ContactAdapter contactAdapter;
    private List<Contact> contacts;

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private boolean hasDetailSpace;
    private ContactDetailFragment cdf;

    /**
     * {@inheritDoc}
     * @param menu
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Depending on whether or not a Contact has been selected, either show the default Toolbar or the Toolbar from ContactDetailActivity.
        if (this.cdf == null) {
            getMenuInflater().inflate(R.menu.contact_bar_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.contact_detail_hybrid_bar_menu, menu);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * @param item
     * @return true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.bar_new_contact): {
                // Remove the ContactDetailFragment if it exists.
                if (this.cdf != null) {
                    ContactActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(ContactActivity.this.cdf)
                            .commit();
                    this.getSupportFragmentManager().popBackStackImmediate();
                }

                // Open the ContactEditActivity.
                Intent contactEditIntent = new Intent(this, ContactEditActivity.class);
                startActivity(contactEditIntent);
                break;
            }
            case (R.id.bar_edit_contact): {
                // Remove the ContactDetailFragment.
                Contact cdfContact = this.cdf.contact;
                if (this.cdf != null) {
                    ContactActivity.this.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(ContactActivity.this.cdf)
                            .commit();
                    this.getSupportFragmentManager().popBackStackImmediate();
                }

                // Open the ContactEditActivity.
                Intent contactEditIntent = new Intent(this, ContactEditActivity.class);
                contactEditIntent.putExtra("contact",cdfContact);
                startActivity(contactEditIntent);
                break;
            }
            case (R.id.bar_help): {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.nc_dialog_help_title))
                        .setMessage(getString(R.string.nc_dialog_help_message))
                        .setPositiveButton(getString(R.string.nc_ok), null)
                        .show();
                break;
            }
        }
        return true;
    }

    /**
     * Required method for ContactDetailFragment.ContactDetailFragmentRemovedListener.
     * Remove the ContactDetailFragment, pop it from the back stack, and update the Toolbar.
     * @see ContactDetailFragment.ContactDetailFragmentRemovedListener
     */
    public void onContactDetailFragmentRemoved() {
        // Reset the Toolbar.
        if (this.cdf != null) {
            // If the Activity is closing, popping isn't an option - so do it onResume, instead.
            try {
                this.getSupportFragmentManager().popBackStackImmediate();
                this.cdf = null;
            } catch (Exception e) {}
        }
        this.invalidateOptionsMenu();
        ContactActivity.this.onCreateOptionsMenu(ContactActivity.this.toolbar.getMenu());
    }

    /**
     * {@inheritDoc}
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        // Get the Views within this Activity.
        this.toolbar = findViewById(R.id.toolbar);
        this.progressBar = findViewById(R.id.progress_bar);

        // Determine whether or not there is space for a ContactDetailFragment.
        this.hasDetailSpace = findViewById(R.id.detail_fragment) != null;

        // Hide the progress bar.
        this.progressBar.setVisibility(ProgressBar.INVISIBLE);

        // Set the toolbar as the action bar for this activity.
        setSupportActionBar(this.toolbar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Set up the LinearLayoutManager.
        RecyclerView recycler = findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(llm);

        // Set up the adapter for the recycler view.
        this.contacts = new ArrayList<>();
        this.contactAdapter = new ContactAdapter(this.contacts);
        recycler.setAdapter(this.contactAdapter);

        // Collect the contacts.
        new ContactSQLHelper(this).new GetContactsTask(this.progressBar, this.contacts, this.contactAdapter).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();

        // If the orientation just changed, pop the fragment.
        if (this.cdf != null) {
            this.getSupportFragmentManager().popBackStackImmediate();
            this.cdf = null;
        }
    }

    /**
     * Extends RecyclerView.Adapter
     */
    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
        private List<Contact> contacts;

        /**
         * Constructor for the ContactAdapter.
         * @param contacts The list of Contacts.
         */
        public ContactAdapter(List<Contact> contacts) {
            this.contacts = contacts;
            Collections.sort(this.contacts);
        }

        /**
         * {@inheritDoc}
         * @param parent
         * @param viewType
         * @return The ContactViewHolder.
         */
        @NonNull
        @Override
        public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the row.
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_contact_row, parent, false);
            return new ContactViewHolder(itemView);
        }

        /**
         * {@inheritDoc}
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
            Contact contact = this.contacts.get(position);
            holder.name.setText(contact.getName());
            holder.number.setText(contact.getPhoneNumbers().get(0));

            if (contact.getIcon() != null) {
                holder.icon.setImageBitmap(contact.getIcon());
            }
        }

        /**
         * {@inheritDoc}
         * @return The length of the contacts array.
         */
        @Override
        public int getItemCount() {
            return this.contacts.size();
        }

        /**
         * Extends RecyclerView.ViewHolder
         */
        public class ContactViewHolder extends RecyclerView.ViewHolder {
            public ImageView icon;
            public TextView name;
            public TextView number;

            /**
             * Constructor.
             * @param itemLayoutView
             */
            public ContactViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                this.icon = itemLayoutView.findViewById(R.id.icon);
                this.name = itemLayoutView.findViewById(R.id.name);
                this.number = itemLayoutView.findViewById(R.id.number);

                // Create a listener that will be attached to the icon, name, and phone number.
                View.OnClickListener ocl = view -> {
                    // Get the selected Contact.
                    Contact selection = ContactActivity.this.contacts.get(ContactViewHolder.this.getAdapterPosition());

                    if (ContactActivity.this.hasDetailSpace) {
                        // Remove any existing ContactDetailFragments.
                        if (ContactActivity.this.cdf != null) {
                            ContactActivity.this.getSupportFragmentManager()
                                    .beginTransaction()
                                    .remove(ContactActivity.this.cdf)
                                    .commit();
                            ContactActivity.this.getSupportFragmentManager().popBackStackImmediate();
                        }

                        // Switch the menu available in the Toolbar.
                        ContactActivity.this.invalidateOptionsMenu();
                        ContactActivity.this.onCreateOptionsMenu(ContactActivity.this.toolbar.getMenu());

                        // Show the ContactDetailFragment.
                        ContactActivity.this.cdf = new ContactDetailFragment();
                        Bundle cdfBundle = new Bundle();
                        cdfBundle.putParcelable("contact", selection);
                        ContactActivity.this.cdf.setArguments(cdfBundle);
                        ContactActivity.this.getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.detail_fragment, ContactActivity.this.cdf)
                                .addToBackStack(null)
                                .commit();
                    } else {
                        Intent intent = new Intent(ContactActivity.this, ContactDetailActivity.class);
                        intent.putExtra("contact", selection);
                        startActivity(intent);
                    }
                };
                itemLayoutView.setOnClickListener(ocl);
                this.icon.setOnClickListener(ocl);
                this.name.setOnClickListener(ocl);
                this.number.setOnClickListener(ocl);
            }
        }
    }
}
