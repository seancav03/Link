package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class aboutEventActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    private TextView date;
    private TextView location;
    private TextView user;
    private Button delete;
    private ImageButton back;

    private String eventID;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_event);

        //back button
        back = findViewById(R.id.back5);
        back.setOnClickListener(new backListener());

        //get views
        title = findViewById(R.id.Title);
        description = findViewById(R.id.Description);
        date = findViewById(R.id.Date);
        location = findViewById(R.id.Location);
        user = findViewById(R.id.User);

        //set text
        title.setText(getIntent().getStringExtra("title"));
        description.setText(getIntent().getStringExtra("description"));
        date.setText(getIntent().getStringExtra("date"));
        location.setText(getIntent().getStringExtra("location"));
        String username = getIntent().getStringExtra("username");
        user.setText("Event by: " + username);

        //get id
        eventID = getIntent().getStringExtra("ID");

        delete = findViewById(R.id.Delete);
        delete.setOnClickListener(new deleteListener());
        if(getIntent().getStringExtra("canDelete").equals("false")){
            delete.setEnabled(false);
            delete.setVisibility(View.GONE);
        }

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

    public class deleteListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {

            Intent returnIntent = new Intent();
            returnIntent.putExtra("delete", "true");
            returnIntent.putExtra("IDNumber", eventID);
            setResult(MapsActivity.RESULT_OK,returnIntent);
            finish();

        }
    }
}
