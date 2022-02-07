package com.farmigo.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.farmigo.app.BuildConfig;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsActivity extends AppCompatActivity {

    private String heading,date,src,img,news;
    private TextView heading_txt,date_txt,src_txt,news_txt;
    private ImageView thumb;
    private LinearLayout share;
    private ImageButton ImgBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        heading_txt = findViewById(R.id.textView78);
        date_txt = findViewById(R.id.textView79);
        src_txt = findViewById(R.id.textView80);
        news_txt = findViewById(R.id.textView83);
        thumb = findViewById(R.id.imageView16);
        share = findViewById(R.id.share);
        ImgBtn = findViewById(R.id.imageButton3);


        heading = getIntent().getStringExtra("heading");
        news = getIntent().getStringExtra("news");
        date = getIntent().getStringExtra("date");
        src = getIntent().getStringExtra("src");
        img = getIntent().getStringExtra("img");


        Picasso.get().load(img).into(thumb);
        heading_txt.setText(heading);
        date_txt.setText(changeDate(date,"yyyy-MM-dd"));
        src_txt.setText(src);
        news_txt.setText(news);


        ImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,heading+
                        "\n\nI am now a part of Smart Farmers' Community on FarmiGO. \nCome join me here: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }


    public String changeDate(String date1,String pattern)
    {
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
}
