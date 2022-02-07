package com.farmigo.app.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.farmigo.app.Models.ChartData;
import com.farmigo.app.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.highsoft.highcharts.common.hichartsclasses.HIChart;
import com.highsoft.highcharts.common.hichartsclasses.HICondition;
import com.highsoft.highcharts.common.hichartsclasses.HICredits;
import com.highsoft.highcharts.common.hichartsclasses.HIData;
import com.highsoft.highcharts.common.hichartsclasses.HIEvents;
import com.highsoft.highcharts.common.hichartsclasses.HILabels;
import com.highsoft.highcharts.common.hichartsclasses.HILegend;
import com.highsoft.highcharts.common.hichartsclasses.HILine;
import com.highsoft.highcharts.common.hichartsclasses.HIMarker;
import com.highsoft.highcharts.common.hichartsclasses.HIOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPlotOptions;
import com.highsoft.highcharts.common.hichartsclasses.HIPoint;
import com.highsoft.highcharts.common.hichartsclasses.HIResponsive;
import com.highsoft.highcharts.common.hichartsclasses.HIRules;
import com.highsoft.highcharts.common.hichartsclasses.HISeries;
import com.highsoft.highcharts.common.hichartsclasses.HISpline;
import com.highsoft.highcharts.common.hichartsclasses.HISubtitle;
import com.highsoft.highcharts.common.hichartsclasses.HITargetOptions;
import com.highsoft.highcharts.common.hichartsclasses.HITitle;
import com.highsoft.highcharts.common.hichartsclasses.HITooltip;
import com.highsoft.highcharts.common.hichartsclasses.HIXAxis;
import com.highsoft.highcharts.common.hichartsclasses.HIYAxis;
import com.highsoft.highcharts.core.HIChartView;
import com.highsoft.highcharts.core.HIFunction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class HighchartsActivity2 extends AppCompatActivity {

    HIOptions options;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseFirestore db;
    private HIChartView chartView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_highcharts);

        database= FirebaseDatabase.getInstance();
        db=FirebaseFirestore.getInstance();
        myRef=database.getReference();

      /*  Button dayButton = findViewById(R.id.dayButSpline);
        Button weekButton =  findViewById(R.id.weekButSpline);
        Button monthButton = findViewById(R.id.monthButSpline);
        Button yearButton = findViewById(R.id.yearButSpline);

       */
        Button add = findViewById(R.id.add);

        chartView = findViewById(R.id.hc);
        chartView.plugins = new ArrayList<>(Arrays.asList("data", "series-label", "export-data"));



        options = new HIOptions();

        HIChart chart = new HIChart();
        chart.setType("line");
        options.setChart(chart);


       // getResults();


        HITitle title = new HITitle();
        title.setText("Daily visits at www.highcharts.com");
        options.setTitle(title);

        HISubtitle subtitle = new HISubtitle();
        subtitle.setText("Source: Google Analytics");
        options.setSubtitle(subtitle);

        HIXAxis xaxis = new HIXAxis();
        xaxis.setTickInterval(7 * 24 * 3600 *1000);
        xaxis.setTickWidth(0);
        xaxis.setGridLineWidth(1);
        xaxis.setLabels(new HILabels());
        xaxis.getLabels().setAlign("left");
        xaxis.getLabels().setX(3);
        xaxis.getLabels().setY(-3);
        options.setXAxis(new ArrayList<>(Collections.singletonList(xaxis)));

        HIYAxis yaxis1 = new HIYAxis();
        yaxis1.setTitle(new HITitle());
        yaxis1.setLabels(new HILabels());
        yaxis1.getLabels().setAlign("left");
        yaxis1.getLabels().setX(3);
        yaxis1.getLabels().setY(16);
        yaxis1.getLabels().setFormat("{value:.,0f}");
        yaxis1.setShowFirstLabel(false);

        HIYAxis yaxis2 = new HIYAxis();
        yaxis2.setLinkedTo(0);
        yaxis2.setGridLineWidth(0);
        yaxis2.setOpposite(true);
        yaxis2.setTitle(new HITitle());
        yaxis2.setLabels(new HILabels());
        yaxis2.getLabels().setX(-3);
        yaxis2.getLabels().setY(16);
        yaxis2.getLabels().setFormat("{value:.,0f}");
        yaxis2.setShowFirstLabel(false);

        options.setYAxis(new ArrayList<>(Arrays.asList(yaxis1, yaxis2)));

        HILegend legend = new HILegend();
        legend.setAlign("left");
        legend.setVerticalAlign("top");
        legend.setY(20);
        legend.setFloating(true);
        legend.setBorderWidth(0);
        options.setLegend(legend);

        HITooltip tooltip = new HITooltip();
        tooltip.setShared(true);
        options.setTooltip(tooltip);

        HICredits credits =new HICredits();
        credits.setEnabled(false);
        options.setCredits(credits);


        HIPlotOptions plotoptions = new HIPlotOptions();
        plotoptions.setSeries(new HISeries());
        plotoptions.getSeries().setCursor("pointer");
        plotoptions.getSeries().setPoint(new HIPoint());
        plotoptions.getSeries().getPoint().setEvents(new HIEvents());
        plotoptions.getSeries().getPoint().getEvents().setClick(new HIFunction("function (e) { hs.htmlExpand(null, { pageOrigin: { x: e.pageX || e.clientX, y: e.pageY || e.clientY }, headingText: this.series.name, maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) + ':<br/> ' + this.y + ' visits', width: 200 }); }"));
        plotoptions.getSeries().setMarker(new HIMarker());
        plotoptions.getSeries().getMarker().setLineWidth(1);
        options.setPlotOptions(plotoptions);


        Number[][] series3_data = new Number[][] { { 1605526771000L, 0 }, { 1605613171000L, 0.25 }, { 1605699571000L, 1.41 }, { 1605872371000L, 1.64 }};

        HISpline line1 = new HISpline();
        line1.setName("Kolhapur");
        line1.setData(new ArrayList<>(Arrays.asList(series3_data)));





        HIResponsive responsive = new HIResponsive();
        HIRules rules1 = new HIRules();
        rules1.setCondition(new HICondition());
        rules1.getCondition().setMaxWidth(600);
        HashMap<String, HashMap> chartLegend = new HashMap<>();
        HashMap<String, Object> legendOptions = new HashMap<>();
        legendOptions.put("verticalAlign", "bottom");
        legendOptions.put("y", 0);
        legendOptions.put("floating", false);
        chartLegend.put("legend", legendOptions);
        rules1.setChartOptions(chartLegend);
        responsive.setRules(new ArrayList<>(Collections.singletonList(rules1)));
        options.setResponsive(responsive);

        options.setSeries(new ArrayList<>(Arrays.asList(line1)));

        chartView.setOptions(options);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Addseries();


            }
        });


    }


    private void getResults(){


        Number[] series3_data1 = new Number[] {0,0.25,1.41,1.64 };
        Number[] series3_data2 = new Number[] {1605526771000L,1605613171000L,1605699571000L,1605785971000L };


       /* ArrayList<Number> data2 = new ArrayList<>();
        data2.add(0);
        data2.add(0.25);
        data2.add(1.41);
        data2.add(1.64);


        ArrayList<Number> date = new ArrayList<Number>();

        date.add(1605526771000L);
        date.add(1605613171000L);
        date.add(1605699571000L);
        date.add(1605785971000L);

        */



       /* ArrayList<ChartData> date = new ArrayList<>();

        date.add(new ChartData(1605526771000L,0));
        date.add(new ChartData(1605613171000L,5));
        date.add(new ChartData(1605699571000L,3));
        date.add(new ChartData(1605785971000L,2));

        */






      //  { 1605613171000L, 0.25 }, { 1605699571000L, 1.41 }, { 1605872371000L, 1.64 }};



        ArrayList<Number[]> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            double random = Math.random() * 49 + 1;
            Number[] number = new Number[2];
            number[0]=i;
            number[1]=random;
            dataList.add(number);
        }


       // Number[][] series3_data = new Number[][] { { 1605526771000L, 0 }, { 1605613171000L, 0.25 }, { 1605699571000L, 1.41 }, { 1605872371000L, 1.64 }};

        HISpline line = new HISpline();
        line.setName("Kolhapur");
        line.setData(dataList);

        options.setSeries(new ArrayList<>(Arrays.asList(line)));
    }




    private ArrayList<Number[]> data1(){
        ArrayList<Number[]> dataList = new ArrayList<>();




        for (int i = 0; i < 365; i++) {
            double random = Math.random() * 49 + 1;
            Number[] number = new Number[2];
            number[0]=i;
            number[1]=random;
            dataList.add(number);
        }
        Log.d("info","array Size:"+dataList.size());
        return dataList;
    }



    private void Addseries(){


        ArrayList<Number[]> dataList = new ArrayList<>();


        myRef.child("test").child("test").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){


                    Number[] number = new Number[2];

                    number[0]=Long.parseLong(snapshot.child("Arrival_Date").getValue().toString());
                    number[1]=Integer.valueOf(snapshot.child("Modal_Price").getValue().toString());
                    dataList.add(number);

                }


                for (Number[] s : dataList) {
                     Log.d("Date: ", String.valueOf(s[0]));
                     Log.d("Price: ",String.valueOf(s[1]));

                }


                HISpline line2 = new HISpline();
                line2.setName("new");
                line2.setData(new ArrayList<>(Arrays.asList(dataList)));

                options.setSeries(new ArrayList<>(Arrays.asList(line2)));

                chartView.addSeries(line2);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // progressBar.setVisibility(View.GONE);
                Log.e("The read failed: " ,databaseError.getMessage());
            }
        });

    }
}
