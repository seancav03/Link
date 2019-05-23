package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;

public class AddEventActivity extends AppCompatActivity {

    //main layout
    private LinearLayout layout;

    //editText's and Date picker which I need to get the data from outside of constructor
    private EditText input1;    //title
    private EditText input2;    //description
    private EditText input3;    //date: Month
    private EditText input4;    //date: Day
    private EditText input5;    //date: Year
    private EditText input6;    //Time: Hour
    private EditText input7;    //Time: Minutes

    //TextView where location is displayed
    private TextView view9;

    private Place location;

    //back button
    private ImageButton back;

    //actual values
    private String title;
    private String description;
    private int month;
    private int day;
    private int year;
    private int hour;
    private int minute;

    //for choosing location
    private Button b;

    //value needed for getting location
    private static final int PLACE_PICKER_REQUEST = 28;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //back button
        back = findViewById(R.id.back2);
        back.setOnClickListener(new backListener());

        //set up layout with horizontal linear layouts for each input row
        layout = findViewById(R.id.LinearLayout);
        //row 1) Event Title: ___ (Key: __ is input space)
        LinearLayout lay1 = new LinearLayout(this);
        TextView view1 = new TextView(this);
        view1.setText("Event Title: ");
        input1 = new EditText(this);
        lay1.addView(view1);
        lay1.addView(input1);
        //row 2) Event Description: ___
        LinearLayout lay2 = new LinearLayout(this);
        TextView view2 = new TextView(this);
        view2.setText("Event Description: ");
        input2 = new EditText(this);
        lay2.addView(view2);
        lay2.addView(input2);
        //row 3) Event Date: __ / __ / __ Time: __ : __
        LinearLayout lay3 = new LinearLayout(this);
        TextView view3 = new TextView(this);
        view3.setText("Event Date: ");
        input3 = new EditText(this);
        input3.setInputType(InputType.TYPE_CLASS_NUMBER);
        input4 = new EditText(this);
        input4.setInputType(InputType.TYPE_CLASS_NUMBER);
        input5 = new EditText(this);
        input5.setInputType(InputType.TYPE_CLASS_NUMBER);
        input6 = new EditText(this);
        input6.setInputType(InputType.TYPE_CLASS_NUMBER);
        input7 = new EditText(this);
        input7.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextView view4 = new TextView(this);
        view4.setText("/");
        TextView view5 = new TextView(this);
        view5.setText("/");
        TextView view6 = new TextView(this);
        view6.setText("Time: ");
        TextView view7 = new TextView(this);
        view7.setText(":");
        //add views and input spaces in order to properly format them
        lay3.addView(view3);
        lay3.addView(input3);
        lay3.addView(view4);
        lay3.addView(input4);
        lay3.addView(view5);
        lay3.addView(input5);
        lay3.addView(view6);
        lay3.addView(input6);
        lay3.addView(view7);
        lay3.addView(input7);
        //row 4) Specifies Date input style
        TextView view8 = new TextView(this);
        view8.setText("Note: Date/Time input must be of form: mm/dd/yyyy, hh:mm (1:00 is entered as 13:00)");
        //row 5) set up place picker button and display location
        LinearLayout lay4 = new LinearLayout(this);
        b = new Button(this);
        b.setText("Choose Location");
        b.setOnClickListener(new pickLocationListener());
        view9 = new TextView(this);
        view9.setText("Choose Location");
        lay4.addView(b);
        lay4.addView(view9);
        //row 6) Enter Button
        Button enter = new Button(this);
        enter.setText("Create New Event");
        enter.setOnClickListener(new createEventListener());
        //add all rows to the main layout
        layout.addView(lay1);
        layout.addView(lay2);
        layout.addView(lay3);
        layout.addView(view8);
        layout.addView(lay4);
        layout.addView(enter);
    }

    //back button listener
    public class backListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    //button listener to go to androids place picker widget
    public class pickLocationListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(AddEventActivity.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                Log.d("Error", "GooglePlayServicesRepairableException: " + e.getMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.d("Error", "GooglePlayServicesNotAvailableException: " + e.getMessage());
            }
        }
    }
    //get values from the place picked in the widget
    //O(1)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                location = place;
                view9.setText(place.getAddress());
            }
        }
    }

    //Create new event button listener
    public class createEventListener implements View.OnClickListener {

        //O(1)
        @Override
        public void onClick(View v) {

            boolean validInputs = true;

            //get all values
            title = input1.getEditableText().toString();
            description = input2.getEditableText().toString();
            if(title == null || title.length() < 1){
                validInputs = false;
                input1.setError("Please enter a title");
            } else if( title.length() > 30){
                validInputs = false;
                input1.setError("Title must be less than 30 characters");
            }
            if(description == null || description.length() < 1){
                validInputs = false;
                input2.setError("Please provide a description");
            } else if(description.length() > 500){
                validInputs = false;
                input2.setError("Description must be less than 500 characters");
            }

            //Parse and check if integer values where necessary
            //month
            try {
                month = Integer.parseInt(input3.getEditableText().toString());
            } catch(Exception e){
                validInputs = false;
                input3.setError("Invalid input: Month must be an integer value");
            }
            //day
            try {
                day = Integer.parseInt(input4.getEditableText().toString());
            } catch(Exception e){
                validInputs = false;
                input4.setError("Invalid input: Day must be an integer value");
            }
            //year
            try {
                year = Integer.parseInt(input5.getEditableText().toString());
            } catch(Exception e){
                validInputs = false;
                input5.setError("Invalid input: Year must be an integer value");
            }
            //hour
            try {
                hour = Integer.parseInt(input6.getEditableText().toString());
            } catch(Exception e){
                validInputs = false;
                input6.setError("Invalid input: Hour must be an integer value");
            }
            //minute
            try {
                minute = Integer.parseInt(input7.getEditableText().toString());
            } catch(Exception e){
                validInputs = false;
                input7.setError("Invalid input: Minute must be an integer value");
            }
            //check for values being within the correct range
            if(month < 1 || month > 12){
                validInputs = false;
                input3.setError("Invalid input: Month must be an integer value");
            }
            //check if year is reasonable
            if(year > 2200 || year < 2018){
                validInputs = false;
                input5.setError("Invalid input: Event must be between now and 2100");
            }
            //check if the day exists within the month
            if(day < 1) { validInputs = false; input4.setError("Invalid input: Not a real day"); }
            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
                if(day > 31) {
                    validInputs = false;
                    input4.setError("Invalid input: Not a real day");
                }
            } else if (month == 4 || month == 6 || month == 9 || month == 11){
                if(day > 30){
                    validInputs = false;
                    input4.setError("Invalid input: Not a real day");
                }
            } else {
                if(isLeapYear(year)){
                    if(day > 29){
                        validInputs = false;
                        input4.setError("Invalid input: Not a real day");
                    }
                } else  {
                    if(day > 28){
                        validInputs = false;
                        input4.setError("Invalid input: Not a real day");
                    }
                }
            }
            //check if hours and minutes are valid
            if(hour < 0 || hour > 23){
                validInputs = false;
                input6.setError("Invalid input: Not an hour. Must be between 0 and 23");
            }
            if(minute < 0 || minute > 59) {
                validInputs = false;
                input7.setError("Invalid input: Not a real minute. Must be between 0 and 59");
            }
            Log.d("Inputs:", "Title: " + title +", Description: " + description);
            Log.d("Inputs:", "Date: " + month + "/" + day + "/" + year + " at " + hour +":" + minute);

            if(location == null){
                b.setError("Choose Location");
                validInputs = false;
            }
            //check if time is in the past (current day is allowed, but event would be deleted when it is a day in the past)
            Calendar cal = Calendar.getInstance();
            int Cyear = cal.get(Calendar.YEAR);
            int Cmonth = cal.get(Calendar.MONTH) + 1;
            int Cday = cal.get(Calendar.DAY_OF_MONTH);
            if(Cyear > year || Cyear == year && Cmonth > month || Cyear == year && Cmonth == month && Cday > day){
                validInputs = false;
                input4.setError("Please chose a date in the future");
            }

            if(validInputs == true){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("title", title);
                returnIntent.putExtra("description", description);
                returnIntent.putExtra("year", year);
                returnIntent.putExtra("month", month);
                returnIntent.putExtra("day", day);
                returnIntent.putExtra("hour", hour);
                returnIntent.putExtra("minute", minute);
                returnIntent.putExtra("lat", location.getLatLng().latitude);
                returnIntent.putExtra("lng", location.getLatLng().longitude);
                setResult(MapsActivity.RESULT_OK,returnIntent);
                finish();
            }
        }
    }

    //check if leap year
    //O(1)
    public boolean isLeapYear(int year) {
        if (year % 4 != 0) {
            return false;
        } else if (year % 400 == 0) {
            return true;
        } else if (year % 100 == 0) {
            return false;
        } else {
            return true;
        }
    }


}
