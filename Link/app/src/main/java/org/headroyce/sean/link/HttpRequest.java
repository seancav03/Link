package org.headroyce.sean.link;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sean on 12/19/18.
 */

public class HttpRequest extends AsyncTask<String, Void, String> {

    // The Centralized Application URL to use
    // public static final String theUrl = "https://powerful-sands-36300.herokuapp.com/";
    public static final String theUrl = "http://10.10.10.119:3775/";
//    public static final String theUrl = "http://10.40.0.129:3775/";

    //https://www.headroyce.org/calendar/team_31.ics


    private LList<keyAndValue> params = new LList<keyAndValue>();
    private String requestType;

    public HttpRequest(String requestType, LList<keyAndValue> params){
        this.params = params;
        this.requestType = requestType;
    }

    //O(n) (n is length of LList<keyAndValue> params)
    @Override
    protected String doInBackground(String... args) {
        String StringURL = args[0];
        String result = "";
        String inputLine;

        try {
            URL url = new URL(StringURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestType);
            if (params != null) {

                // Create URL Encoded key/value pairs for now
                // This is probably better done as JSON-encodeds
                StringBuilder output = new StringBuilder();

                boolean p = false;
                for (keyAndValue pair : params) {

                    if( pair.getKey().equals("pic") ){
                        p = true;
                    }
                    output.append("&"+pair.getKey()+"="+pair.getValue());
                    //connection.addRequestProperty(pair.getKey(), pair.getValue());
                }

                // Remove the & from the first parameter
                output.delete(0,1);
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                if( p ) {
                    Log.d("pic2", output.toString());
                }
                bw.write(output.toString());
                bw.flush();
                bw.close();
                out.close();
            }

            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);

            connection.connect();

            //Create a new InputStreamReader
            InputStreamReader streamReader = new
                    InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while ((inputLine = reader.readLine()) != null) {
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            result = stringBuilder.toString();

        } catch (IOException e) {
            Log.d("Connection 2", e.getMessage());
        }
        return result;
    }
}



//testing stuff ignore
//                Uri.Builder builder = new Uri.Builder();
//                if(params != null){
//                    for(keyAndValue pair : params){
//                        builder.appendQueryParameter(pair.getKey(), pair.getValue());
//                    }
//                }
//                String query = builder.build().getEncodedQuery();
//                OutputStream os = connection.getOutputStream();
//                BufferedWriter writer = new BufferedWriter(
//                        new OutputStreamWriter(os, "UTF-8"));
//                writer.write(query);
//                writer.flush();
//                writer.close();
//                os.close();
