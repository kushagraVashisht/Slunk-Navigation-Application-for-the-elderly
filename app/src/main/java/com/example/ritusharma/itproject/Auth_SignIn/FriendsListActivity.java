package com.example.ritusharma.itproject.Auth_SignIn;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ritusharma.itproject.BaseActivity;
import com.example.ritusharma.itproject.Calendar.CalendarLayout;
import com.example.ritusharma.itproject.ChatModules.ChatRoomActivity;
import com.example.ritusharma.itproject.MainActivity;
import com.example.ritusharma.itproject.R;
import com.example.ritusharma.itproject.SinchModules.VideoCallScreenActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendsListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference firDB_users;
    private DatabaseReference firDB_friends;
    private Intent c_intent;
    private ListView lv_userList;
    private FloatingActionButton fab_add_user;
    private UserObj currUser;
    private UserObjAdapter adapter;
    private ArrayList<UserObj> userList;
    private ArrayList<String> users_friends_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name) + " - " + getString(R.string.title_friends));

        /* Get DB references */
        firDB_users = FirebaseDatabase.getInstance().getReference(getString(R.string.User_Tag));
        firDB_friends = FirebaseDatabase.getInstance().getReference(getString(R.string.Friends_Tag));
        c_intent = getIntent();
        currUser = new UserObj(
                c_intent.getStringExtra("User/" + getString(R.string.usr_uid)),
                c_intent.getStringExtra("User/" + getString(R.string.usr_dispname)),
                c_intent.getStringExtra("User/" + getString(R.string.usr_fname)),
                c_intent.getStringExtra("User/" + getString(R.string.usr_lname)),
                CaringStatus.valueOf(c_intent.getStringExtra("User/" + getString(R.string.usr_caringstat))),
                c_intent.getStringExtra("User/" + getString(R.string.usr_mobNum))
        );

        /* Get References to objects on the page */
        lv_userList = (ListView) findViewById(R.id.lv_userList);

        /* Called when the user clicks one of the user names! */
        lv_userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Opens a ChatRoom page with the messages with the clicked user displaying.
             *
             * @param adapter  - Adatpter View
             * @param v        - View
             * @param position - int, The positions clicked in the list
             * @param id       - long, the id of the item clicked
             */
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {

                /* The ListView holds references to UserObjs, so get the one we clicked on */
                UserObj item = (UserObj) adapter.getItemAtPosition(position);

                Intent n_intent = new Intent(FriendsListActivity.this, ChatRoomActivity.class);
                /* Pass the Recvr's details that we clicked to the new activity */
                n_intent.putExtra("revcr/" + getString(R.string.usr_uid), item.getUID());
                n_intent.putExtra("revcr/" + getString(R.string.usr_fname), item.getFname());
                n_intent.putExtra("revcr/" + getString(R.string.usr_lname), item.getLname());
                n_intent.putExtra("revcr/" + getString(R.string.usr_dispname), item.getUsername());
                n_intent.putExtra("revcr/" + getString(R.string.usr_mobNum), item.getMobNum());
                n_intent.putExtra("revcr/" + getString(R.string.usr_caringstat), item.getCaringStat());

                /* Pass the sender's details to the new activity */
                n_intent.putExtra("sender/" + getString(R.string.usr_uid), currUser.getUID());
                n_intent.putExtra("sender/" + getString(R.string.usr_fname), currUser.getFname());
                n_intent.putExtra("sender/" + getString(R.string.usr_lname), currUser.getLname());
                n_intent.putExtra("sender/" + getString(R.string.usr_dispname), currUser.getUsername());
                n_intent.putExtra("sender/" + getString(R.string.usr_mobNum), currUser.getMobNum());
                n_intent.putExtra("sender/" + getString(R.string.usr_caringstat), currUser.getCaringStat());
                startActivity(n_intent);
            }
        });

        /**
         * Takes you to the "Add a Friend" page
         */
        fab_add_user = (FloatingActionButton) findViewById(R.id.fab_add_user);
        fab_add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* Send the User details so the app doesn't have to pull them from
                 * FireBase again */
                Intent n_intent = new Intent(FriendsListActivity.this, AddFriendActivity.class);
                n_intent.putExtra("User/" + getString(R.string.usr_uid), currUser.getUID());
                n_intent.putExtra("User/" + getString(R.string.usr_fname), currUser.getFname());
                n_intent.putExtra("User/" + getString(R.string.usr_lname), currUser.getLname());
                n_intent.putExtra("User/" + getString(R.string.usr_dispname), currUser.getUsername());
                n_intent.putExtra("User/" + getString(R.string.usr_mobNum), currUser.getMobNum());
                n_intent.putExtra("User/" + getString(R.string.usr_caringstat), currUser.getCaringStat());
                startActivity(n_intent);
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

    /**
     * Loads the User List on the start of the activity
     */
    @Override
    protected void onStart() {
        super.onStart();
        displayUserList();
    }

    /*********************************************************************************************/

    /**
     * Displays the list of Firebase Users
     *
     * Code based on here:
     * https://github.com/eddydn/ChatApp/blob/master/app/src/main/java/dev/edmt/chatapp/MainActivity.java
     */
    private void displayUserList() {

        userList = new ArrayList<>();
        adapter = new UserObjAdapter<>(getApplicationContext(), userList);

        users_friends_list = new ArrayList<>();

        /*
         * Get the list of users who this user has added who has also added them. Start
         * by getting out the Friends hook from firebase.
         */
        firDB_friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot friendsSnapshot) {
                HashMap<String, ArrayList<String>> friendsMap = extractFriendsNode(friendsSnapshot);
                users_friends_list = getMutualFriends(friendsMap, c_intent.getStringExtra("User/" + getString(R.string.usr_uid)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do Nothing
            }
        });

        /* Attach a listener to read the data at our users reference */
        firDB_users.addValueEventListener(new ValueEventListener() {

            /**
             * Gets the user list from the DB and filters them in some way
             *
             * @param users - DataSnapshot, the Database snap.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot users) {
                /* Clear the current users and get the new set */
                adapter.clear();
                HashMap<String, UserObj> userMap = extractUsers(users);
                userList = filterUsers(userMap, users_friends_list);
                adapter.addAll(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        /* Update the list adapter */
        lv_userList.setAdapter(adapter);
    }

    /*********************************************************************************************/

    /**
     * Extracts the Users from the FireBase DataSnapshot into a HashMap where the keys are the
     * FireBase User Keys
     * @param user_snap - The FireBase DataSnapshop of the Users
     * @return HashMap of the Users
     */
    protected static HashMap<String, UserObj> extractUsers(DataSnapshot user_snap) {

        HashMap<String, UserObj> messageMap = new HashMap<>();

        for (DataSnapshot ds : user_snap.getChildren()) {
            messageMap.put(ds.getKey(), ds.getValue(UserObj.class));
        }

        return messageMap;
    }

    /*********************************************************************************************/

    /**
     * Gets the List of User's Friends from the FireBase DataSnapshot
     *
     * @param user_friends - FireBase DataSnapShot of the User's friend List
     * @return ArrayList of Friend UserIDs (as Strings)
     */
    protected static ArrayList<String> extractFriendIDs(DataSnapshot user_friends) {

        ArrayList<String> friends = new ArrayList<>();

        for (DataSnapshot ds : user_friends.getChildren()) {
            friends.add(ds.getValue(String.class));
        }

        return friends;
    }

    /*********************************************************************************************/

    /**
     * Gets the User's friends list by filtering only the friends they have added, whomst
     * have also added the user.
     *
     * @param friendsMap - The DataSnapshot of the Friends hook in FireBase.
     * @param currUID - The UserID of the Current User.
     * @return teh ArrayList of the user's confirmed friends.
     */
    public static ArrayList<String> getMutualFriends(HashMap<String, ArrayList<String>> friendsMap, String currUID) {

        ArrayList<String> mutualFriends = new ArrayList<>(2);

        /*
         * Search through this user's friend requests, and add any who have added them
         * as well to their friends array list.
         */
        ArrayList<String> this_user_requests = friendsMap.get(currUID);
        for (String friend_req : this_user_requests) {
            ArrayList<String> requested_friends_requests = friendsMap.get(friend_req);

            if (requested_friends_requests.contains(currUID)) {
                mutualFriends.add(friend_req);
            }
        }

        return mutualFriends;
    }

    /*********************************************************************************************/

    /**
     * Converts the Friends Hook from the Firebase DB into a HashMap structure that holds each user's
     * list of friend requests
     * @param friendsSnapshot - The DataSnapshot of Friends in the Firebase DB
     * @return A Hashmap of the User's friends where wach user's UID is the key.
     */
    protected static HashMap<String, ArrayList<String>> extractFriendsNode(DataSnapshot friendsSnapshot) {

        HashMap<String, ArrayList<String>> friendsNode = new HashMap<>();

        for(DataSnapshot ds : friendsSnapshot.getChildren()) {
            ArrayList<String> users_friends = new ArrayList<>();

            for(DataSnapshot friend : ds.getChildren()) {
                users_friends.add(friend.getValue(String.class));
            }
            friendsNode.put(ds.getKey(), users_friends);
        }

        return friendsNode;
    }

    /*********************************************************************************************/

    /**
     * Returns the user list that's been filtered in some way
     *
     * @param users - The HashMap of all the users in the DB
     * @param users_friends_list - The ArrayList of the user's friend requests
     * @return Filtered ArrayList of UserObjs
     */
    public static ArrayList<UserObj> filterUsers(HashMap<String, UserObj> users, ArrayList<String> users_friends_list) {
        ArrayList<UserObj> filteredUsers = new ArrayList<>();

        /*
         * Goes through all the users and adds one to the list if their
         * friends list contains that user's ID
         */
        for (Object value : users.values()) {
            UserObj user_obj = (UserObj)value;
            try {
                String userID = user_obj.getUID();

                /* If this user is in the mutual friends list, add them to the ListView */
                if (users_friends_list.contains(userID)) {
                    filteredUsers.add(user_obj);
                }

            } catch (Exception e) {
                // Do Nothing
            }
        }
        return filteredUsers;
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
        // Already There!!
    }


    /*********************************************************************************************/

    /**
     * Opens the calendar view
     */
    public void OpenCalendar() {
        startActivity(new Intent(FriendsListActivity.this, CalendarLayout.class));
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
        Intent i = new Intent(FriendsListActivity.this, EditUser.class);

        i.putExtra("User/" + getString(R.string.usr_uid), currUser.getUID());
        i.putExtra("User/" + getString(R.string.usr_fname), currUser.getFname());
        i.putExtra("User/" + getString(R.string.usr_lname), currUser.getLname());
        i.putExtra("User/" + getString(R.string.usr_dispname), currUser.getUsername());
        i.putExtra("User/" + getString(R.string.usr_mobNum), currUser.getMobNum());
        i.putExtra("User/" + getString(R.string.usr_caringstat), currUser.getCaringStat());

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
}
