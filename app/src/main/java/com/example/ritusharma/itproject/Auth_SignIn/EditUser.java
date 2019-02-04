package com.example.ritusharma.itproject.Auth_SignIn;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ritusharma.itproject.MainActivity;
import com.example.ritusharma.itproject.R;
import com.example.ritusharma.itproject.BaseActivity;
import com.example.ritusharma.itproject.SinchModules.SinchService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

/**
 * Allows user to edit their own details (or add them if they are a new user)
 */
public class EditUser extends BaseActivity implements SinchService.StartFailedListener {
    /* Member variables */
    private Button fab_Done;
    private EditText fname;
    private EditText lname;
    private EditText dispname;
    private RadioGroup carer_stat;
    private DatabaseReference firDB_users_ref;
    private FirebaseUser fir_user;
    private Switch sinchLoginSwitch;
    private ProgressDialog mSpinner;

    /*********************************************************************************************/

    /**
     * Gets all of the objects from the screen, and sets their values if they exist
     * in the DB.
     * 
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        /* Get firebase user and activity elements */
        firDB_users_ref = FirebaseDatabase.getInstance().getReference(getString(R.string.User_Tag));
        fir_user = FirebaseAuth.getInstance().getCurrentUser();

        fname = (EditText) findViewById(R.id.et_userFname);
        lname = (EditText) findViewById(R.id.et_userLname);
        dispname = (EditText) findViewById(R.id.et_userdispName);
        carer_stat = (RadioGroup) findViewById(R.id.rg_carerstat);
        sinchLoginSwitch = (Switch) findViewById(R.id.sinchSwitch);

        /**
         * Executes when the Floating Action Button is clicked, 
         * writes a new user to the DB
         */
        fab_Done = (Button) findViewById(R.id.fab_Done);
        fab_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Which side of the carer-link is this? */
                CaringStatus care;
                care = (carer_stat.getCheckedRadioButtonId() == R.id.rbtn_cared_for) 
                        ? CaringStatus.CARED_FOR
                        : CaringStatus.CARER;

                /* Writes the user to the DB */
                Intent i = getIntent();
                writeUser(i.getStringExtra("User/" + getString(R.string.usr_uid)), 
                    dispname.getText().toString(),
                    fname.getText().toString(), 
                    lname.getText().toString(), care,
                    i.getStringExtra("User/" + getString(R.string.usr_mobNum))
                );

                try {
                    /* Update the User's Firebase Name */
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(dispname.getText().toString()).build();

                    user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        /* Log if there is success */
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(getString(R.string.User_Tag), "User profile updated.");
                            }
                        }
                    });
                } catch (Exception e) {
                    // If we fail, we fail gracefully, it's NBD
                }

                /* Go to main activity */
                loginSinch();
                Intent mainActivity = new Intent(EditUser.this, MainActivity.class);
                startActivity(mainActivity);
            }
        });

        /* Toggle Since Login on the toggle switch */
        sinchLoginSwitch.setChecked(true);
        sinchLoginSwitch.setText("Tap to get Offline");
        sinchLoginSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                loginSinch();
                sinchLoginSwitch.setText("Tap to get Offline");
                Toast.makeText(getApplicationContext(), "Getting Online", Toast.LENGTH_SHORT).show();
            } else {
                logoutSinch();
                sinchLoginSwitch.setText("Tap to get Online");
                Toast.makeText(getApplicationContext(), "Getting Offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*********************************************************************************************/

    /**
     * Sets the text of the objects to the current values in the DB (if user exists)
     */
    private void setObjectText() {

        try {
            firDB_users_ref.child(fir_user.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                /**
                 * Grab the user's details once and set the activity objects to the
                 * corresponding text
                 */
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(getString(R.string.usr_fname)).getValue() != null) {
                        fname.setText(dataSnapshot.child(getString(R.string.usr_fname)).getValue(String.class));
                        lname.setText(dataSnapshot.child(getString(R.string.usr_lname)).getValue(String.class));
                        dispname.setText(dataSnapshot.child(getString(R.string.usr_dispname)).getValue(String.class));

                        if (dataSnapshot.child(getString(R.string.usr_caringstat)).getValue(CaringStatus.class)
                                .equals(CaringStatus.CARER)) {
                            RadioButton rb_carer = findViewById(R.id.rbtn_carer);
                            rb_carer.setChecked(true);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError dbErr) {
                    Toast.makeText(getApplicationContext(), dbErr.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // If the data isn't there, then the user mustn't exist, that's fine :D
        }
    }

    /*********************************************************************************************/

    /**
     * When the activity begins, set the activity object's text to the user's details
     */
    @Override
    public void onStart() {
        super.onStart();
        setObjectText();
    }

    /*********************************************************************************************/

    /**
     * Writes a new user to the DB
     * 
     * @param userID     - The ID of the user to add to the DB (Firebase Generated)
     * @param name       - The user's chosen username
     * @param fname      - The user's first name
     * @param lname      - The user's last name
     * @param caringStat - The user's caring status
     * @param mobNum     - The user's mobile number (as a string)
     */
    public void writeUser(String userID, String name, String fname, String lname, CaringStatus caringStat,
            String mobNum) {
        UserObj user = new UserObj(userID, name, fname, lname, caringStat, mobNum);
        firDB_users_ref.child(userID).setValue(user);
    }

    /*********************************************************************************************/

    /* ***** Sinch Stuff ******/
    
    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    /**
     * When activity goes out of focus
     */
    protected void onPause() {

        //dismiss the spinner if activity paused
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    /*********************************************************************************************/

    @Override
    /**
     * Catches any errors while logging user into sinch service
     * @param error SinchErrow while logging in
     */
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, "Cannot Log in with Sinch", Toast.LENGTH_LONG).show();
        Log.d("SinchStartFailed", error.toString());
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    /*********************************************************************************************/

    // Invoked when just after the service is connected with Sinch
    @Override
    public void onStarted() {
        //dismiss the spinner if connected to sinch
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    /*********************************************************************************************/

    // Login is Clicked to manually to connect to the Sinch Service
    private void loginSinch() {

        //this will be the user-id
        String userName = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        }
    }

    /*********************************************************************************************/

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in with Sinch");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }

    /*********************************************************************************************/

    private void logoutSinch() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
    }



}

/*********************************************************************************************/
