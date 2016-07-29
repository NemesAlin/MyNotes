package com.example.alinnemes.mynotes.Utility;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.example.alinnemes.mynotes.fragments.EditFragment;

import java.io.File;

/**
 * Created by alin.nemes on 29-Jul-16.
 */
public class VideoUtility {

    public static void dispatchTakeVideoIntent(Fragment fragment) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(takeVideoIntent, EditFragment.REQUEST_VIDEO_CAPTURE);
        }
    }

    public static boolean checkIfVideoExist(String videoPath) {
        File videoFile;
        try {
            videoFile = new File(videoPath);
            if (videoFile.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static void playVideo(String videoPath, VideoView mVideoView, Fragment fragment) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mVideoView.getLayoutParams();
        boolean existingCondition = checkIfVideoExist(videoPath);
        if (videoPath != null && existingCondition) {
            mVideoView.setVideoPath(videoPath);
            lp.height = 1000;
            mVideoView.setLayoutParams(lp);
            mVideoView.requestFocus();
            mVideoView.setMediaController(new MediaController(fragment.getContext()));
            mVideoView.start();
        } else {
            lp.height = 0;
            mVideoView.setLayoutParams(lp);
            mVideoView.setVisibility(View.GONE);
        }
    }

}
