package com.farmigo.app.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.Preference;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Adapters.Hourly_forecast_Adapter;
import com.farmigo.app.Adapters.MycropsAdapter_home;
import com.farmigo.app.Adapters.Weather_forecast_Adapter;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.Models.Todays_hourly_Forcast;
import com.farmigo.app.Models.WeatherForcast;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.polyak.iconswitch.IconSwitch;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.MycropsAdapter_home;
import com.farmigo.app.Models.News;
import com.farmigo.app.activities.HighChartActivity;
import com.farmigo.app.activities.MainActivity;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.R;
import com.farmigo.app.activities.NewsActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.polyak.iconswitch.IconSwitch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * A simple {@author Vivek Lahole} subclass.
 */
public class HomeFragment extends Fragment implements IconSwitch.CheckedChangeListener {

    private TextView Description;
    private TextView temp, precipitation;
    private TextView humidity;
    private TextView bulletin_head, bulletin_date, bulletin_desc;
    private ImageView img;
    private String lang, zipcode;
    ;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private Button addCrops;
    private static final String TAG = "HomeFragment";
    ;


    private ArrayList<Fav_commodities> mExampleList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private long old_price, new_price, Cprice, Yprice, DBYPrice;
    private String new_date = "";
    private MycropsAdapter_home mAdapter;
    private GridLayoutManager mLayoutManager;
    private IconSwitch iconSwitch;
    private String lat, lon, distance, Nearest_apmc, Nearest_state;
    private int minDist = (Integer.MAX_VALUE);
    private CardView weather_card, news_card;
    private String news_artical, news_date, news_heading, news_img, news_src;
    private String mymandis = "";
    private String my_crops_string = "";
    private AlertDialog.Builder builder;
    private AlertDialog progressDialog;;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private CollectionReference collectionReference = db.collection(("price_table"));


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if(auth.getCurrentUser().getUid()==null)
        {
            Intent intent = new Intent(getContext(), Select_lang_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());

        SharedPreferences prefs = requireActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        String zipcode = prefs.getString("zipcode", "");
        lang = prefs.getString("lang", "en");
        boolean isNewUser = prefs.getBoolean("isNewUser", false);
        lat = prefs.getString("lat", "");
        lon = prefs.getString("lon", "");


        iconSwitch = (IconSwitch) view.findViewById(R.id.switch1);
        iconSwitch.setCheckedChangeListener(this);

        progressDialog=getDialogProgressBar().show();

        img = view.findViewById(R.id.imageView3);
        Description = view.findViewById(R.id.textView17);
        temp = view.findViewById(R.id.textView34);
        humidity = view.findViewById(R.id.textView31);
        progressBar = view.findViewById(R.id.progressBar2);

        bulletin_head = view.findViewById(R.id.textView18);
        bulletin_desc = view.findViewById(R.id.textView19);
        bulletin_date = view.findViewById(R.id.textView20);
        precipitation = view.findViewById(R.id.textView28);
        layout = view.findViewById(R.id.bulletinlayout);
        weather_card = view.findViewById(R.id.cardView2);
        news_card = view.findViewById(R.id.cardView3);

        addCrops = view.findViewById(R.id.button3);

        if (lang.equals("hi") || lang.equals("mar")) {
            lang = "hi";
        } else {
            lang = "en";
        }

        String[] parameters={lat,lon,lang,getString(R.string.OPEN_WEATHER_MAP_API_KEY)};

        new GetWheatherMapData().execute(parameters);

        if (isNewUser) {
            getNearestMandi2();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("isNewUser", false);
            editor.apply();
        } else {
            getData();
        }

        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.HORIZONTAL, false);

        mAdapter = new MycropsAdapter_home(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnCardClickListener(new MycropsAdapter_home.OnCardClickListener() {
            @Override
            public void onCardClick(View v, int position) {

                Intent intent = new Intent(getContext(), HighChartActivity.class);
                intent.putExtra("apmc_name", mAdapter.getItem(position).getApmc_name());
                intent.putExtra("commodity_name", mAdapter.getItem(position).getName());
                intent.putExtra("commodity_type", mAdapter.getItem(position).getType());
                intent.putExtra("state", mAdapter.getItem(position).getState());
                intent.putExtra("category",mAdapter.getItem(position).getCategory());
                intent.putExtra("my_crops_string",my_crops_string);
                SharedPreferences sph= getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sph.edit();
                editor.putString("my_crops_string",my_crops_string);
                editor.apply();
                startActivity(intent);
            }
        });

        getBulletin();

        weather_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openWeathertab();
            }
        });

        addCrops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) requireActivity()).openManditab();
            }
        });

        news_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewsActivity.class);
                intent.putExtra("heading", news_heading);
                intent.putExtra("news", news_artical);
                intent.putExtra("date", news_date);
                intent.putExtra("src", news_src);
                intent.putExtra("img", news_img);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCheckChanged(IconSwitch.Checked current) {

        switch (iconSwitch.getChecked()) {
            case LEFT:
                mAdapter.setVisibility(true);

                break;
            case RIGHT:
                mAdapter.setVisibility(false);

                break;

        }

    }


    private void getData() {

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    if (snapshot.getString("my_crops_string") != null) {
                        if (!snapshot.getString("my_crops_string").equals("")) {
                            addCrops.setVisibility(View.GONE);
                            getResults();
                        } else {

                            addCrops.setVisibility(View.VISIBLE);
//                            getNearestMandi();
                            progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            //getResults();
                        }
                    }
                    else{
                        progressDialog.dismiss();
                        progressBar.setVisibility(View.GONE);
                        addCrops.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                    progressDialog.dismiss();

                }
            }
        });
    }

    private void getResults() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mExampleList.clear();
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Map<String, Object> mp = new HashMap<>();
                        mp = documentSnapshot.getData();
                        Map<String, Object> crop = (Map<String, Object>) mp.get("my_crops");
                        my_crops_string=(String) mp.get("my_crops_string");

                        if(crop!=null)
                        {
                            if(!crop.isEmpty())
                            {
                                Iterator iterator = crop.keySet().iterator();
                                while (iterator.hasNext()) {
                                    String key = (String) iterator.next();
                                    Map<String, Object> Cfeilds = (Map<String, Object>) crop.get(key);

                                    Fav_commodities t = new Fav_commodities();
                                    String name = (String) Cfeilds.get("name");
                                    String type = (String) Cfeilds.get("type");
                                    String apmc_name = (String) Cfeilds.get("apmc_name");
                                    String nametype = name.concat(type);

                                    t.setName(name);
                                    t.setApmc_name(apmc_name);

                                    t.setCategory((String) Cfeilds.get("category"));
                                    t.setState((String) Cfeilds.get("state"));
                                    t.setColor1((String) Cfeilds.get("color1"));
                                    t.setColor2((String) Cfeilds.get("color2"));
                                    t.setImg((String) Cfeilds.get("img"));
                                    t.setType(type);

                                    GetLatestPrices(apmc_name, nametype, currentDate, new GetLatestPrices() {
                                        @Override
                                        public void AfterPricecallBack(long new_price, long old_price, String new_date) {
                                            Log.d("oncallback", "AfterPricecallBack: " + new_price + " " + old_price);

                                            t.setDate(new_date);

                                            t.setPrice(String.valueOf(new_price));
                                            long increase = new_price - old_price;

                                            long percentage_change = 0;
                                            if (old_price != 0) {
                                                percentage_change = (increase * 100) / old_price;
                                            }

                                            t.setPercentage(String.valueOf(percentage_change));
                                            t.setRise(String.valueOf(increase));
                                            mExampleList.add(t);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    });
                                }
                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                progressDialog.dismiss();
                                addCrops.setVisibility(View.VISIBLE);
                            }
                        }
                        else {
                            progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                            addCrops.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    public AlertDialog.Builder getDialogProgressBar() {

        if (builder == null) {
            builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.layout_progress_custom);
        }
        return builder;
    }


    private String distance(String latB, String logB) {

        Location locationA = new Location("point A");

        locationA.setLatitude(Double.valueOf(lat));
        locationA.setLongitude(Double.valueOf(lon));

        Location locationB = new Location("point B");

        locationB.setLatitude(Double.valueOf(latB));
        locationB.setLongitude(Double.valueOf(logB));

        distance = String.valueOf(Math.round(locationA.distanceTo(locationB) / 1000));

        return distance;
    }

    public interface FirestorecallbackForCommodityList {
        void oncallback(Map<String, Object> listCommodity);
    }

    public interface FirestorecallbackNearestapmcs {
        void onNearestapmcCallback(String Nearestapms,String NearestState);
    }

    public interface GetLatestPrices {
        void AfterPricecallBack(long new_price, long old_price, String new_date);
    }

    public void GetLatestPrices(String apmc_name, String nametype, String currentDate, GetLatestPrices getLatestPrices) {
        collectionReference.document(apmc_name).collection(nametype).orderBy("Arrival_Date", Query.Direction.DESCENDING).whereLessThanOrEqualTo("Arrival_Date", currentDate).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }

                Boolean b1 = true, b2 = true;
                new_price = 0;
                old_price = 0;

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot.exists() && b1) {
                        new_price = snapshot.getLong("Modal_Price");
                        new_date = snapshot.getString("Arrival_Date");
                        b1 = false;
                    } else if (snapshot.exists() && b2) {
                        old_price = snapshot.getLong("Modal_Price");
                        b2 = false;
                    }
                }
                getLatestPrices.AfterPricecallBack(new_price, old_price, new_date);
            }
        });
    }

    public void DataFromCallback(String Nearest_apmc, FirestorecallbackForCommodityList firestorecallbackForCommodityList) {
        Map<String, Object> listCommodity = new HashMap<>();
        db.collection("commodities").whereEqualTo(Nearest_apmc, true).limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        listCommodity.put(document.getString("name") + document.getString("type"), true);
                    }
                    firestorecallbackForCommodityList.oncallback(listCommodity);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void GetNearestapmcs(FirestorecallbackNearestapmcs firestorecallbackNearestapmcs) {
        db.collection("apmcs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        if (minDist >= Integer.parseInt(distance(snapshot.getString("lat"), snapshot.getString("lon")))) {
                            minDist = Integer.parseInt(distance(snapshot.getString("lat"), snapshot.getString("lon")));
                            Nearest_apmc = snapshot.getString("name");
                            Nearest_state = snapshot.getString("state");
                        }
                    }
                    firestorecallbackNearestapmcs.onNearestapmcCallback(Nearest_apmc,Nearest_state);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void getNearestMandi2() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);

        GetNearestapmcs(new FirestorecallbackNearestapmcs() {
            @Override
            public void onNearestapmcCallback(String Nearestapms,String NearestState) {
                Log.d("onNearestapmcCallback", "onNearestapmcCallback1: " + Nearestapms);
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            //mymandis = snapshot.getString("my_mandis");
                            mymandis += Nearestapms + ",";
                            documentReference.update("my_mandis", mymandis);

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

                DataFromCallback(Nearestapms, new FirestorecallbackForCommodityList() {
                    @Override
                    public void oncallback(Map<String, Object> listCommodity) {

                        Log.d("getNearestMandi2()", "oncallback: "+listCommodity);

                        Iterator iterator = listCommodity.keySet().iterator();

                        Map<String, Object> mp1 = new HashMap<>();

                        while (iterator.hasNext()) {

                            String key = (String) iterator.next();
                            Log.d("oncallback", "onEvent oncallback: " + key);

                            db.collection("commodities").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {
                                        DocumentSnapshot snapshot = task.getResult();
                                        Log.d("oncallback", "onComplete snap: " + snapshot);
//                                        if (snapshot.exists()) {
                                        Fav_commodities t = new Fav_commodities();
                                        String name = (String) snapshot.getString("name");
                                        String type = (String) snapshot.getString("type");
                                        String apmc_name = (String) Nearestapms;
                                        String nametype = name.concat(type);
                                        my_crops_string += nametype + Nearestapms + ",";

                                        t.setName(name);
                                        t.setApmc_name(apmc_name);
                                        t.setCategory((String) snapshot.getString("category"));
                                        t.setState(Nearest_state);
                                        t.setColor1((String) snapshot.getString("color1"));
                                        t.setColor2((String) snapshot.getString("color2"));
                                        t.setImg((String) snapshot.getString("img"));
                                        t.setType(type);
                                        mp1.put(key + Nearestapms, t);

                                        GetLatestPrices(Nearest_apmc, snapshot.getString("name") + snapshot.getString("type"), currentDate, new GetLatestPrices() {
                                            @Override
                                            public void AfterPricecallBack(long new_price, long old_price, String new_date) {
                                                Log.d("oncallback", "AfterPricecallBack: " + new_price + " " + old_price);

                                                t.setDate(new_date);

                                                t.setPrice(String.valueOf(new_price));
                                                long increase = new_price - old_price;

                                                long percentage_change = 0;
                                                if (old_price != 0) {
                                                    percentage_change = (increase * 100) / old_price;
                                                }

                                                t.setPercentage(String.valueOf(percentage_change));
                                                t.setRise(String.valueOf(increase));
                                                mExampleList.add(t);
                                                mAdapter.notifyDataSetChanged();
                                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot snapshot = task.getResult();
                                                            documentReference.update("my_crops_string", my_crops_string);
                                                            documentReference.update("my_crops", mp1);
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    e.printStackTrace();
                                }
                            });

                        }
                        Log.d("mexample", "onEvent: " + mExampleList);
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }


    private void getBulletin() {


        db.collection("news").limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    new LoadBackground(snapshot.getString("img"),
                            "bulletinbackground").execute();
                    news_artical = snapshot.getString("artical");
                    news_date = snapshot.getString("date");
                    news_heading = snapshot.getString("heading");
                    news_img = snapshot.getString("img");
                    news_src = snapshot.getString("arc");

                    bulletin_desc.setText(news_artical);
                    bulletin_date.setText(changeDate(news_date, "yyyy-MM-dd"));
                    bulletin_head.setText(news_heading);
                }
            }
        });
    }


    public class LoadBackground extends AsyncTask<String, Void, Drawable> {

        private String imageUrl, imageName;

        private LoadBackground(String url, String file_name) {
            this.imageUrl = url;
            this.imageName = file_name;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Drawable doInBackground(String... urls) {

            try {
                InputStream is = (InputStream) this.fetch(this.imageUrl);
                Drawable d = Drawable.createFromStream(is, this.imageName);
                return d;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private Object fetch(String address) throws MalformedURLException, IOException {
            URL url = new URL(address);
            Object content = url.getContent();
            return content;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            super.onPostExecute(result);
            layout.setBackground(result);

        }
    }

    public String changeDate(String date1, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("dd MMM yyyy");
        String yourFormatedDateString = sdf.format(date);

        return yourFormatedDateString;
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
                String temperature= jsonObject.getJSONObject("current").getString("temp");
                int humidityValue=jsonObject.getJSONObject("current").getInt("humidity");
                String description=jsonObject.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("description");
                String precipitation1=jsonObject.getJSONArray("minutely").getJSONObject(0).getString("precipitation");

                double t1 = Double.parseDouble(temperature);
                int t2 = (int) t1;

                temp.setText(String.valueOf(t2));
                humidity.setText(String.format("%s", String.valueOf(humidityValue)));
                precipitation.setText(String.format("%s",precipitation1));

                String str = description;
                String[] strArray = str.split(" ");
                StringBuilder builder = new StringBuilder();
                for (String st : strArray) {
                    String cap = st.substring(0, 1).toUpperCase() + st.substring(1);
                    builder.append(cap + " ");
                }
                Description.setText(builder.toString());

                if (lang.equals("en")) {

                    if (str.contains("clear sky")) {
                        // Picasso.get().load(R.drawable.ic_sunny).into(img);
                        img.setImageResource(R.drawable.ic_sunny);
                    } else if (str.contains("clouds")) {
                        img.setImageResource(R.drawable.ic_cloudy);
                    } else if (str.contains("rain")) {
                        img.setImageResource(R.drawable.ic_rain);
                    } else if (str.contains("thunderstorm")) {
                        img.setImageResource(R.drawable.ic_thunderstorm);
                    } else if (str.contains("snow")) {
                        img.setImageResource(R.drawable.ic_snow);
                    } else if (str.contains("mist") || str.contains("Smoke") || str.contains("Haze") || str.contains("fog")) {
                        img.setImageResource(R.drawable.ic_mist);
                    }
                } else {
                    if (str.contains("साफ")) {
                        // Picasso.get().load(R.drawable.ic_sunny).into(img);
                        img.setImageResource(R.drawable.ic_sunny);
                    } else if (str.contains("बादल")) {
                        img.setImageResource(R.drawable.ic_cloudy);
                    } else if (str.contains("वर्षा")) {
                        img.setImageResource(R.drawable.ic_rain);
                    } else if (str.contains("तूफान")) {
                        img.setImageResource(R.drawable.ic_thunderstorm);
                    } else if (str.contains("हिमपात")) {
                        img.setImageResource(R.drawable.ic_snow);
                    } else if (str.contains("धुंध") || str.contains("धुआं") || str.contains("धुन्ध") || str.contains("कोहरा")) {
                        img.setImageResource(R.drawable.ic_mist);
                    }
                }

            } catch (JSONException e) {
                Log.d("GetWheatherMapData", "onPostExecute: "+e.toString());
                e.printStackTrace();
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
            }
            return null;
        }
    }

}
