package com.example.alinnemes.mynotes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.alinnemes.mynotes.data.MyNotesDBAdapter;
import com.example.alinnemes.mynotes.model.Note;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HashMap<String, String> notesHashMap;
    private ArrayList<Note> noteArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //get from intent the hashmap containing notes;
//        Intent intent = getIntent();
//        notesHashMap = (HashMap<String, String>)intent.getSerializableExtra("map");
//        Log.v("HashMapTest", notesHashMap.get("Location note"));

        MyNotesDBAdapter myNotesDBAdapter = new MyNotesDBAdapter(getApplicationContext());
        myNotesDBAdapter.open();
        noteArrayList = myNotesDBAdapter.getAllNotes();
        myNotesDBAdapter.close();


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

            if (Character.isDigit(c)) {
                String[] parts = locationCoords.split(",");
                LatLng latLng = new LatLng(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]))
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
