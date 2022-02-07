package com.farmigo.app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.farmigo.app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class EmptyActivity extends AppCompatActivity {

    private Button three_day,one_week,one_month,one_year,six_month,all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        three_day = findViewById(R.id.button1);
        one_week = findViewById(R.id.button2);
        one_month = findViewById(R.id.button3);
        six_month = findViewById(R.id.button4);
        one_year= findViewById(R.id.button5);
        all = findViewById(R.id.button6);


        one_year.setBackgroundResource(R.drawable.range_button_background);

        three_day.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                three_day.setBackgroundResource(R.drawable.range_button_background);

                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                all.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });

        one_week.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                one_week.setBackgroundResource(R.drawable.range_button_background);

                three_day.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                all.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });


        one_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                one_month.setBackgroundResource(R.drawable.range_button_background);

                three_day.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                all.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });


        six_month.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                six_month.setBackgroundResource(R.drawable.range_button_background);

                three_day.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                all.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });

        one_year.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                one_year.setBackgroundResource(R.drawable.range_button_background);

                three_day.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                all.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });

        all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                all.setBackgroundResource(R.drawable.range_button_background);

                three_day.setBackgroundResource(R.drawable.range_button_background_default);
                one_week.setBackgroundResource(R.drawable.range_button_background_default);
                one_year.setBackgroundResource(R.drawable.range_button_background_default);
                one_month.setBackgroundResource(R.drawable.range_button_background_default);
                six_month.setBackgroundResource(R.drawable.range_button_background_default);
            }
        });

    }
}
