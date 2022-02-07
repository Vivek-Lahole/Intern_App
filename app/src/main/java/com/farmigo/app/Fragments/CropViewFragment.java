package com.farmigo.app.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.farmigo.app.Adapters.CommodityAdapter;
import com.farmigo.app.Adapters.CropViewAdapter;
import com.farmigo.app.Models.APMClist_Distance;
import com.farmigo.app.Models.Commodities;
import com.farmigo.app.Models.CropViewCommodity;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.Models.User;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.activities.HighChartActivity;
import com.farmigo.app.activities.MandiViewActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A simple {@author Vivek Lahole} subclass.
 */

public class CropViewFragment extends Fragment {

    private static final String TAG = "123456";
    private ArrayList<String> commoditylist = new ArrayList<>();
    private ArrayList<String> apmclist;
    private ArrayList<String> allCropList = new ArrayList<>();
    private ArrayList<String> allCropList2 = new ArrayList<>();
    private ArrayList<APMClist_Distance> apmClist_distances;
    private ArrayList<CropViewCommodity> cropViewCommodityArrayList = new ArrayList<>();
    private ArrayList<CropViewCommodity> temp1=new ArrayList<>();
    //    private ArrayAdapter<String> crop_adapter;
    private String all = "All", new_date = "", my_crops_string = "";
    private long new_price, old_price;
    private String lat, lon, distance, Nearest_apmc, Nearest_state;
    private int minDist = (Integer.MAX_VALUE);
    private Location myCurrentLoc;
    private TextView no_crops;
    private String category_filter = "All", state_filter, crop_filter = "All";

    private Spinner crop_spinner, category_spinner, state_spinner;
    private SwitchMaterial switch_filters;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private CropViewAdapter cropViewAdapter;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder;
    private AlertDialog progressDialog;;


    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection(("commodities"));
    private DocumentReference documentReference;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public CropViewFragment() {
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

        View view = inflater.inflate(R.layout.fragment_crop_view, container, false);
        documentReference= db.collection("users").document(auth.getCurrentUser().getUid());

        state_spinner = view.findViewById(R.id.state_spinner);
        crop_spinner = view.findViewById(R.id.crop_spinner);
        progressBar = view.findViewById(R.id.progressBar);
        no_crops = view.findViewById(R.id.no_crops);
        no_crops.setVisibility(View.GONE);
        category_spinner = view.findViewById(R.id.category_spinner);
        mRecyclerView = view.findViewById(R.id.recyclerView3);

        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);

        lat = prefs.getString("lat", "");
        lon = prefs.getString("lon", "");
        myCurrentLoc = new Location("point A");
        myCurrentLoc.setLatitude(Double.valueOf(lat));
        myCurrentLoc.setLongitude(Double.valueOf(lon));

        Nearest_apmc = prefs.getString("nearest_apmc", "");
        Nearest_state = prefs.getString("selected_state", "");

        ArrayAdapter<String> state_adapter = CreateAdapter(R.array.india_states_all1);
        state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        state_spinner.setAdapter(state_adapter);
        state_spinner.setSelection(state_adapter.getPosition(Nearest_state), true);

        ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(getContext(), R.array.category,R.layout.layout_custom_spinner);
        category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category_spinner.setAdapter(category_adapter);

        category_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_category = adapterView.getItemAtPosition(i).toString();
                category_filter = selected_category;

                if (category_filter.contains("All")) {
                    category_filter = "All";
                    Log.d(TAG, "onItemSelected: " + selected_category);
                    ArrayAdapter<String> crop_adapter2 = new ArrayAdapter<String>(getContext(), R.layout.layout_custom_spinner, allCropList);
                    crop_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    crop_spinner.setAdapter(crop_adapter2);
                    crop_spinner.setSelection(crop_adapter2.getPosition("All"));
                    crop_filter = "All";
                    GetCropFiletr(crop_filter, category_filter);
                } else {
                    Log.d(TAG, "onItemSelected: " + selected_category);

                    GetCropFiletr(crop_filter, category_filter);
                    GetCropList(selected_category, new GetCropList() {
                        @Override
                        public void GetCropListcallback(Map<String, Object> croplist) {
                            ArrayList<String> templist = new ArrayList<>(croplist.keySet());
                            Collections.sort(templist);
                            templist.add(0, "All");
                            ArrayAdapter<String> crop_adapter2 = new ArrayAdapter<String>(getContext(), R.layout.layout_custom_spinner, templist);
                            crop_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            crop_spinner.setAdapter(crop_adapter2);
                            crop_spinner.setSelection(crop_adapter2.getPosition("All"));
                            crop_filter = "All";
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        crop_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected_crop = adapterView.getItemAtPosition(i).toString();
                crop_filter = selected_crop;
                GetCropFiletr(crop_filter, category_filter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long text) {

                String selected_State = arg0.getItemAtPosition(pos).toString();
                progressBar.setVisibility(View.VISIBLE);

                String finalSelected_State = selected_State;
                GetAPMClist(selected_State, new GetAPMClist() {
                    @Override
                    public void GetAPMClistcallback(ArrayList<String> list, ArrayList<APMClist_Distance> list2) {

                        Log.d(TAG, "GetAPMClistcallback123: " + list2.toString());
                        if (list2.isEmpty()) {
//                            no_crops.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            no_crops.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            for (int i = 0; i < list2.size(); i++) {
                                GetCommodity(list2.get(i).getApmc_name(), finalSelected_State, list2.get(i).getDistance(), new GetCommodity() {
                                    @Override
                                    public void GetCommoditycallback(ArrayList<String> list, ArrayList<CropViewCommodity> clist) {

//                                        Toast.makeText(getContext(), "List is added alreaady", Toast.LENGTH_SHORT).show();
                                        cropViewAdapter = new CropViewAdapter(cropViewCommodityArrayList);
                                        mRecyclerView.setAdapter(cropViewAdapter);
                                        cropViewAdapter.notifyDataSetChanged();
                                        cropViewAdapter.setOnCardClickListener1(new CropViewAdapter.OnCardClickListener1() {
                                            @Override
                                            public void onCardClick(View view, int position) {
                                                Intent intent = new Intent(view.getContext(), HighChartActivity.class);
                                                intent.putExtra("apmc_name", cropViewAdapter.getItem(position).getApmc_name());
                                                intent.putExtra("commodity_name", cropViewAdapter.getItem(position).getName());
                                                intent.putExtra("commodity_type", cropViewAdapter.getItem(position).getType());
                                                intent.putExtra("state", cropViewAdapter.getItem(position).getState());
                                                intent.putExtra("category", cropViewAdapter.getItem(position).getCategory());
                                                SharedPreferences sph= getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor=sph.edit();
                                                editor.putString("my_crops_string",my_crops_string);
                                                editor.apply();
                                                startActivity(intent);
                                            }
                                        });
//                                        new loadlist().execute();
                                    }
                                });
                            }
                            progressBar.setVisibility(View.GONE);
                            ArrayAdapter<CharSequence> category_adapter1 = ArrayAdapter.createFromResource(getContext(), R.array.category, R.layout.layout_custom_spinner);
                            category_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            category_spinner.setAdapter(category_adapter1);
                            category_filter = "All";
                            ArrayAdapter<String> crop_adapter1 = new ArrayAdapter<>(getContext(), R.layout.layout_custom_spinner, allCropList);
                            crop_adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            crop_spinner.setAdapter(crop_adapter1);
                            crop_filter = "All";
                        }
                    }
                });
            }

            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        GetCropList(category_filter, new GetCropList() {
            @Override
            public void GetCropListcallback(Map<String, Object> croplist) {
                allCropList.clear();
                ArrayList<String> sortedKeys
                        = new ArrayList<String>(croplist.keySet());
                Collections.sort(sortedKeys);
                allCropList=sortedKeys;
                allCropList.add(0, "All");
                ArrayAdapter<String> crop_adapter = new ArrayAdapter<String>(getContext(), R.layout.layout_custom_spinner, allCropList);
                crop_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                crop_spinner.setAdapter(crop_adapter);
                crop_spinner.setSelection(crop_adapter.getPosition("All"));
                progressBar.setVisibility(View.GONE);
            }
        });

        GetAPMClist(Nearest_state, new GetAPMClist() {
            @Override
            public void GetAPMClistcallback(ArrayList<String> list, ArrayList<APMClist_Distance> list2) {

                if (list2.isEmpty()) {
//                    no_crops.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                } else {
                    no_crops.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    for (int i = 0; i < list2.size(); i++) {
                        Log.d(TAG, "GetAPMClistcallback: " + list2.get(i).getDistance() + " " + list2.get(i).getApmc_name());
                        GetCommodity(list2.get(i).getApmc_name(), Nearest_state, list2.get(i).getDistance(), new GetCommodity() {
                            @Override
                            public void GetCommoditycallback(ArrayList<String> list, ArrayList<CropViewCommodity> clist) {

                                Collections.sort(cropViewCommodityArrayList, new Comparator<CropViewCommodity>() {
                                    public int compare(CropViewCommodity o1, CropViewCommodity o2) {
                                        if (o1.getDistance() == o2.getDistance())
                                            return 0;
                                        return o1.getDistance() < o2.getDistance() ? -1 : 1;
                                    }
                                });
                                cropViewAdapter.updateList(cropViewCommodityArrayList);
                                cropViewAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        cropViewAdapter = new CropViewAdapter(cropViewCommodityArrayList);
        mRecyclerView.setAdapter(cropViewAdapter);
        cropViewAdapter.notifyDataSetChanged();

        cropViewAdapter.setOnCardClickListener1(new CropViewAdapter.OnCardClickListener1() {
            @Override
            public void onCardClick(View view, int position) {
                Intent intent = new Intent(view.getContext(), HighChartActivity.class);
                intent.putExtra("apmc_name", cropViewAdapter.getItem(position).getApmc_name());
                intent.putExtra("commodity_name", cropViewAdapter.getItem(position).getName());
                intent.putExtra("commodity_type", cropViewAdapter.getItem(position).getType());
                intent.putExtra("state", cropViewAdapter.getItem(position).getState());
                intent.putExtra("category", cropViewAdapter.getItem(position).getCategory());
                SharedPreferences sph= getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sph.edit();
                editor.putString("my_crops_string",my_crops_string);
                editor.apply();
                startActivity(intent);
            }
        });

        GetUserDetails(new GetUserDetails() {
            @Override
            public void GetUserDetailscallback(String s) {
                my_crops_string=s;
            }
        });

        return view;

    }

    private void GetCategoryFiletr(String selected_category) {

        ArrayList<CropViewCommodity> temp = new ArrayList<>();

        Log.d(TAG, "GetCategoryFiletr: " + cropViewCommodityArrayList);

        for (CropViewCommodity cropViewCommodity : cropViewCommodityArrayList) {
            if (selected_category.equals("All")) {
                temp.add(cropViewCommodity);
            } else {
                if (cropViewCommodity.getCategory().toLowerCase().contains(selected_category.toLowerCase())) {
                    temp.add(cropViewCommodity);
                }
            }
        }

        Log.d(TAG, "GetCategoryFiletr: temp " + temp);

        cropViewAdapter.updateList(temp);
        cropViewAdapter.notifyDataSetChanged();
    }

    private void GetCropFiletr(String selected_crop, String selected_category) {

        ArrayList<CropViewCommodity> temp = new ArrayList<>();

        if (selected_category.equals("All")) {
            for (CropViewCommodity cropViewCommodity : cropViewCommodityArrayList) {
                if (selected_crop.equals("All")) {
                    temp.add(cropViewCommodity);
                } else {
                    if (cropViewCommodity.getName().toLowerCase().equals(selected_crop.toLowerCase())) {
                        temp.add(cropViewCommodity);
                    }
                }
            }
        } else {
            for (CropViewCommodity cropViewCommodity : cropViewCommodityArrayList) {

                if (selected_crop.equals("All")) {
                    if (cropViewCommodity.getCategory().toLowerCase().equals(selected_category.toLowerCase())) {
                        temp.add(cropViewCommodity);
                    }
                } else {
                    if (cropViewCommodity.getName().toLowerCase().equals(selected_crop.toLowerCase()) && cropViewCommodity.getCategory().toLowerCase().equals(selected_category.toLowerCase())) {
                        temp.add(cropViewCommodity);
                    }
                }
            }
        }

        if(temp.isEmpty())
        {
            no_crops.setVisibility(View.VISIBLE);
        }
        else
        {
            no_crops.setVisibility(View.GONE);
        }

        cropViewAdapter.updateList(temp);
        cropViewAdapter.notifyDataSetChanged();
    }

    public ArrayAdapter<String> CreateAdapter(int id) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),R.layout.layout_custom_spinner, getResources().getStringArray(id)) {

            @Override
            public boolean isEnabled(int position) {
                if (position == 0) {
                    return false;
                } else {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };

        return adapter;
    }

    public interface GetCropList {
        void GetCropListcallback(Map<String, Object> croplist);
    }

    public interface GetAPMClist {
        void GetAPMClistcallback(ArrayList<String> list, ArrayList<APMClist_Distance> list2);
    }

    public interface GetUserDetails {
        void GetUserDetailscallback(String my_crops_string);
    }

    public interface GetCommodity {
        void GetCommoditycallback(ArrayList<String> list, ArrayList<CropViewCommodity> clist);
    }

    public interface GetLatestPrices {
        void AfterPricecallBack(long new_price, long old_price, String new_date);
    }

    public void GetCropList(String category, GetCropList getCropList) {

        progressBar.setVisibility(View.VISIBLE);
        ArrayList<String> croplist = new ArrayList<>();
        Map<String, Object> mcroplist = new HashMap<>();

        if (!category.equals("All")) {
            db.collection("commodities").whereEqualTo("category", category).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            croplist.add(snapshot.getString("name"));
                            mcroplist.put(snapshot.getString("name"), snapshot.getString("name"));
                        }

                        getCropList.GetCropListcallback(mcroplist);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            db.collection("commodities").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            croplist.add(snapshot.getString("name"));
                            mcroplist.put(snapshot.getString("name"), snapshot.getString("name"));
                        }
                        getCropList.GetCropListcallback(mcroplist);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void GetAPMClist(String selected_state, GetAPMClist getAPMClist) {
        progressBar.setVisibility(View.VISIBLE);
        apmclist = new ArrayList<>();
        apmClist_distances = new ArrayList<>();
        db.collection("apmcs").whereEqualTo("state", selected_state).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (DocumentSnapshot snapshot : task.getResult()) {
                        String name = snapshot.getString("name");
                        APMClist_Distance o = new APMClist_Distance();
                        String a = (snapshot.getString("lat"));
                        String b = (snapshot.getString("lon"));
                        Location tempLocation = new Location("");
                        tempLocation.setLatitude(Double.parseDouble(a));
                        tempLocation.setLongitude(Double.parseDouble(b));
                        int distance1 = (int) myCurrentLoc.distanceTo(tempLocation);
                        distance1 = distance1 / 1000;
                        o.setLon(b);
                        o.setLat(a);
                        o.setApmc_name(name);
                        o.setDistance(distance1);
                        apmClist_distances.add(o);
                        apmclist.add(name);
                    }
                    getAPMClist.GetAPMClistcallback(apmclist, apmClist_distances);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void GetUserDetails(GetUserDetails getUserDetails) {

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.getString("my_crops_string") != null) {
                        my_crops_string = snapshot.getString("my_crops_string");
                    }
                }
                getUserDetails.GetUserDetailscallback(my_crops_string);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void GetLatestPrices(String apmc_name, String nametype, String currentDate, GetLatestPrices getLatestPrices) {
        db.collection("price_table").document(apmc_name).collection(nametype).orderBy("Arrival_Date", Query.Direction.DESCENDING).whereLessThanOrEqualTo("Arrival_Date", currentDate).limit(2).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Log.w(TAG, "listen:error", error);
                    return;
                }

                Boolean b1 = true, b2 = true;
                new_price = 0;
                old_price = 0;

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot.exists() && b1) {
                        new_price = snapshot.getLong("Modal_Price");
                        new_date = snapshot.getString("Arrival_Date");
                        b1 = false;
                    } else if (snapshot.exists() && b2) {
                        old_price = snapshot.getLong("Modal_Price");
                        b2 = false;
                    }
                }
                getLatestPrices.AfterPricecallBack(new_price, old_price, new_date);
            }
        });
    }

    private void GetCommodity(String apmc, String state, int dist, GetCommodity getCommodity) {

        progressBar.setVisibility(View.VISIBLE);
        cropViewCommodityArrayList.clear();

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);

        if (!apmc.contains(".")) {
            collectionReference.whereEqualTo(apmc, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {

                            CropViewCommodity cropViewCommodity = new CropViewCommodity();

                            String name = snapshot.getString("name");
                            String type = snapshot.getString("type");
                            String nametype = name.concat(type);

                            cropViewCommodity.setName(name);
                            cropViewCommodity.setCategory(snapshot.getString("category"));
                            cropViewCommodity.setColor1(snapshot.getString("color1"));
                            cropViewCommodity.setColor2(snapshot.getString("color2"));
                            cropViewCommodity.setImg(snapshot.getString("img"));
                            cropViewCommodity.setType(type);
                            cropViewCommodity.setState(state);
                            cropViewCommodity.setApmc_name(apmc);
                            cropViewCommodity.setDistance(dist);

                            GetLatestPrices(apmc, nametype, currentDate, new CropViewFragment.GetLatestPrices() {
                                @Override
                                public void AfterPricecallBack(long new_price, long old_price, String new_date) {

                                    cropViewCommodity.setDate(new_date);

                                    cropViewCommodity.setPrice(String.valueOf(new_price));
                                    long increase = new_price - old_price;

                                    long percentage_change = 0;
                                    if (old_price != 0) {
                                        percentage_change = (increase * 100) / old_price;
                                    }

                                    cropViewCommodity.setPercentage(String.valueOf(percentage_change));
                                    cropViewCommodity.setRise(String.valueOf(increase));
                                    Collections.sort(cropViewCommodityArrayList, new Comparator<CropViewCommodity>() {
                                        public int compare(CropViewCommodity o1, CropViewCommodity o2) {
                                            if (o1.getDistance() == o2.getDistance())
                                                return 0;
                                            return o1.getDistance() < o2.getDistance() ? -1 : 1;
                                        }
                                    });
                                    cropViewCommodityArrayList.add(cropViewCommodity);
                                    cropViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        getCommodity.GetCommoditycallback(commoditylist, cropViewCommodityArrayList);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void GetCommodity1(String apmc, String state) {

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = sdf.format(date);

        if (!apmc.contains(".")) {
            collectionReference.whereEqualTo(apmc, true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot snapshot : task.getResult()) {
                            CropViewCommodity cropViewCommodity = new CropViewCommodity();

                            String name = snapshot.getString("name");
                            String type = snapshot.getString("type");
                            String nametype = name.concat(type);

                            cropViewCommodity.setName(name);
                            cropViewCommodity.setCategory(snapshot.getString("category"));
                            cropViewCommodity.setColor1(snapshot.getString("color1"));
                            cropViewCommodity.setColor2(snapshot.getString("color2"));
                            cropViewCommodity.setImg(snapshot.getString("img"));
                            cropViewCommodity.setType(type);
                            cropViewCommodity.setState(state);
                            cropViewCommodity.setApmc_name(apmc);

                            GetLatestPrices("Pune", nametype, currentDate, new CropViewFragment.GetLatestPrices() {
                                @Override
                                public void AfterPricecallBack(long new_price, long old_price, String new_date) {
                                    Log.d("oncallback321", "AfterPricecallBack crop: " + new_price + " " + old_price);

                                    cropViewCommodity.setDate(new_date);

                                    cropViewCommodity.setPrice(String.valueOf(new_price));
                                    long increase = new_price - old_price;

                                    long percentage_change = 0;
                                    if (old_price != 0) {
                                        percentage_change = (increase * 100) / old_price;
                                    }

                                    cropViewCommodity.setPercentage(String.valueOf(percentage_change));
                                    cropViewCommodity.setRise(String.valueOf(increase));
                                    cropViewCommodityArrayList.add(cropViewCommodity);
                                    refreshLists();
                                }
                            });

                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public AlertDialog.Builder getDialogProgressBar() {

        if (builder == null) {
            builder = new AlertDialog.Builder(getContext());
            builder.setCancelable(false);
            builder.setView(R.layout.layout_progress_custom);
        }
        return builder;
    }

    private void refreshLists() {
        progressBar.setVisibility(View.VISIBLE);
        cropViewAdapter.notifyDataSetChanged();
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

//    private class loadlist extends AsyncTask< Void,Void,Void>{
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            for(int i=0;i<cropViewCommodityArrayList.size();i++)
//            {
//                temp1.add(cropViewCommodityArrayList.get(i));
//            }
//            cropViewAdapter.updateList(temp1);
//            cropViewAdapter.notifyDataSetChanged();
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog=getDialogProgressBar().show();
//        }
//
//        @Override
//        protected void onPostExecute(Void unused) {
//            super.onPostExecute(unused);
//            progressDialog.dismiss();
//        }
//    }
}