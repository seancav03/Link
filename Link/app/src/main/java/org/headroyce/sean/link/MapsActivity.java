package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

//    private float zoomLevel;

    //buttons
    private ImageButton add;
    private ImageButton refresh;
    private ImageButton profile;
    private ImageButton listView;

    //user logged in
    private String currentAccount = null;

    //Linked List stores all events
    private LList<Event> events = new LList<Event>();
    //Linked List stores list of markers of the events
    private LList<Marker> markers;

    //create new event startActivityForResult request code for creating new events
    private static final int REQUEST_CODE_NEW_EVENT = 3775;
    //request code for logging in
    private static final int REQUEST_CODE_LOGIN = 5773;
    //request code for looking at event description and deleting events
    private static final int REQUEST_CODE_EVENT_INFO = 2828;
    //request code for viewing profile (tells main activity user logged out)
    private static final int REQUEST_CODE_PROFILE = 8282;
    //request code for showing list of events
    private static final int REQUEST_CODE_EVENTS_LIST = 2882;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        //Buttons
        add = findViewById(R.id.Add);
        add.setOnClickListener(new addEventListener());
        refresh = findViewById(R.id.Refresh);
        refresh.setOnClickListener(new refreshListener());
        profile = findViewById(R.id.Profile);
        profile.setOnClickListener(new profileListener());
        listView = findViewById(R.id.imageButton2);
        listView.setOnClickListener(new showListListener());


        //initialize Linked Lists
        events = new LList();
        markers = new LList();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //O(n^2)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));

        if (!success) {
            Log.e("Eror" , "Style parsing failed.");
        }

        //Get events from server and plot on map
        reloadEvents();

        //set listener to detect long click of events
        mMap.setOnInfoWindowClickListener(new infoWindowClickedListener());
//        zoomLevel = mMap.getCameraPosition().zoom;
//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//
//                if(mMap.getCameraPosition().zoom != zoomLevel){
//                    mapEvents();
//                    zoomLevel = mMap.getCameraPosition().zoom;
//                }
//            }
//        });
    }

    //Profile Button Listener
    public class profileListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            if(currentAccount != null) {
                Intent intent = new Intent(MapsActivity.this, Profile.class);
                intent.putExtra("username", currentAccount);
                startActivityForResult(intent, REQUEST_CODE_PROFILE);
            } else {
                startActivityForResult(new Intent(MapsActivity.this, loginActivity.class), REQUEST_CODE_LOGIN);
            }




        }
    }

    //add Event Button's on click listener
    public class addEventListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            if (currentAccount != null){
                startActivityForResult(new Intent(MapsActivity.this, AddEventActivity.class), REQUEST_CODE_NEW_EVENT);
            } else {
                startActivityForResult(new Intent(MapsActivity.this, loginActivity.class), REQUEST_CODE_LOGIN);
            }
        }
    }

    //view list of events listener
    public class showListListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            if(currentAccount != null) {
                Intent intent = new Intent(MapsActivity.this, ListActivity.class);
                intent.putExtra("username", currentAccount);
                startActivityForResult(intent, REQUEST_CODE_EVENTS_LIST);
            } else {
                startActivityForResult(new Intent(MapsActivity.this, loginActivity.class), REQUEST_CODE_LOGIN);
            }
        }
    }

    //get results from AddEventActivity
    //O(n^2)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if results are good, get all the data from the result intent
        if (requestCode == this.REQUEST_CODE_NEW_EVENT) {
            if(resultCode == Activity.RESULT_OK){
                String title = data.getStringExtra("title");
                String description = data.getStringExtra("description");
                int month = data.getIntExtra("month", 1);
                int day = data.getIntExtra("day", 1);
                int year = data.getIntExtra("year", 0);
                int hour = data.getIntExtra("hour", 0);
                int minute = data.getIntExtra("minute", 0);
                double lat = data.getDoubleExtra("lat", 0.0);
                double lng = data.getDoubleExtra("lng", 0.0);
                LatLng location = new LatLng(lat, lng);

                if(year == 0){ Log.d("ERROR", "No input data received"); return; }

                //create event and call the method to add to server
                Event newEvent = new Event(title, description, month, day, year, hour, minute, location);
                newEvent.setUsername(currentAccount);
                addEvent(newEvent);
                reloadEvents();

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result -- I don't care.
            }
        } else if(requestCode == this.REQUEST_CODE_LOGIN){
            if(resultCode == Activity.RESULT_OK){
                currentAccount = data.getStringExtra("username");
                Toast.makeText(MapsActivity.this, "Logged in",
                        Toast.LENGTH_SHORT).show();
                reloadEvents();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result -- I don't care.
            }
        } else if(requestCode == this.REQUEST_CODE_EVENT_INFO){
            if(resultCode == Activity.RESULT_OK){
                String toDelete = data.getStringExtra("delete");
                if(toDelete.equals("true")){

                    String id = data.getStringExtra("IDNumber");

                    //removes event with this ID
                    removeEvent(id);

                    Toast.makeText(MapsActivity.this, "Event Deleted",
                            Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result -- I don't care.
            }
        } else if(requestCode == this.REQUEST_CODE_PROFILE){
            if(resultCode == Activity.RESULT_OK){

                String logout = data.getStringExtra("logout");
                if(logout.equals("true")){
                    currentAccount = null;
                    Toast.makeText(MapsActivity.this, "Logged Out",
                            Toast.LENGTH_SHORT).show();
                }
                reloadEvents();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                reloadEvents();
            }
        } else if(requestCode == this.REQUEST_CODE_EVENTS_LIST){
            if(resultCode == Activity.RESULT_OK){

                //find event with id of event chosen from list
                //then find marker from list for that event
                //finally, move camera to show that event, and open its info window
                String selectedID = data.getStringExtra("chosenID");
                Event chosen = null;
                for(Event ev : events){
                    if(ev.getIDNumber().equals(selectedID)){
                        chosen  = ev;
                        break;
                    }
                }
                if(chosen == null){
                    Toast.makeText(MapsActivity.this, "Error. Could not find selected event",
                            Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Could not find event with ID passed from ListActivity");
                    return;
                }
                LatLng eventLocation = chosen.getLocation();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 17));
                Marker mark = null;
                for(Marker m : markers) {
                    if (m.getId().equals(chosen.getID())) {
                        mark = m;
                    }
                }
                if(mark == null){
                    Toast.makeText(MapsActivity.this, "Error. Could not show info window as pin was not found",
                            Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Could not find marker from LList with id matching that of the chosen event");
                    return;
                }
                mark.showInfoWindow();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //I don't care
            }
        }
    }

    //refresh event listener
    public class refreshListener implements View.OnClickListener {
        //O(n^2)
        @Override
        public void onClick(View v) {
            reloadEvents();
        }
    }

    //makes HttpPostRequest object and makes the request to the right place with the right data
    //O(1)
    public void addEvent(Event ev){

        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("title", ev.getTitle()));
        params.add(new keyAndValue("description", ev.getDescription()));
        params.add(new keyAndValue("month", Integer.toString(ev.getMonth())));
        params.add(new keyAndValue("day", Integer.toString(ev.getDay())));
        params.add(new keyAndValue("year", Integer.toString(ev.getYear())));
        params.add(new keyAndValue("hour", Integer.toString(ev.getHour())));
        params.add(new keyAndValue("minute", Integer.toString(ev.getMinute())));
        params.add(new keyAndValue("lat", Double.toString(ev.getLocation().latitude)));
        params.add(new keyAndValue("lng", Double.toString(ev.getLocation().longitude)));
        params.add(new keyAndValue("username", ev.getUsername()));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);

        String url = HttpRequest.theUrl + "addEvent/";
        String result = "";

        try {
            result = request.execute(url).get();
        }
        catch( Exception e ) {
            //Log.d("CONNECTION 1", e.getMessage());
        }
        Toast.makeText(MapsActivity.this, "Event Created",
                Toast.LENGTH_SHORT).show();

    }

    //removes Event with id
    //O(n^2)
    public void removeEvent(String id){


        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("eventID", id));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);

        //HttpPostRequest request = new HttpPostRequest(id);

        String url = HttpRequest.theUrl + "removeEvent/";
        String result = "";

        try {
            result = request.execute(url).get();
        }

        catch( Exception e ) {
            //Log.d("CONNECTION 1", e.getMessage());
        }

        reloadEvents();

    }

    //O(n^2)
    //get list of events from the server
    public void reloadEvents(){

        String user = currentAccount;
        if(user == null){
            Toast.makeText(MapsActivity.this, "Log in to View Events",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("username", user));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);

        String url = HttpRequest.theUrl + "getMyFeed/";
        String result = "";

        try {
            result = request.execute(url).get();
        } catch (Exception e) {
//            Log.d("CONNECTION 1", e.getMessage());
        }
//        Log.d("Result: ", "Result: " + result);

        LList<Event> eventList = null;

        mMap.clear();


        try {
//            Log.d("124+", "--> " + result);
            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("data");

            eventList = new LList<Event>();
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
//                Log.d("LAT AND LNG Getting", tempHere.latitude + ", " + tempHere.longitude);
                Event it = new Event(name1, name2, Integer.parseInt(name3), Integer.parseInt(name4), Integer.parseInt(name5), Integer.parseInt(name6), Integer.parseInt(name7), tempHere);
                it.setUsername(name10);
                it.setIDNumber(name11);
                //add event to list
                eventList.add(it);
            }
        } catch (JSONException e) {
            e.printStackTrace();
//            Log.d("FAILED", "FAILED TO PARSE EVENTS ARRAY");
        }

        if(eventList != null) {
            events = eventList;
//            Log.d("SUCCESS", "GOT EVENTS FROM SERVER");
            mapEvents();
        } else {
            events = null;
        }
    }

    //O(n^2)
    public void mapEvents() {

        // Add a marker at every event
        markers.clear();    //make sure past markers are gone from list
        mMap.clear();
        for(Event ev : events) {
            LatLng loc = ev.getLocation();
            //check if event is future or past. Red for future, blue for past
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            MarkerOptions m;
            if(year > ev.getYear() || year == ev.getYear() && month > ev.getMonth() || year == ev.getYear() && month == ev.getMonth() && day > ev.getDay()){
                m = new MarkerOptions().position(loc).title(ev.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            } else {
                m = new MarkerOptions().position(loc).title(ev.getTitle()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }

            //minute must be a string so that 8:00 isn't desplayed as 8:0
            String minute;
            if(ev.getMinute() < 10){
                minute = "0" + ev.getMinute();
            } else {
                minute = Integer.toString(ev.getMinute());
            }
            m.snippet("Date: "  + + ev.getMonth() + "/" + ev.getDay() + "/" + ev.getYear() + " at " + ev.getHour() +":" + minute +", CLICK ME for more info");
            //set icon to user's profile pic if they have one
            Bitmap b = getProfPic(ev.getUsername());
            double width = (double)b.getWidth();
            double height = (double)b.getHeight();
            //make sure icons don't get too small
            double zoom = mMap.getCameraPosition().zoom;
//            if(zoom < 4){
//                zoom = 4;
//            }
            if(width > height){

//                b = Bitmap.createScaledBitmap(b, ((int)(zoom * 6.0 * (width/height))), ((int)zoom * 6), false);
                b = Bitmap.createScaledBitmap(b, ((int)(70 * (width/height))), ((int)70), false);
            } else {
//                b = Bitmap.createScaledBitmap(b, ((int)zoom * 6), ((int)(zoom * 6.0 * (height/width))), false);
                b = Bitmap.createScaledBitmap(b, ((int)70), ((int)(zoom * 70 * (height/width))), false);
            }
            b = getCroppedBitmap(b);
            if(b != null) {
                m.icon(BitmapDescriptorFactory.fromBitmap(b));
            }
            Marker m2 = mMap.addMarker(m);
            ev.setID(m2.getId());
            markers.add(m2);
        }

    }

    //get profile picture from username
    public Bitmap getProfPic(String username){

        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("username", username));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);

        String url = HttpRequest.theUrl + "getProfilePic/";
        String result = "";

        try {
            result = request.execute(url).get();
        }
        catch( Exception e ) {
//            Log.d("CONNECTION 111", e.getMessage());
        }

        //check what I get back for if there is an image stored
        if(result.equals("NONE") || result.equals("")){
            //No profile Pic, leave icon as default
            return null;
        } else {
            try {
                result = result.replace("-", "+");
                byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                return decodedByte;
            } catch (Exception e){
                return null;
            }
        }
    }

    //crop bitmap into a circle
    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }


    //find the event which was selcted by the info window being clicked
    public class infoWindowClickedListener implements GoogleMap.OnInfoWindowClickListener {

        //O(n)
        @Override
        public void onInfoWindowClick(Marker marker) {
            String ID = marker.getId();
            Event selected = null;
            for (Event ev : events) {
                if (ev.getID().equals(ID)) {
                    selected = ev;
                    break;
                }
            }
            if (selected == null) {
//                Log.d("OH NO!", "Event ID from clicked info window does not exist for any known event");
                //reload events to remove any pins that should not exist
                reloadEvents();
                return;
            }

            Intent intent = new Intent(MapsActivity.this, aboutEventActivity.class);
            intent.putExtra("title", selected.getTitle());
            intent.putExtra("description", selected.getDescription());
            intent.putExtra("username", selected.getUsername());
            if(currentAccount != null && selected.getUsername().equals(currentAccount)){
                intent.putExtra("canDelete", "true");
            } else {
                intent.putExtra("canDelete", "false");
            }
            String minute;
            if(selected.getMinute() < 10){
                minute = "0" + selected.getMinute();
            } else {
                minute = Integer.toString(selected.getMinute());
            }
            intent.putExtra("date", "Date: "  + + selected.getMonth() + "/" + selected.getDay() + "/" + selected.getYear() + " at " + selected.getHour() +":" + minute);
            intent.putExtra("location", "Location: " + selected.getLocation().latitude + ", " + selected.getLocation().longitude);
            intent.putExtra("ID", selected.getIDNumber());

            startActivityForResult(intent, REQUEST_CODE_EVENT_INFO);
        }
    }
}
