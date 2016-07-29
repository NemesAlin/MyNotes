package com.example.alinnemes.mynotes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.alinnemes.mynotes.R;
import com.example.alinnemes.mynotes.data.MyNotesDB;
import com.example.alinnemes.mynotes.model.Note;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Note> noteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MyNotesDB myNotesDB = new MyNotesDB(getApplicationContext());
        myNotesDB.open();
        noteArrayList = myNotesDB.getAllNotes();
        myNotesDB.close();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (int i = 0; i < noteArrayList.size(); i++) {
            String locationCoords = noteArrayList.get(i).getLocationCreated();
            char c = locationCoords.charAt(0);
            //DISPLAY ON THE MAP ONLY THE NOTES THAT HAVE COORDS
            if (Character.isDigit(c)) {
                String[] parts = locationCoords.split(",");
                LatLng latLng = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .visible(true)
                        .title(noteArrayList.get(i).getSubject()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }


//
    }
}
