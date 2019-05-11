package org.headroyce.sean.link;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

public class Profile extends AppCompatActivity {

    //Views from layout
    private ImageButton friends;
    private ImageButton logout;
    private TextView username;
    private ImageButton back;
    private ImageButton pickProfile;

    private ImageView profilePic;

    private int SELECT_PROFILE_PIC_REQUEST_CODE = 2468;
//    private static String theUrl = "https://powerful-sands-36300.herokuapp.com/";
    private static String theUrl = "http://10.10.10.119:3775/";

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
        pickProfile = findViewById(R.id.profilePicker);
        pickProfile.setOnClickListener(new editProfilePictureListener());
        profilePic = findViewById(R.id.Icon);


        //get profile pic and put it in if there is one
        getProfPic(user);

    }

    //get user's profile pic
    public void getProfPic(String username){

        LList<keyAndValue> params = new LList<keyAndValue>();
        params.add(new keyAndValue("username", user));

        //create object to send request to server
        HttpRequest request = new HttpRequest("GET", params);

        String url = theUrl + "getProfilePic/";
        String result = "";

        try {
            result = request.execute(url).get();
        }
        catch( Exception e ) {
            Log.d("CONNECTION 111", e.getMessage());
        }

        //check what I get back for if there is an image stored
        if(result.equals("NONE") || result.equals("")){
            Log.d("NO Image Available", "NONE");
            //DO NOTHING. NO PROFILE PIC. SHOULD I GIVE MESSAGE HERE ASKING FOR ONE?
        } else {
            byte[] decodedString = Base64.decode(result, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            profilePic.setImageBitmap(decodedByte);
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

    //add a profile picture
    public class editProfilePictureListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, SELECT_PROFILE_PIC_REQUEST_CODE);
        }
    }

    //converts bitmap photo to String to send
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == SELECT_PROFILE_PIC_REQUEST_CODE){
            Uri targetUri = data.getData();
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));

                //TODO: WORK HERE
                String imageStr = getStringImage(bitmap);

                LList<keyAndValue> params = new LList<keyAndValue>();
                params.add(new keyAndValue("username", user));
                params.add(new keyAndValue("pic", imageStr));

                //create object to send request to server
                HttpRequest request = new HttpRequest("POST", params);

                String url = theUrl + "editProfilePic/";
                String result = "";

                try {
                    result = request.execute(url).get();
                }
                catch( Exception e ) {
                    Log.d("CONNECTION 1", e.getMessage());
                }
                Toast.makeText(Profile.this, "Added Profile Pic",
                        Toast.LENGTH_SHORT).show();

                //set image
                profilePic.setImageBitmap(bitmap);


            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
