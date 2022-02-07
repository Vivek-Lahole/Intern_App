package com.farmigo.app.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GetWheatherMapData {

    public GetWheatherMapData() {
    }

    public String getHTTPData(String requestURL)
    {
        URL url;
        String response="";

        try {
            url=new URL(requestURL);
            HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(15000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            int ResponseCode=httpURLConnection.getResponseCode();

            if(ResponseCode==httpURLConnection.HTTP_OK)
            {
                String key;
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                while ((key= bufferedReader.readLine())!=null)
                {
                    response+=key;
                }
            }
            else {
                Log.d("getHTTPData", "getHTTPData: "+ResponseCode);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
