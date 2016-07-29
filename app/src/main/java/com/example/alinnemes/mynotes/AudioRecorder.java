package com.example.alinnemes.mynotes;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by alin.nemes on 22-Jul-16.
 */
public class AudioRecorder {
    private static final String LOG_TAG = "AudioRecordTest";

    //    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    //    private PlayButton   mPlayButton = null;
    private MediaPlayer mPlayer;

    public AudioRecorder() {
    }

    public void onRecord(boolean start, String audioPath) {
        if (start) {
            startRecording(audioPath);
        } else {
            stopRecording();
        }
    }

    public void onPlay(boolean start, String audioPath) {
        if (start) {
            startPlaying(audioPath);
        } else {
            stopPlaying();
        }
    }

    public void startPlaying(String audioPath) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(audioPath);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void startRecording(String audioPath) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(audioPath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;
    }
}