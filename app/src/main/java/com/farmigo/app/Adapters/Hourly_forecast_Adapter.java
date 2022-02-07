package com.farmigo.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.Todays_hourly_Forcast;
import com.farmigo.app.Models.WeatherForcast;
import com.farmigo.app.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Hourly_forecast_Adapter extends RecyclerView.Adapter<Hourly_forecast_Adapter.ExampleViewHolder> {
    private ArrayList<Todays_hourly_Forcast> mExampleList;
    private OnItemClickListener mListener;
    private boolean isVisible = true;


    DecimalFormat decim = new DecimalFormat("#,###.##");


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView temp;
        public ImageView icon;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);



            time = itemView.findViewById(R.id.textView63);
            temp = itemView.findViewById(R.id.textView72);
            icon = itemView.findViewById(R.id.imageView15);

            itemView.setOnClickListener(new View.OnClickListener() {
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
        }
    }


    public Hourly_forecast_Adapter(ArrayList<Todays_hourly_Forcast> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Todays_hourly_Forcast currentItem = mExampleList.get(position);

        Picasso.get().load("https://openweathermap.org/img/wn/"+currentItem.getIcon()+"@2x.png").into(holder.icon);
        holder.time.setText(currentItem.getTime());
        holder.temp.setText(currentItem.getTemp());

    }

    public void setVisibility(boolean val){
        isVisible = val;
        this.notifyDataSetChanged();
    }


    public void updateList(ArrayList<WeatherForcast> list){
        Collections.reverse(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }






}