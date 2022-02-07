package com.farmigo.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.mandis;
import com.farmigo.app.R;
import com.farmigo.app.activities.MandiViewActivity;

import java.util.ArrayList;
import java.util.Locale;


public class MyMandisAdapter extends RecyclerView.Adapter<MyMandisAdapter.ExampleViewHolder>  {
    private ArrayList<mandis> mExampleList;
    // private ArrayList<mandis> mandisFiltered;
    private OnItemClickListener mListener;
    private OnCardClickListener mcardListener;

    private String distance;
    private String lat;
    private String lon;


    public interface OnItemClickListener {
        void onItemClick(View view,int position);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public interface OnCardClickListener {
        void onCardClick(View view,int position);

    }
    public void setOnCardClickListener(OnCardClickListener listener) {
        mcardListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView name;
        public TextView add;
        public TextView distance;
        public ImageButton dot_menu;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener,final OnCardClickListener listener2) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView9);
            name = itemView.findViewById(R.id.textView45);
            add = itemView.findViewById(R.id.textView46);
            distance = itemView.findViewById(R.id.textView47);
            cardView = itemView.findViewById(R.id.cardView3);
            dot_menu = itemView.findViewById(R.id.imageButton);


         /*   itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

          */


           dot_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(v,position);
                        }
                    }
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener2 != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener2.onCardClick(v,position);
                        }
                    }

                    /*Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                    intent.putExtra("title", getItem(position).getName());
                    view.getContext().startActivity(intent);

                     */
                }
            });


        }
    }


    /*@Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mExampleList = mExampleList;
                } else {
                    ArrayList<mandis> filteredList = new ArrayList<>();
                    for (mandis row : mExampleList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getAdd().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    mExampleList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mExampleList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mExampleList = (ArrayList<mandis>) filterResults.values;

                // refresh the list with filtered data
                notifyDataSetChanged();
            }
        };
    }

     */



    public MyMandisAdapter(ArrayList<mandis> exampleList,Context ctx) {
        mExampleList = exampleList;

        SharedPreferences prefs = ctx.getSharedPreferences("pref", Context.MODE_PRIVATE);
        lat = prefs.getString("lat", "");
        lon = prefs.getString("lon", "");
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mandi_card_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener,mcardListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        mandis currentItem = mExampleList.get(position);

        holder.name.setText((currentItem.getName()));
        holder.add.setText(currentItem.getAdd());
        holder.distance.setText(distance(currentItem.getLat(),currentItem.getLon())+" km away");


       /* holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(), MandiViewActivity.class);
                intent.putExtra("title", mExampleList.get(position).getName());
                view.getContext().startActivity(intent);
            }
        });

        */


    }


    private String distance(String latB, String logB)
    {
        Location locationA = new Location("point A");

        locationA.setLatitude(Double.valueOf(lat));
        locationA.setLongitude(Double.valueOf(lon));

        Location locationB = new Location("point B");

        locationB.setLatitude(Double.valueOf(latB));
        locationB.setLongitude(Double.valueOf(logB));

        //distance =  String.format(Locale.ENGLISH,"%.2f",locationA.distanceTo(locationB)/1000);
        distance = String.valueOf(Math.round(locationA.distanceTo(locationB)/1000));

        return distance;
    }


    public void updateList(ArrayList<mandis> list){
        mExampleList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public mandis getItem(int position) {
        return mExampleList.get(position);
    }




}