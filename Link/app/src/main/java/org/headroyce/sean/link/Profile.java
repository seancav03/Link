package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class Profile extends AppCompatActivity {

    //Views from layout
    private ImageButton friends;
    private ImageButton logout;
    private TextView username;
    private ImageButton back;

    private String user;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        friends = findViewById(R.id.Friends);
        friends.setOnClickListener(new friendListener());
        logout = findViewById(R.id.Logout);
        logout.setOnClickListener(new LogoutListener());
        username = findViewById(R.id.Name);
        user = getIntent().getStringExtra("username");
        username.setText(user);
        back = findViewById(R.id.back);
        back.setOnClickListener(new backListener());

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

    //add, view, and manage friends
    public class friendListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Profile.this, Friends.class);
            intent.putExtra("username", user);
            startActivity(intent);
        }
    }

    //logout button
    public class LogoutListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            returnIntent.putExtra("logout", "true");
            setResult(MapsActivity.RESULT_OK,returnIntent);
            finish();
        }
    }


}
