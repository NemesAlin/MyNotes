package com.example.alinnemes.mynotes.Utility;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.EditText;

import com.example.alinnemes.mynotes.activities.EditActivity;

import java.io.File;

/**
 * Created by alin.nemes on 29-Jul-16.
 */
public class GeneralUtility {

    public static String getPhotoVideoPath(Uri uri, Fragment fragment) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = fragment.getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public static void discarding(Fragment fragment,String localNoteSubjectVerif, EditText noteSubject, String localNoteBodyVerif,EditText noteBody,String localNotePicturePathVerif,String photoPath,String localNoteAudioPathVerif,String audioPath,String localNoteVideoPathVerif,String videoPath){
        if (!(localNoteSubjectVerif.equals(noteSubject.getText().toString())) || !(localNoteBodyVerif.equals(noteBody.getText().toString())) || (localNotePicturePathVerif != photoPath) || (localNoteAudioPathVerif != (audioPath)) || (localNoteVideoPathVerif != (videoPath))) {
            EditActivity.CHANGES = 1;
            fragment.getActivity().onBackPressed();
        } else {
            EditActivity.CHANGES = 0;
            fragment.getActivity().onBackPressed();
        }
    }

}
