package com.farmigo.app.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.MandisAdapter;
import com.farmigo.app.Adapters.MyMandisAdapter;
import com.farmigo.app.Adapters.StatesAdapter;
import com.farmigo.app.Models.State;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.activities.MandiViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class MandiFragment extends Fragment {


    private DatabaseReference myRef;
    private ProgressBar progressBar;
    private ConstraintLayout layout, mymandilayout, mandinearmelayout;
    private EditText text;


    private ArrayList<mandis> mExampleList = new ArrayList<>();
    private ArrayList<mandis> mymandisExampleList = new ArrayList<>();
    private RecyclerView mRecyclerView, mymandiRecyclerView, statesview;
    private MandisAdapter mAdapter;
    private StatesAdapter statesAdapter;
    private ArrayList<State> statesList;
    private MyMandisAdapter mymandiAdapter;
    private RecyclerView.LayoutManager mLayoutManager, mLayoutManager2, llm;
    private TextView emptyView2, emptyView1, mandis_near_me;
    private TextView searchView, change,remove, mymanditext, mandinearmetext;
    Location myCurrentLoc;
    private String[] eArray;
    private AlertDialog alertDialog;
    //private GridLayoutManager mLayoutManager;
    //String[] mymandis1 = { "Pune Universit
    // y", "is", "a", "really", "silly", "Aga Khan Palace" };
    // ArrayList<String> mymandis = new ArrayList<String>();
    private String mymandis = "";
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference;
    private String lat, lon;

    public MandiFragment() {
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

        View view = inflater.inflate(R.layout.fragment_mandi, container, false);

//        mymanditext=view.findViewById(R.id.textView48);
        mymandilayout = view.findViewById(R.id.constraintLayout5);
        mandis_near_me = view.findViewById(R.id.textView50);
        mRecyclerView = view.findViewById(R.id.recyclerView3);
        change = view.findViewById(R.id.textView51);
        searchView = view.findViewById(R.id.search_mandi);
        remove=view.findViewById(R.id.remove);

        SharedPreferences sh = getContext().getSharedPreferences("key", Context.MODE_PRIVATE);
        Boolean check = sh.getBoolean("check", true);


        if (check) {

            mRecyclerView.setAlpha(0);
            change.setVisibility(View.GONE);
            remove.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
        } else {
            mymandilayout.setVisibility(View.GONE);
        }


        // Inflate the layout for this fragment

        SharedPreferences prefs = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        lat = prefs.getString("lat", "");
        lon = prefs.getString("lon", "");

        myCurrentLoc = new Location("point A");

        myCurrentLoc.setLatitude(Double.valueOf(lat));
        myCurrentLoc.setLongitude(Double.valueOf(lon));


        eArray = requireActivity().getResources().getStringArray(R.array.india_states);

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


        progressBar = view.findViewById(R.id.progressBar3);
        layout = view.findViewById(R.id.layout);
        emptyView1 = view.findViewById(R.id.empty_view1);
        emptyView2 = view.findViewById(R.id.empty_view2);


        layout.setVisibility(View.GONE);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        getmyMandisString();
        //getMandis();
        mymandiRecyclerView = view.findViewById(R.id.recyclerView2);
        mymandiRecyclerView.setHasFixedSize(true);
        mLayoutManager2 = new LinearLayoutManager(getActivity());
        mymandiAdapter = new MyMandisAdapter(mymandisExampleList, getContext());
        mymandiRecyclerView.setLayoutManager(mLayoutManager2);
        /*SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mymandiRecyclerView);
         */
        mymandiRecyclerView.setAdapter(mymandiAdapter);
        mymandiAdapter.setOnItemClickListener(new MyMandisAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //OnItemClicked(position, "Clicked");

               /* Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                intent.putExtra("title", mAdapter.getItem(position).getName());
                view.getContext().startActivity(intent);
                */

                showPopupMenu(v, position, 1);

            }
        });

        mymandiAdapter.setOnCardClickListener(new MyMandisAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                //OnItemClicked(position, "Clicked");

                Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                intent.putExtra("title", mymandiAdapter.getItem(position).getName());
                intent.putExtra("add", mymandiAdapter.getItem(position).getAdd());
                intent.putExtra("state", mAdapter.getItem(position).getState());
                intent.putExtra("lat", mymandiAdapter.getItem(position).getLat());
                intent.putExtra("lon", mymandiAdapter.getItem(position).getLon());
                intent.putExtra("bookmark", true);
                view.getContext().startActivity(intent);
            }
        });


       /* mRecyclerView = view.findViewById(R.id.recyclerView3);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.HORIZONTAL, false);
        mAdapter = new MandisAdapter(mExampleList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        */


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new MandisAdapter(mExampleList, getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MandisAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //OnItemClicked(position, "Clicked");

               /* Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                intent.putExtra("title", mAdapter.getItem(position).getName());
                view.getContext().startActivity(intent);
                */

                showPopupMenu(v, position, 2);
            }
        });

        mAdapter.setOnCardClickListener(new MandisAdapter.OnCardClickListener() {
            @Override
            public void onCardClick(View v, int position) {
                //OnItemClicked(position, "Clicked");

                Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                intent.putExtra("title", mAdapter.getItem(position).getName());
                intent.putExtra("add", mAdapter.getItem(position).getAdd());
                intent.putExtra("state", mAdapter.getItem(position).getState());
                intent.putExtra("lat", mAdapter.getItem(position).getLat());
                intent.putExtra("lon", mAdapter.getItem(position).getLon());
                intent.putExtra("bookmark", false);
                view.getContext().startActivity(intent);

            }
        });


        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                // ...Irrelevant code for customizing the buttons and title
                View dialogView = inflater.inflate(R.layout.state_dialog, null);
                dialogBuilder.setView(dialogView);

                statesview = dialogView.findViewById(R.id.rv);
                statesview.setHasFixedSize(true);
                llm = new LinearLayoutManager(getActivity());
                statesAdapter = new StatesAdapter(statesList);
                statesview.setLayoutManager(llm);
                statesview.setAdapter(statesAdapter);


                initializeData();
                initializeAdapter();


                alertDialog = dialogBuilder.create();
                alertDialog.show();

            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove.setVisibility(View.GONE);
                change.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                emptyView2.setVisibility(View.GONE);
                mAdapter.updateList(mExampleList);
                mandis_near_me.setText("");
                mAdapter.notifyDataSetChanged();
            }
        });


        return view;
    }




    private void initializeAdapter() {
        statesAdapter = new StatesAdapter(statesList);
        statesview.setAdapter(statesAdapter);
        //statesAdapter.notifyDataSetChanged();
        statesAdapter.setOnItemClickListener(new StatesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                remove.setVisibility(View.VISIBLE);
                change.setVisibility(View.GONE);
                filterstate(statesAdapter.getItem(position).getState());
                mandis_near_me.setText("Mandis in " + statesAdapter.getItem(position).getState());
                //Toast.makeText(getContext(),statesAdapter.getItem(position).getState(), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }


    private void initializeData() {
        statesList = new ArrayList<>();

        for (String s : eArray) {
            statesList.add(new State(s));
            statesAdapter.notifyDataSetChanged();
        }
    }

    private void filter(String text) {
        ArrayList<mandis> temp = new ArrayList<>();
        for (mandis d : mExampleList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getName().toLowerCase().contains(text.toLowerCase()) || d.getAdd().contains(text)) {
                temp.add(d);
            }

//            if (d.getState().toLowerCase().contains(text.toLowerCase())){
//                temp.add(d);
//            }
        }

        //update recyclerview
        mAdapter.updateList(temp);
        if (temp.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView2.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.GONE);
        }
    }

    private void filterstate(String text) {
        ArrayList<mandis> temp = new ArrayList<>();
        for (mandis d : mExampleList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
//            if (d.getName().toLowerCase().contains(text.toLowerCase()) || d.getState().contains(text)) {
//                temp.add(d);
//            }

            if (d.getState().toLowerCase().contains(text.toLowerCase())){
                temp.add(d);
            }
        }

        //update recyclerview
        mAdapter.updateList(temp);
        if (temp.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView2.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.GONE);
        }
    }


  /*  private void OnItemClicked(int position, String text) {
        // mExampleList.get(position).changeText1(text);
        // mAdapter.notifyItemChanged(position);
        Intent intent = new Intent(getActivity(), MandiViewActivity.class);
        intent.putExtra("title", mExampleList.get(position).getName());
        startActivity(intent);
       // Toast.makeText(getActivity(),mExampleList.get(position).getName(), Toast.LENGTH_SHORT).show();
    }

   */


    private void getmyMandisString() {
//        reference.child("my_mandis").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                //In this case, "shalom" will be stored in mName
//                if(snapshot.exists()){
//                    mymandis = (String) snapshot.getValue();
//                }
//                // Toast.makeText(getActivity(),"fetche: "+mymandis, Toast.LENGTH_SHORT).show();
//                getMandis();
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
                    Log.w("getmyMandisString", "Listen failed.", e);
                    return;
                }
//                Toast.makeText(getContext(), "DONE", Toast.LENGTH_SHORT).show();

                if (snapshot != null && snapshot.exists() && snapshot.getString("my_mandis") != null) {
                    mymandis = snapshot.getString("my_mandis");
                } else {
                    Log.d("getmyMandisString", "Current data: null");
                }
                getMandis();
            }
        });

    }


    private void getMandis() {

//        myRef.child("apmcs").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mExampleList.clear();
//                mymandisExampleList.clear();
//                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
//                    mandis t = new mandis();
//                    t.setName(snapshot.child("name").getValue().toString());
//                    t.setAdd(snapshot.child("name").getValue().toString()+","+snapshot.child("state").getValue().toString());
//                    t.setLat(snapshot.child("lat").getValue().toString());
//                    t.setLon(snapshot.child("lon").getValue().toString());
//                    t.setState(snapshot.child("state").getValue().toString());
//
//                    Location tempLocation = new Location("");
//                    tempLocation.setLatitude(Double.parseDouble(snapshot.child("lat").getValue().toString()));
//                    tempLocation.setLongitude(Double.parseDouble(snapshot.child("lon").getValue().toString()));
//                    int distance = (int) myCurrentLoc.distanceTo(tempLocation);
//                    distance = distance/1000;
//                    t.setPlaceDistance(distance);
//
//                    if(mymandis.contains(snapshot.child("name").getValue().toString())){
//                        mymandisExampleList.add(t);
//                    }else{
//                        mExampleList.add(t);
//                    }
//
//                }
//
//                Collections.sort(mExampleList, (o1, o2) -> o1.getPlaceDistance() - o2.getPlaceDistance());
//
//
//                //mAdapter.updateDatalist(result);
//                refreshLists();
//
//                progressBar.setVisibility(View.GONE);
//                layout.setVisibility(View.VISIBLE);
//                Log.e("The read success: " ,"su"+mExampleList.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressBar.setVisibility(View.GONE);
//                layout.setVisibility(View.VISIBLE);
//
//                Log.e("The read failed: " ,databaseError.getMessage());
//            }
//        });

        db.collection("apmcs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mExampleList.clear();
                            mymandisExampleList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                mandis t = new mandis();
                                t.setName(document.getString("name"));
                                t.setAdd(document.getString("state"));
                                t.setLat(document.getString("lat"));
                                t.setLon(document.getString("lon"));
                                t.setState(document.getString("state"));

                                Location tempLocation = new Location("");
                                tempLocation.setLatitude(Double.parseDouble(document.getString("lat")));
                                tempLocation.setLongitude(Double.parseDouble(document.getString("lon")));
                                int distance = (int) myCurrentLoc.distanceTo(tempLocation);
                                distance = distance / 1000;
                                t.setPlaceDistance(distance);

//                                if (mymandis != null) {
                                if (mymandis.contains(document.getString("name"))) {
                                    mymandisExampleList.add(t);
                                } else {
                                    mExampleList.add(t);
                                }
//                                }


                            }

                            Collections.sort(mExampleList, (o1, o2) -> o1.getPlaceDistance() - o2.getPlaceDistance());


                            //mAdapter.updateDatalist(result);
                            refreshLists();

                            progressBar.setVisibility(View.GONE);
                            layout.setVisibility(View.VISIBLE);
                            Log.e("The read success: ", "su" + mExampleList.size());

                        } else {
                            Log.d("getMandis", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void refreshLists() {
        mAdapter.notifyDataSetChanged();
        mymandiAdapter.notifyDataSetChanged();
        if (mExampleList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            emptyView2.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            emptyView2.setVisibility(View.GONE);
        }

        if (mymandisExampleList.isEmpty()) {
            mymandiRecyclerView.setVisibility(View.GONE);
            emptyView1.setVisibility(View.VISIBLE);
        } else {
            mymandiRecyclerView.setVisibility(View.VISIBLE);
            emptyView1.setVisibility(View.GONE);
        }
        super.onResume();
    }

    private void showPopupMenu(View view, int position, int x) {
        PopupMenu popup = new PopupMenu(view.getContext(), view, Gravity.END);
        MenuInflater inflater = popup.getMenuInflater();

        if (x == 1) {
            inflater.inflate(R.menu.mymandis_menu, popup.getMenu());
            //set menu item click listener here
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, x));
        } else {
            inflater.inflate(R.menu.mandis_menu, popup.getMenu());
            //set menu item click listener here
            popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position, x));
        }
        popup.show();
    }

    public class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position, x;

        /**
         * @param position
         */
        MyMenuItemClickListener(int position, int x) {

            this.position = position;
            this.x = x;
        }

        /**
         * Click listener for popup menu items
         */

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.item1:
                    // ...
                    // Toast.makeText(getActivity(),mAdapter.getItem(position).getName(), Toast.LENGTH_SHORT).show();
                    //System.out.println("************"+mAdapter.getItem(position).getName()+"**************addded");
                    if (x == 1) {
                        //Toast.makeText(getActivity(),mymandiAdapter.getItem(position).getName()+"removed", Toast.LENGTH_SHORT).show();
                        mandis t = new mandis();
                        t.setName(mymandiAdapter.getItem(position).getName());
                        t.setAdd(mymandiAdapter.getItem(position).getAdd());
                        t.setLat(mymandiAdapter.getItem(position).getLat());
                        t.setLon(mymandiAdapter.getItem(position).getLon());


                        mExampleList.add(t);
                        // mymandis.(mymandiAdapter.getItem(position).getName());
                        mymandis = mymandis.replace(mymandiAdapter.getItem(position).getName() + ",", "");
                        //System.out.println("*******mymandis*****"+mymandis+"**************");
                        //Toast.makeText(getActivity(),mymandis, Toast.LENGTH_SHORT).show();
                        // mAdapter.notifyDataSetChanged();

                        mymandisExampleList.remove(position);
                        refreshLists();
                        // mymandiAdapter.notifyItemRemoved(position);
//                        reference.child("my_mandis").setValue(mymandis);
                        documentReference.update("my_mandis", mymandis);

                    } else {
                        mandis t = new mandis();
                        t.setName(mAdapter.getItem(position).getName());
                        t.setAdd(mAdapter.getItem(position).getAdd());
                        t.setLat(mAdapter.getItem(position).getLat());
                        t.setLon(mAdapter.getItem(position).getLon());

                        mymandisExampleList.add(t);
                        // mymandis.add(mAdapter.getItem(position).getName());
                        mymandis += mAdapter.getItem(position).getName() + ",";
                        //System.out.println("*******mymandis*****"+mymandis+"**************");
                        //Toast.makeText(getActivity(),mymandis, Toast.LENGTH_SHORT).show();
                        //mymandiAdapter.notifyDataSetChanged();

                        mExampleList.remove(position);
                        //mAdapter.notifyItemRemoved(position);
                        refreshLists();

//                        reference.child("my_mandis").setValue(mymandis);
                        documentReference.update("my_mandis", mymandis);

                    }
                    return true;
                case R.id.item2:
                    // ...
                    System.out.println("***********************item2 clciked");
                    return true;
                default:
            }
            return false;
        }
    }


}
