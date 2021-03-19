package com.group.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ContactDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Contact contact = null;

    private ContactDetailFragment cdf;

    /**
     * {@inheritDoc}
     * @param menu
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_detail_bar_menu, menu);
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
            case (R.id.bar_edit_contact): {
                // Start the edit Activity.
                Intent intent = new Intent(this, ContactEditActivity.class);
                intent.putExtra("contact", this.contact);
                startActivityForResult(intent, ContactEditActivity.REQUEST_CODE_EDIT);
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
     * {@inheritDoc}
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        // Set the toolbar.
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);

        // Get the Contact from the extras.
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("contact")) {
            this.contact = extras.getParcelable("contact");
        }

        // Add the ContactDetailFragment.
        this.cdf = new ContactDetailFragment();
        this.cdf.setArguments(extras);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.detail_fragment, this.cdf)
                .commit();
    }
}
