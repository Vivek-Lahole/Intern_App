package com.farmigo.app.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.farmigo.app.Adapters.MycropsAdapter;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.activities.HighChartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.polyak.iconswitch.IconSwitch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class MycropsFragment extends Fragment implements IconSwitch.CheckedChangeListener {

    // private ProgressBar progressBar;
    private ConstraintLayout layout;

    private long old_price, new_price;


    private ArrayList<Fav_commodities> mExampleList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private MycropsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //private GridLayoutManager mLayoutManager;
    private IconSwitch iconSwitch;
    private TextView searchView;
    private String mycrops;
    private ItemTouchHelper mItemTouchHelper;


    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private ShimmerRecyclerView shimmerRecycler;

    public MycropsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();

        if(auth.getCurrentUser().getUid()==null)
        {
            Intent intent = new Intent(getContext(), Select_lang_Activity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mycrops, container, false);

        shimmerRecycler = view.findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.showShimmerAdapter();

        //progressBar = view.findViewById(R.id.progressBar5);

        iconSwitch = (IconSwitch) view.findViewById(R.id.switch1);
        iconSwitch.setCheckedChangeListener(this);

        getResults();

        mRecyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.search_mandi);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());


        mAdapter = new MycropsAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new MycropsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position1) {
                //changeItem(position, "Clicked");
                //Toast.makeText(getContext(),mAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();

                Toasty.success(requireContext(), mAdapter.getItem(position1).getName() + " Removed!", Toast.LENGTH_SHORT, true).show();
                String key = mAdapter.getItem(position1).getName() + mAdapter.getItem(position1).getType() + mAdapter.getItem(position1).getApmc_name();

                Log.d("item", "onItemClick: "+position1);

                mycrops = mycrops.replace(mAdapter.getItem(position1).getName() + mAdapter.getItem(position1).getType() + mAdapter.getItem(position1).getApmc_name() + ",", "");

//                reference.child("my_crops_string").setValue(mycrops);
                documentReference.update("my_crops_string", mycrops);
//                reference.child("my_crops").child(mAdapter.getItem(position).getName()+mAdapter.getItem(position).getType()+mAdapter.getItem(position).getApmc_name()).removeValue();
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot=task.getResult();

                            if (snapshot.get("my_crops")!=null) {
                                Map<String,String> crop=(Map<String,String>)snapshot.get("my_crops");
                                Log.d("item", "onItemClick: "+key);
                                crop.remove(key);
                                Log.d("item", ""+crop);
                                documentReference.update("my_crops",crop);
                            }
                        }
                    }
                });

                mExampleList.remove(position1);
                mAdapter.notifyItemRemoved(position1);

               /* mAdapter.updateList(mExampleList);
                mAdapter.notifyDataSetChanged();
                */

            }
        });


        mAdapter.setOnCardClickListener(new MycropsAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(View v, int position) {

                Intent intent = new Intent(getContext(), HighChartActivity.class);
                intent.putExtra("apmc_name", mAdapter.getItem(position).getApmc_name());
                intent.putExtra("commodity_name", mAdapter.getItem(position).getName());
                intent.putExtra("commodity_type", mAdapter.getItem(position).getType());
                intent.putExtra("state", mAdapter.getItem(position).getState());
                intent.putExtra("category",mAdapter.getItem(position).getCategory());
                intent.putExtra("my_crops_string",mycrops);
                SharedPreferences sph= getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sph.edit();
                editor.putString("my_crops_string",mycrops);
                editor.apply();
                startActivity(intent);
            }
        });


        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {

                // TODO Auto-generated method stub
                // mAdapter.getFilter().filter(query);
            }

            @Override
            public void beforeTextChanged(CharSequence query, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable query) {

                // filter your list from your input
                filter(query.toString());
                //you can use runnable postDelayed like 500 ms to delay search text

                // mAdapter.getFilter().filter(query);
            }
        });

        getmyCropsString();


        return view;
    }


    private void filter(String text) {
        ArrayList<Fav_commodities> temp = new ArrayList<>();
        for (Fav_commodities d : mExampleList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase())) {
                temp.add(d);
            }
        }

        //update recyclerview
        mAdapter.updateList(temp);
    }


    @Override
    public void onCheckChanged(IconSwitch.Checked current) {

        switch (iconSwitch.getChecked()) {
            case LEFT:
                mAdapter.setVisibility(true);
                //Log.d("You are :", "Checked left");
                // Toast.makeText(getActivity(),"Checked left", Toast.LENGTH_LONG).show();
                break;
            case RIGHT:
                mAdapter.setVisibility(false);
                //Log.d("You are :", "Checked right");
                //Toast.makeText(getActivity(), "Checked right", Toast.LENGTH_LONG).show();
                break;

        }

    }


    private void getmyCropsString() {
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                //In this case, "shalom" will be stored in mName
//                if(snapshot.exists()){
//
//                    if (snapshot.child("my_crops_string").exists()) {
//                        mycrops = (String) snapshot.child("my_crops_string").getValue();
//                    }
//                }
//
//            }
//
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("getmyCropsString", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {

                    if (snapshot.getString("my_crops_string") != null) {
                        {
                            mycrops = (String) snapshot.getString("my_crops_string");
                        }
                    }

                } else {
                    Log.d("getmyCropsString", "Current data: null");
                }
            }
        });
    }

    private void getResults() {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);


        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        String yesterday = sdf.format(cal.getTime());

        cal.add(Calendar.DATE, -1);
        String dayBeforeyesterday = sdf.format(cal.getTime());

//        reference.child("my_crops").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mExampleList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//
//                    //   if(mymandis.contains(snapshot.child("name").getValue().toString())){
//
//                    Fav_commodities t = new Fav_commodities();
//
//
//                    String name = snapshot.child("name").getValue().toString();
//                    String type = snapshot.child("type").getValue().toString();
//                    String apmc_name = snapshot.child("apmc_name").getValue().toString();
//
//                    String nametype = name.concat(type);
//
//
//                    t.setName(name);
//                    t.setApmc_name(apmc_name);
//                    t.setCategory(snapshot.child("category").getValue().toString());
//                    t.setState(snapshot.child("state").getValue().toString());
//                    t.setColor1(snapshot.child("color1").getValue().toString());
//                    t.setColor2(snapshot.child("color2").getValue().toString());
//                    t.setImg(snapshot.child("img").getValue().toString());
//                    t.setType(type);
//
//                    myRef.child("price_table").child(apmc_name).child(nametype).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot mandiSnapshot) {
//
//                            if (mandiSnapshot.child(currentDate).exists()) {
//
//                                new_price = Integer.valueOf(mandiSnapshot.child(currentDate).child("Modal_Price").getValue().toString());
//
//                                t.setDate(currentDate);
//
//                                if (mandiSnapshot.child(yesterday).exists()) {
//
//                                    old_price = Integer.valueOf(mandiSnapshot.child(yesterday).child("Modal_Price").getValue().toString());
//                                } else {
//                                    old_price = Integer.valueOf(mandiSnapshot.child(dayBeforeyesterday).child("Modal_Price").getValue().toString());
//                                }
//
//                            } else {
//                                new_price = Integer.valueOf(mandiSnapshot.child(yesterday).child("Modal_Price").getValue().toString());
//                                t.setDate(yesterday);
//
//                                old_price = Integer.valueOf(mandiSnapshot.child(dayBeforeyesterday).child("Modal_Price").getValue().toString());
//                            }
//
//
//                            t.setPrice(String.valueOf(new_price));
//
//                            int increase = new_price - old_price;
//                            int percentage_change = (increase * 100) / old_price;
//
//                            t.setPercentage(String.valueOf(percentage_change));
//                            t.setRise(String.valueOf(increase));
//
//                            mExampleList.add(t);
//
//
//                            mAdapter.notifyDataSetChanged();
//
//                            //progressBar.setVisibility(View.GONE);
//                            shimmerRecycler.hideShimmerAdapter();
//                            Log.e("The read success: ", "success2");
//
//                        }
//
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                           // progressBar.setVisibility(View.GONE);
//                            shimmerRecycler.hideShimmerAdapter();
//                            throw databaseError.toException();
//                        }
//
//                    });
//
//
//
//
//
////                    db.collection("price_table")
////                            .get()
////                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                @Override
////                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                    if (task.isSuccessful()) {
////
////                                        QueryDocumentSnapshot document=task.ge
////
////                                        for (QueryDocumentSnapshot document : task.getResult()) {
////
////                                            document.
////
////                                        }
////
////                                    } else {
////                                        Log.d("getMandis", "Error getting documents: ", task.getException());
////                                    }
////                                }
////                            });
//
//
//                }
//                //mAdapter.updateDatalist(result);
//                // mAdapter.notifyDataSetChanged();
//
//                //progressBar.setVisibility(View.GONE);
//                //layout.setVisibility(View.VISIBLE);
//                // Log.e("The read success: " ,"su"+mExampleList.size());
//
//                //progressBar.setVisibility(View.GONE);
//                shimmerRecycler.hideShimmerAdapter();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //progressBar.setVisibility(View.GONE);
//                shimmerRecycler.hideShimmerAdapter();
//                Log.e("The read failed: ", databaseError.getMessage());
//            }
//        });

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                mExampleList.clear();
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Map<String, Object> mp = new HashMap<>();
                        mp = documentSnapshot.getData();
                        Map<String, Object> crop = (Map<String, Object>) mp.get("my_crops");

                        if (crop != null&&!crop.isEmpty()) {
                            Iterator iterator = crop.keySet().iterator();

                            while (iterator.hasNext()) {
                                String key = (String) iterator.next();
                                Map<String, Object> Cfeilds = (Map<String, Object>) crop.get(key);

                                Fav_commodities t = new Fav_commodities();

                                String name = (String) Cfeilds.get("name");
                                String type = (String) Cfeilds.get("type");
                                String apmc_name = (String) Cfeilds.get("apmc_name");
                                String nametype = name.concat(type);

//                                if(name.contains("("))
//                                {
//                                    String name1 = name.substring(0, name.indexOf("("));
//                                    t.setName(name1);
//                                }
//                                else
//                                {
//                                    t.setName(name);
//                                }
//
//                                if(apmc_name.contains("("))
//                                {
//                                    String apmc_name1=apmc_name.substring(0, apmc_name.indexOf("("));
//                                    t.setName(apmc_name1);
//                                }
//                                else
//                                {
//                                    t.setApmc_name(apmc_name);
//                                }
                                t.setName(name);
                                t.setApmc_name(apmc_name);
                                t.setCategory((String) Cfeilds.get("category"));
                                t.setState((String) Cfeilds.get("state"));
                                t.setColor1((String) Cfeilds.get("color1"));
                                t.setColor2((String) Cfeilds.get("color2"));
                                t.setImg((String) Cfeilds.get("img"));
                                t.setType(type);

                                db.collection("price_table").document(apmc_name).collection(nametype).orderBy("Arrival_Date", Query.Direction.DESCENDING).whereLessThanOrEqualTo("Arrival_Date",currentDate).limit(3).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                                        if (error != null) {
                                            Log.w("TAG", "listen:error", error);
                                            return;
                                        }

                                        Boolean b1 = true, b2 = true;
                                        new_price = 0;
                                        old_price = 0;

                                        for (DocumentSnapshot snapshot : value.getDocuments()) {
                                            if (snapshot.exists() && b1) {
                                                new_price = snapshot.getLong("Modal_Price");

//                                                String Cdate1 = currentDate, datetoset = "";
//                                                String Ldate2 = snapshot.getString("Arrival_Date");
//
//                                                try {
//                                                    Date Cdate = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
//                                                            .parse(getStringFormatted(Cdate1));
//
//                                                    Date Ldate = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
//                                                            .parse(getStringFormatted(Ldate2));
//
//                                                    if (Cdate.compareTo(Ldate) > 0) {
//                                                        t.setDate(Ldate2);
//
//                                                    } else if (Cdate.compareTo(Ldate) < 0) {
//                                                        t.setDate(Cdate1);
//
//                                                    } else {
//                                                        t.setDate(currentDate);
//                                                    }
//                                                } catch (ParseException e) {
//                                                    e.printStackTrace();
//                                                }

                                                t.setDate(snapshot.getString("Arrival_Date"));

                                                b1 = false;
                                            } else if (snapshot.exists() && b2) {
                                                old_price = snapshot.getLong("Modal_Price");
                                                b2 = false;
                                            }
                                        }

                                        //Log.d("20119055", "onComplete: " + old_price + " " + new_price);

                                        t.setPrice(String.valueOf(new_price));
                                        long increase = new_price - old_price;

                                        long percentage_change = 0;
                                        if (old_price != 0) {
                                            percentage_change = (increase * 100) / old_price;
                                        }
                                        Log.d("old_price", "onComplete: " + old_price + "percent_Change " + percentage_change);

                                        t.setPercentage(String.valueOf(percentage_change));
                                        t.setRise(String.valueOf(increase));
                                        mExampleList.add(t);
                                        mAdapter.notifyDataSetChanged();


                                    }
                                });

                            }

                        }

                        shimmerRecycler.hideShimmerAdapter();

                    }
                }
            }
        });
    }

    public String getStringFormatted(String datestring) {
        String format = "yyyy/MM/dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(datestring.replaceAll("-", "/")));
    }


}

