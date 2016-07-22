package com.example.alinnemes.mynotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alinnemes.mynotes.data.MyNotesDBAdapter;
import com.example.alinnemes.mynotes.model.Note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditFragment extends Fragment {

    private AlertDialog saveConfirmDialogObject;
    private EditText noteSubject, noteBody;
    private ImageView mImageView;
    public String localNoteSubjectVerif = "";
    public String localNoteBodyVerif = "";
    public String localNotePathVerif = "";
    private static final String TAKE_PHOTO_EXTRA = "TAKE_PHOTO_EXTRA";


    public boolean newNote = false;
    public String picturePath;
    public File photoFile;
    private long noteID = 0;

    public EditFragment() {
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

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            newNote = bundle.getBoolean(EditActivity.NEW_NOTE_EXTRA, false);
        }

        noteSubject = (EditText) view.findViewById(R.id.editNoteSubject);
        noteBody = (EditText) view.findViewById(R.id.editNoteBody);
        mImageView = (ImageView) view.findViewById(R.id.imageView);

        Button saveButton = (Button) view.findViewById(R.id.saveNote);
        Button discardButton = (Button) view.findViewById(R.id.discardNote);


        Intent intent = getActivity().getIntent();
        if (noteSubject != null || noteBody != null) {
            noteSubject.setText(intent.getExtras().getString(MainActivity.NOTE_SUBJECT_EXTRA));
            noteBody.setText(intent.getExtras().getString(MainActivity.NOTE_BODY_EXTRA));
            picturePath = intent.getExtras().getString(MainActivity.NOTE_PATH_EXTRA);

            localNoteBodyVerif = noteBody.getText().toString();
            localNoteSubjectVerif = noteSubject.getText().toString();
            localNotePathVerif = picturePath;

            if (picturePath != null) {
                try {
//                    mImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeFile(picturePath), 1000, 1500, true));
                    loadBitmap(picturePath, mImageView);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "The photo has been deleted from the phone's memory or cannot find!", Toast.LENGTH_SHORT).show();
                    picturePath = null;
                }
            }
            noteID = intent.getExtras().getInt(MainActivity.NOTE_ID_EXTRA, 0);
        }
        buildSaveConfirmDialog();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveConfirmDialogObject.show();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (localNotePathVerif != null || picturePath != null) {
                    if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString())) || !(localNotePathVerif.equals(picturePath))) {
                        EditActivity.CHANGES = 1;
                        getActivity().onBackPressed();
                    } else {
                        EditActivity.CHANGES = 0;
                        getActivity().onBackPressed();
                    }
                } else {
                    if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString()))) {
                        EditActivity.CHANGES = 1;
                        getActivity().onBackPressed();
                    } else {
                        EditActivity.CHANGES = 0;
                        getActivity().onBackPressed();
                    }
                }
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
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                dispatchTakePictureIntent();
                return true;
            case R.id.action_gallery:
                pickImageFromGallery();
                return true;
            case android.R.id.home:
                if (localNotePathVerif != null || picturePath != null) {
                    if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString())) || !(localNotePathVerif.equals(picturePath))) {
                        EditActivity.CHANGES = 1;
                        getActivity().onBackPressed();
                    } else {
                        EditActivity.CHANGES = 0;
                        getActivity().onBackPressed();
                    }
                } else {
                    if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString()))) {
                        EditActivity.CHANGES = 1;
                        getActivity().onBackPressed();
                    } else {
                        EditActivity.CHANGES = 0;
                        getActivity().onBackPressed();
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    ////method to intent to the camera hardware
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PICK = 2;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(TAKE_PHOTO_EXTRA, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            picturePath = getPhotoPath(selectedImage);
            loadBitmap(picturePath, mImageView);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            picturePath = getPhotoPath(selectedImage);
            loadBitmap(picturePath, mImageView);
            galleryAddPic();///curios!!!!!
        }

    }

    public String getPhotoPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void pickImageFromGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQUEST_IMAGE_PICK);
    }

    ////^^^^^^^--------methods to intent to the camera hardware

    public void buildSaveConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle("Save");
        confirmBuilder.setMessage("Are you sure you want to save the note?");

        confirmBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("Save Note", "NoteSubject: " + noteSubject.getText() + " NoteBody: " + noteBody.getText());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
                myNotesDBAdapter.open();

                //if is a new note create it in database
                if (newNote) {
                    if (noteSubject.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), "Cannot add a note without subject!", Toast.LENGTH_LONG).show();
                    } else {
                        Note note =
                                myNotesDBAdapter.createNote(noteSubject.getText() + "", noteBody.getText() + "", picturePath);

                        Log.d("DEBUG THE ADD METHOD", "NOTE SUBJECT: " + note.getSubject() + ", NOTE BODY: " + note.getBody());

                        startActivity(intent);
                    }
                } else {
                    //otherwise, is an existing note, update it!
                    long id = myNotesDBAdapter.updateNote(noteID, noteSubject.getText() + "", noteBody.getText() + "", picturePath);

                    Log.d("DEBUG THE UPDATE METHOD", Long.toString(id));

                    startActivity(intent);
                }

                myNotesDBAdapter.close();


            }
        });

        confirmBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing here
            }
        });

        saveConfirmDialogObject = confirmBuilder.create();
    }

}
