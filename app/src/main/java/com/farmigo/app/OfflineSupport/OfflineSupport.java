package com.farmigo.app.OfflineSupport;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class OfflineSupport extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
