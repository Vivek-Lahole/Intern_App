package com.farmigo.app.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.ChartData;
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

public class Weather_forecast_Adapter extends RecyclerView.Adapter<Weather_forecast_Adapter.ExampleViewHolder> {
    private ArrayList<WeatherForcast> mExampleList;
    private OnItemClickListener mListener;
    private boolean isVisible = true;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView day;
        public TextView date;
        public ImageView icon;
        public TextView temp;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);


            day = itemView.findViewById(R.id.textView58);
            date = itemView.findViewById(R.id.textView59);
            temp = itemView.findViewById(R.id.textView60);
            icon = itemView.findViewById(R.id.imageView14);

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


    public Weather_forecast_Adapter(ArrayList<WeatherForcast> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_forecast_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        WeatherForcast currentItem = mExampleList.get(position);

        Picasso.get().load("https://openweathermap.org/img/wn/"+currentItem.getIcon()+"@2x.png").into(holder.icon);
        holder.day.setText(currentItem.getDay());
        holder.date.setText(currentItem.getDate());
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