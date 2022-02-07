package com.farmigo.app.Helpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public interface GetRealtimeDataListener {
    //make new interface for call back
    void onGetRealtimeDataSuccess(String child, DataSnapshot dataSnapshot);

    void onGetRealtimeDataFail(DatabaseError error);
}