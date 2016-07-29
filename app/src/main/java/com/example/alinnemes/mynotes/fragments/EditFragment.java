package com.example.alinnemes.mynotes.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.alinnemes.mynotes.R;
import com.example.alinnemes.mynotes.Utility.AudioRecorder;
import com.example.alinnemes.mynotes.Utility.AudioUtility;
import com.example.alinnemes.mynotes.Utility.GeneralUtility;
import com.example.alinnemes.mynotes.Utility.PhotoUtility;
import com.example.alinnemes.mynotes.Utility.VideoUtility;
import com.example.alinnemes.mynotes.activities.EditActivity;
import com.example.alinnemes.mynotes.activities.MainActivity;
import com.example.alinnemes.mynotes.data.MyNotesDB;
import com.example.alinnemes.mynotes.model.Note;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class EditFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //PUBLIC KEYS
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;
    public static final int REQUEST_VIDEO_CAPTURE = 3;
    public static final int REQUEST_LOCATION_PERMISSION = 4;
    public static final String TAKE_PHOTO_EXTRA = "TAKE_PHOTO_EXTRA";

    //locals
    public String localNoteSubjectVerif = "";
    public String localNoteBodyVerif = "";
    public String localNotePicturePathVerif = "";
    public String localNoteAudioPathVerif = "";
    public String localNoteVideoPathVerif = "";

    //PATH'S
    private String photoPath;
    private String audioPath;
    private String videoPath;
    private String locationCoords;

    //CONDITIONS
    private boolean mStartRecording = true;
    private boolean mStartPlaying = true;
    private boolean newNote = false;

    private AudioRecorder audioRecorder = new AudioRecorder();
    private long noteID = 0;

    //VIEWS
    private AlertDialog saveConfirmDialogObject;
    private EditText noteSubject, noteBody;
    private ImageView mImageView;
    private VideoView mVideoView;
    private TextView notifAudioRecordTV;
    private Button startStopREC;
    private Button startStopPLAY;
    private GoogleApiClient mGoogleApiClient;

    public EditFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            newNote = bundle.getBoolean(EditActivity.NEW_NOTE_EXTRA, false);
        }

        if (newNote) {
            // Create an instance of GoogleAPIClient.
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit, container, false);

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
            photoPath = intent.getExtras().getString(MainActivity.NOTE_PHOTOPATH_EXTRA);
            audioPath = intent.getExtras().getString(MainActivity.NOTE_AUDIOPATH_EXTRA);
            videoPath = intent.getExtras().getString(MainActivity.NOTE_VIDEOPATH_EXTRA);

            boolean existingAudio = AudioUtility.checkIfAudioRecordExist(audioPath);

            localNoteBodyVerif = noteBody.getText().toString();
            localNoteSubjectVerif = noteSubject.getText().toString();
            localNotePicturePathVerif = photoPath;
            if (existingAudio) {
                localNoteAudioPathVerif = audioPath;
            } else {
                localNoteAudioPathVerif = null;
            }
            localNoteVideoPathVerif = videoPath;

            VideoUtility.playVideo(videoPath, mVideoView, this);

            if (photoPath != null) {
                try {
                    PhotoUtility.loadBitmap(photoPath, mImageView);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), R.string.cannot_find_image, Toast.LENGTH_SHORT).show();
                    photoPath = null;
                }
            }

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
                GeneralUtility.discarding(EditFragment.this, localNoteSubjectVerif, noteSubject, localNoteBodyVerif, noteBody, localNotePicturePathVerif, photoPath, localNoteAudioPathVerif, audioPath, localNoteVideoPathVerif, videoPath);
            }
        });
        mImageView.setLongClickable(true);
        registerForContextMenu(mImageView);
        return view;
    }

    public void createOrRedrawAudioRecordContent() {

        boolean existingFile = AudioUtility.checkIfAudioRecordExist(audioPath);

        if (audioPath != null && existingFile) {
            notifAudioRecordTV.setText(R.string.audio_record_exist);
            try {
                startStopREC.setOnClickListener(new View.OnClickListener() {
                    File audioFile = AudioUtility.createAudioFile(getContext());

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
                    File audioFile = AudioUtility.createAudioFile(getContext());

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
                photoPath = null;
                return true;
        }

        return super.onContextItemSelected(item);
    }


    @Override
    public void onStart() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem action_record_item = menu.findItem(R.id.action_deleteRecord);
        MenuItem action_image_item = menu.findItem(R.id.action_deletePhoto);
        MenuItem action_video_item = menu.findItem(R.id.action_deleteVideo);
        super.onPrepareOptionsMenu(menu);
        if (audioPath == null) {
            action_record_item.setVisible(false);
        } else {
            action_record_item.setVisible(true);
        }
        if (photoPath == null) {
            action_image_item.setVisible(false);
        } else {
            action_image_item.setVisible(true);
        }
        if (videoPath == null) {
            action_video_item.setVisible(false);
        } else {
            action_video_item.setVisible(true);
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
            case R.id.action_deleteRecord:
                File audioFile = new File(audioPath);
                if (audioFile.exists()) {
                    audioFile.delete();
                } else {
                    Toast.makeText(getActivity(), R.string.audio_file_cannot_be_found, Toast.LENGTH_SHORT).show();
                }
                audioPath = null;
                createOrRedrawAudioRecordContent();
                return true;
            case R.id.action_deletePhoto:
                File photoFile = new File(photoPath);
                if (photoFile.exists()) {
                    photoFile.delete();
                    mImageView.setImageResource(0);
                } else {
                    Toast.makeText(getActivity(), R.string.image_file_cannot_be_found, Toast.LENGTH_SHORT).show();
                }
                photoPath = null;
                return true;
            case R.id.action_deleteVideo:
                File videoFile = new File(videoPath);
                if (videoFile.exists()) {
                    videoFile.delete();
                } else {
                    Toast.makeText(getActivity(), R.string.video_file_cannot_be_found, Toast.LENGTH_SHORT).show();
                }
                videoPath = null;
                return true;
            case R.id.action_photo:
                PhotoUtility.dispatchTakePictureIntent(this);
                return true;
            case R.id.action_video:
                VideoUtility.dispatchTakeVideoIntent(this);
                return true;
            case R.id.action_gallery:
                PhotoUtility.pickImageFromGallery(this);
                return true;
            case android.R.id.home:
                GeneralUtility.discarding(EditFragment.this, localNoteSubjectVerif, noteSubject, localNoteBodyVerif, noteBody, localNotePicturePathVerif, photoPath, localNoteAudioPathVerif, audioPath, localNoteVideoPathVerif, videoPath);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            photoPath = GeneralUtility.getPhotoVideoPath(selectedImage, this);
            PhotoUtility.loadBitmap(photoPath, mImageView);
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            photoPath = GeneralUtility.getPhotoVideoPath(selectedImage, this);
            PhotoUtility.loadBitmap(photoPath, mImageView);
            PhotoUtility.galleryAddPic(photoPath, this);///curios!!!!!
        } else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            videoPath = GeneralUtility.getPhotoVideoPath(videoUri, this);
            VideoUtility.playVideo(videoPath, mVideoView, this);

        }

    }

    public void buildSaveConfirmDialog() {
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(getActivity());
        confirmBuilder.setTitle(R.string.save);
        confirmBuilder.setMessage(R.string.save_q);

        confirmBuilder.setPositiveButton(R.string.confirm_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                Intent intent = new Intent(getActivity(), MainActivity.class);
                MyNotesDB myNotesDB = new MyNotesDB(getActivity().getBaseContext());
                myNotesDB.open();

                //if is a new note create it in database
                if (newNote) {
                    if (noteSubject.getText().toString().equals("")) {
                        Toast.makeText(getActivity(), R.string.cannot_add_a_note_without_subject, Toast.LENGTH_LONG).show();
                    } else {
                        String timeStamp = new SimpleDateFormat("dd.MM.yyyy\nHH:mm").format(new Date());
                        if (locationCoords == null) {
                            Toast.makeText(getActivity(), "Location Services UNAVAILABLE! The Note will be saved with no location data.", Toast.LENGTH_LONG).show();
                            locationCoords = "none";
                        }
                        Note note =
                                myNotesDB.createNote(noteSubject.getText() + "", noteBody.getText() + "", timeStamp, locationCoords, photoPath, audioPath, videoPath);

                        Log.e("DEBUG THE ADD METHOD", "NOTE SUBJECT: " + note.getSubject() + ", NOTE BODY: " + note.getBody() + ", NOTE CREATED: " + note.getDateCreated() + ", NOTE LOCATION: " + note.getLocationCreated() + ", NOTE PHOTOPATH: " + note.getPhotoPath() + ", NOTE AUDIOPATH: " + note.getAudioPath() + ", NOTEVIDEOPATH: " + note.getVideoPath());

                        startActivity(intent);
                    }
                } else {
                    //otherwise, is an existing note, update it!
                    long id = myNotesDB.updateNote(noteID, noteSubject.getText() + "", noteBody.getText() + "", photoPath, audioPath, videoPath);
                    Log.d("DEBUG THE UPDATE METHOD", Long.toString(id));

                    startActivity(intent);
                }
                getActivity().overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
                myNotesDB.close();


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

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                String latitude = String.valueOf(mLastLocation.getLatitude());
                String longitude = String.valueOf(mLastLocation.getLongitude());
                locationCoords = latitude + "," + longitude;
            } else {
                Toast.makeText(getActivity(), "Location Services UNAVAILABLE! The Note will be saved with no location data.", Toast.LENGTH_LONG).show();
                locationCoords = "none";
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), " LOCATION PERMISSION GRANTED!", Toast.LENGTH_SHORT).show();
                    if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        if (mLastLocation != null) {
                            String latitude = String.valueOf(mLastLocation.getLatitude());
                            String longitude = String.valueOf(mLastLocation.getLongitude());
                            locationCoords = latitude + "," + longitude;
                        } else {
                            Toast.makeText(getActivity(), "Location Services UNAVAILABLE! The Note will be saved with no location data.", Toast.LENGTH_LONG).show();
                            locationCoords = "none";
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Location Services UNAVAILABLE! The Note will be saved with no location data.", Toast.LENGTH_LONG).show();
                    locationCoords = "none";
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Toast.makeText(getActivity(), "The location services are unavailable!", Toast.LENGTH_SHORT).show();
        }

    }
}
