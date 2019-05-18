package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class FeedActivity extends AppCompatActivity {


    //back button
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        //back button
        back = findViewById(R.id.back2);
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
}
