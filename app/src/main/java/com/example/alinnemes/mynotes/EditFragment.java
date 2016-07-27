package com.example.alinnemes.mynotes;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.alinnemes.mynotes.data.MyNotesDBAdapter;
import com.example.alinnemes.mynotes.model.Note;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditFragment extends Fragment {

    ////methods to intent to the camera hardware
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_PICK = 2;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    private static final String TAKE_PHOTO_EXTRA = "TAKE_PHOTO_EXTRA";
    public String localNoteSubjectVerif = "";
    public String localNoteBodyVerif = "";
    public String localNotePicturePathVerif = "";
    public String localNoteAudioPathVerif = "";
    public String localNoteVideoPathVerif = "";
    public boolean newNote = false;
    public String picturePath;
    public String audioPath;
    public String videoPath;
    public File photoFile;
    boolean mStartRecording = true;
    boolean mStartPlaying = true;
    AudioRecorder audioRecorder = new AudioRecorder();
    String mCurrentPhotoPath;
    String mCurrentAudioPath;
    private AlertDialog saveConfirmDialogObject;
    private EditText noteSubject, noteBody;
    private ImageView mImageView;
    private VideoView mVideoView;
    private long noteID = 0;
    private TextView notifAudioRecordTV;
    private Button startStopREC;
    private Button startStopPLAY;

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
        mVideoView = (VideoView) view.findViewById(R.id.videoView_EDIT);
        notifAudioRecordTV = (TextView) view.findViewById(R.id.audioRecordTextEdit);
        startStopREC = (Button) view.findViewById(R.id.playStopRecordingBTN_EDIT);
        startStopPLAY = (Button) view.findViewById(R.id.playStopPlayingBTN_EDIT);


        Button saveButton = (Button) view.findViewById(R.id.saveNote);
        Button discardButton = (Button) view.findViewById(R.id.discardNote);

        Intent intent = getActivity().getIntent();
        if (noteSubject != null || noteBody != null) {
            noteSubject.setText(intent.getExtras().getString(MainActivity.NOTE_SUBJECT_EXTRA));
            noteBody.setText(intent.getExtras().getString(MainActivity.NOTE_BODY_EXTRA));
            picturePath = intent.getExtras().getString(MainActivity.NOTE_PHOTOPATH_EXTRA);
            audioPath = intent.getExtras().getString(MainActivity.NOTE_AUDIOPATH_EXTRA);
            videoPath = intent.getExtras().getString(MainActivity.NOTE_VIDEOPATH_EXTRA);

            localNoteBodyVerif = noteBody.getText().toString();
            localNoteSubjectVerif = noteSubject.getText().toString();
            localNotePicturePathVerif = picturePath;
            localNoteAudioPathVerif = audioPath;
            localNoteVideoPathVerif = videoPath;

            playVideo();

            if (picturePath != null) {
                try {
                    loadBitmap(picturePath, mImageView);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.cannot_find_image, Toast.LENGTH_SHORT).show();
                    picturePath = null;
                }
            }

            checkIfAudioRecordExist();
            createOrRedrawAudioRecordContent();

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
                try {
                    if (picturePath != null || videoPath != null || audioPath != null) {
                        if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString())) || !(localNotePicturePathVerif.equals(picturePath)) || !(localNoteAudioPathVerif.equals(audioPath)) || !(localNoteVideoPathVerif.equals(videoPath))) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mImageView.setLongClickable(true);
        registerForContextMenu(mImageView);
        return view;
    }

    public void playVideo() {
        if (videoPath != null) {
            mVideoView.setVideoPath(videoPath);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
            lp.height = 1000;
            mVideoView.setLayoutParams(lp);
            mVideoView.requestFocus();
            mVideoView.setMediaController(new MediaController(getContext()));
            mVideoView.start();
        }
    }

    public boolean checkIfAudioRecordExist() {
        File audioFile;
        try {
            audioFile = new File(audioPath);
            if (audioFile.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        audioPath = null;
        return false;
    }

    public void createOrRedrawAudioRecordContent() {

        boolean existingFile = checkIfAudioRecordExist();

        if (audioPath != null && existingFile) {
            notifAudioRecordTV.setText(R.string.audio_record_exist);
            try {
                startStopREC.setOnClickListener(new View.OnClickListener() {
                    File audioFile = createAudioFile();

                    @Override
                    public void onClick(View view) {
                        audioPath = audioFile.getAbsolutePath();
                        audioRecorder.onRecord(mStartRecording, audioPath);
                        if (mStartRecording) {
                            startStopREC.setText(R.string.stop_recording);
                        } else {
                            startStopREC.setText(R.string.start_recording);

                        }
                        mStartRecording = !mStartRecording;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            startStopPLAY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    audioRecorder.onPlay(mStartPlaying, audioPath);
                    if (mStartPlaying) {
                        startStopPLAY.setText(R.string.stop_playing);
                    } else {
                        startStopPLAY.setText(R.string.start_playing);
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        } else {
            audioPath = null;
            final RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) startStopREC.getLayoutParams();
            lp.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);

            notifAudioRecordTV.setText(R.string.audio_record_dont_exist);
            startStopPLAY.setVisibility(View.GONE);
            startStopREC.setLayoutParams(lp);
            try {
                startStopREC.setOnClickListener(new View.OnClickListener() {
                    File audioFile = createAudioFile();

                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(View view) {
                        audioPath = audioFile.getAbsolutePath();
                        audioRecorder.onRecord(mStartRecording, audioPath);
                        if (mStartRecording) {
                            startStopREC.setText(R.string.stop_recording);
                        } else {
                            startStopREC.setText(R.string.start_recording);
                            if (audioPath != null) {
                                notifAudioRecordTV.setText(R.string.audio_record_exist);
                                startStopPLAY.setVisibility(View.VISIBLE);
                                lp.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                                startStopREC.setLayoutParams(lp);
                            }
                        }
                        mStartRecording = !mStartRecording;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            startStopPLAY.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    audioRecorder.onPlay(mStartPlaying, audioPath);
                    if (mStartPlaying) {
                        startStopPLAY.setText(R.string.stop_playing);
                    } else {
                        startStopPLAY.setText(R.string.start_playing);
                    }
                    mStartPlaying = !mStartPlaying;
                }
            });
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(R.string.image);
        ArrayList<String> menuItems = new ArrayList<String>();
        menuItems.add(getString(R.string.delete_image));
        for (int i = 0; i < menuItems.size(); i++) {
            menu.add(Menu.NONE, i, i, menuItems.get(i));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                mImageView.setImageResource(0);
                picturePath = null;
                return true;
        }

        return super.onContextItemSelected(item);
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
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem action_record_item = menu.findItem(R.id.action_record);
        MenuItem action_image_item = menu.findItem(R.id.action_image);
        super.onPrepareOptionsMenu(menu);
        if (audioPath == null) {
            action_record_item.setVisible(false);
        } else {
            action_record_item.setVisible(true);
        }
        if (picturePath == null) {
            action_image_item.setVisible(false);
        } else {
            action_image_item.setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        menu.setGroupVisible(R.id.group_attach, true);
        menu.setGroupEnabled(R.id.group_attach, true);
        menu.setGroupCheckable(R.id.group_attach, true, true);
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_record:
                File audioFile = new File(audioPath);
                if (audioFile.exists()) {
                    audioFile.delete();
                    audioPath = null;
                } else {
                    Toast.makeText(getActivity(), R.string.audio_file_cannot_be_found, Toast.LENGTH_SHORT).show();
                    audioPath = null;
                }
                createOrRedrawAudioRecordContent();
                return true;
            case R.id.action_image:
                mImageView.setImageResource(0);
                picturePath = null;
                return true;
            case R.id.action_photo:
                dispatchTakePictureIntent();
                return true;
            case R.id.action_video:
                dispatchTakeVideoIntent();
                return true;
            case R.id.action_gallery:
                pickImageFromGallery();
                return true;
            case android.R.id.home:
                if (localNotePicturePathVerif != null || picturePath != null) {
                    if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString())) || !(localNotePicturePathVerif.equals(picturePath))) {
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

    private void dispatchTakeVideoIntent() {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void pickImageFromGallery() {
        Intent pickImage = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            picturePath = getPhotoVideoPath(selectedImage);
            loadBitmap(picturePath, mImageView);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            picturePath = getPhotoVideoPath(selectedImage);
            loadBitmap(picturePath, mImageView);
            galleryAddPic();///curios!!!!!
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            videoPath = getPhotoVideoPath(videoUri);
            playVideo();

        }

    }

    public String getPhotoVideoPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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

    private File createAudioFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "REC_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir("Records");
        File audio = File.createTempFile(
                audioFileName,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );

        mCurrentAudioPath = "file:" + audio.getAbsolutePath();
        return audio;
    }


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    ////^^^^^^^--------methods to intent to the camera hardware

    public void buildSaveConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.save);
        confirmBuilder.setMessage(R.string.save_q);

        confirmBuilder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Intent intent = new Intent(getActivity(), MainActivity.class);
                MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getActivity().getBaseContext());
                myNotesDBAdapter.open();

                //if is a new note create it in database
                if (newNote) {
                    if (noteSubject.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), R.string.cannot_add_a_note_without_subject, Toast.LENGTH_LONG).show();
                    } else {
                        String timeStamp = new SimpleDateFormat("dd.MM.yyyy\nHH:mm").format(new Date());
                        Note note =
                                myNotesDBAdapter.createNote(noteSubject.getText() + "", noteBody.getText() + "", timeStamp, "location", picturePath, audioPath, videoPath);

                        Log.d("DEBUG THE ADD METHOD", "NOTE SUBJECT: " + note.getSubject() + ", NOTE BODY: " + note.getBody() + ", NOTE CREATED: " + note.getDateCreated() + ", NOTE LOCATION: " + note.getLocationCreated() + ", NOTE PHOTOPATH: " + note.getPhotoPath() + ", NOTE AUDIOPATH: " + note.getAudioPath());

                        startActivity(intent);
                    }
                } else {
                    //otherwise, is an existing note, update it!
                    long id = myNotesDBAdapter.updateNote(noteID, noteSubject.getText() + "", noteBody.getText() + "", picturePath, audioPath, videoPath);
                    Log.d("DEBUG THE UPDATE METHOD", Long.toString(id));

                    startActivity(intent);
                }
                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
                myNotesDBAdapter.close();


            }
        });

        confirmBuilder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Nothing here
            }
        });

        saveConfirmDialogObject = confirmBuilder.create();
    }

}
