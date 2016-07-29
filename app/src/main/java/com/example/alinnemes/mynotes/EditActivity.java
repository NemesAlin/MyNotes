package com.example.alinnemes.mynotes;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class EditActivity extends AppCompatActivity {

    public static final String NEW_NOTE_EXTRA = "new_NOTE";
    public static final String NOTE_SUBJECT_STR = "note subject";
    public static final String NOTE_BODY_STR = "note body";
    public static final String NOTE_SHARE_HASHTAG = " #MyNotesApp";
    public static int CHANGES = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
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
                    .setTitle(R.string.discard)
                    .setMessage(R.string.discard_q)
                    .setNegativeButton(R.string.cancel_btn, null)
                    .setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            EditActivity.super.onBackPressed();
                            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
                        }
                    }).create().show();
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    private void createAndAddFragment() {

        //.setCustomAnimations(R.anim.card_flip_right_in, R.anim.card_flip_right_out, R.anim.card_flip_left_in, R.anim.card_flip_left_out)
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
