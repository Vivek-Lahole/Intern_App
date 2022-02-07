package com.farmigo.app.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.Historical_Prices_Adapter;
import com.farmigo.app.BuildConfig;
import com.farmigo.app.Models.ChartData;
import com.farmigo.app.Models.Commodities;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.R;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HICondition;
import com.highsoft.highcharts.common.hichartsclasses.HICredits;
import com.highsoft.highcharts.common.hichartsclasses.HICrosshair;
import com.highsoft.highcharts.common.hichartsclasses.HIEvents;
import com.highsoft.highcharts.common.hichartsclasses.HIExporting;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPoint;
import com.highsoft.highcharts.common.hichartsclasses.HIResponsive;
import com.highsoft.highcharts.common.hichartsclasses.HIRules;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HISpline;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import nl.bryanderidder.themedtogglebuttongroup.SelectAnimation;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

import static com.farmigo.app.activities.MandiViewActivity.PERMISSION_REQUEST_CODE;

public class HighChartActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HighChartActivity";

    private HISpline line1, line2, line3, line4, line5;
    private HIOptions options;
    private HIXAxis xaxis;
    private HIChartView chartView;
    private int i = 1;
    private FirebaseDatabase database;
    private LinearLayout addmandi;
    private TextView addtext;
    private ImageView bookmark1, bookmark2;
    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private ArrayList<HISpline> serieslist=new ArrayList<>();
    private Map<String,HISpline> seriesmap=new HashMap<>();
    private ArrayList<ChartData> mExampleList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private Historical_Prices_Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean flag = true;
    private int old_price;
    private String date = "";
    private Date date1;
    private String apmc_name, commodity_name, commodity_type, state,category,my_mandi_string="";
    private String selected_mandi = "";
    public Button three_day, one_week, one_month, one_year, six_month, all;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DocumentReference documentReference;
    private Boolean b1=false;
    private Map<String,Object> mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());

        setContentView(R.layout.activity_highcharts);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        progressBar = findViewById(R.id.progressBar6);
        addmandi = findViewById(R.id.addmandi);
        bookmark1 = findViewById(R.id.bookmark1);
        bookmark2 = findViewById(R.id.bookmark2);
        addtext=findViewById(R.id.addtext);

        SharedPreferences sph1=getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences sph=getSharedPreferences("isadded",MODE_PRIVATE);
        final SharedPreferences.Editor editor = sph.edit();

        apmc_name = getIntent().getStringExtra("apmc_name");
        commodity_name = getIntent().getStringExtra("commodity_name");
        commodity_type = getIntent().getStringExtra("commodity_type");
        state = getIntent().getStringExtra("state");
        category=getIntent().getStringExtra("category");
        my_mandi_string=sph1.getString("my_crops_string","");

        CheckBookmark();
        if(my_mandi_string.contains(commodity_name+commodity_type+apmc_name))
        {
            b1=true;
            bookmark2.setVisibility(View.VISIBLE);
            bookmark1.setVisibility(View.GONE);
            addtext.setText("Remove");
        }
        else
        {
            b1=false;
            bookmark2.setVisibility(View.GONE);
            bookmark1.setVisibility(View.VISIBLE);
            addtext.setText("Add");
        }

        addmandi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b1)
                {
                    b1=false;
                    bookmark2.setVisibility(View.GONE);
                    bookmark1.setVisibility(View.VISIBLE);
                    addtext.setText("Add");
                    editor.putBoolean("isadded",false);
                    editor.apply();

                    my_mandi_string = my_mandi_string.replace(commodity_name+commodity_type+apmc_name + ",", "");

                    if(mp!=null)
                    {
                        mp.remove(commodity_name+commodity_type+apmc_name);
                        documentReference.update("my_crops",mp);
                    }
                    documentReference.update("my_crops_string",my_mandi_string);
                }
                else
                {
                    b1=true;
                    bookmark2.setVisibility(View.VISIBLE);
                    bookmark1.setVisibility(View.GONE);
                    addtext.setText("Remove");
                    editor.putBoolean("isadded",true);
                    editor.apply();
                    my_mandi_string+=commodity_name+commodity_type+apmc_name+",";

                    Fav_commodities c= new Fav_commodities();
                    c.setCategory(category);
                    c.setColor1("#d9d598");
                    c.setColor2("#fffeee");
                    c.setImg("https://firebasestorage.googleapis.com/v0/b/farmigo-app.appspot.com/o/commodities%2FCabbage.png?alt=media&token=29652eb3-1684-4baf-a0b0-1c8cc35399ba");
                    c.setName(commodity_name);
                    c.setType(commodity_type);
                    c.setState(state);
                    c.setApmc_name(apmc_name);

                    if ((mp!=null))
                    {
                        mp.put(commodity_name+commodity_type+apmc_name,c);
                    }
                    else
                    {
                        mp=new HashMap<>();
                        mp.put(commodity_name+commodity_type+apmc_name,c);
                    }
                    documentReference.update("my_crops",mp);
                    documentReference.update("my_crops_string",my_mandi_string);
                }

            }
        });




        getData(apmc_name, commodity_name, commodity_name.concat(commodity_type));

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        mAdapter = new Historical_Prices_Adapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new Historical_Prices_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //changeItem(position, "Clicked");

            }
        });

        one_week = findViewById(R.id.button2);
        one_month = findViewById(R.id.button3);
        six_month = findViewById(R.id.button4);
        one_year = findViewById(R.id.button5);
        all = findViewById(R.id.button6);

        ImageButton share = findViewById(R.id.share);
        share.setOnClickListener(this);

        Button add = findViewById(R.id.add);

        all.setBackgroundResource(R.drawable.range_button_background);
        all.setTextColor(Color.WHITE);


        one_week.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Date date2 = new Date();
                Date today2 = new Date(date2.getTime() + (1000 * 60 * 60 * 24));
                Date week = new Date(today2.getTime() - 8 * (1000 * 60 * 60 * 24));
                long min2 = week.getTime();
                long max2 = today2.getTime();
                xaxis.setMax(max2);
                xaxis.setMin(min2);
                options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));


                one_week.setBackgroundResource(R.drawable.range_button_background);
                one_week.setTextColor(Color.WHITE);

                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setTextColor(Color.parseColor("#9b9b9b"));
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setTextColor(Color.parseColor("#9b9b9b"));
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setTextColor(Color.parseColor("#9b9b9b"));
                all.setBackgroundResource(R.drawable.range_button_background_default);
                all.setTextColor(Color.parseColor("#9b9b9b"));
            }
        });


        one_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Date date3 = new Date();
                Date today3 = new Date(date3.getTime() + (1000 * 60 * 60 * 24));

                Calendar c = Calendar.getInstance();   // this takes current date
                c.set(Calendar.MONTH, -1);

                Date datemin = c.getTime();

                long min3 = datemin.getTime();

                long max3 = today3.getTime();

                xaxis.setMax(max3);
                xaxis.setMin(min3);
                options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));


                one_month.setBackgroundResource(R.drawable.range_button_background);
                one_month.setTextColor(Color.WHITE);

                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setTextColor(Color.parseColor("#9b9b9b"));
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setTextColor(Color.parseColor("#9b9b9b"));
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setTextColor(Color.parseColor("#9b9b9b"));
                all.setBackgroundResource(R.drawable.range_button_background_default);
                all.setTextColor(Color.parseColor("#9b9b9b"));
            }
        });


        six_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Date date4 = new Date();
                Date today4 = new Date(date4.getTime() + (1000 * 60 * 60 * 24));

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MONTH, -6);
                Date result = cal.getTime();

                long min4 = result.getTime();
                long max4 = today4.getTime();

                xaxis.setMax(max4);
                xaxis.setMin(min4);
                options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));

                six_month.setBackgroundResource(R.drawable.range_button_background);
                six_month.setTextColor(Color.WHITE);

//                three_day.setBackgroundResource(R.drawable.range_button_background_default);
//                three_day.setTextColor(Color.parseColor("#9b9b9b"));
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setTextColor(Color.parseColor("#9b9b9b"));
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setTextColor(Color.parseColor("#9b9b9b"));
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setTextColor(Color.parseColor("#9b9b9b"));
                all.setBackgroundResource(R.drawable.range_button_background_default);
                all.setTextColor(Color.parseColor("#9b9b9b"));
            }
        });

        one_year.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                Date date5 = new Date();
                Date today5 = new Date(date5.getTime() + (1000 * 60 * 60 * 24));

                Calendar cal5 = Calendar.getInstance();
                cal5.add(Calendar.MONTH, -12);
                Date result5 = cal5.getTime();

                long min5 = result5.getTime();
                long max5 = today5.getTime();

                xaxis.setMax(max5);
                xaxis.setMin(min5);
                options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));


                one_year.setBackgroundResource(R.drawable.range_button_background);
                one_year.setTextColor(Color.WHITE);

//                three_day.setBackgroundResource(R.drawable.range_button_background_default);
//                three_day.setTextColor(Color.parseColor("#9b9b9b"));
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setTextColor(Color.parseColor("#9b9b9b"));
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setTextColor(Color.parseColor("#9b9b9b"));
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setTextColor(Color.parseColor("#9b9b9b"));
                all.setBackgroundResource(R.drawable.range_button_background_default);
                all.setTextColor(Color.parseColor("#9b9b9b"));
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                getMindate(apmc_name, new GetMinDate() {
                    @Override
                    public void GetMinDatecallback(String s) {

                        Log.d(TAG, "GetMinDatecallback: "+s);

                        Date dat = new Date();

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        try {
                            dat = sdf.parse(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Date date6 = new Date();
                        Date today6 = new Date(date6.getTime() + (1000 * 60 * 60 * 24));

                        Calendar cal6 = Calendar.getInstance();
                        cal6.add(Calendar.MONTH, -24);
                        Date result6 = cal6.getTime();

                        long min6 = dat.getTime();
                        long max6 = today6.getTime();


                        xaxis.setMax(max6);
                        xaxis.setMin(min6);
                        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));


                        all.setBackgroundResource(R.drawable.range_button_background);
                        all.setTextColor(Color.WHITE);

                        one_week.setBackgroundResource(R.drawable.range_button_background_default);
                        one_week.setTextColor(Color.parseColor("#9b9b9b"));
                        one_year.setBackgroundResource(R.drawable.range_button_background_default);
                        one_year.setTextColor(Color.parseColor("#9b9b9b"));
                        one_month.setBackgroundResource(R.drawable.range_button_background_default);
                        one_month.setTextColor(Color.parseColor("#9b9b9b"));
                        six_month.setBackgroundResource(R.drawable.range_button_background_default);
                        six_month.setTextColor(Color.parseColor("#9b9b9b"));

                    }
                });

            }
        });


        chartView = findViewById(R.id.hc);


        chartView.setVerticalScrollBarEnabled(true);


        options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("spline");
        options.setChart(chart);


        HIExporting exporting = new HIExporting();
        exporting.setEnabled(false);
        options.setExporting(exporting);


        HITitle title = new HITitle();
        title.setText("");
        options.setTitle(title);

       /* HITitle title = new HITitle();
        title.setText("Daily visits at www.highcharts.com");
        options.setTitle(title);



        HISubtitle subtitle = new HISubtitle();
        subtitle.setText("Source: Google Analytics");
        options.setSubtitle(subtitle);
        */

        xaxis = new HIXAxis();
        HICrosshair crosshair = new HICrosshair();
       /* xaxis.setTickInterval(7 * 24 * 3600 *1000);
        xaxis.setTickWidth(0);
        */

        HILabels hiLabels = new HILabels();
        hiLabels.setEnabled(true);
        xaxis.setLabels(hiLabels);

        xaxis.setGridLineWidth(0);
        xaxis.setType("datetime");
       /* xaxis.setDateTimeLabelFormats(new HIDateTimeLabelFormats());
        xaxis.getDateTimeLabelFormats().setDay(new HIDay());
        xaxis.getDateTimeLabelFormats().getDay().setMain("%e of %b"); // this will give e.g.: 1 of Jan

        */
        xaxis.setCrosshair(crosshair);
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));


        HIYAxis yaxis1 = new HIYAxis();
        yaxis1.setGridLineWidth(0);
        yaxis1.setTitle(new HITitle());
        yaxis1.getTitle().setText("");


        HIYAxis yaxis2 = new HIYAxis();
        HILabels labels = new HILabels();
        labels.setEnabled(false);
        yaxis2.setLabels(labels);
        yaxis2.setLinkedTo(0);
        yaxis2.setGridLineWidth(0);
        yaxis2.setOpposite(true);


        options.setYAxis(new ArrayList<>(Collections.singletonList(yaxis1)));
        HILegend legend = new HILegend();
        legend.setAlign("right");
        legend.setVerticalAlign("top");
        legend.setY(20);
        legend.setFloating(true);
        legend.setBorderWidth(0);
        options.setLegend(legend);

        HITooltip tooltip = new HITooltip();
        tooltip.setShared(true);
        tooltip.setXDateFormat("%d/%m/%Y (%A)");
        options.setTooltip(tooltip);

        HICredits credits = new HICredits();
        credits.setEnabled(false);
        options.setCredits(credits);


        HIPlotOptions plotoptions = new HIPlotOptions();
        plotoptions.setSeries(new HISeries());
        plotoptions.getSeries().setCursor("pointer");
        plotoptions.getSeries().setPoint(new HIPoint());
        plotoptions.getSeries().getPoint().setEvents(new HIEvents());
        plotoptions.getSeries().getPoint().getEvents().setClick(new HIFunction("function (e) { hs.htmlExpand(null, { pageOrigin: { x: e.pageX || e.clientX, y: e.pageY || e.clientY }, headingText: this.series.name, maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) + ':<br/> ' + this.y + ' visits', width: 200 }); }"));
        plotoptions.getSeries().setMarker(new HIMarker());
        plotoptions.getSeries().getMarker().setLineWidth(0);
        plotoptions.getSeries().getMarker().setEnabled(false);
//        HIEvents hiEvents=new HIEvents();
//        hiEvents.setLegendItemClick(new HIFunction("function (e) {e.preventDefault()}"));
//        plotoptions.getSeries().setEvents(hiEvents);
//
//        plotoptions.getSeries().setPointStart(new Date(20,0,2).getTime()); // starting from 1.1.1999
//         plotoptions.getSeries().setPointInterval(24 * 3600 * 1000);
//        options.setPlotOptions(plotoptions);

        HIResponsive responsive = new HIResponsive();
        HIRules rules1 = new HIRules();
        rules1.setCondition(new HICondition());
        rules1.getCondition().setMaxWidth(600);
        HashMap<String, HashMap> chartLegend = new HashMap<>();
        HashMap<String, Object> legendOptions = new HashMap<>();
        legendOptions.put("verticalAlign", "top");
        legendOptions.put("y", 0);
        legendOptions.put("floating", false);
        chartLegend.put("legend", legendOptions);
        rules1.setChartOptions(chartLegend);
        responsive.setRules(new ArrayList<>(Collections.singletonList(rules1)));
        options.setResponsive(responsive);

        // options.setSeries(new ArrayList<>(Arrays.asList(line1)));

        //options.setSeries(new ArrayList<>(Arrays.asList(line1, line2, line3, line4, line5)));

        chartView.setOptions(options);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialogButtonClicked();

            }
        });

    }

    private void CheckBookmark() {

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }

                if(value.exists())
                {
                    mp=(Map<String,Object>)value.get("my_crops");
                    if(mp!=null)
                    {
                        if(mp.get(commodity_name+commodity_type+apmc_name)!=null)
                        {
                            bookmark2.setVisibility(View.VISIBLE);
                            bookmark1.setVisibility(View.GONE);
                            addtext.setText("Remove");
                            b1=true;
                        }
                        else
                        {
                            bookmark2.setVisibility(View.GONE);
                            bookmark1.setVisibility(View.VISIBLE);
                            addtext.setText("Add");
                            b1=false;
                        }
                    }
                    else
                    {
                        bookmark2.setVisibility(View.GONE);
                        bookmark1.setVisibility(View.VISIBLE);
                        addtext.setText("Add");
                        b1=false;
                    }
                }

            }
        });
    }


    public interface GetMinDate{
        void GetMinDatecallback(String s);
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<String> mandiList1);
    }

    public interface GetMandisinState {
        void Oncallback(ArrayList<String> mandiList);
    }

    public interface ComaperData {
        void GetCompareData(ArrayList<Number[]> number);
    }

    public interface InitialChartData{
        void InitialChartDatacallback(ArrayList<Number[]> number);
    }

    public void getMindate(String apmc_name,GetMinDate getMinDate) {

        date="";

        db.collection("price_table").document(apmc_name).collection(commodity_name+commodity_type).orderBy("Arrival_Date", Query.Direction.ASCENDING).limit(1).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot.exists() ) {
                        date= snapshot.getString("Arrival_Date");
                    }
                }
                getMinDate.GetMinDatecallback(date);
            }
        });

    }

    public void readdata(ArrayList<String> mandiList, FirestoreCallback firestoreCallback) {

        ArrayList<String> mandiList1 = new ArrayList<>();

        for (int i = 0; i <= mandiList.size(); i++) {
            int a = i;
            db.collection("commodities").document(commodity_name + commodity_type).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();

                        if (a < mandiList.size()) {
                            if (snapshot.getBoolean(mandiList.get(a)) != null && snapshot.getBoolean(mandiList.get(a))) {
                                mandiList1.add(mandiList.get(a));
                            }
                        } else {
                            Log.d(TAG, "snapshot: " + mandiList1);
                            firestoreCallback.onCallback(mandiList1);
                        }
                    } else {
                        Log.d(TAG, "Error getting Documents " + task.getException());
                    }
                }
            });
        }
        firestoreCallback.onCallback(mandiList1);
    }

    public void GetMandisinState(String apmc_name,String selectedState, GetMandisinState getMandisinState) {

        ArrayList<String> mandiList = new ArrayList<>();

        db.collection("apmcs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String address = document.getString("add");

                        if (address.contains(selectedState)) {

                            String mandi = document.getString("name");
                            if(!mandi.equals(apmc_name))
                            {
                                mandiList.add(mandi);
                            }
                        }
                    }
                    getMandisinState.Oncallback(mandiList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void GetDataToCompare(String apmc_name, String commodity, String strDt, ComaperData comaperData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ArrayList<Number[]> dataList = new ArrayList<>();

        db.collection("price_table").document(apmc_name).collection(commodity).orderBy("Arrival_Date").endAt(strDt)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            dataList.clear();
                            mExampleList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ChartData t = new ChartData();
                                Number[] number = new Number[2];


                                String myDate = document.getString("Arrival_Date");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());


                                Date date = null;
                                try {
                                    date = sdf.parse(myDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                assert date != null;

                                Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24));
                                long millis = tomorrow.getTime();

                                int new_price = Math.toIntExact(document.getLong("Modal_Price"));

                                number[0] = millis;
                                number[1] = (document.getLong("Modal_Price"));
                                dataList.add(number);

                                if (flag) {
                                    old_price = new_price;
                                    flag = false;
                                }

                                int increase = new_price - old_price;

                                old_price = new_price;

                                t.setDate(myDate);
                                t.setPrice(String.valueOf(document.getLong("Modal_Price")));
                                t.setRise(String.valueOf(increase));

                                mExampleList.add(t);
                            }
                            comaperData.GetCompareData(dataList);
                        } else {
                            Log.d("getData", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void InitialChartData(String apmc_name, String commodity, String strDt, InitialChartData initialChartData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        ArrayList<Number[]> dataList = new ArrayList<>();

        db.collection("price_table").document(apmc_name).collection(commodity).orderBy("Arrival_Date").endAt(strDt)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            dataList.clear();
                            mExampleList.clear();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ChartData t = new ChartData();


                                Number[] number = new Number[2];

                                String myDate = document.getString("Arrival_Date");

                                Date date = null;
                                try {
                                    date = sdf.parse(myDate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                assert date != null;

                                Date tomorrow = new Date(date.getTime() + (1000 * 60 * 60 * 24));
                                long millis = tomorrow.getTime();

                                int new_price = Math.toIntExact(document.getLong("Modal_Price"));

                                number[0] = millis;
                                number[1] = new_price;
                                dataList.add(number);


                                if (flag) {
                                    old_price = new_price;
                                    flag = false;
                                }

                                int increase = new_price - old_price;

                                old_price = new_price;

                                t.setDate(myDate);
                                t.setPrice(String.valueOf(document.getLong("Modal_Price")));
                                t.setRise(String.valueOf(increase));

                                mExampleList.add(t);
                            }
                            initialChartData.InitialChartDatacallback(dataList);
                        } else {
                            Log.d("getData", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }



    public void showAlertDialogButtonClicked() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.compare_crop_dialog, null);
        ProgressBar p = dialogView.findViewById(R.id.MP);

        builder.setView(dialogView);

        Spinner state_spinner = dialogView.findViewById(R.id.state_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.india_states, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setAdapter(adapter);
        if (state != null) {
            int spinnerPosition = adapter.getPosition(state);
            state_spinner.setSelection(spinnerPosition);
        }

        Spinner mandi_spinner = dialogView.findViewById(R.id.mandi_spinner);

        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long text) {


                String selected_State = arg0.getItemAtPosition(pos).toString();

                ArrayList<String> mandiList = new ArrayList<>();
                p.setVisibility(View.VISIBLE);

                GetMandisinState(apmc_name,selected_State, new GetMandisinState() {
                    @Override
                    public void Oncallback(ArrayList<String> mandiList) {
                        Log.d(TAG, "Oncallback: " + mandiList);
                        readdata(mandiList, new FirestoreCallback() {
                            @Override
                            public void onCallback(ArrayList<String> mandiList1) {

                                Log.d(TAG, "onCallback2: " + mandiList1);

                                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(HighChartActivity.this, android.R.layout.simple_spinner_item, mandiList1);
                                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                mandi_spinner.setAdapter(areasAdapter);

                                p.setVisibility(View.GONE);

                            }
                        });
                    }
                });

            }


            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        mandi_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long text) {

                selected_mandi = arg0.getItemAtPosition(pos).toString();
                Log.d("C", "onItemSelected: " + selected_mandi);
                // Toast.makeText(HighChartActivity.this,selected_State ,Toast.LENGTH_LONG).show();

            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        // add the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!selected_mandi.equals("")) {
                    Addseries(selected_mandi, commodity_name, commodity_name.concat(commodity_type));
                }

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.share:

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

                //Toast.makeText(this,"Share", Toast.LENGTH_SHORT).show();


                break;
            default:
                break;
        }
    }


    public Bitmap takeScreenshot() {
        //  View rootView = findViewById(R.id.recyler).getRootView();
        View rootView = findViewById(R.id.constraintLayout);
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(HighChartActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(HighChartActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

      /*  if (ActivityCompat.shouldShowRequestPermissionRationale(MandiViewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MandiViewActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MandiViewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

       */
    }


    private void shareIt(Bitmap bitmap) {


        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(this.getContentResolver(), bitmap, "Title", null);
        Uri imageUri = Uri.parse(path);


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

    private void getData(String apmc_name, String commodity_name, String commodity) {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = new Date();

        String strDt = sdf.format(date);

        InitialChartData(apmc_name, commodity, strDt, new InitialChartData() {
            @Override
            public void InitialChartDatacallback(ArrayList<Number[]> dataList) {
                progressBar.setVisibility(View.VISIBLE);

                Log.d(TAG, "InitialChartDatacallback: "+dataList.toString());

                if(!dataList.isEmpty())
                {
                    HISpline line2 = new HISpline();
//                    line2.setName(commodity_name + "," + apmc_name);
                    line2.setName("");
                    line2.setData(dataList);
                    line2.setMarker(new HIMarker());
                    line2.getMarker().setEnabled(false);


                    GetDataToCompare(apmc_name, commodity, strDt, new ComaperData() {
                        @Override
                        public void GetCompareData(ArrayList<Number[]> dataList1) {

                            if (!dataList.isEmpty()) {
                                HISpline line = new HISpline();
                                line.setName(commodity_name + "," + apmc_name);
                                line.setEvents(new HIEvents());;
                                line.getEvents().setLegendItemClick(new HIFunction("function (e) {e.preventDefault()}"));
                                line.setData(dataList1);
                                line.setMarker(new HIMarker());
                                line.getMarker().setEnabled(false);
                                chartView.addSeries(line);
                                Collections.reverse(mExampleList);
                                mAdapter.notifyDataSetChanged();
                            }
                            progressBar.setVisibility(View.GONE);

                        }
                    });
                }


            }
        });

    }


    private void Addseries(String apmc_name, String commodity_name, String commodity) {

        progressBar.setVisibility(View.VISIBLE);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date = new Date();

        String strDt = sdf.format(date);

        GetDataToCompare(apmc_name, commodity, strDt, new ComaperData() {
            @Override
            public void GetCompareData(ArrayList<Number[]> dataList) {
                if (!dataList.isEmpty()) {
                    HISpline line = new HISpline();
                    line.setName(commodity_name + "," + apmc_name);
                    line.setData(dataList);
                    line.setMarker(new HIMarker());
                    line.getMarker().setEnabled(false);
                    chartView.addSeries(line);

//                    line.setEvents(new HIEvents());;
//                    line.getEvents().setLegendItemClick(new HIFunction(
//                            f -> {
//                                Toast.makeText(getApplicationContext(), "Clicked point", Toast.LENGTH_SHORT);
//                            },
//                            new String[] {"x", "y"}
//                    ));
//                    line.getEvents().setLegendItemClick(new HIFunction(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getApplicationContext(), "succes", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    ));
//                    seriesmap.put(line.getName(),line);
//                    serieslist.add(line);
////                    chartView.removeAllViews();
//                    Iterator iterator = seriesmap.keySet().iterator();
//                    while(iterator.hasNext())
//                    {
//                        String key = (String) iterator.next();
//                        chartView.addSeries(seriesmap.get(key));
//                    }

                    Log.d("serieslist", "GetCompareData: "+serieslist);
//                    chartView.setBackgroundResource();
                    Collections.reverse(mExampleList);
                    mAdapter.notifyDataSetChanged();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private ArrayList<String> getMandis(String state) {

        ArrayList<String> dataList = new ArrayList<>();


        db.collection("apmcs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String address = document.getString("add");

                                if (address.contains(state)) {
                                    dataList.add(document.getString("name"));
                                }
                            }

                            progressBar.setVisibility(View.GONE);
                            Log.e("The read success: ", "su" + mExampleList.size());

                        } else {
                            Log.d("getMandis", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                progressBar.setVisibility(View.GONE);

            }
        });

        return dataList;
    }


}
