package com.example.alinnemes.mynotes;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alinnemes.mynotes.data.MyNotesDBAdapter;
import com.example.alinnemes.mynotes.model.Note;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    private AlertDialog deleteConfirmDialogObject;
    //textViews
    private TextView noteSubject;
    private TextView noteBody;
    private TextView notifAudioRecordTV;
    //imageViews
    private ImageView mImageView;
    //buttons
    private Button deleteButton;
    private Button editButton;
    private Button startStopPLAYBTN;
    //strings
    private String picturePath;
    private String audioPath;
    private String noteSubjectSTR;
    private String noteBodySTR;

    boolean mStartPlaying = true;
    AudioRecorder audioRecorder = new AudioRecorder();

    public ViewFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            noteSubjectSTR = bundle.getString(EditActivity.NOTE_SUBJECT_STR, "");
            noteBodySTR = bundle.getString(EditActivity.NOTE_BODY_STR, "");
        }

        noteSubject = (TextView) view.findViewById(R.id.viewNoteSubject);
        noteBody = (TextView) view.findViewById(R.id.viewNoteBody);
        notifAudioRecordTV = (TextView) view.findViewById(R.id.audioRecordText);
        notifAudioRecordTV.setVisibility(View.GONE);
        mImageView = (ImageView) view.findViewById(R.id.imageView_ViewFrag);

        deleteButton = (Button) view.findViewById(R.id.deleteNote);
        editButton = (Button) view.findViewById(R.id.editNote);
        startStopPLAYBTN = (Button) view.findViewById(R.id.playStopAudioBTN);
        startStopPLAYBTN.setVisibility(View.GONE);

        Intent intent = getActivity().getIntent();

//        if(savedInstanceState!=null)
        noteSubject.setText(intent.getExtras().getString(MainActivity.NOTE_SUBJECT_EXTRA));
        noteBody.setText(intent.getExtras().getString(MainActivity.NOTE_BODY_EXTRA));
        picturePath = intent.getExtras().getString(MainActivity.NOTE_PHOTOPATH_EXTRA);
        audioPath = intent.getExtras().getString(MainActivity.NOTE_AUDIOPATH_EXTRA);
        if (picturePath != null) {
            try {
                loadBitmap(picturePath, mImageView);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "The photo has been deleted from the phone's memory or cannot find!", Toast.LENGTH_SHORT).show();
                picturePath = null;
            }
        }
        if (audioPath != null) {
            notifAudioRecordTV.setVisibility(View.VISIBLE);
            startStopPLAYBTN.setVisibility(View.VISIBLE);
;
            startStopPLAYBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    audioRecorder.onPlay(mStartPlaying,audioPath);
                    if (mStartPlaying) {
                        startStopPLAYBTN.setText("Stop playing");
                    } else {
                        startStopPLAYBTN.setText("Start playing");
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        }

        buildDeleteConfirmDialog();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ///Try something here!!! if don;t work, delete!!!!:)
                MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
                myNotesDBAdapter.open();
                Note note = myNotesDBAdapter.getNote(noteSubject.getText().toString(), noteBody.getText().toString());
                myNotesDBAdapter.close();
                Intent intent = new Intent(getActivity(), EditActivity.class);
                intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getId());
                intent.putExtra(MainActivity.NOTE_SUBJECT_EXTRA, note.getSubject());
                intent.putExtra(MainActivity.NOTE_BODY_EXTRA, note.getBody());
                intent.putExtra(MainActivity.NOTE_PHOTOPATH_EXTRA, note.getPhotoPath());
                intent.putExtra(MainActivity.NOTE_AUDIOPATH_EXTRA, note.getAudioPath());
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, MainActivity.FragmentToLaunch.EDIT);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmDialogObject.show();
            }
        });

        return view;
    }

    public void loadBitmap(String picturePath, ImageView imageView) {
//        final String imageKey = picturePath;
//        final Bitmap bitmap = MainActivity.getBitmapFromMemCache(imageKey);
//        if (bitmap != null) {
//            mImageView.setImageBitmap(bitmap);
//        } else {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(picturePath);
//        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_view, menu);
        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareNoteIntent());
        } else {
            Log.d("ShareProblem?", "Share Action Provider is null?");
        }
    }

    private Intent createShareNoteIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                noteSubjectSTR.concat(" \n\n " + noteBodySTR) + EditActivity.NOTE_SHARE_HASHTAG);
        return shareIntent;
    }

    public void buildDeleteConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Delete");
        confirmBuilder.setMessage("Are you sure you want to delete this note?");

        confirmBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
                myNotesDBAdapter.open();
                Note note = myNotesDBAdapter.getNote(noteSubject.getText().toString(), noteBody.getText().toString());
                myNotesDBAdapter.deleteNote(note.getId());
                Toast.makeText(getActivity(), "Note deleted: " + note.getSubject(), Toast.LENGTH_LONG).show();
                myNotesDBAdapter.close();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing here
            }
        });

        deleteConfirmDialogObject = confirmBuilder.create();
    }
}
