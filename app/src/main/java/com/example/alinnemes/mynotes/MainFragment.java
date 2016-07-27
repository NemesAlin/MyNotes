package com.example.alinnemes.mynotes;

import android.annotation.TargetApi;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alinnemes.mynotes.data.MyNotesDBAdapter;
import com.example.alinnemes.mynotes.model.Note;
import com.example.alinnemes.mynotes.model.NoteAdapter;

import java.util.ArrayList;

public class MainFragment extends ListFragment {

    private ArrayList<Note> notes;
    private NoteAdapter noteAdapter;
    private AlertDialog deleteConfirmDialogObject;
    private int pos;


    public MainFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
        myNotesDBAdapter.open();
        notes = myNotesDBAdapter.getAllNotes();
        myNotesDBAdapter.close();

        noteAdapter = new NoteAdapter(getActivity(), notes);

        //set the adapter to display the list of notes
        setListAdapter(noteAdapter);

        //build the delete dialog
        buildDeleteConfirmDialog();

        //register the menu
        registerForContextMenu(getListView());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateNotesList();

    }

    public void updateNotesList() {
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        launchEditActivity(MainActivity.FragmentToLaunch.VIEW, position);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.long_press_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //the position of what item is selected by the contextItemSelected on the longPress
        AdapterView.AdapterContextMenuInfo nfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int rowPosition = nfo.position;
        switch (item.getItemId()) {
            case R.id.edit:
                launchEditActivity(MainActivity.FragmentToLaunch.EDIT, rowPosition);
                Log.d("TAG", "Edit pressed");
                return true;
            case R.id.delete:
                Log.d("TAG", "Delete pressed");
                setPositionToDeleteNote(rowPosition);
                deleteConfirmDialogObject.show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private int getPositionToDeleteNote() {
        return pos;
    }

    private void setPositionToDeleteNote(int pos) {
        this.pos = pos;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void launchEditActivity(MainActivity.FragmentToLaunch ftl, int position) {
        Note note = (Note) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), EditActivity.class);
        intent.putExtra(MainActivity.NOTE_ID_EXTRA, note.getId());
        intent.putExtra(MainActivity.NOTE_SUBJECT_EXTRA, note.getSubject());
        intent.putExtra(MainActivity.NOTE_BODY_EXTRA, note.getBody());
        intent.putExtra(MainActivity.NOTE_PHOTOPATH_EXTRA, note.getPhotoPath());
        intent.putExtra(MainActivity.NOTE_AUDIOPATH_EXTRA, note.getAudioPath());
        intent.putExtra(MainActivity.NOTE_VIDEOPATH_EXTRA, note.getVideoPath());
        intent.putExtra(MainActivity.NOTE_DATECREATED_EXTRA, note.getDateCreated());

        switch (ftl) {
            case VIEW:
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, MainActivity.FragmentToLaunch.VIEW);
                break;
            case EDIT:
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, MainActivity.FragmentToLaunch.EDIT);
                break;
        }
//        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    public void buildDeleteConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.delete);
        confirmBuilder.setMessage(R.string.delete_q);

        confirmBuilder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int rowPosition = getPositionToDeleteNote();
                Note note = (Note) getListAdapter().getItem(rowPosition);
                MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
                myNotesDBAdapter.open();
                myNotesDBAdapter.deleteNote(note.getId());
                notes.clear();
                notes.addAll(myNotesDBAdapter.getAllNotes());
                noteAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), getString(R.string.note_deleted) + " " + note.getSubject(), Toast.LENGTH_LONG).show();
                myNotesDBAdapter.close();
            }
        });

        confirmBuilder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing here
            }
        });

        deleteConfirmDialogObject = confirmBuilder.create();
    }
}
