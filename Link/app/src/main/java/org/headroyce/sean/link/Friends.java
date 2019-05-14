package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Friends extends AppCompatActivity {

    private LinearLayout layout;
    private LinearLayout topRow;
    private Button add;
    private EditText username;
    private ImageButton back;

    private String user;

    private LList<String> friends;

//    private static String theUrl = "https://powerful-sands-36300.herokuapp.com/";
    private static String theUrl = "http://10.10.10.119:3775/";

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        back = findViewById(R.id.back3);
        back.setOnClickListener(new backListener());

        user = getIntent().getStringExtra("username");

        layout = findViewById(R.id.Layout);
        topRow = findViewById(R.id.layTop);
        add = findViewById(R.id.Add);
        add.setOnClickListener(new addFriendListener());
        username = findViewById(R.id.username);

        friends = new LList<String>();

        getFriends(user);

    }

    //O(1)
    //back button listener
    public class backListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
    }

    //get friends of user from server
    //O(n)
    public void getFriends(String user){

        //Clear list of friends
        friends.clear();

        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("username", user));

        //create object to send request to server
        HttpRequest request = new HttpRequest("POST", params);


        String url = theUrl + "getFriends/";
        String result = "";

        try {
            result = request.execute(url).get();
        } catch( Exception e ) {
            Log.d("CONNECTION 1", e.getMessage());
        }

        try {

            Log.d("+++++++", "============");

            JSONObject jsonObject = new JSONObject(result);
            JSONArray array = jsonObject.getJSONArray("data");

            for(int i = 0; i < array.length(); i++) {
                String name1 = array.get(i).toString();    //Get friends name
                if(name1.equals(user)){
                    continue;
                }
                Log.d("+++++++", "Friend: " + name1);
                friends.add(name1);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("FAILED", "FAILED TO GET FRIENDS");
        }

        showFriends();


    }

    //show friends
    //O(n)
    public void showFriends(){

        layout.removeAllViews();
        layout.addView(topRow);

        for(int i = 0; i < friends.size(); i++){

            //set up view displaying username and button to remove them
            TextView v = new TextView(this);
            v.setText(friends.get(i));
            Button b = new Button(this);
            b.setText("Remove");
            b.setId(i);
            b.setOnClickListener(new removeFriendListener());
            //put in layout
            LinearLayout l = new LinearLayout(this);
            l.addView(v);
            l.addView(b);

            layout.addView(l);

        }
    }

    //remove Friend button Listener
    public class removeFriendListener implements View.OnClickListener {

        //O(1)
        @Override
        public void onClick(View v) {
            int id = ((Button)v).getId();


            LList<keyAndValue> params = new LList<keyAndValue>();
            params.add(new keyAndValue("username", user));
            params.add(new keyAndValue("theirs", friends.get(id)));

            HttpRequest request = new HttpRequest("POST", params);

            String url = theUrl + "unfollow/";
            String result = "";

            try {
                result = request.execute(url).get();
            } catch( Exception e ) {
                Log.d("CONNECTION 1", e.getMessage());
            }
            if(result.equals("Done")){
                Toast.makeText(Friends.this, "Removed Friend: " + friends.get(id),
                        Toast.LENGTH_SHORT).show();
            } else {
                Log.d("ERROR", "Failed to unfollow");
            }

            //get new list of friends
            getFriends(user);

        }
    }

    //add friend listener
    //O(1)
    public class addFriendListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            String theirs = username.getText().toString();
            if(theirs != null && theirs.length() > 0){

                LList<keyAndValue> params = new LList<keyAndValue>();
                params.add(new keyAndValue("username", user));
                params.add(new keyAndValue("theirs", theirs));

                //create object to send request to server
                HttpRequest request = new HttpRequest("POST", params);


                String url = theUrl + "follow/";
                String result = "";

                try {
                    result = request.execute(url).get();
                } catch( Exception e ) {
                    Log.d("CONNECTION 1", e.getMessage());
                }
                if(result.equals("Done")){
                    Toast.makeText(Friends.this, "Added Friend: " + theirs,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Friends.this, "Could not find user: " + theirs + ", or user already added",
                            Toast.LENGTH_SHORT).show();
                }

                //get new list of friends
                getFriends(user);

            } else {
                username.setError("Type username to add");
            }

        }
    }

}
