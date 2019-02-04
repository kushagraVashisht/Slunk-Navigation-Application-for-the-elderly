package com.example.ritusharma.itproject.Calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.ritusharma.itproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * This class is the main class thorough which a user interacts with the calendar.
 *
 * */
public class CalendarLayout extends AppCompatActivity {

    /* Main navigation components */
    private CalendarView mCalendarView;
    private TextView dateLabel;
    private FloatingActionButton addEvent;

    /* Events recycler View */
    private RecyclerView eventsList;
    private EventAdapter adapter;
    private List<Event> events;

    /* Firebase setup code */
    private DatabaseReference firDB_cal;
    private String cuid;

    /**********************************************************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        /* Instatntiate UI components*/
        mCalendarView = findViewById(R.id.calendarView);
        dateLabel = findViewById(R.id.date);
        addEvent = findViewById(R.id.addeventbtn);
        eventsList = findViewById(R.id.ewentsView);
        eventsList.setHasFixedSize(true);
        eventsList.setLayoutManager(new LinearLayoutManager(this));

        /*Adaptors and animators fro the Recycleriew*/
        ((SimpleItemAnimator) eventsList.getItemAnimator()).setSupportsChangeAnimations(false);
        adapter = new EventAdapter(events, this);
        eventsList.setAdapter(adapter);

        /* Instantiate FireBase Authentication and get User tag*/
        firDB_cal = FirebaseDatabase.getInstance().getReference(getString(R.string.Calendar_Tag));
        cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /*A List to store all the events */
        events = new ArrayList<>();

        /*
         When a date is changed on the calendar view, we update the corresponding
        * UI component.
        *
        * Along with this we also pull new datat from FireBase - namely calendar events and
        * update the recycler with newly created events
        * */
        mCalendarView.setOnDateChangeListener((CalendarView, year, month, dayOfMonth) -> {

            // Update the date
            String date = dayOfMonth + "-" + (month + 1) + "-" + year;
            dateLabel.setText("Date: " + date);
            

            // Retreive data from firebase
            firDB_cal.child(cuid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    // Firebase contains a list of Event obj's. Iterate through this list .
                    for (DataSnapshot c : dataSnapshot.getChildren()) {
                        // Add each event to the List - which updates the recycler view
                        Event newEvent = c.getValue(Event.class);
                        events.add(newEvent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        });

        /*
        * Clicking on the '+' button in this view leads us to a view where new events can be added
        * */
        addEvent.setOnClickListener(view -> {
            startActivity(new Intent(CalendarLayout.this, AddEvent.class));
        });

        /*
        * When the calendar is opened set the date to current date.
        * */

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yyyy");
        String nowDate = df2.format(c.getTime());
        dateLabel.setText("Date: " + nowDate);

        getSupportActionBar().setTitle("Your Calendar");
    }

}

/**********************************************************************************************/
