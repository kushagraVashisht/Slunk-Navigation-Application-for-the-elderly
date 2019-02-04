package com.example.ritusharma.itproject.Calendar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Stack;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TimePicker;
import android.widget.Toast;
import org.w3c.dom.Text;
import com.example.ritusharma.itproject.R;
import com.example.ritusharma.itproject.Calendar.CalendarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 *
 * This activty lets users enter details for a calendar event such as event
 * name, event start day/time, event end day/time and a description if necessary
 *
 */
public class AddEvent extends AppCompatActivity {

    /*
     * UI Components
     */

    private EditText mEventName;
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;
    private EditText mEventDesc;
    private Button mSaveEvent;
    private String cuid;
    private DatabaseReference firDB_cal;

    /*
     * Listeners for updating widget input on main screen
     */
    private DatePickerDialog.OnDateSetListener mStartDateSetListener;
    private DatePickerDialog.OnDateSetListener mEndDateSetListener;
    private TimePickerDialog.OnTimeSetListener mStartTimeSetListener;
    private TimePickerDialog.OnTimeSetListener mEndTimeSetListener;

    /*********************************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        /*
         * Instantiation of input components
         */
        mStartDate = findViewById(R.id.Startdate);
        mStartTime = findViewById(R.id.Starttime);
        mEndDate = findViewById(R.id.Enddate);
        mEndTime = findViewById(R.id.Endtime);
        mSaveEvent = findViewById(R.id.saveEvent);
        mEventName = findViewById(R.id.EventName);
        mEventDesc = (EditText) findViewById(R.id.Description);

        /* Instantiation of firebase components */
        firDB_cal = FirebaseDatabase.getInstance().getReference(getString(R.string.Calendar_Tag));
        cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        /*
         *
         * When the user wanted to select a start day for the event, create a new
         * DatePicker Dialog
         *
         * get date input from the dialogue and set it to the relevant textField
         *
         */

        mStartDate.setOnClickListener(view -> {
            mStartDate.setText("Choose Start Date");
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(AddEvent.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mStartDateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mStartDateSetListener = new DatePickerDialog.OnDateSetListener() {

            /*********************************************************************************************/

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mStartDate.setText(date);
            }
        };

        mStartTime.setOnClickListener(view -> {
            mStartTime.setText("Choose Start Time");
            Calendar cal = Calendar.getInstance();
            int curHour = cal.get(Calendar.HOUR_OF_DAY);
            int curMin = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(AddEvent.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mStartTimeSetListener, curHour, curMin,
                    android.text.format.DateFormat.is24HourFormat(this));

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mStartTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            /*********************************************************************************************/

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                mStartTime.setText(hour + ":" + min);
            }
        };

        mEndDate.setOnClickListener(view -> {
            mEndDate.setText("Choose End Date");
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(AddEvent.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mEndDateSetListener, year, month, day);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mEndDateSetListener = new DatePickerDialog.OnDateSetListener() {

            /*********************************************************************************************/

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = month + "/" + day + "/" + year;
                mEndDate.setText(date);
            }
        };

        mEndTime.setOnClickListener(view -> {
            mEndTime.setText("Choose End Time");
            Calendar cal = Calendar.getInstance();
            int curHour = cal.get(Calendar.HOUR_OF_DAY);
            int curMin = cal.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(AddEvent.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth, mEndTimeSetListener, curHour, curMin,
                    android.text.format.DateFormat.is24HourFormat(this));

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mEndTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            /*********************************************************************************************/

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                mEndTime.setText(hour + ":" + min);
            }
        };


        /*
        * Upon clicking the save event button, fetch datat from all UI components and create a
        * new event object.
        *
        * This object is then written to firebase, and we are re-directed to the main calendar
        * home-page
        * */
        mSaveEvent.setOnClickListener(view -> {

            /*********************************************************************************************/

            Event e = new Event(mStartDate.getText().toString(), mEndDate.getText().toString(),
                    mStartTime.getText().toString(), mEndTime.getText().toString(), mEventName.getText().toString(),
                    mEventDesc.getText().toString(), cuid);

            firDB_cal.child(cuid).push().setValue(e);

            startActivity(new Intent(AddEvent.this, CalendarLayout.class));
        });
    }
}

/*********************************************************************************************/

