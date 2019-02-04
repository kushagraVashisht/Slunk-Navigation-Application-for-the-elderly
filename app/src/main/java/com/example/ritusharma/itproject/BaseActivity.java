package com.example.ritusharma.itproject;

/**
 * Created by ujaspatel on 19/09/18.
 */

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.support.v7.widget.Toolbar;

import com.example.ritusharma.itproject.SinchModules.SinchService;
import com.example.ritusharma.itproject.SinchModules.VideoCallScreenActivity;

public abstract class BaseActivity extends AppCompatActivity implements ServiceConnection {

    private SinchService.SinchServiceInterface mSinchServiceInterface;
    public Toolbar ongoingCallToolbar;

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);
        ongoingCallToolbar = findViewById(R.id.ongoingCallToolbar);

        ongoingCallToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallScreen();
            }
        });
    }

    /*********************************************************************************************/

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    /*********************************************************************************************/

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    /*********************************************************************************************/

    protected void onServiceConnected() {
        // for subclasses
    }

    /*********************************************************************************************/

    protected void onServiceDisconnected() {
        // for subclasses
    }

    /*********************************************************************************************/

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }

    /*********************************************************************************************/

    public void openCallScreen()
    {
        Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
        callScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(callScreen);
    }

    /*********************************************************************************************/

    public void showCallBar(){
        this.ongoingCallToolbar.setVisibility(View.VISIBLE);
    }

    /*********************************************************************************************/

    public void hideCallBar(){
        this.ongoingCallToolbar.setVisibility(View.GONE);
    }

    /*********************************************************************************************/

}