package com.farmigo.app.Helpers;

import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class getReverseGeoCoding {
    private String address1, address2, city, state, country, county, PIN;
    private static final String LOG_TAG = getReverseGeoCoding.class.getSimpleName();

    public getReverseGeoCoding(double latitude, double longitude) {
        init();
        retrieveData(latitude, longitude);
    }

    private void retrieveData(double latitude, double longitude) {
        try {
            String responseFromHttpUrl = getResponseFromHttpUrl(buildUrl(latitude, longitude));
            JSONObject jsonResponse = new JSONObject(responseFromHttpUrl);
            String status = jsonResponse.getString("status");
            if (status.equalsIgnoreCase("OK")) {
                JSONArray results = jsonResponse.getJSONArray("results");
                JSONObject zero = results.getJSONObject(0);
                JSONArray addressComponents = zero.getJSONArray("address_components");

                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject zero2 = addressComponents.getJSONObject(i);
                    String longName = zero2.getString("long_name");
                    JSONArray types = zero2.getJSONArray("types");
                    String type = types.getString(0);


                    if (!TextUtils.isEmpty(longName)) {
                        if (type.equalsIgnoreCase("street_number")) {
                            address1 = longName + " ";
                        } else if (type.equalsIgnoreCase("route")) {
                            address1 = address1 + longName;
                        } else if (type.equalsIgnoreCase("sublocality")) {
                            address2 = longName;
                        } else if (type.equalsIgnoreCase("locality")) {
                            // address2 = address2 + longName + ", ";
                            city = longName;
                        } else if (type.equalsIgnoreCase("administrative_area_level_2")) {
                            county = longName;
                        } else if (type.equalsIgnoreCase("administrative_area_level_1")) {
                            state = longName;
                        } else if (type.equalsIgnoreCase("country")) {
                            country = longName;
                        } else if (type.equalsIgnoreCase("postal_code")) {
                            PIN = longName;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        address1 = "";
        address2 = "";
        city = "";
        state = "";
        country = "";
        county = "";
        PIN = "";
    }

    private URL buildUrl(double latitude, double longitude) {
        Uri uri = Uri.parse("http://maps.googleapis.com/maps/api/geocode/json").buildUpon()
                .appendQueryParameter("latlng", latitude + "," + longitude)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "can't construct location object");
            return null;
        }
    }

    private String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getCounty() {
        return county;
    }

    public String getPIN() {
        return PIN;
    }

}

