package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class ListActivity extends AppCompatActivity {

    //back button
    private ImageButton back;
    //layout
    private LinearLayout eventsList;

    //username
    private String username;
    //Binary Search Tree of events (Sorted by date)
    private BST<Event> events = null;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //get username from intent
        username = getIntent().getStringExtra("username");

        //back button
        back = findViewById(R.id.back6);
        back.setOnClickListener(new backListener());

        //get layout for events
        eventsList = findViewById(R.id.events);

        listEvents();

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

    //O(n^2), though theoretical average of Θ(n log n)
    //Show location of events being listed and list events by nearness
    public void listEvents(){

        //get events list
        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("username", username));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);

        String url = HttpRequest.theUrl + "getMyFeed/";
        String result = "";

        try {
            result = request.execute(url).get();
        } catch (Exception e) {
            Log.d("CONNECTION 1", e.getMessage());
        }
        Log.d("Result: ", "Result: " + result);

        try {

            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("data");

            events = new BST<Event>();
            for(int i = 0; i < array.length(); i++) {
                JSONArray arrayOfStrings = array.getJSONArray(i);
                String name1 = arrayOfStrings.get(0).toString();    //Title
                String name2 = arrayOfStrings.get(1).toString();    //Description
                String name3 = arrayOfStrings.get(2).toString();    //Month
                String name4 = arrayOfStrings.get(3).toString();    //Day
                String name5 = arrayOfStrings.get(4).toString();    //Year
                String name6 = arrayOfStrings.get(5).toString();    //Hour
                String name7 = arrayOfStrings.get(6).toString();    //Minute
                String name8 = arrayOfStrings.get(7).toString();    //Lat
                String name9 = arrayOfStrings.get(8).toString();    //Lng
                String name10 = arrayOfStrings.get(9).toString();   //Username
                String name11 = arrayOfStrings.get(10).toString();  //ID
                LatLng tempHere = new LatLng(Double.parseDouble(name8), Double.parseDouble(name9));
                Log.d("LAT AND LNG Getting", tempHere.latitude + ", " + tempHere.longitude);
                Event it = new Event(name1, name2, Integer.parseInt(name3), Integer.parseInt(name4), Integer.parseInt(name5), Integer.parseInt(name6), Integer.parseInt(name7), tempHere);
                it.setUsername(name10);
                it.setIDNumber(name11);
                events.add(it);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("FAILED", "FAILED TO PARSE EVENTS ARRAY");
        }

        if(events == null){ Log.d("Error", "No Events in Binary Search Tree"); return; }

        ArrayList<Event> sortedEvents = events.inOrder();
        if(sortedEvents == null){
            Toast.makeText(ListActivity.this, "No Events to Display. Create events or add friends to see events",
                    Toast.LENGTH_LONG).show();
            return;
        }
        for(Event ev : sortedEvents){
            LinearLayout tempLin = new LinearLayout(this);
            tempLin.setOrientation(LinearLayout.HORIZONTAL);
            TextView tempView = new TextView(this);
            tempView.setText(ev.getTitle() + " on: " + ev.getMonth()+"/"+ev.getDay()+"/"+ev.getYear()+" at: "+ev.getHour()+":"+ev.getMinute());
            Button b = new Button(this);
            b.setText("Map");
            try {
                b.setId(Integer.parseInt(ev.getIDNumber()));
            } catch (Exception e){
                Log.d("ERROR", "Failed to Parse Event ID");
                return;
            }
            b.setOnClickListener(new viewOnMapListener());
            tempLin.addView(tempView);
            tempLin.addView(b);
            eventsList.addView(tempLin);
        }


    }


    public class viewOnMapListener implements View.OnClickListener {

        //O(n)
        @Override
        public void onClick(View v) {
            String id = Integer.toString(((Button)v).getId());
            ArrayList<Event> checking = events.inOrder();
            for(Event ev : checking){
                if(ev.getIDNumber() == id){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("chosenID", ev.getIDNumber());
                    setResult(MapsActivity.RESULT_OK,returnIntent);
                    finish();
                    return;
                }
            }
            Log.d("OH NO!", "Didn't find event with that id");
        }
    }



}
