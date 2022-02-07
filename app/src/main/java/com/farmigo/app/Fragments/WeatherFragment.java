package com.farmigo.app.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.Hourly_forecast_Adapter;
import com.farmigo.app.Adapters.Weather_forecast_Adapter;
import com.farmigo.app.Helpers.GetWheatherMapData;
import com.farmigo.app.Models.Todays_hourly_Forcast;
import com.farmigo.app.Models.WeatherForcast;
import com.farmigo.app.R;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.ThreeHourForecastCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

//import static androidx.constraintlayout.motion.widget.MotionScene.TAG;

/**
 * A simple {@author Vivek Lahole} subclass.
 */
public class WeatherFragment extends Fragment {




    public WeatherFragment() {

    }

    private ArrayList<WeatherForcast> forcastList =new ArrayList<>();
    private ArrayList<Todays_hourly_Forcast> hourlyForcasts =new ArrayList<>();
    private RecyclerView mRecyclerView,mRecyclerView2;
    private Weather_forecast_Adapter mAdapter;
    private Hourly_forecast_Adapter mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView temp,desc,date,humidity,prec,wind,time,max,min;
    private ImageView img;
    private ConstraintLayout bgImag;
    private ProgressBar progressBar;
    private static final String TAG="123";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_weather, container, false);

        new GetWheatherMapData().execute("");

        temp = view.findViewById(R.id.textView61);
        desc = view.findViewById(R.id.textView64);
        date = view.findViewById(R.id.textView65);
        humidity = view.findViewById(R.id.textView67);
        prec = view.findViewById(R.id.textView69);
        wind = view.findViewById(R.id.textView71);
        img = view.findViewById(R.id.imageView6);
        bgImag = view.findViewById(R.id.constraintLayout);
        time=view.findViewById(R.id.time);
//        max=view.findViewById(R.id.max);
//        min=view.findViewById(R.id.min);

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        String dateString = dateFormat.format(new Date()).toString();
        time.setText(dateString);

//        progressBar=view.findViewById(R.id.progressBar5);

        Date date1 = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date1);


        SharedPreferences prefs = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String zipcode = prefs.getString("zipcode", "");
        String lat = prefs.getString("lat","");
        String lon = prefs.getString("lon","");
        Log.d(TAG, "onCreateView: "+lat+" "+lon);
        String lang = prefs.getString("lang", "en");


        OpenWeatherMapHelper helper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));
        helper.setUnits(Units.METRIC);
        if(lang.equals("hi")){
            lang="hi";
        }else {
            lang="en";
        }

        helper.setLang(lang);


        helper.getCurrentWeatherByGeoCoordinates( Double.parseDouble(lat), Double.parseDouble(lon), new CurrentWeatherCallback() {
            @Override
            public void onSuccess(CurrentWeather currentWeather) {
               /* Log.v(TAG, "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon() +"\n"
                        +"Weather Description: " + currentWeather.getWeather().get(0).getDescription() + "\n"
                        +"Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                        +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                        +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
                );
                */

                //Picasso.get().load("https://openweathermap.org/img/wn/"+ currentWeather.getWeather().get(0).getIcon()+"@2x.png").into(img);

                String str = currentWeather.getWeather().get(0).getDescription();
                String[] strArray = str.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String s : strArray) {
                    String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                    builder.append(cap + " ");
                }
                desc.setText(builder.toString());

                //Description.setText(currentWeather.getWeather().get(0).getDescription());
                double t1 = currentWeather.getMain().getTempMax();
                int t2 = (int) t1;
                temp.setText(String.valueOf(t2));


                humidity.setText(String.format("%s %%", String.valueOf(currentWeather.getMain().getHumidity())));

                //prec.setText(currentWeather.get);
                date.setText(getDate(currentWeather.getDt()));


                wind.setText(String.format("%s km/h", currentWeather.getWind().getSpeed()));


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

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.v(TAG, throwable.getMessage());
            }
        });




        helper.getThreeHourForecastByGeoCoordinates(Double.parseDouble(lat), Double.parseDouble(lon), new ThreeHourForecastCallback() {
            @Override
            public void onSuccess(ThreeHourForecast threeHourForecast) {

                for(int i=0;i<8;i++) {

                    Todays_hourly_Forcast t=new Todays_hourly_Forcast();

                    if(threeHourForecast.getList().get(i).getDtTxt().contains(currentDate)){


                        t.setTime(convertSecondsToHMmSs(threeHourForecast.getList().get(i).getDt()));
                        double t1 = threeHourForecast.getList().get(i).getMain().getTempMax();
                        int t2 = (int) t1;
                        t.setTemp(String.valueOf(t2));
                        t.setIcon(threeHourForecast.getList().get(i).getWeatherArray().get(0).getIcon());

                        hourlyForcasts.add(t);


                        /*System.out.println("Date:"+getDate(threeHourForecast.getList().get(i).getDt()));
                      System.out.println("Time: " + convertSecondsToHMmSs(threeHourForecast.getList().get(i).getDt()));
                      System.out.println("Temp:"+threeHourForecast.getList().get(i).getMain().getTempMax());
                      System.out.println("Icon:"+threeHourForecast.getList().get(i).getWeatherArray().get(0).getIcon());
                         */
                    }

                    mAdapter2.notifyDataSetChanged();
                }


               // int n = threeHourForecast.getCnt();
                int x=6;
                for(int i=0;i<5;i++) {

                    if (i>0){
                        x=x+8;
                    }


                    WeatherForcast t=new WeatherForcast();

                    t.setDate(getDate(threeHourForecast.getList().get(x).getDt()));
                    t.setDay(getDay(threeHourForecast.getList().get(x).getDt()));
                    t.setIcon(threeHourForecast.getList().get(x).getWeatherArray().get(0).getIcon());

                    double t1 = threeHourForecast.getList().get(x).getMain().getTempMax();
                    int t2 = (int) t1;

                    t.setTemp(String.format("%s°C", t2));

                    forcastList.add(t);

                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable) {
                progressBar.setVisibility(View.GONE);
                Log.v(TAG, throwable.getMessage());
            }
        });



        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new Weather_forecast_Adapter(forcastList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new Weather_forecast_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");
            }
        });


        mRecyclerView2 = view.findViewById(R.id.recyclerView4);
        mRecyclerView2.setHasFixedSize(true);
        mAdapter2 = new Hourly_forecast_Adapter(hourlyForcasts);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView2.setAdapter(mAdapter2);
        mAdapter2.setOnItemClickListener(new Hourly_forecast_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });

        return  view;
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
       // long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d", h,m);
    }

        private class GetWheatherMapData extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                JSONObject jsonObject=new JSONObject(s);
                String temp= jsonObject.getJSONObject("current").getString("temp");
//                String lon= ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry").getJSONObject("location").get("lon").toString();

                Log.d("GetWheatherMapData", "onPostExecute: "+temp);
            } catch (JSONException e) {
                Log.d("GetWheatherMapData", "onPostExecute: "+e.toString());
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            String response ;

            try {
                String add=strings[0];
                com.farmigo.app.Helpers.GetWheatherMapData getWheatherMapData=new com.farmigo.app.Helpers.GetWheatherMapData();
                String responseURL=String.format("https://api.openweathermap.org/data/2.5/onecall?lat=20.263848&lon=76.633712&exclude=minutely&units=metric&appid=1cb26593a6f3b38848ab0a55f39a4f98");
                response= getWheatherMapData.getHTTPData(responseURL);
                Log.d("GetWheatherMapData", "doInBackground: "+response);
                return response;
            } catch (Exception e) {
                Log.d("GetWheatherMapData", "doInBackground: ");
                e.printStackTrace();
            }
            return null;
        }
    }


}
