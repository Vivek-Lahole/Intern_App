package com.farmigo.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.GetLocationDetail;
import com.example.easywaylocation.Listener;
import com.example.easywaylocation.LocationData;
import com.farmigo.app.Fragments.CropViewFragment;
import com.farmigo.app.Helpers.GetRealtimeDataListener;
import com.farmigo.app.Helpers.RoundedCornersTransform;
import com.farmigo.app.Models.User;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.utility.MySpinner;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE;

import es.dmoral.toasty.Toasty;


public class Profile_Activity extends AppCompatActivity implements Listener, LocationData.AddressCallBack {

    ImageView profile;
    private static final String TAG = "20119055";
    public static final int REQUEST_IMAGE = 100;
    EditText mobile;
    EasyWayLocation easyWayLocation;
    public String lat, lon, zipcode, location, downloadUrl, Nearest_apmc, Nearest_state, distance;
    ;
    private String prof = "Farmer", location_string=null;
    private int last_selected = 0;
    private int minDist = (Integer.MAX_VALUE);
    private String selected_state, selecte_district, selected_taluka, selected_village;
    private int position;


    FusedLocationProviderClient mFusedLocationClient;
    EditText editTextField, name, email;
    ImageView nametick, emailtick, mobiletick, locpin, loctick, location_icon;
    User user = null;
    Button btncomplete;
    ImageButton backbutton;
    private MySpinner state_spinner, district_spinner, taluka_spinner, village_spinner;
    private Button ok;
    private EditText test, loc,location_check;
    private RadioButton farmer, trader;
    public ProgressBar progressBar;
    private ConstraintLayout constraintLayout2;
    private RadioButton gps, Manual, zip;
    private String img;


    TextView address, select_manually;
    int PERMISSION_ID = 44;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    String taluka;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private StorageReference mStorageRef;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean flag = true;
    public boolean fromNav = false;
    private Context mContext = Profile_Activity.this;
    private static final int REQUEST = 112;
    private Boolean location_added=true;

    @Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser().getUid()==null)
        {
            Intent intent = new Intent(this, Select_lang_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());

        mStorageRef = FirebaseStorage.getInstance().getReference();
        // AndroidNetworking.initialize(getApplicationContext());
        easyWayLocation = new EasyWayLocation(this, false, this);


        fromNav = getIntent().getBooleanExtra("fromNav", false);

        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        editTextField = new EditText(this);

        SharedPreferences shared = getSharedPreferences("USER_PREF", MODE_PRIVATE);
        String number = (shared.getString("phoneNumber", ""));

        profile = findViewById(R.id.profile);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        address = findViewById(R.id.location);
        select_manually = findViewById(R.id.textView13);
        nametick = findViewById(R.id.imageView5);
        emailtick = findViewById(R.id.email_tick);
        loctick = findViewById(R.id.location_tick);
        mobiletick = findViewById(R.id.mobile_tick);
        btncomplete = findViewById(R.id.button);
        backbutton = findViewById(R.id.backbutton);
        locpin = findViewById(R.id.location_pin);
        progressBar = findViewById(R.id.progressBar);
        loc = findViewById(R.id.loc);
        state_spinner = findViewById(R.id.state_spinner);
        district_spinner = findViewById(R.id.district_spinner);
        taluka_spinner = findViewById(R.id.taluka_spinner);
        village_spinner = findViewById(R.id.village_spinner);
        zip = findViewById(R.id.Use_Zipcode);
        Manual = findViewById(R.id.Manual_Address);
        gps = findViewById(R.id.GPS);
        farmer = findViewById(R.id.farmer);
        trader = findViewById(R.id.trader);
        location_check=findViewById(R.id.location_check);

        location_check.setKeyListener(null);
        location_check.setEnabled(false);


        nametick.setVisibility(View.GONE);
        emailtick.setVisibility(View.GONE);
        loctick.setVisibility(View.GONE);
        mobile.setText(number);
        mobile.setKeyListener(null);
        mobile.setEnabled(false);
        loc.setText(shared.getString("location", ""));
        loc.setKeyListener(null);
        loc.setEnabled(false);

        constraintLayout2 = findViewById(R.id.constraintLayout2);

        location_icon = findViewById(R.id.imageView18);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    onGetRealtimeDataSuccess("", snapshot);
                    if (snapshot.getString("location") != null && !snapshot.getString("location").equals("")) {
                        loc.setText(snapshot.getString("location"));
                    }
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        SharedPreferences sph = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sph.edit();

        selecte_district=sph.getString("selected_district","");
        selected_state=sph.getString("selected_state","");
        selected_taluka=sph.getString("selected_taluka","");
        selected_village=sph.getString("selected_village","");

        location_added=sph.getBoolean("location_added",true);
        location_string=sph.getString("location","");

        if (sph.getString("usertype", "Farmer").equals("Farmer")) {
            farmer.setChecked(true);
            trader.setChecked(false);
            profile.setImageResource(R.drawable.kissan);
        } else {
            farmer.setChecked(false);
            trader.setChecked(true);
            profile.setImageResource(R.drawable.trader);
        }

        farmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prof = "Farmer";

                if(user!=null)
                {
                    if(user.getImgurl()!=null)
                    {
                        Picasso.get().load(user.getImgurl()).placeholder(R.drawable.kissan).transform(new RoundedCornersTransform()).into(profile);
                    }
                    else
                    {
                        Picasso.get().load("https://storage.cloud.google.com/farmigo-app.appspot.com/images/Kissan.png?authuser=1").placeholder(R.drawable.kissan).transform(new RoundedCornersTransform()).into(profile);
                    }
                }
                else
                {
                    Picasso.get().load("https://storage.cloud.google.com/farmigo-app.appspot.com/images/Kissan.png?authuser=1").placeholder(R.drawable.kissan).transform(new RoundedCornersTransform()).into(profile);
                }
            }
        });

        trader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prof = "Trader";
                if(user!=null)
                {
                    if(user.getImgurl()!=null)
                    {
                        Picasso.get().load(user.getImgurl()).placeholder(R.drawable.kissan).transform(new RoundedCornersTransform()).into(profile);
                    }
                    else
                    {
                        Picasso.get().load("https://storage.cloud.google.com/farmigo-app.appspot.com/images/trader.jpg?authuser=1").placeholder(R.drawable.trader).transform(new RoundedCornersTransform()).into(profile);
                    }

                }
                else
                {
                    Picasso.get().load("https://storage.cloud.google.com/farmigo-app.appspot.com/images/trader.jpg?authuser=1").placeholder(R.drawable.trader).transform(new RoundedCornersTransform()).into(profile);
                }

            }
        });

        showMaunalDialog();


        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(mContext, PERMISSIONS)) {
                        ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST);
                    } else {
                        //do here
                        //pickImage();
                        onProfileImageClick();

                    }
                } else {
                    //do here
                    // pickImage();
                    onProfileImageClick();
                }
            }
        });


        btncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataSubmitted();
            }
        });

        name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                String strUserName = name.getText().toString();

                if (!TextUtils.isEmpty(strUserName)) {
                    //etUserName.setError("Your message");
                    nametick.setVisibility(View.VISIBLE);
                } else {
                    nametick.setVisibility(View.GONE);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                //  teach.setEnabled(true);
            }
        });


        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    onGetRealtimeDataSuccess("", snapshot);
                    if (snapshot.getString("location") != null && !snapshot.getString("location").equals("")) {
                        loc.setText(snapshot.getString("location"));
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fromNav) {
                    if(location_added)
                    {
//                      onDataSubmitted();
                        location_check.setError("Location Required");
                    }else
                    {
//                        onDataSubmitted();
                        finish();
                    }

                } else {
                    if(location_added)
                    {
                        onDataSubmitted();
                    }
                    else
                    {
                        onDataSubmitted();
                        Intent intent = new Intent(Profile_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });


    }

    public ArrayAdapter<String> CreateAdapter(ArrayList<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.layout_custom_spinner, list) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }


    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        if (fromNav) {
            finish();
            // overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        } else {
            Intent intent = new Intent(Profile_Activity.this, MainActivity.class);
            startActivity(intent);
            //overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        }
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_permission)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Profile_Activity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
//                        locationManager.requestLocationUpdates(provider, 400, 1, this);
                        doLocationWork();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                } else {
                    Toast.makeText(mContext, "The app was not allowed to read storage.", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void doLocationWork() {
        easyWayLocation.startLocation();
    }

    public void onGetRealtimeDataSuccess(String child, DocumentSnapshot dataSnapshot) {
        /*if(dataSnapshot.getValue(User.class)==null) {
            continueExecution();
        }
        else {

            User user = dataSnapshot.getValue(User.class);
            if(!user.getPhone().equals(auth.getCurrentUser().getPhoneNumber())) {
                continueExecution();
            }
            else {

         */
        user = dataSnapshot.toObject(User.class);
        continueExecutionWithUserDetails(user);
          /*  }
        }

           */
    }

    public void continueExecutionWithUserDetails(User user) {
        this.user = user;

        if (user.getImgurl() != null) {
            Picasso.get().load(user.getImgurl()).placeholder(R.drawable.kissan).transform(new RoundedCornersTransform()).into(profile);
        } else {
            if (prof.equals("Farmer")) {
                profile.setImageResource(R.drawable.kissan);
            } else {
                profile.setImageResource(R.drawable.trader);
            }
        }

        name.setText(user.getName());
        email.setText(user.getEmail());
        zipcode = user.getZipcode();
        lat = user.getLat();
        lon = user.getLon();
        String usertype = (user.getUsertype());

        if (fromNav) {
            address.setText(user.getLocationl());

        } else if (checkLocationPermission()) {
            doLocationWork();
        }

    }

    private void popup() {

        if (checkLocationPermission()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            // ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_edit_zipcode, null);
            dialogBuilder.setView(dialogView);

            EditText editText = (EditText) dialogView.findViewById(R.id.editText);
            // editText.setText("test label");

            dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    zipcode = editText.getText().toString();
                    new JsonTask().execute("https://nominatim.openstreetmap.org/search.php?country=india&postalcode=" + zipcode + "&polygon_geojson=1&format=jsonv2");

                }
            }); //End of alert.setPositiveButton
            dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                    dialog.cancel();
                }
            });//End of alert.setNegativeButton

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

    }

    public interface Getdistrictlist {
        void getdistrictlistcallback(ArrayList<String> list);
    }

    public interface Gettalukalist {
        void gettalukalistcallback(ArrayList<String> list);
    }

    public interface GetVillagelist {
        void getVillagelistcallback(ArrayList<String> list);
    }

    public void getvillagelist(String taluka, GetVillagelist getVillagelist) {

        ArrayList<String> village_temp = new ArrayList<>();
        db.collection("state").document(selected_state).collection(selecte_district).document(taluka).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    Map<String, Object> mp = snapshot.getData();

                    Iterator iterator = mp.keySet().iterator();

                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        Log.d("getVillagelistcallback", "onComplete: " + village_temp);
                        village_temp.add(key);
                    }

                    getVillagelist.getVillagelistcallback(village_temp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void gettalukalist(String district, Gettalukalist gettalukalist) {

        ArrayList<String> taluka_temp = new ArrayList<>();
        db.collection("state").document(selected_state).collection(district).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        taluka_temp.add(snapshot.getId());
                    }
                    gettalukalist.gettalukalistcallback(taluka_temp);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getdistrictlist(String state, Getdistrictlist getdistrictlist) {

        db.collection("state").document(state).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    ArrayList<String> district_temp = (ArrayList<String>) snapshot.get("district");
                    getdistrictlist.getdistrictlistcallback(district_temp);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void showMaunalDialog() {

        ArrayList<String> state_list=new ArrayList( Arrays.asList( getResources().getStringArray(R.array.india_states_all1)));
        ArrayAdapter<String> state_Adapter = CreateAdapter(state_list);
        SharedPreferences sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor1 = sharedPreferences.edit();

        String s,d,t,v;
        s=sharedPreferences.getString("selected_state","State");
        d=sharedPreferences.getString("selected_district","District");
        t=sharedPreferences.getString("selected_taluka","Taluka");
        v=sharedPreferences.getString("selected_village","Village");

        String[] param={s,d,t,v};
        ArrayList<String> selectedValues=new ArrayList(Arrays.asList(param));
        ArrayAdapter<String> initial=new ArrayAdapter<>(getApplicationContext(), R.layout.layout_custom_spinner, selectedValues);
//        state_spinner.setAdapter(initial);
//        state_spinner.setSelection(0,true);
        district_spinner.setAdapter(initial);
        district_spinner.setSelection(1,true);
        taluka_spinner.setAdapter(initial);
        taluka_spinner.setSelection(2,true);
        village_spinner.setAdapter(initial);
        village_spinner.setSelection(3,true);

        district_spinner.setEnabled(false);
        taluka_spinner.setEnabled(false);
        village_spinner.setEnabled(false);

        state_Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setAdapter(state_Adapter);
        state_spinner.setSelection(sharedPreferences.getInt("StatePos",0),true);

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d(TAG, "onItemSelected: "+i);

                if (i > 0) {
                    selected_state = adapterView.getItemAtPosition(i).toString();
                    position=i;
                    getdistrictlist(selected_state, new Getdistrictlist() {
                        @Override
                        public void getdistrictlistcallback(ArrayList<String> list) {
                            Collections.sort(list);
                            list.add(0, "District");
                            district_spinner.setAdapter(CreateAdapter(list));
                        }
                    });

                    district_spinner.setEnabled(true);
                    district_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            if (i > 0) {
                                selecte_district = adapterView.getItemAtPosition(i).toString();
                                gettalukalist(selecte_district, new Gettalukalist() {
                                    @Override
                                    public void gettalukalistcallback(ArrayList<String> list) {
                                        Collections.sort(list);
                                        list.add(0, "Taluka");
                                        taluka_spinner.setAdapter(CreateAdapter(list));
                                    }
                                });

                                taluka_spinner.setEnabled(true);
                                taluka_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                        if (i > 0) {
                                            selected_taluka = adapterView.getItemAtPosition(i).toString();
                                            getvillagelist(selected_taluka, new GetVillagelist() {
                                                @Override
                                                public void getVillagelistcallback(ArrayList<String> list) {
                                                    Collections.sort(list);
                                                    list.add(0, "village");
                                                    village_spinner.setAdapter(CreateAdapter(list));
                                                    ;
                                                }
                                            });

                                            village_spinner.setEnabled(true);
                                            village_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                                    if (i > 0) {
                                                        selected_village = adapterView.getItemAtPosition(i).toString();
                                                        location_string = selected_village + "," + selected_taluka + "," + selecte_district + "," + selected_state;
                                                        editor1.putBoolean("location_added",false);
                                                        location_added=false;
                                                        GeoLocate(location_string);

                                                        editor1.apply();
                                                    }
                                                }
                                                @Override
                                                public void onNothingSelected(AdapterView<?> adapterView) {

                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    // method to requestfor permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager
                locationManager
                = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == LOCATION_SETTING_REQUEST_CODE) {
            easyWayLocation.onActivityResult(resultCode);

        } else if (reqCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                StorageReference ref = mStorageRef.child("/images/" + auth.getUid() + ".jpg");

                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                    byte[] data1 = baos.toByteArray();

                    UploadTask uploadTask = ref.putBytes(data1);

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }

                            // Continue with the task to get the download URL
                            return ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                downloadUrl = downloadUri.toString();

                                documentReference.update("imgurl", downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(@NonNull Void unused) {
                                        Log.d(TAG, "imgurl updated");
                                    }
                                });
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    // loading profile image from local cache
                    // loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Picasso.get().load(uri).placeholder(R.drawable.trader).transform(new RoundedCornersTransform()).into(profile);
            }
        }

    }

    public interface FirestorecallbackNearestapmcs {
        void onNearestapmcCallback(String Nearestapms, String NearestState);
    }


    public void GetNearestapmcs(FirestorecallbackNearestapmcs firestorecallbackNearestapmcs) {
        progressBar.setVisibility(View.VISIBLE);
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
                    firestorecallbackNearestapmcs.onNearestapmcCallback(Nearest_apmc, Nearest_state);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private String distance(String latB, String logB) {

        Location locationA = new Location("point A");

        locationA.setLatitude(Double.valueOf(lat));
        locationA.setLongitude(Double.valueOf(lon));

        Location locationB = new Location("point B");

        locationB.setLatitude(Double.valueOf(latB));
        locationB.setLongitude(Double.valueOf(logB));

        //distance =  String.format(Locale.ENGLISH,"%.2f",locationA.distanceTo(locationB)/1000);
        distance = String.valueOf(Math.round(locationA.distanceTo(locationB) / 1000));

        return distance;
    }

    public void onDataSubmitted() {

        GeoLocate(location_string);

        progressBar.setVisibility(View.VISIBLE);
        // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();


        String userType = prof;


        if (TextUtils.isEmpty((name.getText()).toString())) {
            //findViewById(R.id.name).setBackgroundColor(getResources().getColor(R.color.red));
            name.setError("Name required");
            nametick.setVisibility(View.GONE);
        }
        else  if(location_added){
            location_check.setError("Location Required");
        }
        else {

            Map<String, Object> user_details = new HashMap<>();
            user_details.put("name", name.getText().toString());
            user_details.put("phone", mobile.getText().toString());
            user_details.put("email", sharedPreferences.getString("selected_village","")+","+sharedPreferences.getString("selected_state",""));
            user_details.put("usertype", prof);
            user_details.put("lat", lat);
            user_details.put("lon", lon);
            user_details.put("zipcode", zipcode);
            user_details.put("location",location_string);

            editor.putString("name", name.getText().toString());
            editor.putString("phone", mobile.getText().toString());
            editor.putString("email", sharedPreferences.getString("selected_village","")+","+sharedPreferences.getString("selected_state",""));
            editor.putString("usertype", prof);
            editor.putString("lat", lat);
            editor.putString("lon", lon);
            editor.putString("zipcode", zipcode);
            editor.putString("profile_uri", downloadUrl);
            editor.putString("prof", prof);
            editor.putString("location",location_string);
            editor.putString("selected_village", selected_village);
            editor.putString("location", location_string);
            editor.putString("lat", lat);
            editor.putString("lon", lon);
            editor.putString("taluka", selected_taluka);
            editor.putString("selected_taluka", selected_taluka);
            editor.putString("selected_district", selecte_district);
            editor.putString("selected_state", selected_state);
            editor.putInt("StatePos",position);

            if (user == null) {
                String timestamp = new Timestamp(new Date().getTime()).toString();
                documentReference.update("timestamp", timestamp);
                editor.putString("timestamp", timestamp);
            } else {
                editor.putString("timestamp", user.getTimestamp());
            }

            editor.apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            documentReference.set(user_details, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(getApplicationContext(), "DATA UPDATED", Toast.LENGTH_SHORT).show();
                            Toasty.success(getApplicationContext(), "DATA UPDATED", Toast.LENGTH_SHORT, true).show();
                            //Toast.makeText(getApplicationContext(), "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "ERROR DATA UPDATED", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

            progressBar.setVisibility(View.GONE);

            startActivity(intent);
        }
    }


    @Override
    public void locationOn() {
        Toast.makeText(this, "Location ON", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void currentLocation(Location location) {
        GetLocationDetail getLocationDetail = new GetLocationDetail(this, this);
        StringBuilder data = new StringBuilder();

        progressBar.setVisibility(View.VISIBLE);


        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf(location.getLongitude());

        getLocationDetail.getAddress(location.getLatitude(), location.getLongitude(), "AIzaSyDIdsye36hJBljtCcSsT07bAmPWvR70uJY");
        //Toast.makeText(this, data,Toast.LENGTH_LONG).show();

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void locationCancelled() {
        Toast.makeText(this, "Location Cancelled", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //easyWayLocation.startLocation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        easyWayLocation.endUpdates();

    }

    @Override
    public void locationData(LocationData locationData) {

        progressBar.setVisibility(View.VISIBLE);

        //Toast.makeText(this, "Setting location",Toast.LENGTH_LONG).show();
        if (flag) {
            address.setText(locationData.getFull_address());
            loc.setText(locationData.getFull_address());
            location = locationData.getFull_address();
            zipcode = locationData.getPincode();
            locpin.setVisibility(View.GONE);
            taluka = null;
            // loctick.setVisibility(View.VISIBLE);
            flag = false;
            //Toast.makeText(this, locationData.getFull_address(),Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }


    }


    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            progressBar.setVisibility(View.VISIBLE);


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String add = "";


            if (result != null) {
                try {

                    JSONArray arr = new JSONArray(result);
                    JSONObject jObj = arr.getJSONObject(0);
                    add = jObj.getString("display_name");
                    lat = jObj.getString("lat");
                    lon = jObj.getString("lon");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            location = add;
            address.setText(add);
            loc.setText(add);

            locpin.setVisibility(View.GONE);
            // loctick.setVisibility(View.VISIBLE);

            String[] splitData = add.split(",", 2);
            taluka = splitData[0];

            progressBar.setVisibility(View.GONE);

        }
    }

    // my button click function
    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        } else {
                            // TODO - handle permission denied case
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(Profile_Activity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Profile_Activity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void GeoLocate(String location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addressList = geocoder.getFromLocationName(location, 1);

            if (addressList.size() > 0) {
                Address address = addressList.get(0);
                lat = String.valueOf(address.getLatitude());
                lon = String.valueOf(address.getLongitude());

                SharedPreferences sph=getSharedPreferences("pref",MODE_PRIVATE);
                SharedPreferences.Editor editor= sph.edit();
                editor.putString("lat",lat);
                editor.putString("lon",lon);
                editor.apply();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}



