package com.example.ritusharma.itproject.Auth_SignIn;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ritusharma.itproject.BaseActivity;
import com.example.ritusharma.itproject.SinchModules.*;
import com.example.ritusharma.itproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sinch.android.rtc.SinchError;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends BaseActivity implements
        View.OnClickListener, SinchService.StartFailedListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private String thisUserId;
    private String thisUserPhNum;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mSignOutButton;

    private ProgressDialog mSpinner;

    /*********************************************************************************************/

    /**
     * The method will set the Bundle null when activity get starts first time and it will get in use when
     * activity orientation get changed
     * @param savedInstanceState - Saves the state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        thisUserId = null;
        thisUserPhNum = null;

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
            loginSinch();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_PHONE_STATE},100);
        }

        // Assign views
        mPhoneNumberViews = findViewById(R.id.phone_auth_fields);
        mSignedInViews = findViewById(R.id.signed_in_buttons);

        mStatusText = findViewById(R.id.status);
        mDetailText = findViewById(R.id.detail);

        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mVerificationField = findViewById(R.id.field_verification_code);

        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mSignOutButton = findViewById(R.id.sign_out_button);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            /*********************************************************************************************/

            /**
             * This method is called when the verification has been completed
             * @param credential - Phone authorization credentials
             */
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            /*********************************************************************************************/

            /**
             * This method is called when the verification fails
             * @param e - Firebase exception
             */
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            /*********************************************************************************************/

            /**
             * This method sends the verification code to the user
             * @param verificationId - String verification Id
             * @param token - Token is generated once the code gets sent
             */
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    /*********************************************************************************************/

    /**
     * This method does nothing
     */
    @Override
    public void onBackPressed() {
        // Do Nothing
    }

    /*********************************************************************************************/

    // [START on_start_check_user]

    /**
     * This method checks if the user is signed in(non-null) and updates the UI accordingly
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    /*********************************************************************************************/

    /**
     * The saved state of the application
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    /*********************************************************************************************/

    /**
     * When the application is restored, the app should keep the user signed in
     * @param savedInstanceState
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
        loginSinch();
    }

    /*********************************************************************************************/

    /**
     * This method starts the phone number verification
     * @param phoneNumber - String phone number
     */
    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    /*********************************************************************************************/

    /**
     * This method verifies the phone number with the code
     * @param verificationId - The verification ID
     * @param code - The code generated
     */
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    /*********************************************************************************************/

    /**
     * This method re-sends the verification code
     * @param phoneNumber - Sends the code to the following phone number
     * @param token - A token is generated
     */
    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    /*********************************************************************************************/
    // [START sign_in_with_phone]

    /**
     * This method starts the sign-in process
     * @param credential - Phone authorization credentials
     */
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                            updateUI(STATE_SIGNIN_SUCCESS, user);
                            // [END_EXCLUDE]

                            //Login into sinch manually if not already signed in
                            loginSinch();

                            thisUserId = user.getUid();
                            thisUserPhNum = user.getPhoneNumber();
                            OpenNewUserActivity();


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    /*********************************************************************************************/

    /**
     * When the user signs out
     */
    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    /*********************************************************************************************/

    /**
     * Updating the UI of the current user
     */
    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    /*********************************************************************************************/

    /**
     * Updating the UI
     */
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    /*********************************************************************************************/

    /**
     * Updating the UI
     */
    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    /*********************************************************************************************/

    /**
     * Updating the UI
     */
    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    /*********************************************************************************************/

    /**
     * Updating the UI
     */
    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mDetailText.setText(R.string.status_code_sent);
                mStartButton.setText("Code Sent");
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }
                }
                break;

            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
            mPhoneNumberViews.setVisibility(View.GONE);
            mSignedInViews.setVisibility(View.VISIBLE);

            enableViews(mPhoneNumberField, mVerificationField);
            mPhoneNumberField.setText(null);
            mVerificationField.setText(null);

            mStatusText.setText(R.string.signed_in);
            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            thisUserId = user.getUid();
            thisUserPhNum = user.getPhoneNumber();
            OpenNewUserActivity();
        }
    }

    /*********************************************************************************************/

    /**
     * This method validates the phone number
     */
    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    /*********************************************************************************************/

    /**
     *This method is used for enabling views
     */
    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    /*********************************************************************************************/

    /**
     *This method is used for disabling views
     * @param views
     */
    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    /*********************************************************************************************/

    /**
     *This method handles on-click events
     * @param view - Views
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }

                startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;

            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    /*********************************************************************************************/

    /**
     * Opens the new user activity
     */
    public void OpenNewUserActivity() {
        Intent i = new Intent(PhoneAuthActivity.this, EditUser.class);
        i.putExtra("User/"+getString(R.string.usr_uid), thisUserId);
        i.putExtra("User/"+getString(R.string.usr_mobNum), thisUserPhNum);
        Log.d("USER", thisUserId);
        Log.d("USER", thisUserPhNum);
        startActivity(i);
    }

    /*SINCH LOGIN METHODS*/

    /*********************************************************************************************/

    @Override
    /**
     * Evoked when sinch service is connected
     */
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
    }

    /*********************************************************************************************/

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

    /**
     * Invoked when just after the service is connected with Sinch
     */
    @Override
    public void onStarted() {
        // Do Nothing once service started
    }

    /*********************************************************************************************/

    /**
     * Login Manually into Sinch Server
     */
    private void loginSinch() {

        //this will be the user-id
        String userName = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        }
    }

    /*********************************************************************************************/

    /**
     * To show a spinner while Logging in user with Sinch Server
     */
    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in with Sinch");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}

/*********************************************************************************************/
