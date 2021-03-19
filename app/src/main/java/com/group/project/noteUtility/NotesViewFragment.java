package com.group.project.noteUtility;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group.project.NotepadMainActivity;
import com.group.project.R;

import java.util.Objects;

/**
 * Fragment used to display a notes contents
 *
 * @author Brendan Whelan
 */
public class NotesViewFragment extends Fragment {
    /**
     * Called when the fragment is created.
     *
     * Inflates the appropriate fragment_note_view and fills in the title and text of the note.
     *
     * @param inflater Inflater used to inflate the view
     * @param container Where the view is contained
     * @param savedInstanceState Bundle containing the information to be displayed
     * @return Returns the view created so the main activity can interact with it.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_view, container, false);
        ImageButton button = view.findViewById(R.id.cancelButton);

        assert getArguments() != null;
        String title = getArguments().getString("title");
        String content = getArguments().getString("content");

        TextView titleText = view.findViewById(R.id.title);
        TextView contentText = view.findViewById(R.id.content);

        titleText.setText(title);
        contentText.setText(content);

        Fragment me = this;
        button.setOnClickListener(view1 -> Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().remove(me).commit());
        return view;
    }

}
