package com.example.ritusharma.itproject.SinchModules;

/**
 * Created by ujaspatel on 19/09/18.
 */

import com.example.ritusharma.itproject.BaseActivity;

import com.example.ritusharma.itproject.R;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

public class IncomingCallScreenActivity extends BaseActivity {

    static final String TAG = IncomingCallScreenActivity.class.getSimpleName();
    private String mCallId;
    private String incomingCallType;
    private AudioPlayer mAudioPlayer;

    private ImageButton answer;
    private ImageButton decline;
    private Call call;
    private TextView remoteUser;
    private String incomingUserName;


    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_incoming);

        answer = (ImageButton) findViewById(R.id.answerButton);
        answer.setOnClickListener(mClickListener);

        decline = (ImageButton) findViewById(R.id.declineButton);
        decline.setOnClickListener(mClickListener);

        mAudioPlayer = new AudioPlayer(this);
        mAudioPlayer.playRingtone();
        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
    }

    /*********************************************************************************************/

    @Override
    /**
     * Once sinch service call is connected
     */
    protected void onServiceConnected() {

        call = getSinchServiceInterface().getCall(mCallId);
        incomingUserName = getIntent().getStringExtra(SinchService.CALLER_NAME);
        incomingCallType = getIntent().getStringExtra(SinchService.CALL_TYPE);

        if (call != null && remoteUser!=null) {

            //add a call listener class defined below to the call
            call.addCallListener(new SinchCallListener());
            remoteUser = (TextView) findViewById(R.id.incomingUser);
            remoteUser.setText(incomingUserName);

        } else {
            Log.e(TAG, "Started with invalid callId, aborting");
            finish();
        }
    }

    private OnClickListener mClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.answerButton:
                    answerClicked();
                    break;
                case R.id.declineButton:
                    declineClicked();
                    break;
            }
        }
    };

    /*********************************************************************************************/

    /**
     * When answer button is clicked, move to  respective CallScreenActivity
     */
    private void answerClicked() {

        //stop the ringtone
        mAudioPlayer.stopRingtone();

        //create a call object
        call = getSinchServiceInterface().getCall(mCallId);

        //answer the call if call object valid
        if (call != null) {
            try{
                call.answer();

                // move to appropriate call screen activity
                Intent callScreenActivity;
                if (this.incomingCallType.equals("VIDEO"))
                    callScreenActivity = new Intent(this, VideoCallScreenActivity.class);
                else
                    callScreenActivity = new Intent(this, VoiceCallScreenActivity.class);

                //pass the call ID and Caller Information
                callScreenActivity.putExtra(SinchService.CALL_ID, mCallId);
                callScreenActivity.putExtra("remote_user_name",incomingUserName);
                startActivity(callScreenActivity);
            }
            catch (MissingPermissionException e) {
                ActivityCompat.requestPermissions(this, new String[]{e.getRequiredPermission()}, 0);
            }

        } else {
            //finish activity if invalid call object
            finish();
        }
    }

    /*********************************************************************************************/

    /**
     * When answer button is clicked, hangup the call for both users
     */
    private void declineClicked() {

        //stop the ringtone
        mAudioPlayer.stopRingtone();

        //get the call object
        Call call = getSinchServiceInterface().getCall(mCallId);

        // if valid call object, hangup the call to terminate on both sides
        if (call != null) {
            call.hangup();
        }

        //finish this activity
        finish();
    }

    /*********************************************************************************************/

    /**
     * Class to implement VideoCall Listener
     */
    private class SinchCallListener implements VideoCallListener {

        /**
         * The method is evoked in case the call is not answered.
         * It stops the ringtone and records the reason for end of call
         * @param call the call object
         *
         */
        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended, cause: " + cause.toString());
            mAudioPlayer.stopRingtone();
            finish();
        }

        /*********************************************************************************************/

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
        }

        /*********************************************************************************************/

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
        }

        /*********************************************************************************************/

        @Override
        public void onShouldSendPushNotification(Call call, List pushPairs) {
            // If you want to send a push through your push provider  e.g. GCM
        }

        /*********************************************************************************************/

        @Override
        public void onVideoTrackAdded(Call call) {
            // If you want to display some kind of icon showing it's a video call
        }

        /*********************************************************************************************/
    }
}
