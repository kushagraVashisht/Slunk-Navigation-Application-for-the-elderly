package com.example.ritusharma.itproject.Auth_SignIn;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ritusharma.itproject.MainActivity;
import com.example.ritusharma.itproject.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Allows a user to add Friends via Phone Number
 */
public class AddFriendActivity extends AppCompatActivity {

    /* Useful member variables */
    private EditText et_add_user;
    private TextView tv_curr_user_phnum;
    private FloatingActionButton fab_add;
    private String currUid;
    private UserObj currUser;
    private String otherUid;
    private DatabaseReference firDB_users;
    private DatabaseReference firDB_friends;

    /*********************************************************************************************/

    /**
     * When the activity is opened, get all of the objects that need editing in the
     * view, read the user out of the given Intent, and set a listener for the Add
     * button. Also rename the screen.
     * 
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        /* Get Activity Objects */
        firDB_users = FirebaseDatabase.getInstance().getReference(getString(R.string.User_Tag));
        firDB_friends = FirebaseDatabase.getInstance().getReference(getString(R.string.Friends_Tag));
        et_add_user = (EditText) findViewById(R.id.et_add_user);
        tv_curr_user_phnum = (TextView) findViewById(R.id.tv_UserCode);
        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        Intent c_intent = getIntent();
        currUid = getIntent().getStringExtra("User/" + getString(R.string.usr_uid));

        /*
         * Gets the current user out of the details passed by the previous screen (to
         * reduce calls to firebase)
         */
        currUser = new UserObj(
            currUid, c_intent.getStringExtra("User/" + getString(R.string.usr_dispname)),
            c_intent.getStringExtra("User/" + getString(R.string.usr_fname)),
            c_intent.getStringExtra("User/" + getString(R.string.usr_lname)),
            CaringStatus.valueOf(c_intent.getStringExtra("User/" + getString(R.string.usr_caringstat))),
            c_intent.getStringExtra("User/" + getString(R.string.usr_mobNum))
        );

        tv_curr_user_phnum.setText(currUser.getMobNum());

        /*
         * When Add button is clicked, search user list for the DisplayName or Phone
         * Number. If successful, send a Friend Request, if not, notify the user via
         * SnackBar.
         */
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user_phnum = et_add_user.getText().toString();
                checkIDRequest(user_phnum);
            }
        });
        /* naming the title bar same as name of user */
        getSupportActionBar().setTitle(getString(R.string.app_name) + ": " + getString(R.string.AddFriend));
    }

    /*********************************************************************************************/

    /**
     * Sends a Friend Request to the requestedFriend, then opens the Home page.
     * @param this_user_id        - String, the UserID of this User.
     * @param requested_friend_id - String, the UserID of the friend we want to add.
     */
    private void sendFriendRequest(final String this_user_id, final String requested_friend_id) {

        firDB_friends.child(requested_friend_id).push().setValue(this_user_id);
        Toast.makeText(getApplicationContext(), R.string.toast_friend_sent_req, Toast.LENGTH_LONG).show();
        et_add_user.setText("");
        startActivity(new Intent(this, MainActivity.class));
    }

    /*********************************************************************************************/

    /**
     * <pre>
     * Checks the entered phone number against the Database, and sends a friend request to that user if:
     * * The number is recorded in the DB (ie the user exists)
     * * They user hasn't tried to add themself
     * * They haven't already sent a request to that user.
     * Warns the user if they've tried to do any of the above via toasts.
     * </pre>
     * @param user_phnum - The Phone Number of the user to add.
     */
    private void checkIDRequest(final String user_phnum) {
        /* Initialisation of the other user */
        otherUid = null;

        /* User's can't add themselves as friends! */
        if (user_phnum.equals(currUser.getMobNum())) {
            et_add_user.setText("");
            Toast.makeText(getApplicationContext(), R.string.toast_friends_add_self_err, Toast.LENGTH_LONG).show();
            return;
        }

        /*
         * First, gets all the users from the DB and gets the User ID of the other user in
         * order to send a request to them.
         */
        firDB_users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot usersHookSnap) {

                HashMap<String, UserObj> usersMap = FriendsListActivity.extractUsers(usersHookSnap);

                for (Map.Entry<String, UserObj> userPair : usersMap.entrySet()) {
                    if (userPair.getValue().getMobNum().equals(user_phnum)) {
                        otherUid = userPair.getKey();

                        /*
                         * Second, gets the other user's friends to check that a friend request hasn't
                         * already been sent to them. Won't send another if it has, and notifies the
                         * user. (It's in here because getting this child will try to occur
                         * before the first listener returns and it crashes the app.)
                         */
                        firDB_friends.child(otherUid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot otherUserFriendSnap) {

                                ArrayList<String> otherUsersRequests = FriendsListActivity.extractFriendIDs(otherUserFriendSnap);

                                if(otherUsersRequests.contains(currUid)) {
                                    Toast.makeText(getApplicationContext(), R.string.toast_friends_already_added,
                                            Toast.LENGTH_LONG).show();

                                } else {
                                    sendFriendRequest(currUid, otherUid);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError dbErr) {
                                Toast.makeText(getApplicationContext(), dbErr.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        return;
                    }
                }
                /* If no user found */
                Toast.makeText(getApplicationContext(), R.string.toast_friends_no_user, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError dbErr) {
                Toast.makeText(getApplicationContext(), dbErr.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

/*********************************************************************************************/
