package com.farmigo.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.farmigo.app.Fragments.HomeFragment;
import com.farmigo.app.Fragments.MandiFragment;
import com.farmigo.app.Fragments.MandiFragment1;
import com.farmigo.app.Fragments.MycropsFragment;
import com.farmigo.app.Fragments.NewsFragment;
import com.farmigo.app.Fragments.WeatherFragment;
import com.farmigo.app.Fragments.WeatherFragmentNew;
import com.farmigo.app.Helpers.RoundedCornersTransform;
import com.farmigo.app.Models.User;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.activities.Profile_Activity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.CurrentWeatherCallback;
import com.kwabenaberko.openweathermaplib.models.common.Main;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private TextView location;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference ;
    private String lang,taluka;
    private double lat,lon;
    private OpenWeatherMapHelper helper;

    @Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser().getUid()==null)
        {
            Intent intent = new Intent(this, Select_lang_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());

        SharedPreferences prefs = Objects.requireNonNull(this).getSharedPreferences("pref", Context.MODE_PRIVATE);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String zipcode = prefs.getString("zipcode", "");
        lang = prefs.getString("lang", "en");
        String email = prefs.getString("selected_village", "")+", "+prefs.getString("selected_state","");
        String name = prefs.getString("name", "");
        taluka = prefs.getString("taluka", "");
       // String prof_url = prefs.getString("profile_uri", "xyz");
        lat = Double.valueOf(prefs.getString("lat", ""));
        lon = Double.valueOf(prefs.getString("lon", ""));

        Bundle bundle = new Bundle();
        bundle.putString("zipcode", zipcode);
        bundle.putString("lang", lang);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        View headerView = navigationView.getHeaderView(0);
        TextView Username =  headerView.findViewById(R.id.name);
        TextView Usermail =  headerView.findViewById(R.id.mail);
        ImageView Profile =  headerView.findViewById(R.id.nav_draweprofile);


        Username.setText(name);
        Usermail.setText(email);
        Profile.setImageResource(R.drawable.kissan);


        tabLayout =  findViewById(R.id.bottomNavigationView);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tabLayout.getSelectedTabPosition() == 0){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }else if(tabLayout.getSelectedTabPosition() == 1){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MycropsFragment()).commit();
                }else if(tabLayout.getSelectedTabPosition() == 2){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new MandiFragment1()).commit();
                }else if(tabLayout.getSelectedTabPosition() == 3){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new WeatherFragmentNew()).commit();
                }else if(tabLayout.getSelectedTabPosition() == 4){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new NewsFragment()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        getAddress(this,lat,lon);


        helper = new OpenWeatherMapHelper(getString(R.string.OPEN_WEATHER_MAP_API_KEY));


    }


    public void setVillage(String village) {
        location =  findViewById(R.id.location);
        location.setText(village);
    }



    public  void getAddress(Context context, double LATITUDE, double LONGITUDE) {
        //Set Address
        String city="",address="",SubLocality="",SubAdminArea="";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null && addresses.size() > 0) {
                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                String Premises = addresses.get(0).getPremises();
                SubAdminArea = addresses.get(0).getSubAdminArea();
                SubLocality = addresses.get(0).getSubLocality();
                String Thoroughfare = addresses.get(0).getThoroughfare();
                String SubThoroughfare = addresses.get(0).getSubThoroughfare();


            }


        } catch (IOException e) {
            e.printStackTrace();
        }


        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        if(!sharedPreferences.getString("selected_village","a").equals("a"))
        {
            setVillage(sharedPreferences.getString("selected_village",""));
        }
        else
        {
            if (taluka != null && !taluka.isEmpty() && !taluka.equals("null")){
                setVillage(taluka);
            }else if(SubLocality != null && !SubLocality.isEmpty() && !SubLocality.equals("null")){
                setVillage(SubLocality);
            }else if(SubAdminArea != null && !SubAdminArea.isEmpty() && !SubAdminArea.equals("null")){
                setVillage(SubAdminArea);
            }
        }

    }

    public void openWeathertab() {

        TabLayout.Tab tab = tabLayout.getTabAt(3);
        assert tab != null;
        tab.select();
    }

    public void openManditab() {

        TabLayout.Tab tab = tabLayout.getTabAt(2);
        assert tab != null;
        tab.select();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;

            case R.id.nav_profile:

                Intent intent = new Intent(this, Profile_Activity.class);
                intent.putExtra("fromNav", true);
                startActivity(intent);
                break;

            case R.id.nav_lang:

                Intent intent2 = new Intent(this, Select_lang_Activity.class);
                intent2.putExtra("fromNav", true);
                intent2.putExtra("locale", lang);
                startActivity(intent2);
                break;

            case R.id.nav_share:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                //Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_logout:
                //Toast.makeText(this, "Send", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent Intent4 = new Intent(this, Select_lang_Activity.class);
                startActivity(Intent4);
                break;

            case R.id.Contact_us:
                String number="14047898565";
                openWhatsApp(number);
                break;

            case R.id.settings:

                Intent Intent5 = new Intent(this, SettingsActivity.class);
                startActivity(Intent5);

                break;


        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void openWhatsApp(String toNumber){
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + "" + toNumber));
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
                Intent Intent4 = new Intent(this, Select_lang_Activity.class);
                startActivity(Intent4);
                return true;

            case R.id.item2:

                Intent intent = new Intent(this, Profile_Activity.class);
                intent.putExtra("fromNav", true);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
