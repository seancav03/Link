package org.headroyce.sean.link;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sean on 12/19/18.
 */

public class HttpRequest extends AsyncTask<String, Void, String> {

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
            if(params != null){
                for(keyAndValue pair : params){
                    connection.addRequestProperty(pair.getKey(), pair.getValue());
                }
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
            //Log.d("Connection 2", e.getMessage());
        }
        return result;
    }
}
