package com.farmigo.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.CommodityAdapter;
import com.farmigo.app.BuildConfig;
import com.farmigo.app.Models.Commodities;
import com.farmigo.app.Models.UID_commodities;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firestore.v1.WriteResult;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;

public class MandiViewActivity extends AppCompatActivity implements View.OnClickListener {

    String title, mandi_lat, mandi_lon, add, state;
    private ProgressBar progressBar;
    private ConstraintLayout layout;
    private ArrayList<Commodities> mExampleList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    public CommodityAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView searchView, addtext;
    private String filters = "";
    private ImageView whatsapp, bookmark1, bookmark2;
    private LinearLayout addmandi;
    private int selectedCard;
    private File imagePath;
    private long old_price, new_price;
    private boolean bookmark,checkbookmark=false;
    public static final int PERMISSION_REQUEST_CODE = 1001;
    private String mymandis = "", mycrops = "";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private boolean fruitFlag = false, vegitablesFlag = false, cerelsFlag = false, flowersFlag = false, micsFlag = false, spicesFlag = false, oilseedsFlag = false;

    @Override
    protected void onStart() {
        super.onStart();

        if(checkbookmark)
        {
            SharedPreferences sph=getSharedPreferences("isadded",MODE_PRIVATE);
//            Log.d("HighChartActivity1", "onStop: "+sph.getInt("pos",0));
            Log.d("HighChartActivity1", "onStop: "+selectedCard+" "+checkbookmark);
            Log.d("HighChartActivity1", "onStop: "+sph.getBoolean("isadded",false));


            if(sph.getBoolean("isadded",false))
            {
                mAdapter.getItem(selectedCard).setBookmark(true);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                mAdapter.getItem(selectedCard).setBookmark(false);
                mAdapter.notifyDataSetChanged();
            }
        }

        checkbookmark=true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        checkbookmark = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());
        setContentView(R.layout.activity_mandi_view);

        TextView mTitle = findViewById(R.id.toolbar_title);

        ImageView directions = findViewById(R.id.directions);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBar4);
        layout = findViewById(R.id.constraintLayout6);
        addmandi = findViewById(R.id.addmandi);
        layout.setVisibility(View.GONE);
        whatsapp = findViewById(R.id.imageButton2);
        bookmark1 = findViewById(R.id.bookmark1);
        bookmark2 = findViewById(R.id.bookmark2);
        addtext = findViewById(R.id.addtext);

        /*SharedPreferences prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        //String users_lat = prefs.getString("lat", "");
        String users_lon = prefs.getString("lon", "");
         */

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = getIntent().getStringExtra("title");
        add = getIntent().getStringExtra("add");
        state = getIntent().getStringExtra("state");
        bookmark = getIntent().getBooleanExtra("bookmark", false);
       /* mandi_lat = getIntent().getStringExtra("lat");
        mandi_lon = getIntent().getStringExtra("lon");
        */

        mTitle.setText(title);

        if (bookmark) {
            bookmark1.setVisibility(View.GONE);
            bookmark2.setVisibility(View.VISIBLE);
            addtext.setText("Remove");

        } else {
            bookmark1.setVisibility(View.VISIBLE);
            bookmark2.setVisibility(View.GONE);
            addtext.setText("Add");
        }

        searchView = findViewById(R.id.search_mandi);


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence query, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable query) {

                // filter your list from your input
                searchfilter(query.toString());
                //you can use runnable postDelayed like 500 ms to delay search text

                // mAdapter.getFilter().filter(query);
            }
        });

        directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+users_lat+","+users_lon+"&daddr="+mandi_lat+","+mandi_lat));
                startActivity(intent);


                String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?daddr=%f,%f (%s)", 12f, 2f, "Where the party is at");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                */


                Uri navigationIntentUri = Uri.parse("http://maps.google.co.in/maps?q=" + title + " " + add);

                // Uri navigationIntentUri = Uri.parse("google.navigation:q=" + mandi_lat +"," + mandi_lon);//creating intent with latlng
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

                try {
                    startActivity(mapIntent);
                } catch (ActivityNotFoundException ex) {
                    try {
                        Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                        startActivity(unrestrictedIntent);
                    } catch (ActivityNotFoundException innerEx) {
                        Toast.makeText(MandiViewActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        ThemedButton Fruits = findViewById(R.id.Fruits);
        Fruits.setOnClickListener(this);
        ThemedButton Vegitables = findViewById(R.id.Vegetables);
        Vegitables.setOnClickListener(this);
        ThemedButton Cerels = findViewById(R.id.Cereals);
        Cerels.setOnClickListener(this);
        ThemedButton Flowers = findViewById(R.id.Pulses);
        Flowers.setOnClickListener(this);
        ThemedButton mics = findViewById(R.id.Other);
        mics.setOnClickListener(this);
        ThemedButton Spices = findViewById(R.id.Spices);
        Spices.setOnClickListener(this);
        ThemedButton Oilseeds = findViewById(R.id.OilSeeds);
        Oilseeds.setOnClickListener(this);


        getmyMandisString();
        getResults();
        mRecyclerView = findViewById(R.id.recyler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new CommodityAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new CommodityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // changeItem(position, "Clicked");
                // Toast.makeText(MandiViewActivity.this,mAdapter.getItem(position).getName()+" crop addded", Toast.LENGTH_SHORT).show();
                if (mAdapter.getItem(position).getBookmark()) {

                    //mAdapter.setVisibility(true);
                    mAdapter.getItem(position).setBookmark(false);
                    mAdapter.notifyDataSetChanged();
                    mycrops = mycrops.replace(mAdapter.getItem(position).getName() + mAdapter.getItem(position).getType() + title + ",", "");
                    //reference.child("my_crops_string").setValue(mycrops);
                    documentReference.update("my_crops_string", mycrops);
                    SharedPreferences sph= getSharedPreferences("pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor=sph.edit();
                    editor.putString("my_crops_string",mycrops);
                    editor.apply();

                    //reference.child("my_crops").child(mAdapter.getItem(position).getName()+mAdapter.getItem(position).getType()+title).removeValue();

                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.get("my_crops") != null) {
                                    Map<String, Object> mp = (Map<String, Object>) snapshot.get("my_crops");
                                    Log.d("item", "onComplete: " + mp);
                                    mp.remove(mAdapter.getItem(position).getName() + mAdapter.getItem(position).getType() + title);
                                    Log.d("item", "onComplete: " + mp);
                                    documentReference.update("my_crops", mp);
                                }
                            }
                        }
                    });

                   /* bookmark1.setVisibility(View.VISIBLE);
                    bookmark2.setVisibility(View.GONE);
                    bookmark=false;

                    mymandis = mymandis.replace(title+",","");
                    reference.child("my_mandis").setValue(mymandis);
                    Toast.makeText(MandiViewActivity.this,"Removed from my mandis", Toast.LENGTH_SHORT).show();
                    addtext.setText("Add");
                    */
                    Toasty.error(MandiViewActivity.this, "Removed from My Crops!", Toast.LENGTH_SHORT, true).show();


                } else {

                    //mAdapter.setVisibility(false);
                    mAdapter.getItem(position).setBookmark(true);
                    mAdapter.notifyDataSetChanged();
                    mycrops += mAdapter.getItem(position).getName() + mAdapter.getItem(position).getType() + title + ",";
//                    reference.child("my_crops_string").setValue(mycrops);
                    //Toast.makeText(getApplicationContext(), "hello"+mycrops, Toast.LENGTH_SHORT).show();
                    documentReference.update(("my_crops_string"), mycrops);

                    ///Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_SHORT).show();

                    UID_commodities obj = new UID_commodities();

                    obj.setName(mAdapter.getItem(position).getName());
                    obj.setApmc_name(title);
                    obj.setColor1(mAdapter.getItem(position).getColor1());
                    obj.setColor2(mAdapter.getItem(position).getColor2());
                    obj.setImg(mAdapter.getItem(position).getImg());
                    obj.setType(mAdapter.getItem(position).getType());
                    obj.setCategory(mAdapter.getItem(position).getCategory());
                    obj.setState(state);

                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.get("my_crops") != null) {
                                    Map<String, Object> mp = (Map<String, Object>) snapshot.get("my_crops");
                                    mp.put(mAdapter.getItem(position).getName() + mAdapter.getItem(position).getType() + title, obj);
                                    documentReference.update("my_crops", mp);
                                } else {
                                    Map<String, Object> mp = new HashMap<>();
                                    mp.put(mAdapter.getItem(position).getName() + mAdapter.getItem(position).getType() + title, obj);
                                    documentReference.update("my_crops", mp);
                                }
                            }
                        }
                    });

                    Toasty.success(MandiViewActivity.this, "Added to My Crops!", Toast.LENGTH_SHORT, true).show();


                }
            }
        });


        mAdapter.setOnCardClickListener(new CommodityAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                //OnItemClicked(position, "Clicked");

                Intent intent = new Intent(MandiViewActivity.this, HighChartActivity.class);
                intent.putExtra("apmc_name", title);
                intent.putExtra("commodity_name", mAdapter.getItem(position).getName());
                intent.putExtra("commodity_type", mAdapter.getItem(position).getType());
                intent.putExtra("state", state);
                intent.putExtra("category",mAdapter.getItem(position).getCategory());
                intent.putExtra("my_crops_string",mycrops);
                intent.putExtra("pos",position);
                selectedCard=position;
                startActivity(intent);
            }
        });


        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        // Code for above or equal 23 API Oriented Device
                        // Your Permission granted already .Do next code

                        Bitmap bitmap = takeScreenshot();
                        // saveBitmap(bitmap);
                        shareIt(bitmap);

                    } else {
                        requestPermission(); // Code for permission
                    }
                } else {

                    // Code for Below 23 API Oriented Device
                    // Do next code
                    Bitmap bitmap = takeScreenshot();
                    //saveBitmap(bitmap);
                    shareIt(bitmap);
                }

            }
        });


        addmandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bookmark) {
                    bookmark1.setVisibility(View.VISIBLE);
                    bookmark2.setVisibility(View.GONE);
                    bookmark = false;

                    mymandis = mymandis.replace(title + ",", "");
//                    reference.child("my_mandis").setValue(mymandis);
                    documentReference.update("my_mandis", mymandis);
                    //Toast.makeText(MandiViewActivity.this,"Removed from my mandis", Toast.LENGTH_SHORT).show();
                    Toasty.info(MandiViewActivity.this, "Removed from My Mandis!", Toast.LENGTH_SHORT, true).show();
                    addtext.setText("Add");


                } else {
                    bookmark1.setVisibility(View.GONE);
                    bookmark2.setVisibility(View.VISIBLE);
                    bookmark = true;

                    mymandis += title + ",";
//                    reference.child("my_mandis").setValue(mymandis);
                    documentReference.update("my_mandis", mymandis);
                    Toasty.success(MandiViewActivity.this, "Added to My Mandis!", Toast.LENGTH_SHORT, true).show();
                    //Toast.makeText(MandiViewActivity.this,"Added to my mandis", Toast.LENGTH_SHORT).show();
                    addtext.setText("Remove");

                }

            }
        });

    }

    public Bitmap takeScreenshot() {
        //  View rootView = findViewById(R.id.recyler).getRootView();
        View rootView = findViewById(R.id.recyler);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public void saveBitmap(Bitmap bitmap) {
        imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }


    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MandiViewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(MandiViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

      /*  if (ActivityCompat.shouldShowRequestPermissionRationale(MandiViewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MandiViewActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MandiViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

       */
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                    Bitmap bitmap = takeScreenshot();
                    //saveBitmap(bitmap);
                    shareIt(bitmap);
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }


    private void shareIt(Bitmap bitmap) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);

        /// Uri imageUri = FileProvider.getUriForFile(MandiViewActivity.this, BuildConfig.APPLICATION_ID + ".provider", imagePath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        //Target whatsapp:
        shareIntent.setPackage("com.whatsapp");
        shareIntent.setType("image/*");
        String shareBody = getResources().getString(R.string.share_caption) + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        //Add text and then Image URI
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);


        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }


    }


    private void getmyMandisString() {

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w("TAG", "listen:error", error);
                    return;
                }

                if (value.exists()) {
                    mymandis = (String) value.getString("my_mandis");
                    if (value.getString("my_crops_string") != null) {
                        mycrops = (String) value.getString("my_crops_string");
                    }

                }

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Fruits:
                if (!fruitFlag) {
                    filters += "Fruits,";
                    filters(filters);
                    fruitFlag = true;

                } else {
                    filters = filters.replace("Fruits,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    fruitFlag = false;
                }
                break;
            case R.id.Vegetables:

                if (!vegitablesFlag) {
                    filters += "Vegetables,";
                    filters(filters);
                    vegitablesFlag = true;

                } else {
                    filters = filters.replace("Vegetables,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    vegitablesFlag = false;
                }

                break;
            case R.id.Cereals:

                if (!cerelsFlag) {
                    filters += "Cereals,";
                    filters(filters);
                    cerelsFlag = true;

                } else {
                    filters = filters.replace("Cereals,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    cerelsFlag = false;
                }

                break;
            case R.id.Pulses:

                if (!flowersFlag) {
                    filters += "Pulses,";
                    filters(filters);
                    flowersFlag = true;

                } else {
                    filters = filters.replace("Pulses,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    flowersFlag = false;
                }

                break;
            case R.id.Other:

                if (!micsFlag) {
                    Log.d("checking", "onClick: on");
                    filters += "Fibre Crops,Oil and Fats,Drug and Narcotics,Live Stock,Poultry,Fisheries,Beverages,Forest Products,Flowers,Local,";
                    filters(filters);
                    micsFlag = true;

                } else {
                    Log.d("checking", "onClick: off");
                    filters = filters.replace("Fibre Crops,Oil and Fats,Drug and Narcotics,Live Stock,Poultry,Fisheries,Beverages,Forest Products,Flowers,Local,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    micsFlag = false;
                }

                break;
            case R.id.Spices:

                if (!spicesFlag) {
                    filters += "Spices,";
                    filters(filters);
                    spicesFlag = true;

                } else {
                    filters = filters.replace("Spices,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    spicesFlag = false;
                }

                break;
            case R.id.OilSeeds:

                if (!oilseedsFlag) {
                    filters += "Oil Seeds,";
                    filters(filters);
                    oilseedsFlag = true;

                } else {
                    filters = filters.replace("Oil Seeds,", "");
                    if (filters.isEmpty()) {
                        searchfilter(filters);
                    } else {
                        filters(filters);
                    }
                    oilseedsFlag = false;
                }

                break;
            default:
                break;
        }
    }


    private void getResults() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String currentDate = sdf.format(date);


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = sdf.format(cal.getTime()); //your formatted date here


        cal.add(Calendar.DATE, -1);
        String dayBeforeyesterday = sdf.format(cal.getTime()); //your formatted date here


        db.collection("commodities").whereEqualTo(title,true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mExampleList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                               // if (document.getBoolean(title) != null) {

                                    Commodities t = new Commodities();

                                    String name = document.getString("name");
                                    String type = document.getString("type");

                                    String nametype = name.concat(type);
                                    String nametypetitle = nametype.concat(title);


                                    t.setName(name);
                                    t.setCategory(document.getString("category"));
                                    // t.setPrice(snapshot.child("new_price").getValue().toString());
                                    t.setColor1(document.getString("color1"));
                                    t.setColor2(document.getString("color2"));
                                    //t.setRise(snapshot.child("rise").getValue().toString());
                                    //t.setDate(snapshot.child("date").getValue().toString());
                                    t.setImg(document.getString("img"));
                                    t.setType(type);
                                    // t.setDays(String.valueOf(Daybetween(snapshot.child("date").getValue().toString(),todayString,"yyyy-mm-dd")));

                                    if (mycrops.contains(nametypetitle)) {
                                        t.setBookmark(true);
                                        //System.out.println("********************true*******");
                                    } else {
                                        t.setBookmark(false);
                                    }

                                    db.collection("price_table").document(title).collection(nametype).orderBy("Arrival_Date", Query.Direction.DESCENDING).whereLessThanOrEqualTo("Arrival_Date", currentDate).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                                            if (error != null) {
                                                Log.w("TAG", "listen:error", error);
                                                return;
                                            }

                                            Boolean b1 = true, b2 = true;
                                            new_price = 0;
                                            old_price = 0;

                                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                                if (snapshot.exists() && b1) {
                                                    new_price = snapshot.getLong("Modal_Price");
//
                                                    t.setDate(snapshot.getString("Arrival_Date"));
                                                    b1 = false;
                                                } else if (snapshot.exists() && b2) {
                                                    old_price = snapshot.getLong("Modal_Price");
                                                    b2 = false;
                                                }
                                            }


                                            t.setPrice(String.valueOf(new_price));
                                            long increase = new_price - old_price;

                                            long percentage_change = 0;
                                            if (old_price != 0) {
                                                percentage_change = (increase * 100) / old_price;
                                            }
                                            Log.d("old_price", "onComplete: " + old_price + "percent_Change " + percentage_change);

                                            t.setPercentage(String.valueOf(percentage_change));
                                            t.setRise(String.valueOf(increase));
                                            mExampleList.add(t);
                                            mAdapter.notifyDataSetChanged();
                                            progressBar.setVisibility(View.GONE);
                                            layout.setVisibility(View.VISIBLE);
                                            Log.e("The read success: ", "success2");


                                        }
                                    });


                            }

                        } else {
                            Log.d("getMandis", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public String getStringFormatted(String datestring) {
        String format = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(datestring.replaceAll("-", "/")));
    }

    private void searchfilter(String text) {
        ArrayList<Commodities> temp = new ArrayList<>();
        for (Commodities d : mExampleList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
//            Log.d("searchfilter", "searchfilter: "+d.getName().toLowerCase());
//            if(text.toLowerCase().contains(d.getName().toLowerCase()))
//            {
//                temp.add(d);
//            }
        }

        //update recyclerview
        mAdapter.updateList(temp);
    }

    private void filters(String text) {
        ArrayList<Commodities> temp = new ArrayList<>();
        for (Commodities d : mExampleList) {

            if(text.toLowerCase().contains(d.getCategory().toLowerCase()))
            {
                temp.add(d);
            }
        }

        //update recyclerview
        mAdapter.updateList(temp);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
