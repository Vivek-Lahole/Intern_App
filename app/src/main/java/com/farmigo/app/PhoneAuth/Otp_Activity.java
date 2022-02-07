package com.farmigo.app.PhoneAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbangbutton.editcodeview.EditCodeListener;
import com.bigbangbutton.editcodeview.EditCodeView;
import com.farmigo.app.activities.Profile_Activity;
import com.farmigo.app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class Otp_Activity extends AppCompatActivity {

    private String verificationId;
    private FirebaseAuth mAuth;

    private static final long START_TIME_IN_MILLIS = 30000;

    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;


    ProgressBar progressBar;
    EditCodeView editText;
    TextView number;
    Button buttonSignIn;
    ImageButton backbutton;
    TextView buttonResendOtp;
    TextView timer;
    //Button buttonResendOtp;
    // Button buttonEditNumber;
    String phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_);

        mAuth = FirebaseAuth.getInstance();


        // progressBar = findViewById(R.id.progressbar);
        editText = findViewById(R.id.editTextCode);
        timer = findViewById(R.id.timer);
        buttonResendOtp = findViewById(R.id.resend);
        buttonResendOtp.setEnabled(false);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        backbutton = findViewById(R.id.backbutton);
       /* buttonResendOtp = findViewById(R.id.button6);
        buttonEditNumber = findViewById(R.id.button7);

        */
        number = findViewById(R.id.textView6);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCode(phoneNumber);

        number.setText(phoneNumber);

        // save phone number
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("USER_PREF",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = editText.getCode().trim();

                if (code.isEmpty() || code.length() < 6) {

                    //  editText.setError("Enter code...");
                    Toast.makeText(Otp_Activity.this, getResources().getString(R.string.Please_Enter_OTP), Toast.LENGTH_SHORT).show();
                    editText.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });

        buttonResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode(phoneNumber);
                resetTimer();
                startTimer();
                buttonResendOtp.setEnabled(false);
                timer.setVisibility(View.VISIBLE);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Otp_Activity.this, Sign_in_Activity.class);
                startActivity(intent);
            }
        });


        editText.setEditCodeListener(new EditCodeListener() {
            @Override
            public void onCodeReady(String code) {

            }
        });


        startTimer();
        updateCountDownText();


    }


    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();


                            SharedPreferences prefs = getApplicationContext().getSharedPreferences("pref",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("isNewUser", isNewUser);
                            editor.apply();

                            // Log.d("MyTAG", "otp:********************************* " + (isNewUser ? "new user" : "old user"));


                            Intent intent = new Intent(Otp_Activity.this, Profile_Activity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            Toast.makeText(Otp_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            // progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {
//        progressBar.setVisibility(View.VISIBLE);
        //Toast.makeText(Otp_Activity.this, getResources().getString(R.string.OTP_Sent), Toast.LENGTH_LONG).show();
        Toasty.success(getApplicationContext(),"Otp Sent",Toasty.LENGTH_SHORT).show();
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                number,
//                60,
//                TimeUnit.SECONDS,
//                TaskExecutors.MAIN_THREAD,
//                mCallBack
//
//
//        );

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth).setPhoneNumber(phoneNumber).setTimeout(60L, TimeUnit.SECONDS).setActivity(this) .setCallbacks(mCallBack).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        mAuth.useAppLanguage();


        // progressBar.setVisibility(View.GONE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                editText.setCode(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Otp_Activity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            // progressBar.setVisibility(View.GONE);
        }
    };


    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                timer.setText("00:00");
                timer.setVisibility(View.GONE);
                //  buttonResendOtp.setTextColor(Color.parseColor("#2743fd"));
                buttonResendOtp.setEnabled(true);

            }
        }.start();
        mTimerRunning = true;
      /*  mButtonStartPause.setText("pause");
        mButtonReset.setVisibility(View.INVISIBLE);

       */
    }

    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
      /*  mButtonStartPause.setText("Start");
        mButtonReset.setVisibility(View.VISIBLE);

       */
    }

    private void resetTimer() {
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
       /* mButtonReset.setVisibility(View.INVISIBLE);
        mButtonStartPause.setVisibility(View.VISIBLE);

        */
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timer.setText(timeLeftFormatted);
    }

}
