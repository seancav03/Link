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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class loginActivity extends AppCompatActivity {

    private Button login;
    private Button createNewAccount;
    private ImageButton back;

    private EditText username;
    private EditText password;

    //O(1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //back button
        back = findViewById(R.id.back4);
        back.setOnClickListener(new backListener());

        //get buttons
        login = findViewById(R.id.Login);
        login.setOnClickListener(new LoginListener());
        createNewAccount = findViewById(R.id.CreateNew);
        createNewAccount.setOnClickListener(new CreateNewAccountListener());
        //get text fields
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);


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

    public class LoginListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {

            String user = username.getText().toString();
            String pass = password.getText().toString();

            if(user == null || pass == null){

                username.setError("Enter a username and password for your account");
                return;
            } else if(user.length() == 0 || pass.length() == 0){

                username.setError("Username or password must have length");
                return;
            }


            LList<keyAndValue> params = new LList<keyAndValue>();
            params.add(new keyAndValue("username", user));
            params.add(new keyAndValue("password", pass));

            //create object to send request to server
            HttpRequest request = new HttpRequest("POST", params);


            String url = HttpRequest.theUrl + "login/";
            String result = "";

            try {
                result = request.execute(url).get();
            }
            catch( Exception e ){
                Log.d( "CONNECTION 1", e.getMessage());
            }

            if(result.equals("Success")){
                Toast.makeText(loginActivity.this, "Welcome " + user,
                        Toast.LENGTH_SHORT).show();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("username", user);
                setResult(MapsActivity.RESULT_OK,returnIntent);
                finish();

            } else if (result.equals("Incorrect username and password")){

                username.setError("Invalid username or password");

            } else {

                username.setError("Bad Connection. Find Better Connection.");
                Log.d("Result", result);

            }

        }
    }

    //create new account with entered username and password
    public class CreateNewAccountListener implements View.OnClickListener {
        //O(1)
        @Override
        public void onClick(View v) {

            String user = username.getText().toString();
            String pass = password.getText().toString();

//            Log.d("ADDING USER", "ADDING");

            if(user == null || pass == null){

                username.setError("Enter a username and password for your account");
                return;
            } else if(user.length() == 0 || pass.length() == 0){

                username.setError("Username or password must have length");
                return;
            } else if(user.length() > 25){
                username.setError("Username must be 25 characters or fewer");
                return;
            }

//            Log.d("RAN", "RAAAAAAAAAAAAAAAAAN");

            //create params for logging in
            LList<keyAndValue> params = new LList<keyAndValue>();
            params.add(new keyAndValue("username", user));
            params.add(new keyAndValue("password", pass));

            //create object to send request to server
            HttpRequest request = new HttpRequest("POST", params);

            String url = HttpRequest.theUrl + "newUser/";
            String result = "";

            try {
                result = request.execute(url).get();
            }
            catch( Exception e ){
//                Log.d( "CONNECTION 1", e.getMessage());
            }
//            Log.d("Result -+-+-+-", result);

            if(result.equals("Failed to add. Username already taken")){

                username.setError("Username already taken. Please try another username");

            } else if (result.equals("User successfully added")){

                Toast.makeText(loginActivity.this, "Account Created! Welcome " + user,
                        Toast.LENGTH_SHORT).show();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("username", user);
                setResult(MapsActivity.RESULT_OK,returnIntent);
                finish();

            } else {
                Log.d("Result", "Bad Result: " + result);
            }

        }
    }

}
