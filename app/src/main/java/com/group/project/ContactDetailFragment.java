package com.group.project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class ContactDetailFragment extends Fragment {
    private ImageView icon;
    private TextView name;

    private NumberAdapter numberAdapter;

    public Contact contact = null;

    /**
     * Required empty constructor.
     */
    public ContactDetailFragment() {
        // Required empty public constructor
    }

    /**
     * {@inheritDoc}
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        // If the Activity hosting this Fragment has a removeContactDetailFragment function, call it.
        if (getActivity() instanceof ContactDetailFragmentRemovedListener) {
            ((ContactDetailFragmentRemovedListener) getActivity()).onContactDetailFragmentRemoved();
        }
    }

    /**
     * {@inheritDoc}
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return The inflated View.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_detail, container, false);

        // Get the Views within this Fragment.
        this.icon = view.findViewById(R.id.icon);
        this.name = view.findViewById(R.id.name);

        // Get the Contact from the arguments.
        Bundle args = this.getArguments();
        if (args != null && args.containsKey("contact")) {
            this.contact = args.getParcelable("contact");
        }

        Snackbar.make(getActivity().findViewById(R.id.detail_fragment), getResources().getString(R.string.nc_toast_contact_detail_message) + " " + this.contact.getName(), Snackbar.LENGTH_SHORT).show();

        // Set up the LinearLayoutManager.
        RecyclerView recycler = view.findViewById(R.id.recycler);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(llm);
        this.numberAdapter = new NumberAdapter(this.contact.getPhoneNumbers());
        recycler.setAdapter(this.numberAdapter);

        // Fill the UI with the Contact's information.
        this.name.setText(this.contact.getName());
        if (this.contact.getIcon() != null) {
            this.icon.setImageBitmap(this.contact.getIcon());
        }
        return view;
    }

    /**
     * Extends RecyclerView.Adapter.
     */
    public class NumberAdapter extends RecyclerView.Adapter<NumberAdapter.NumberViewHolder> {
        List<String> numbers;

        /**
         * Constructor
         * @param numbers List of phone numbers.
         */
        public NumberAdapter(List<String> numbers) {
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
        public NumberAdapter.NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the row.
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.activity_contact_detail_number_row, parent, false);
            return new NumberAdapter.NumberViewHolder(itemView);
        }

        /**
         * {@inheritDoc}
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(@NonNull NumberViewHolder holder, int position) {
            holder.number.setText(this.numbers.get(holder.getAdapterPosition()));
        }

        /**
         * {@inheritDoc}
         * @return The size of the numbers List.
         */
        @Override
        public int getItemCount() {
            return this.numbers.size();
        }

        /**
         * Extension of RecyclerView.ViewHolder.
         */
        public class NumberViewHolder extends RecyclerView.ViewHolder {
            protected TextView number;
            protected ImageButton callButton;
            protected ImageButton smsButton;

            /**
             * Constructor.
             * @param view
             */
            public NumberViewHolder(View view) {
                super(view);
                this.number = view.findViewById(R.id.number);
                this.callButton = view.findViewById(R.id.call_button);
                this.smsButton = view.findViewById(R.id.sms_button);

                // Dial the phone number for the user when they click the telephone button.
                this.callButton.setOnClickListener(v-> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + NumberAdapter.this.numbers.get(NumberViewHolder.this.getAdapterPosition())))));

                // Begin a text for the user when they click the sms button.
                this.smsButton.setOnClickListener(v-> startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",  NumberAdapter.this.numbers.get(NumberViewHolder.this.getAdapterPosition()), null))));
            }
        }
    }

    /**
     * And interface that tells ContactDetailFragment to call onContactDetailFragmentRemoved when it is destroyed.
     */
    interface ContactDetailFragmentRemovedListener {
        void onContactDetailFragmentRemoved();
    }
}
