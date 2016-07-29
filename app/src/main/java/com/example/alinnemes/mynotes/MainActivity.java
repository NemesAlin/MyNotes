package com.example.alinnemes.mynotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    public static final String NOTE_SUBJECT_EXTRA = "com.example.alinnemes.NOTE TITLE";
    public static final String NOTE_BODY_EXTRA = "com.example.alinnemes.NOTE BODY";
    public static final String NOTE_ID_EXTRA = "com.example.alinnemes.NOTE ID";
    public static final String NOTE_PHOTOPATH_EXTRA = "com.example.alinnemes.NOTE_PHOTOPATH";
    public static final String NOTE_AUDIOPATH_EXTRA = "com.example.alinnemes.NOTE_AUDIOPATH";
    public static final String NOTE_VIDEOPATH_EXTRA = "com.example.alinnemes.NOTE_VIDEOPATH";
    public static final String NOTE_DATECREATED_EXTRA = "com.example.alinnemes.NOTE_DATECREATED";
    public static final String NOTE_FRAGMENT_TO_LOAD = "com.example.alinnemes.FRAGMENT_TO_LOAD";


    private static LruCache<String, Bitmap> mMemoryCache;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /* TODO: Modify the My Notes app to:
            Detect the user’s Location when he makes a Note and add the Date and Location info to the saved Note.
           Show a Google Map with all the Locations (similar to the Places Map in FB)
            Add a notification with the possibility to play, pause, stop
    */

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, FragmentToLaunch.ADD);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.exit_to_top);
            }
        });

      /*  // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };*/

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.\
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                openPreferredLocationInMap();
                return true;

            case R.id.action_settings:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String sharedSubjectText;
        String sharedBodyText;
        if (sharedText.contains("\n\n")) {
            String[] parts = sharedText.split("\n\n", 2);
            sharedSubjectText = parts[0];
            sharedBodyText = parts[1];
        } else if (sharedText.contains("\n")) {
            String[] parts = sharedText.split("\n");
            sharedSubjectText = parts[0];
            sharedBodyText = parts[1];
        } else {
            sharedSubjectText = new String("");
            sharedBodyText = sharedText;
        }
        if (sharedText != null) {
            Intent handleSharedTextIntent = new Intent(getApplicationContext(), EditActivity.class);
            handleSharedTextIntent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, FragmentToLaunch.SHARED);
            handleSharedTextIntent.putExtra(MainActivity.NOTE_SUBJECT_EXTRA, sharedSubjectText);
            handleSharedTextIntent.putExtra(MainActivity.NOTE_BODY_EXTRA, sharedBodyText);
            startActivity(handleSharedTextIntent);
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            // Update UI to reflect image being shared
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.alinnemes.mynotes/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.alinnemes.mynotes/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public enum FragmentToLaunch {VIEW, EDIT, ADD, SHARED}
}
