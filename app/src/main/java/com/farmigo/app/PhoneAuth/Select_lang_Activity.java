package com.farmigo.app.PhoneAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.farmigo.app.activities.MainActivity;
import com.farmigo.app.R;
import com.farmigo.app.activities.Profile_Activity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class Select_lang_Activity extends AppCompatActivity {

    Locale myLocale;
    String lang;
    TextView title;
    Button button;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Boolean fromNav = false;
    private boolean isadded=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_lang_);

        // This will get the radiogroup
        RadioGroup rGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        button = findViewById(R.id.button);
        // title = findViewById(R.id.textView2);
        prefs = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = prefs.edit();
        isadded=prefs.getBoolean("location_added",true);

        fromNav = getIntent().getBooleanExtra("fromNav", false);
        lang = getIntent().getStringExtra("locale");

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!fromNav) {
                    Intent in = new Intent(Select_lang_Activity.this, Sign_in_Activity.class);
                    startActivity(in);
                } else {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            Log.d("logout", "onStart: fromnavwala up");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        } else {
                            Intent in = new Intent(Select_lang_Activity.this, Sign_in_Activity.class);
                            startActivity(in);
                        }
                    }
                    else{
                        Intent in = new Intent(Select_lang_Activity.this, Sign_in_Activity.class);
                        startActivity(in);
                    }
                }
            }
        });


        // This will get the radiobutton in the radiogroup that is checked
        RadioButton checkedRadioButton = (RadioButton) rGroup.findViewById(rGroup.getCheckedRadioButtonId());


        // This overrides the radiogroup onCheckListener
        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
                // This puts the value (true/false) into the variable
                boolean isChecked = checkedRadioButton.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked) {
                    // Changes the textview's text to "Checked: example radiobutton text"
                    //tv.setText("Checked:" + checkedRadioButton.getText());
                    // Toast.makeText(Select_lang_Activity.this,checkedRadioButton.getText(),Toast.LENGTH_SHORT).show();
                    //setLocale("en");
                }
            }
        });


        if (fromNav) {
            if (lang.equals("mar")) {
                rGroup.check(R.id.radio2);
            } else if (lang.equals("hi")) {
                rGroup.check(R.id.radio3);
            } else {
                rGroup.check(R.id.radio1);
            }

        }


        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup arg0, int id) {
                switch (id) {
                    case R.id.radio1:
                        // Toast.makeText(Select_lang_Activity.this,"English",Toast.LENGTH_SHORT).show();
                        setLocale("en");
                        //title.setText("Choose Your Language");
                        button.setText("Next");

                        editor.putString("lang", "en");
                        editor.apply();
                        break;
                    case R.id.radio2:
                        //Toast.makeText(Select_lang_Activity.this,"Marathi",Toast.LENGTH_SHORT).show();
                        setLocale("mar");
                        //title.setText("आपली भाषा निवडा");
                        button.setText("पुढे जा");

                        editor.putString("lang", "mar");
                        editor.apply();
                        break;
                    case R.id.radio3:
                        //Toast.makeText(Select_lang_Activity.this,"Hindi",Toast.LENGTH_SHORT).show();
                        setLocale("hi");
                        // title.setText("अपनी भाषा चुनिए");
                        button.setText("आगे बढ़ें");

                        editor.putString("lang", "hi");
                        editor.apply();
                        break;
                    default:
                        //title.setText("Choose Your Language");
                        button.setText("Next");
                        setLocale("en");

                        editor.putString("lang", "en");
                        editor.apply();
                        break;
                }
            }
        });

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


    @Override
    protected void onStart() {
        super.onStart();

        isadded=prefs.getBoolean("location_added",true);

//        if (fromNav) {
//
//        } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//
//            if(isadded&&FirebaseAuth.getInstance().getCurrentUser()!=null)
//            {
//                Intent in = new Intent(Select_lang_Activity.this, Profile_Activity.class);
//                startActivity(in);
//            }
//            else
//            {
//                if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
//                    Intent intent = new Intent(this, MainActivity.class);
//                    Log.d("logout", "onStart: fromnavwala down");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                } else {
//                    Intent in = new Intent(Select_lang_Activity.this, Sign_in_Activity.class);
//                    startActivity(in);
//                }
//            }
//        }

        if(fromNav){

        }else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(intent);
            }

        }

    }
}
