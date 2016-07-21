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

public class MainActivity extends AppCompatActivity {

    public static final String NOTE_SUBJECT_EXTRA = "com.example.alinnemes.NOTE TITLE";
    public static final String NOTE_BODY_EXTRA = "com.example.alinnemes.NOTE BODY";
    public static final String NOTE_ID_EXTRA = "com.example.alinnemes.NOTE ID";
    public static final String NOTE_PATH_EXTRA = "com.example.alinnemes.NOTE_PATH";
    public static final String NOTE_FRAGMENT_TO_LOAD = "com.example.alinnemes.FRAGMENT_TO_LOAD";
    private static LruCache<String, Bitmap> mMemoryCache;

    public enum FragmentToLaunch {VIEW, EDIT, ADD, SHARED}

    /* TODO: Modify the My Notes app to:
            * Support adding an image to the note by
            * Taking an image with the device’s Camera Create your own Custom Camera Canvas Select an existing image from the storage
    */

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
                intent.putExtra(MainActivity.NOTE_FRAGMENT_TO_LOAD, MainActivity.FragmentToLaunch.ADD);
                startActivity(intent);
            }
        });

        // Get max available VM memory, exceeding this amount will throw an
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
        };

    }

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.\
        menu.clear();
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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
}
