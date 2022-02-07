package com.farmigo.app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.State;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.R;
import com.farmigo.app.activities.MandiViewActivity;

import java.util.ArrayList;


public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.ExampleViewHolder>  {
    private ArrayList<State> mExampleList;
    // private ArrayList<mandis> mandisFiltered;
    private OnItemClickListener mListener;



    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }



    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView state;



        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView9);
            state = itemView.findViewById(R.id.textView45);
            cardView = itemView.findViewById(R.id.cardView3);



            cardView.setOnClickListener(new View.OnClickListener() {
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




        }
    }


    public StatesAdapter(ArrayList<State> exampleList) {
        mExampleList = exampleList;

    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_card, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        State currentItem = mExampleList.get(position);

        holder.state.setText(currentItem.getState());



    }




    public void updateList(ArrayList<State> list){
        mExampleList = list;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return mExampleList.size();
    }

    public State getItem(int position) {
        return mExampleList.get(position);
    }






}