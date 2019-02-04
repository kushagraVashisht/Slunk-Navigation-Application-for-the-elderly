package com.example.ritusharma.itproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.ritusharma.itproject.Auth_SignIn.PhoneAuthActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LocationActivity extends BaseActivity {

    private static final int PERMISSIONS_REQUEST = 1;
    private static final String TAG = LocationActivity.class.getSimpleName();
    private FirebaseUser currUser;

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startService();
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSIONS_REQUEST);
        }
    }

    /*********************************************************************************************/

    private void startTrackerService() {
        startService(new Intent(this, LocationService.class));
        finish();
    }

    /*********************************************************************************************/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }

    /*********************************************************************************************/

    private void startService() {
        currUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currUser == null) {
            startActivity(new Intent(this, PhoneAuthActivity.class));
        } else {
            startTrackerService();
        }

        Log.d(TAG, "firebase auth success");
    }

    /*********************************************************************************************/
}
