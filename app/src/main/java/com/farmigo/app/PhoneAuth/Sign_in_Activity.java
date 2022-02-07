package com.farmigo.app.PhoneAuth;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.farmigo.app.activities.MainActivity;
import com.farmigo.app.R;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsOptions;
import com.google.android.gms.auth.api.credentials.HintRequest;

import com.google.firebase.auth.FirebaseAuth;
import com.hbb20.CountryCodePicker;

public class Sign_in_Activity extends AppCompatActivity {

    EditText editTextPhone;
    Button getotp;
    CountryCodePicker ccp;
    ImageView tick;
    int RESOLVE_HINT = 1008;
    ImageButton backbutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_);


        requestHint();

        editTextPhone = findViewById(R.id.editText_carrierNumber);
        getotp = findViewById(R.id.button);
        ccp = findViewById(R.id.ccp);
        tick = findViewById(R.id.tick);
        backbutton = findViewById(R.id.backbutton);

        tick.setVisibility(View.INVISIBLE);

        ccp.registerCarrierNumberEditText(editTextPhone);


        getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String code = editTextCountryCode.getText().toString().trim();
                String number = editTextPhone.getText().toString().trim();

                if (!ccp.isValidFullNumber()) {
                    editTextPhone.setError(getResources().getString(R.string.Valid_number_is_required));
                    editTextPhone.requestFocus();
                    return;
                }

                //String phoneNumber = code + number;
                //String phoneNumber = "+91" + number;
                // String phone=ccp.getFullNumberWithPlus();
                String ccode = ccp.getSelectedCountryCode();
                String phoneNumber = "+" + ccode + " " + number;


                Intent intent = new Intent(Sign_in_Activity.this, Otp_Activity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





       /* getotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String code = editTextCountryCode.getText().toString().trim();
                String number = editTextPhone.getText().toString().trim();

                if (number.isEmpty() || number.length() < 10) {
                    editTextPhone.setError("Valid number is required");
                    editTextPhone.requestFocus();
                    return;
                }

                //String phoneNumber = code + number;
                String phoneNumber = "+91" + number;

                Intent intent = new Intent(Sign_in_Activity.this, Otp_Activity.class);
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);

            }
        });

        */
    }

    private void requestHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build();
        CredentialsOptions options = new CredentialsOptions.Builder()
                .forceEnableSaveDialog()
                .build();
        PendingIntent intent = Credentials.getClient(this, options)
                .getHintPickerIntent(hintRequest);

        try {
            startIntentSenderForResult(intent.getIntentSender(),
                    RESOLVE_HINT, null, 0, 0, 0, null);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                String mobileNumber = credential.getId();
                //Log.i(TAG,mobileNumber);
                //Toast.makeText(this,mobileNumber,Toast.LENGTH_SHORT).show();
                ccp.setFullNumber(mobileNumber);
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        ccp.isValidFullNumber();
        ccp.setPhoneNumberValidityChangeListener(new CountryCodePicker.PhoneNumberValidityChangeListener() {
            @Override
            public void onValidityChanged(boolean isValidNumber) {

                if (isValidNumber) {
                    tick.setVisibility(View.VISIBLE);
                    //Toast.makeText(Sign_in_Activity.this,"valid",Toast.LENGTH_SHORT).show();
                } else {
                    tick.setVisibility(View.INVISIBLE);
                    //Toast.makeText(Sign_in_Activity.this,"Invalid",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
