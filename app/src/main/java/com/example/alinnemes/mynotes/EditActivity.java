package com.example.alinnemes.mynotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class EditActivity extends AppCompatActivity {

    public static final String NEW_NOTE_EXTRA = "new_NOTE";
    public static final String NOTE_SUBJECT_STR = "note subject";
    public static final String NOTE_BODY_STR = "note body";
    public static final String NOTE_SHARE_HASHTAG = " #MyNotesApp";
    public static int CHANGES = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            createAndAddFragment();
        }
    }

    @Override
    public void onBackPressed() {
        String frag = getSupportFragmentManager().findFragmentById(R.id.note_container).getClass().getName();
        if (frag.equals("com.example.alinnemes.mynotes.EditFragment") && CHANGES == 1) {
            new AlertDialog.Builder(this)
                    .setTitle("Discard")
                    .setMessage("Are you sure you want to discard the note?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            EditActivity.super.onBackPressed();
                        }
                    }).create().show();
        } else {
            super.onBackPressed();
        }
    }

    private void createAndAddFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Get intent, action and MIME type
        Intent intent = getIntent();

        MainActivity.FragmentToLaunch fragmentToLaunch = (MainActivity.FragmentToLaunch) intent.getSerializableExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD);

        switch (fragmentToLaunch) {
            case ADD:
                EditFragment addFragment = new EditFragment();
                setTitle(R.string.title_add_fragment);
                Bundle bundle = new Bundle();
                bundle.putBoolean(NEW_NOTE_EXTRA, true);
                addFragment.setArguments(bundle);
                fragmentTransaction.add(R.id.note_container, addFragment, "NOTE_ADD_FRAGMENT");
                break;
            case SHARED:
                EditFragment sharedFragment = new EditFragment();
                setTitle(R.string.title_add_fragment);
                Bundle sharedBundle = new Bundle();
                sharedBundle.putBoolean(NEW_NOTE_EXTRA, true);
                sharedFragment.setArguments(sharedBundle);
                fragmentTransaction.add(R.id.note_container, sharedFragment, "NOTE_SHARED_FRAGMENT");
                break;
            case EDIT:
                EditFragment editFragment = new EditFragment();
                setTitle(R.string.title_edit_fragment);
                fragmentTransaction.add(R.id.note_container, editFragment, "NOTE_EDIT_FRAGMENT");
                break;
            case VIEW:
                ViewFragment viewFragment = new ViewFragment();
                setTitle(R.string.title_view_fragment);
                Bundle newBundle = new Bundle();
                newBundle.putString(NOTE_SUBJECT_STR, intent.getExtras().getString(MainActivity.NOTE_SUBJECT_EXTRA));
                newBundle.putString(NOTE_BODY_STR, intent.getExtras().getString(MainActivity.NOTE_BODY_EXTRA));
                viewFragment.setArguments(newBundle);
                fragmentTransaction.add(R.id.note_container, viewFragment, "NOTE_VIEW_FRAGMENT");
                break;
        }
        fragmentTransaction.commit();
    }
}
