package com.farmigo.app.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.farmigo.app.Adapters.Hourly_forecast_Adapter;
import com.farmigo.app.Adapters.Weather_forecast_Adapter;
import com.farmigo.app.Models.Todays_hourly_Forcast;
import com.farmigo.app.Models.WeatherForcast;
import com.farmigo.app.R;
import com.google.protobuf.StringValue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@author Vivek Lahole} subclass.
 */
public class WeatherFragmentNew extends Fragment {

    private ArrayList<WeatherForcast> forcastList =new ArrayList<>();
    private ArrayList<Todays_hourly_Forcast> hourlyForcasts =new ArrayList<>();
    private RecyclerView mRecyclerView,mRecyclerView2;
    private Weather_forecast_Adapter mAdapter;
    private Hourly_forecast_Adapter mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView temp,desc,date,humidity,prec,wind,time,currentdate;
    private ImageView img;
    private ConstraintLayout bgImag;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder;
    private AlertDialog progressDialog;;

    public WeatherFragmentNew() {
            //Empty Constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_weather, container, false);

        temp = view.findViewById(R.id.textView61);
        desc = view.findViewById(R.id.textView64);
        date = view.findViewById(R.id.textView65);
        humidity = view.findViewById(R.id.textView67);
        prec = view.findViewById(R.id.textView69);
        wind = view.findViewById(R.id.textView71);
        img = view.findViewById(R.id.imageView6);
        bgImag = view.findViewById(R.id.constraintLayout);
        time=view.findViewById(R.id.time);
//        progressBar=view.findViewById(R.id.progressBar5);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView2 = view.findViewById(R.id.recyclerView4);

        progressDialog=getDialogProgressBar().show();

        SharedPreferences prefs = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String lat = prefs.getString("lat","");
        String lon = prefs.getString("lon","");
        String lang = prefs.getString("lang", "en");

        String[] parameters={lat,lon,lang,getString(R.string.OPEN_WEATHER_MAP_API_KEY)};

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String timeString = dateFormat.format(new Date());
        time.setText(timeString);

        Date date1 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date1);

        new GetWheatherMapData().execute(parameters);

        return view;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd MMM", cal).toString();
        return date;
    }

    private String getDay(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date dateFormat = new java.util.Date(time * 1000);
        String weekday = sdf.format(dateFormat);
        return weekday;
    }

    private  String convertSecondsToHMmSs(long seconds) {

        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 12;

        return String.format("%d:%02d", h,m);
    }

    public AlertDialog.Builder getDialogProgressBar() {

        if (builder == null) {
            builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.layout_progress_custom);
        }
        return builder;
    }

<<<<<<< HEAD
    private class GetWheatherMapData extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject=new JSONObject(s);
                String temperature= jsonObject.getJSONObject("current").getString("temp");
                int testdate= jsonObject.getJSONObject("current").getInt("dt");
                int humidityValue=jsonObject.getJSONObject("current").getInt("humidity");
                String windValue=jsonObject.getJSONObject("current").getString("wind_speed");
                String description=jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
                String currentDate = getDate(jsonObject.getJSONObject("current").getInt("dt"));
                String precipitation=jsonObject.getJSONArray("minutely").getJSONObject(0).getString("precipitation");

                double t1 = Double.parseDouble(temperature);
                int t2 = (int) t1;

                temp.setText(String.valueOf(t2));
                date.setText(currentDate);
                humidity.setText(String.format("%s %%", String.valueOf(humidityValue)));
                wind.setText(String.format("%s km/h", windValue));
                prec.setText(String.format("%s mm",precipitation));

                String str = description;
                String[] strArray = str.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String st : strArray) {
                    String cap = st.substring(0, 1).toUpperCase() + st.substring(1);
                    builder.append(cap + " ");
                }
                desc.setText(builder.toString());

                if(str.contains("clear sky")){
                    bgImag.setBackgroundResource(R.drawable.sunny);
                    img.setImageResource(R.drawable.ic_sunny);
                }else if(str.contains("clouds")){
                    bgImag.setBackgroundResource(R.drawable.cloudy);
                    img.setImageResource(R.drawable.ic_cloudy);
                }else if(str.contains("rain")){
                    bgImag.setBackgroundResource(R.drawable.rain);
                    img.setImageResource(R.drawable.ic_rain);
                }else if(str.contains("thunderstorm")){
                    bgImag.setBackgroundResource(R.drawable.thunder);
                    img.setImageResource(R.drawable.ic_thunderstorm);
                }else if(str.contains("snow")){
                    bgImag.setBackgroundResource(R.drawable.snow);
                    img.setImageResource(R.drawable.ic_snow);
                }else if(str.contains("mist")){
                    bgImag.setBackgroundResource(R.drawable.mist);
                    img.setImageResource(R.drawable.ic_mist);
                }

                JSONArray hourly=jsonObject.getJSONArray("hourly");

                for(int i=0;i<48;i++)
                {
                    Log.d("DATe", "onPostExecute123: "+getDate((hourly.getJSONObject(i).getInt("dt")))+i);
                    if(getDate((hourly.getJSONObject(i).getInt("dt"))).equals(currentDate))
                    {
                        Todays_hourly_Forcast todays_hourly_forcast= new Todays_hourly_Forcast();

                        todays_hourly_forcast.setTime(convertSecondsToHMmSs(hourly.getJSONObject(i).getInt("dt")));
                        String t= String.valueOf((int)(hourly.getJSONObject(i).getDouble("temp")));
                        todays_hourly_forcast.setTemp(t);
                        todays_hourly_forcast.setIcon(hourly.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));

                        hourlyForcasts.add(todays_hourly_forcast);
                    }
                }

                JSONArray daily=jsonObject.getJSONArray("daily");

                for(int i=0;i<8;i++)
                {
                    WeatherForcast weatherForcast= new WeatherForcast();
                    weatherForcast.setDate(getDate(Long.parseLong(daily.getJSONObject(i).getString("dt"))));
                    weatherForcast.setDay(getDay(Long.parseLong(daily.getJSONObject(i).getString("dt"))));
                    String t= String.valueOf((int)(daily.getJSONObject(i).getJSONObject("temp").getDouble("max")))+"°C | "+String.valueOf((int)(daily.getJSONObject(i).getJSONObject("temp").getDouble("min")))+"°C";
                    weatherForcast.setTemp(t);
                    weatherForcast.setIcon(daily.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("icon"));

                    forcastList.add(weatherForcast);
                }

                mRecyclerView.setHasFixedSize(true);
                mLayoutManager = new LinearLayoutManager(getContext());
                mAdapter = new Weather_forecast_Adapter(forcastList);
                mRecyclerView.setLayoutManager(mLayoutManager);
                mRecyclerView.setAdapter(mAdapter);

                mRecyclerView2.setHasFixedSize(true);
                mAdapter2 = new Hourly_forecast_Adapter(hourlyForcasts);
                mRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView2.setAdapter(mAdapter2);

                progressDialog.dismiss();;
            } catch (JSONException e) {
                Log.d("GetWheatherMapData", "onPostExecute: "+e.toString());
                e.printStackTrace();
                progressDialog.setTitle("Error Getting Data");
                progressDialog.dismiss();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String response ;

            try {

                com.farmigo.app.Helpers.GetWheatherMapData getWheatherMapData=new com.farmigo.app.Helpers.GetWheatherMapData();
                String responseURL=String.format("https://api.openweathermap.org/data/2.5/onecall?lat="+strings[0]+"&lon="+strings[1]+"&units=metric&lang"+strings[2]+"&appid="+strings[3]);
                response= getWheatherMapData.getHTTPData(responseURL);
                Log.d("GetWheatherMapData", "doInBackground: "+response);
                return response;
            } catch (Exception e) {
                Log.d("GetWheatherMapData", "doInBackground: ");
                e.printStackTrace();
                progressDialog.setTitle("Error Getting Data");
                progressDialog.dismiss();
            }
            return null;
        }
=======
  
>>>>>>> 9a99941807a11791a83bb7aaf9eb6a83865254bf
    }
}
