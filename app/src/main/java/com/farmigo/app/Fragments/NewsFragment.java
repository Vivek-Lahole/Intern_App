package com.farmigo.app.Fragments;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.farmigo.app.Adapters.News_Adapter;
import com.farmigo.app.Models.News;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.PhoneAuth.Select_lang_Activity;
import com.farmigo.app.R;
import com.farmigo.app.activities.NewsActivity;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class NewsFragment extends Fragment {


    private ArrayList<News> newsArrayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private News_Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DatabaseReference myRef = database.getReference();
    private ShimmerRecyclerView shimmerRecycler;


    public NewsFragment() {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        shimmerRecycler = view.findViewById(R.id.shimmer_recycler_view);
        shimmerRecycler.showShimmerAdapter();


        mRecyclerView = view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());

        mAdapter = new News_Adapter(newsArrayList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new News_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                //changeItem(position, "Clicked");
                Intent intent = new Intent(getContext(), NewsActivity.class);
                intent.putExtra("heading", mAdapter.getItem(position).getHeading());
                intent.putExtra("news", mAdapter.getItem(position).getArtical());
                intent.putExtra("date", mAdapter.getItem(position).getDate());
                intent.putExtra("src", mAdapter.getItem(position).getSrc());
                intent.putExtra("img", mAdapter.getItem(position).getImg());
                startActivity(intent);
            }
        });


        getBulletin();

        return view;
    }


    private void getBulletin() {

//        myRef.child("news").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                newsArrayList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    News t = new News();
//                    t.setDate(snapshot.child("date").getValue().toString());
//                    t.setHeading(snapshot.child("heading").getValue().toString());
//                    t.setImg(snapshot.child("img").getValue().toString());
//                    t.setSrc(snapshot.child("src").getValue().toString());
//                    t.setArtical(snapshot.child("artical").getValue().toString());
//
//                    newsArrayList.add(t);
//                }
//
//
//                mAdapter.notifyDataSetChanged();
//                //progressBar.setVisibility(View.GONE);
//                shimmerRecycler.hideShimmerAdapter();
//                Log.e("The read success: ", "su" + newsArrayList.size());
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                // progressBar.setVisibility(View.GONE);
//                shimmerRecycler.hideShimmerAdapter();
//                Log.e("The read failed: ", databaseError.getMessage());
//            }
//        });

        db.collection("news")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                News t = new News();
                                t.setDate(document.getString("date"));
                                t.setHeading(document.getString("heading"));
                                t.setImg(document.getString("img"));
                                t.setSrc(document.getString("src"));
                                t.setArtical(document.getString("artical"));

                                newsArrayList.add(t);

                            }

                            mAdapter.notifyDataSetChanged();
                            //progressBar.setVisibility(View.GONE);
                            shimmerRecycler.hideShimmerAdapter();
                            Log.e("The read success: ", "su" + newsArrayList.size());

                        } else {
                            Log.d("getMandis", "Error getting documents: ", task.getException());
                        }
                    }
                }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                shimmerRecycler.hideShimmerAdapter();
            }
        });
    }


}
