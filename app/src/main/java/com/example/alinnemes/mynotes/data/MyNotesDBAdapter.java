package com.example.alinnemes.mynotes.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.alinnemes.mynotes.model.Note;

import java.util.ArrayList;

/**
 * Created by alin.nemes on 14-Jul-16.
 */
public class MyNotesDBAdapter {

    private static final String DATABASE_NAME = "mynotes.db";
    private static final int DATABASE_VERSION = 9;

    private static final String NOTE_TABLE = "note";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_SUBJECT = "subject";
    private static final String COLUMN_BODY = "body";
    private static final String COLUMN_DATECREATED = "dateCreated";
    private static final String COLUMN_LOCATIONCREATED = "locationCreated";
    private static final String COLUMN_PHOTOPATH = "photo";
    private static final String COLUMN_AUDIOPATH = "audio";
    private static final String COLUMN_VIDEOPATH = "video";
    private static final String CREATE_TABLE = "CREATE TABLE " + NOTE_TABLE + " ( " +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SUBJECT + " TEXT NOT NULL, " +
            COLUMN_BODY + " TEXT NOT NULL, " +
            COLUMN_PHOTOPATH + " TEXT, " +
            COLUMN_AUDIOPATH + " TEXT, " +
            COLUMN_VIDEOPATH + " TEXT, " +
            COLUMN_DATECREATED + " TEXT NOT NULL, " +
            COLUMN_LOCATIONCREATED + " TEXT NOT NULL " +
            ");";
    private String[] allColumns = {COLUMN_ID, COLUMN_SUBJECT, COLUMN_BODY, COLUMN_PHOTOPATH, COLUMN_AUDIOPATH, COLUMN_VIDEOPATH, COLUMN_DATECREATED, COLUMN_LOCATIONCREATED};
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private MyNotesDbHelper myNotesDbHelper;

    public MyNotesDBAdapter(Context cxt) {
        this.context = cxt;
    }

    public MyNotesDBAdapter open() throws android.database.SQLException {
        myNotesDbHelper = new MyNotesDbHelper(context);
        sqLiteDatabase = myNotesDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myNotesDbHelper.close();
    }

    public Note createNote(String subject, String body, String dateCreated, String locationCreated, String photoPath, String audioPath, String videoPath) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, subject);
        values.put(COLUMN_BODY, body);
        values.put(COLUMN_PHOTOPATH, photoPath);
        values.put(COLUMN_AUDIOPATH, audioPath);
        values.put(COLUMN_VIDEOPATH, videoPath);
        values.put(COLUMN_DATECREATED, dateCreated);
        values.put(COLUMN_LOCATIONCREATED, locationCreated);

        long insertId = sqLiteDatabase.insert(NOTE_TABLE, null, values);

        Cursor cursor = sqLiteDatabase.query(NOTE_TABLE, allColumns, COLUMN_ID + " = " + insertId, null, null, null, null);

        cursor.moveToFirst();
        Note newNote = cursorToNote(cursor);
        cursor.close();
        return newNote;
    }

    public long updateNote(long idToUpdate, String newSubject, String newBody, String newPhotoPath, String newAudioPath, String newVideoPath) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SUBJECT, newSubject);
        values.put(COLUMN_BODY, newBody);
        values.put(COLUMN_PHOTOPATH, newPhotoPath);
        values.put(COLUMN_AUDIOPATH, newAudioPath);
        values.put(COLUMN_VIDEOPATH, newVideoPath);

        return sqLiteDatabase.update(NOTE_TABLE, values, COLUMN_ID + " = " + idToUpdate, null);
    }

    public long deleteNote(long idToDelete) {
        return sqLiteDatabase.delete(NOTE_TABLE, COLUMN_ID + " = " + idToDelete, null);
    }

    public ArrayList<Note> getAllNotes() {
        ArrayList<Note> allNotes = new ArrayList<Note>();

        //grall all the information from your dataBase
        Cursor cursor = sqLiteDatabase.query(NOTE_TABLE, allColumns, null, null, null, null, null);
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()) {
            Note note = cursorToNote(cursor);
            allNotes.add(note);
            cursor.moveToPrevious();
        }
        cursor.close();
        return allNotes;
    }

    public Note getNote(String subject) {
        Cursor cursor = sqLiteDatabase.query(NOTE_TABLE, allColumns, COLUMN_SUBJECT + " = " + "\"" + subject + "\"", null, null, null, null);
        cursor.moveToFirst();
        Note note = cursorToNote(cursor);
        cursor.close();
        return note;
    }

    private Note cursorToNote(Cursor cursor) {
        Note newNote = new Note(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7));
        return newNote;
    }

    private static class MyNotesDbHelper extends SQLiteOpenHelper {

        MyNotesDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            //create note table
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            Log.w(MyNotesDbHelper.class.getName(), "Updating database from version: " + oldVersion + " to: " + newVersion + ",witch will destroy the old data");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

}
