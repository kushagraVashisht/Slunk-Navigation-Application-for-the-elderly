package com.example.ritusharma.itproject;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class DisplayLocation extends BaseActivity {

    private static final String TAG = DisplayLocation.class.getSimpleName();

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginToFirebase();
    }

    /*********************************************************************************************/

    private void loginToFirebase() {
        FirebaseAuth.getInstance().getCurrentUser();
        subscribeToUpdates();
        Log.d(TAG, "firebase auth success");

    }

    /*********************************************************************************************/

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            /*********************************************************************************************/

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            /*********************************************************************************************/

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            /*********************************************************************************************/

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            /*********************************************************************************************/

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }

            /*********************************************************************************************/
        });
    }

    /*********************************************************************************************/

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
    }

    /*********************************************************************************************/
}