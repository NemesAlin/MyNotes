package com.example.alinnemes.mynotes.Utility;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alin.nemes on 29-Jul-16.
 */
public class AudioUtility {

    public static boolean checkIfAudioRecordExist(String audioPath) {
        File audioFile;
        try {
            audioFile = new File(audioPath);
            if (audioFile.exists()) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static File createAudioFile(Context cxt) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String audioFileName = "REC_" + timeStamp + "_";
        File storageDir = cxt.getExternalFilesDir("Records");
        File audio = File.createTempFile(
                audioFileName,  /* prefix */
                ".3gp",         /* suffix */
                storageDir      /* directory */
        );
        return audio;
    }

}
