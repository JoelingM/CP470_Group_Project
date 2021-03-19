package com.group.project;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ContactEditActivity extends AppCompatActivity {
    public static final int MAX_ICON_DIM = 100; // Maximum number of pixels an icon can be in any direction.

    public static final int REQUEST_CODE_EDIT = 401;
    public static final int RESULT_CODE_SAVED = 400;
    public static final int RESULT_CODE_DELETED = 401;

    public static final int PERM_REQ_CAMERA = 400;
    public static final int PERM_REQ_READ_EXTERNAL_STORAGE = 401;

    public static final int REQ_LOCAL_CAMERA = 400;
    public static final int REQ_PICK_GALLERY = 401;

    private String currentPhotoPath;
    private Uri currentPhotoUri;

    private ProgressBar progressBar;
    private ImageView icon;
    private ImageButton editIconButton;
    private TextView name;

    private EditNumberAdapter editNumberAdapter;

    private Contact contact = null;

    /**
     * {@inheritDoc}
     * @param menu
     * @return true.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (this.contact.getID() == -1) {
            menu.removeItem(R.id.bar_delete_contact);
        }
        getMenuInflater().inflate(R.menu.contact_edit_bar_menu, menu);
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
            case (R.id.bar_new_number): {
                // Listen for a click on the edit name button which brings up an AlertDialog for entering the new number.
                EditText editText = new EditText(ContactEditActivity.this);
                editText.setText("");
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                new AlertDialog.Builder(ContactEditActivity.this)
                        .setTitle(R.string.nc_dialog_change_number_title)
                        .setMessage(R.string.nc_dialog_change_number_message)
                        .setView(editText)
                        .setPositiveButton(R.string.nc_ok, (d,w)-> {
                            this.contact.getPhoneNumbers().add(editText.getText().toString());
                            this.editNumberAdapter.notifyItemInserted(this.contact.getPhoneNumbers().size() - 1);
                        })
                        .setNeutralButton(R.string.nc_cancel, null)
                        .show();
                break;
            }
            case (R.id.bar_save_contact): {
                // Check if any property is invalid.
                if (this.contact.getName().isEmpty()) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.nc_dialog_empty_name_title))
                            .setMessage(getString(R.string.nc_dialog_empty_name_message))
                            .setNeutralButton(getString(R.string.nc_cancel), null)
                            .show();
                } else if (this.contact.getPhoneNumbers().size() < 1) {
                    new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.nc_dialog_empty_numbers_title))
                            .setMessage(getString(R.string.nc_dialog_empty_numbers_message))
                            .setNeutralButton(getString(R.string.nc_cancel), null)
                            .show();
                } else {
                    // Save the updated Contact to the database.
                    new ContactSQLHelper(this).new SaveContactsTask(this, RESULT_CODE_SAVED, this.contact, this.progressBar).execute();
                }
                break;
            }
            case (R.id.bar_delete_contact): {
                // Confirm the deletion.
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.nc_dialog_delete_contact_title))
                        .setMessage(getString(R.string.nc_dialog_delete_contact_message))
                        .setPositiveButton(getString(R.string.nc_yes), (d, w) -> {
                            // Delete the Contact from the database.
                            new ContactSQLHelper(this).new DeleteContactsTask(this, RESULT_CODE_DELETED, this.contact, this.progressBar).execute();
                        })
                        .setNegativeButton(R.string.nc_no, null)
                        .setNeutralButton(getString(R.string.nc_cancel), null)
                        .show();
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
     * Create an image file.
     * @link https://developer.android.com/training/camera/photobasics#TaskScalePhoto
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        this.currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * {@inheritDoc}
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case (PERM_REQ_CAMERA): {
                // Let the user choose their contact's icon.
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException e) { e.printStackTrace(); }

                    if (photoFile != null) {
                        this.currentPhotoUri = FileProvider.getUriForFile(this, "com.group.project", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.currentPhotoUri);
                        startActivityForResult(takePictureIntent, REQ_LOCAL_CAMERA);
                    }
                }
                break;
            }
            case (PERM_REQ_READ_EXTERNAL_STORAGE): {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickImageIntent, REQ_PICK_GALLERY);
                break;
            }
        }
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

        Bitmap hdIcon = null;
        int orientation = 1;
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case (REQ_LOCAL_CAMERA): {
                    // Read the hd icon selected.
                    Uri selectedImage = this.currentPhotoUri;
                    try {
                        ParcelFileDescriptor parcelFileDescriptor =
                                getContentResolver().openFileDescriptor(selectedImage, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        hdIcon = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Get the icon's orientation so it can be rotated (if this is necessary).
                    try {
                        orientation = new ExifInterface(this.currentPhotoPath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case (REQ_PICK_GALLERY): {
                    // Ref: https://stackoverflow.com/questions/10473823/android-get-image-from-gallery-into-imageview
                    // Read the hd icon selected.
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String hdIconPath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        ParcelFileDescriptor parcelFileDescriptor =
                                getContentResolver().openFileDescriptor(selectedImage, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        hdIcon = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                        parcelFileDescriptor.close();
                    } catch (IOException e) { e.printStackTrace(); }

                    // Get the icon's orientation so it can be rotated (if this is necessary).
                    try {
                        orientation = new ExifInterface(hdIconPath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        if (hdIcon != null) {
            // Shrink the image (so it's smaller for the database).
            int greaterDim = Math.max(hdIcon.getWidth(), hdIcon.getHeight());
            Bitmap icon = Bitmap.createScaledBitmap(hdIcon, Math.round(MAX_ICON_DIM * hdIcon.getWidth() / greaterDim), Math.round(MAX_ICON_DIM * hdIcon.getHeight() / greaterDim), false);

            // Free the hd icon's memory (it may be very large).
            hdIcon.recycle();

            // Compress the icon to a PNG and save it to the Contact.
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            icon.compress(Bitmap.CompressFormat.PNG, 100, bos);
            byte[] byteArr = bos.toByteArray();
            icon = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length);

            // Rotate the icon based on the orientation (cameras are dumb and they don't all use the same default rotation).
            if (orientation != ExifInterface.ORIENTATION_NORMAL) {
                Matrix rotMatrix = new Matrix();
                switch (orientation) {
                    case (ExifInterface.ORIENTATION_ROTATE_90): {
                        rotMatrix.postRotate(90);
                        break;
                    }
                    case (ExifInterface.ORIENTATION_ROTATE_180): {
                        rotMatrix.postRotate(180);
                        break;
                    }
                    case (ExifInterface.ORIENTATION_ROTATE_270): {
                        rotMatrix.postRotate(270);
                        break;
                    }
                }
                icon = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), rotMatrix, true);
            }

            // Save the icon.
            this.contact.setIcon(icon);
            this.icon.setImageBitmap(this.contact.getIcon());
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("id", this.contact.getID());
        outState.putString("name", this.contact.getName());
        outState.putParcelable("icon", this.contact.getIcon());
        outState.putStringArrayList("numbers", new ArrayList<>(this.contact.getPhoneNumbers()));
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_edit);

        // Get the Views within this Activity.
        Toolbar toolbar = findViewById(R.id.toolbar);
        this.progressBar = findViewById(R.id.progress_bar);
        this.icon = findViewById(R.id.icon);
        this.editIconButton = findViewById(R.id.edit_icon_button);
        this.name = findViewById(R.id.name);
        ImageButton editNameButton = findViewById(R.id.edit_name_button);

        // Hide the progress bar.
        this.progressBar.setVisibility(ProgressBar.INVISIBLE);

        // Determine whether a new contact is being added or an existing contact is being updated.
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("contact")) {
            this.contact = extras.getParcelable("contact");
        } else {
            // Set this contact to a new Contact.
            this.contact = new Contact("", null, Arrays.asList());
        }

        try {
            if (savedInstanceState != null) {
                Contact savedContact = new Contact("", null, new ArrayList<>());
                if (savedInstanceState.containsKey("id"))
                    savedContact.setID(savedInstanceState.getLong("id"));
                if (savedInstanceState.containsKey("name"))
                    savedContact.setName(savedInstanceState.getString("name"));
                if (savedInstanceState.containsKey("icon"))
                    savedContact.setIcon(savedInstanceState.getParcelable("icon"));
                if (savedInstanceState.containsKey("numbers"))
                    savedContact.setPhoneNumbers(savedInstanceState.getStringArrayList("numbers"));
                this.contact = savedContact;
            }
        } catch (Exception e) {

        }

        // Set the toolbar as the action bar for this activity.
        setSupportActionBar(toolbar);

        // Set up the LinearLayoutManager.
        RecyclerView recycler = findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(llm);
        this.editNumberAdapter = new EditNumberAdapter(this.contact.getPhoneNumbers());
        recycler.setAdapter(this.editNumberAdapter);

        // Fill the UI with the Contact's information.
        this.name.setText(this.contact.getName());
        if (this.contact.getIcon() != null) {
            this.icon.setImageBitmap(this.contact.getIcon());
        }

        // Listen for a click on the edit icon button.
        this.editIconButton.setOnClickListener(v->{
            if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                // Create a popup menu asking the user if they want to get an image from their camera or gallery.
                PopupMenu camGalPopup = new PopupMenu(ContactEditActivity.this, this.editIconButton);
                camGalPopup.getMenuInflater().inflate(R.menu.contact_edit_icon_dropdown, camGalPopup.getMenu());
                camGalPopup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case (R.id.menu_from_camera): {
                            // Get permission to use the camera.
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                // Let the user choose their contact's icon.
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                    File photoFile = null;
                                    try {
                                        photoFile = createImageFile();
                                    } catch (IOException e) { e.printStackTrace(); }

                                    if (photoFile != null) {
                                        this.currentPhotoUri = FileProvider.getUriForFile(this, "com.group.project", photoFile);
                                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, this.currentPhotoUri);
                                        startActivityForResult(takePictureIntent, REQ_LOCAL_CAMERA);
                                    }
                                }
                            } else {
                                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA }, PERM_REQ_CAMERA);
                            }
                            break;
                        }
                        case (R.id.menu_from_gallery): {
                            // Get permission to use the gallery.
                            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                Intent pickImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(pickImageIntent, REQ_PICK_GALLERY);
                            } else {
                                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE }, PERM_REQ_READ_EXTERNAL_STORAGE);
                            }

                            break;
                        }
                    }
                    return true;
                });
                camGalPopup.show();
            } else {

            }
        });

        // Listen for a click on the edit name button which brings up an AlertDialog for entering the new name.
        editNameButton.setOnClickListener(v->{
            EditText editText = new EditText(this);
            editText.setText(this.contact.getName());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.nc_dialog_change_name_title)
                    .setMessage(R.string.nc_dialog_change_name_message)
                    .setView(editText)
                    .setPositiveButton(R.string.nc_ok, (d,w)-> {
                        this.contact.setName(editText.getText().toString());
                        this.name.setText(this.contact.getName());
                    })
                    .setNeutralButton(R.string.nc_cancel, null)
                    .show();
        });
    }

    /**
     * Extends RecyclerView.Adapter.
     */
    public class EditNumberAdapter extends RecyclerView.Adapter<ContactEditActivity.EditNumberAdapter.EditNumberViewHolder> {
        List<String> numbers;

        /**
         * Constructor.
         * @param numbers The List of phone numbers.
         */
        public EditNumberAdapter(List<String> numbers) {
            this.numbers = numbers;
        }

        /**
         * {@inheritDoc}
         * @param parent
         * @param viewType
         * @return The ViewHolder.
         */
        @NonNull
        @Override
        public EditNumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the row.
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_contact_edit_number_row, parent, false);
            return new ContactEditActivity.EditNumberAdapter.EditNumberViewHolder(itemView);
        }

        /**
         * {@inheritDoc}
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull EditNumberViewHolder holder, int position) {
            holder.number.setText(this.numbers.get(holder.getAdapterPosition()));

            // Add a listener to the delete button.
            holder.deleteButton.setOnClickListener((v) -> {
                    this.numbers.remove(holder.getAdapterPosition());
                    this.notifyItemRemoved(holder.getAdapterPosition());
            });
        }

        /**
         * {@inheritDoc}
         * @return The size of the numbers array.
         */
        @Override
        public int getItemCount() {
            return this.numbers.size();
        }

        /**
         * Extends RecyclerView.Holder.
         */
        public class EditNumberViewHolder extends RecyclerView.ViewHolder {
            protected TextView number;
            protected ImageButton editButton;
            protected ImageButton deleteButton;

            /**
             * Constructor.
             * @param view
             */
            public EditNumberViewHolder(View view) {
                super(view);
                this.number = view.findViewById(R.id.number);
                this.editButton = view.findViewById(R.id.edit_button);
                this.deleteButton = view.findViewById(R.id.delete_button);

                // Listen for a click on the edit number button which brings up an AlertDialog for entering the new number.
                this.editButton.setOnClickListener(v->{
                    EditText editText = new EditText(ContactEditActivity.this);
                    editText.setText(EditNumberAdapter.this.numbers.get(EditNumberViewHolder.this.getAdapterPosition()));
                    editText.setInputType(InputType.TYPE_CLASS_PHONE);
                    new AlertDialog.Builder(ContactEditActivity.this)
                            .setTitle(R.string.nc_dialog_change_number_title)
                            .setMessage(R.string.nc_dialog_change_number_message)
                            .setView(editText)
                            .setPositiveButton(R.string.nc_ok, (d,w)-> {
                                EditNumberAdapter.this.numbers.set(EditNumberViewHolder.this.getAdapterPosition(), editText.getText().toString());
                                this.number.setText(editText.getText().toString());
                            })
                            .setNeutralButton(R.string.nc_cancel, null)
                            .show();
                });

                // Listen for a click on the delete number button.
                this.deleteButton.setOnClickListener(v->{
                    EditNumberAdapter.this.numbers.remove(EditNumberViewHolder.this.getAdapterPosition());
                    EditNumberAdapter.this.notifyItemRemoved(EditNumberViewHolder.this.getAdapterPosition());
                });
            }
        }
    }
}
