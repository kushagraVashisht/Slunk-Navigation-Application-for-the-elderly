package com.example.ritusharma.itproject.ChatModules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ritusharma.itproject.Auth_SignIn.CaringStatus;
import com.example.ritusharma.itproject.Auth_SignIn.EditUser;
import com.example.ritusharma.itproject.Auth_SignIn.FriendsListActivity;
import com.example.ritusharma.itproject.Auth_SignIn.PhoneAuthActivity;
import com.example.ritusharma.itproject.Auth_SignIn.UserObj;
import com.example.ritusharma.itproject.BaseActivity;
import com.example.ritusharma.itproject.Calendar.CalendarLayout;
import com.example.ritusharma.itproject.MainActivity;
import com.example.ritusharma.itproject.R;
import com.example.ritusharma.itproject.SinchModules.SinchService;
import com.example.ritusharma.itproject.SinchModules.VideoCallScreenActivity;
import com.example.ritusharma.itproject.SinchModules.VoiceCallScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.MissingPermissionException;
import com.sinch.android.rtc.calling.Call;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MSG_LIMIT = 40;
    /* Firebase Link */
    private DatabaseReference firDB_messages;

    /* Users in this chat */
    private UserObj sender;
    private UserObj recvr;

    /* Activity UI Elements */
    private EditText messagebox;
    private FloatingActionButton send_msg;

    /* Firebase setup code */
    private MessagesAdapter<Message> adapter;
    ArrayList<Message> messageList;

    private Map<String, String> callHeaders;
    private Call call;

    /*********************************************************************************************/
    /**
     * Loads the chat on start
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayChatMessage();
    }

    /**
     * Kills the adapter on stop
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Destroy the activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();

        /* Get Users in this chat */
        sender = new UserObj(i.getStringExtra("sender/" + getString(R.string.usr_uid)),
                i.getStringExtra("sender/" + getString(R.string.usr_dispname)),
                i.getStringExtra("sender/" + getString(R.string.usr_fname)),
                i.getStringExtra("sender/" + getString(R.string.usr_lname)),
                CaringStatus.valueOf(i.getStringExtra("sender/" + getString(R.string.usr_caringstat))),
                i.getStringExtra("sender/" + getString(R.string.usr_mobNum)));

        recvr = new UserObj(i.getStringExtra("revcr/" + getString(R.string.usr_uid)),
                i.getStringExtra("revcr/" + getString(R.string.usr_dispname)),
                i.getStringExtra("revcr/" + getString(R.string.usr_fname)),
                i.getStringExtra("revcr/" + getString(R.string.usr_lname)),
                CaringStatus.valueOf(i.getStringExtra("revcr/" + getString(R.string.usr_caringstat))),
                i.getStringExtra("revcr/" + getString(R.string.usr_mobNum)));

        /* Get DB section */
        firDB_messages = FirebaseDatabase.getInstance().getReference(getString(R.string.Messages_Tag));

        /* Naming the title bar same as name of user */
        getSupportActionBar().setTitle(recvr.getFname() + " " + recvr.getLname());

        /* Get Activity Elements */
        messagebox = findViewById(R.id.et_input);
        send_msg = findViewById(R.id.fab_sendMessage);

        /**
         * Called by the Floating Action Button, writes a new message to the database
         */
        send_msg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Create a new message
                Message message = new Message(sender.getUID(), recvr.getUID(), messagebox.getText().toString());

                // Write the message
                writeMessage(message);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /*********************************************************************************************/

    /**
     * Writes a message to the database
     *
     * @param message - The message to send
     */
    private void writeMessage(Message message) {

        firDB_messages.child(sender.getUID()).push().setValue(message);
        firDB_messages.child(recvr.getUID()).push().setValue(message);
        messagebox.setText("");
    }

    /*********************************************************************************************/

    /**
     * Gets the chat messages from the DB and puts them into the listView.
     */
    private void displayChatMessage() {

        final ListView listOfMessage = findViewById(R.id.lv_old_messages);
        messageList = new ArrayList<>();
        adapter = new MessagesAdapter<>(getApplicationContext(), messageList, sender, recvr);

        // Attach a listener to read the data at our messages reference
        firDB_messages.child(sender.getUID()).limitToLast(MSG_LIMIT).addValueEventListener(new ValueEventListener() {

            /**
             * Gets the messages from the DB and filters out the ones that aren't related to
             * the selected receiver.
             *
             * @param usr_messages - DataSnapshot, the Database snap.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot usr_messages) {
                /* Clear the current messages and get the new set */
                adapter.clear();
                HashMap<String,Message> messageMap = extract_messages(usr_messages);
                messageList = filterUserMessages(messageMap, sender, recvr);
                adapter.addAll(messageList);

                // Select the last row so it will scroll into view...
                try {
                    listOfMessage.setSelection(adapter.getCount() - 1);
                } catch (Exception e) {
                    // Do Nothing if list is empty
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /* Set the listview's adapter */
        listOfMessage.setAdapter(adapter);
    }

    /*********************************************************************************************/

    /**
     * Converts Firebase DataSnapshot to a HashMap of Messages.
     * @param usr_messages - The FireBase DataSnapShot of Messages
     * @return A HashMap of the Messages with they FireBase key as the Hash Key.
     */
    private HashMap<String, Message> extract_messages(DataSnapshot usr_messages) {

        HashMap<String, Message> messageMap = new HashMap<>();

        for (DataSnapshot ds : usr_messages.getChildren()) {
            messageMap.put(ds.getKey(), ds.getValue(Message.class));
        }

        return messageMap;
    }

    /*********************************************************************************************/

    /**
     * Creates an ArrayList of the messages between the sender and receiver
     * selected.
     *
     * @param usr_messages - DataSnapshot, The Database snapshot of the Messages
     *                     related to the current user.
     * @return - ArrayList< Messages > - The messages between the 2 users selected
     */
    public static ArrayList<Message> filterUserMessages(HashMap usr_messages, UserObj sender, UserObj recvr) {
        ArrayList<Message> filteredMessages = new ArrayList<>();

        /*
         * Goes through all of the messages from the sender, makes a message object from
         * the relevant ones between the sender and receiver, and adds it to the List.
         */
        for (Object value : usr_messages.values()) {
            Message message = (Message)value;
            try {
                String msg_sender = message.getSenderID();
                String msg_recvr = message.getRecverID();

                /* If the UserID's match this chat room, add them to the ArrayList */
                if (msg_sender.equals(recvr.getUID()) && msg_recvr.equals(sender.getUID())
                        || msg_sender.equals(sender.getUID()) && msg_recvr.equals(recvr.getUID())) {

                    filteredMessages.add(message);
                }

            } catch (Exception e) {
                // Do Nothing
            }
        }
        return filteredMessages;
    }



    /*********************************************************************************************/

    /**
     * To implement clickListeners to menu items
     * @param item - menu item to be clicked
     * @return boolean - if successfully created clickListeners for menu items
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.voiceCallBtn:
                voiceCallButtonClicked();
                return true;
            case R.id.videoCallBtn:
                videoCallButtonClicked();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*********************************************************************************************/

    @Override
    /**
     * @param menu - takes a menu_layout and inflates it to current menu
     * @return boolean value if successful in creating menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_room_appbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*********************************************************************************************/

    /**
     * To place the Video Call to the user in the Chat Room
     */
    private void videoCallButtonClicked() {

        // name of the receiver
        String receiverName = recvr.getUID();

        assert (!receiverName.isEmpty());

        try {

            //pass information about who is calling, with the call
            callHeaders = new HashMap<>();
            callHeaders.put("CallerName", sender.getFname() + " " + sender.getLname());
            callHeaders.put("CallType", "video");

            //request a call with receiver and caller information
            call = getSinchServiceInterface().callUserVideo(receiverName, callHeaders);
            String callId = call.getCallId();

            //move to videoCallingScreen to see the status of call
            //pass the call information to be used in videocalling screen
            Intent videoCallScreen = new Intent(this, VideoCallScreenActivity.class);
            videoCallScreen.putExtra(SinchService.CALL_ID, callId);
            videoCallScreen.putExtra("remote_user_name", recvr.getFname() + " " + recvr.getLname());
            startActivity(videoCallScreen);

        }

        //catch any Exceptions because of missing permissions, and  Get them again
        catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[] { e.getRequiredPermission() }, 0);
        }

        //catch any Exceptions in case current user is not logged in with sinch service
        catch (NullPointerException e) {
            Toast.makeText(this, "Sinch Service Not Logged in", Toast.LENGTH_LONG).show();
        }

    }

    /*********************************************************************************************/

    /**
     * To place the Voice Call to the user in the Chat Room
     */
    private void voiceCallButtonClicked() {

        // name of the receiver
        String receiverName = recvr.getUID();

        assert (!receiverName.isEmpty());

        try {

            //pass information about who is calling, with the call
            callHeaders = new HashMap<>();
            callHeaders.put("CallerName", sender.getFname() + " " + sender.getLname());
            callHeaders.put("CallType", "voice");

            //request a call with receiver and caller information
            call = getSinchServiceInterface().callUser(receiverName, callHeaders);
            String callId = call.getCallId();

            //move to voice-calling screen to see the status of call
            //pass the call information to be used in voice-calling screen
            Intent voiceCallScreen = new Intent(this, VoiceCallScreenActivity.class);
            voiceCallScreen.putExtra(SinchService.CALL_ID, callId);
            voiceCallScreen.putExtra("remote_user_name", recvr.getFname() + " " + recvr.getLname());
            startActivity(voiceCallScreen);

        }

        //catch any Exceptions because of missing permissions, and  Get them again
        catch (MissingPermissionException e) {
            ActivityCompat.requestPermissions(this, new String[] { e.getRequiredPermission() }, 0);
        }

        //catch any Exceptions in case current user is not logged in with sinch service
        catch (NullPointerException e) {
            Toast.makeText(this, "Sinch Service not Logged in", Toast.LENGTH_LONG).show();
        }

    }

    /*********************************************************************************************/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* What to open, selection dependant */
        switch (id) {
            case R.id.nav_home:
                OpenHomeScreen();
                break;
            case R.id.nav_calendar:
                OpenCalendar();
                break;
            case R.id.nav_friends:
                OpenFriendsList();
                break;
            case R.id.nav_mydetails:
                OpenEditUser();
                break;
            case R.id.nav_logout:
                Logout();
                break;
            default:
                /* Do Nothing */
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*********************************************************************************************/

    /**
     * Opens the MainActivity
     */
    private void OpenHomeScreen() {
        startActivity(new Intent(this, MainActivity.class));
    }

    /*********************************************************************************************/

    /**
     * Opens the Activity that shows the user's friend list
     */
    public void OpenFriendsList() {

        /* Puts the user's details into the next activity if it can get them */
        Intent i = new Intent(this, FriendsListActivity.class);

        i.putExtra("User/" + getString(R.string.usr_uid), sender.getUID());
        i.putExtra("User/" + getString(R.string.usr_fname), sender.getFname());
        i.putExtra("User/" + getString(R.string.usr_lname), sender.getLname());
        i.putExtra("User/" + getString(R.string.usr_dispname), sender.getUsername());
        i.putExtra("User/" + getString(R.string.usr_mobNum), sender.getMobNum());
        i.putExtra("User/" + getString(R.string.usr_caringstat), sender.getCaringStat());

        startActivity(i);

    }


    /*********************************************************************************************/

    /**
     * Opens the calendar view
     */
    public void OpenCalendar() {
        startActivity(new Intent(this, CalendarLayout.class));
    }

    /*********************************************************************************************/

    /**
     * Opens the login for the VideoCall module
     */
    public void OpenCallScreen() {
        Intent callScreen = new Intent(this, VideoCallScreenActivity.class);
        callScreen.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(callScreen);
    }

    /*********************************************************************************************/

    /**
     * Opens the user details screen, allowing the user to change their details
     */
    public void OpenEditUser() {
        /* Sends the user details to the new activity */
        Intent i = new Intent(this, EditUser.class);

        i.putExtra("User/" + getString(R.string.usr_uid), sender.getUID());
        i.putExtra("User/" + getString(R.string.usr_fname), sender.getFname());
        i.putExtra("User/" + getString(R.string.usr_lname), sender.getLname());
        i.putExtra("User/" + getString(R.string.usr_dispname), sender.getUsername());
        i.putExtra("User/" + getString(R.string.usr_mobNum), sender.getMobNum());
        i.putExtra("User/" + getString(R.string.usr_caringstat), sender.getCaringStat());

        startActivity(i);
    }

    /*********************************************************************************************/

    /**
     * Opens the main login window for the user
     */
    public void OpenMainLogin() {
        startActivity(new Intent(this, PhoneAuthActivity.class));
    }

    /*********************************************************************************************/

    /**
     * Logs out the user session and opens the sign-in page
     */
    public void Logout() {
        FirebaseAuth.getInstance().signOut();
        sinchLogout();
        OpenMainLogin();
    }

    /*********************************************************************************************/

    /**
     * Logs out Sinch Services
     */
    private void sinchLogout() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }

    /*********************************************************************************************/
}

/*********************************************************************************************/

