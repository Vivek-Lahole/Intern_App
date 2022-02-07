package com.farmigo.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;

import java.util.Locale;
import java.util.Objects;

public class SplashScreenActivity extends AppCompatActivity {


    Locale myLocale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, Select_lang_Activity.class);
                startActivity(i);
                finish();
            }
        }, 2000);


        SharedPreferences prefs = Objects.requireNonNull(this.getSharedPreferences("pref", Context.MODE_PRIVATE));
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String lang = prefs.getString("lang", "en");

        if(lang.equals("mar")){
            setLocale("mar");
        }else if(lang.equals("hi")) {
            setLocale("hi");
        }else {
            setLocale("en");
        }

    }


    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        /*Intent refresh = new Intent(this, Select_lang_Activity.class);
        startActivity(refresh);
         */
    }
}
