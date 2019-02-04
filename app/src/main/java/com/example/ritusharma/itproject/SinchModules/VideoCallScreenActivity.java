package com.example.ritusharma.itproject.SinchModules;

/**
 * Created by ujaspatel on 19/09/18.
 */


import com.example.ritusharma.itproject.R;
import com.example.ritusharma.itproject.*;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.calling.CallState;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class VideoCallScreenActivity extends BaseActivity {

    static final String TAG = VideoCallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;

    private TextView mCallDuration;
    private TextView mCallState;
    private TextView mCallerName;

    private ToggleButton videoOnOff;
    private Button endCallButton;


    private Toolbar toolbarCall;

    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            VideoCallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    /*********************************************************************************************/

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    /*********************************************************************************************/

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);

        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    /*********************************************************************************************/

    @Override
    protected void onPause() {

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(CALL_START_TIME, mCallStart);
        editor.putBoolean(ADDED_LISTENER,mAddedListener );
        mDurationTask.cancel();
        mTimer.cancel();

        // Commit to storage
        editor.apply();

        super.onPause();

    }

    /*********************************************************************************************/

    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mCallStart = preferences.getLong(CALL_START_TIME, 0);
        preferences.getBoolean(ADDED_LISTENER,false);
        mTimer = new Timer();
        mDurationTask = new VideoCallScreenActivity.UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);

        updateCallDuration();
        super.onResume();
    }

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_callscreen);

        mAudioPlayer = new AudioPlayer(this);
        mCallDuration = (TextView) findViewById(R.id.videoCallDuration);
        mCallerName = (TextView) findViewById(R.id.videoRemoteUser);
        mCallState = (TextView) findViewById(R.id.videoCallState);
        endCallButton = (Button) findViewById(R.id.videoEndcallButton);
        videoOnOff = (ToggleButton) findViewById(R.id.video_mode_toggle);

        endCallButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                endCall();
            }
        });

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }

        videoOnOff.setChecked(true);
        videoOnOff.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                addVideoViews();
            }
            else{
                removeVideoViews();
            }
        });


        View inflatedView = getLayoutInflater().inflate(R.layout.activity_base, null);
        toolbarCall= (Toolbar) inflatedView.findViewById(R.id.ongoingCallToolbar);


    }

    /*********************************************************************************************/

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener());
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }

        updateUI();
    }

    /*********************************************************************************************/

    //method to update video feeds in the UI
    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            mCallerName.setText(getIntent().getStringExtra("remote_user_name"));
            mCallState.setText(call.getState().toString());
            if (call.getState() == CallState.ESTABLISHED) {
                //when the call is established, addVideoViews configures the video to  be shown
                addVideoViews();
            }

            toolbarCall.setVisibility(View.VISIBLE);
        }
    }

    /*********************************************************************************************/

    //stop the timer when call is ended
    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
    }

    /*********************************************************************************************/

    //start the timer for the call duration here
    @Override
    public void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    /*********************************************************************************************/

    @Override
    public void onBackPressed() {
        //when back button is pressed
        startActivity(new Intent(this, MainActivity.class));
    }

    /*********************************************************************************************/

    //method to end the call
    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();

        toolbarCall.setVisibility(View.GONE);
    }

    /*********************************************************************************************/

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    /*********************************************************************************************/

    //method to update live duration of the call
    private void updateCallDuration() {
        if (mCallStart > 0) {
            mCallDuration.setText(formatTimespan(System.currentTimeMillis() - mCallStart));
        }
    }

    /*********************************************************************************************/

    //method which sets up the video feeds from the server to the UI of the activity
    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());

            ImageButton toggleLocalCamera = (ImageButton) findViewById(R.id.toggle_camera_btn);
            toggleLocalCamera.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //this toggles the front camera to rear camera and vice versa
                    Toast.makeText(VideoCallScreenActivity.this, "toggle pressed", Toast.LENGTH_LONG).show();
                    vc.toggleCaptureDevicePosition();
                }
            });

            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    /*********************************************************************************************/

    //removes video feeds from the app once the call is terminated
    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            LinearLayout view = (LinearLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            RelativeLayout localView = (RelativeLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }

    /*********************************************************************************************/

    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
            Toast.makeText(VideoCallScreenActivity.this, "Call Ended", Toast.LENGTH_LONG).show();
            endCall();

        }

        /*********************************************************************************************/

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            mCallState.setText(call.getState().toString());
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
        }

        /*********************************************************************************************/

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
        }

        /*********************************************************************************************/

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        /*********************************************************************************************/

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }

        /*********************************************************************************************/
    }
}