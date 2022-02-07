package com.farmigo.app.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.ChartData;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class Historical_Prices_Adapter extends RecyclerView.Adapter<Historical_Prices_Adapter.ExampleViewHolder> {
    private ArrayList<ChartData> mExampleList;
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
        public TextView price;
        public TextView date;
        public TextView rise;
        public ImageView arrow;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);

            price = itemView.findViewById(R.id.textView55);
            date = itemView.findViewById(R.id.textView57);
            rise = itemView.findViewById(R.id.textView56);
            arrow = itemView.findViewById(R.id.imageView11);

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


    public Historical_Prices_Adapter(ArrayList<ChartData> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.historical_price_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        ChartData currentItem = mExampleList.get(position);
       // holder.mImageView.setImageResource(currentItem.getImageResource());

        holder.price.setText(decim.format(Long.valueOf(currentItem.getPrice())));

        holder.rise.setText(currentItem.getRise());
        holder.date.setText(changeDate(currentItem.getDate(),"yyyy-MM-dd"));


        if (Integer.valueOf(currentItem.getRise()) < 0) {
            holder.rise.setTextColor(Color.parseColor("#e10000"));
            Picasso.get().load(R.drawable.arrow_down_right).into(holder.arrow);
        } else {
            holder.rise.setText("+" + currentItem.getRise());
            holder.rise.setTextColor(Color.parseColor("#0b996a"));
            Picasso.get().load(R.drawable.arrow_up_right).into(holder.arrow);
        }


       /* if(isVisible){
            //holder.rise.setVisibility(View.GONE);
           // holder.removeBagItem.setVisibility(View.VISIBLE);
            holder.percentage.setVisibility(View.VISIBLE);
            if (Integer.valueOf(currentItem.getPercentage()) < 0) {
                holder.rise.setTextColor(Color.parseColor("#e10000"));
                holder.percentage.setTextColor(Color.parseColor("#e10000"));
                Picasso.get().load(R.drawable.arrow_down_right).into(holder.arrow);
            } else {
                holder.rise.setText("+" + currentItem.getPercentage());
                holder.rise.setTextColor(Color.parseColor("#0b996a"));
                holder.percentage.setTextColor(Color.parseColor("#0b996a"));
                Picasso.get().load(R.drawable.arrow_up_right).into(holder.arrow);
            }

        }else{
           // holder.removeBagItem.setVisibility(View.GONE);
            holder.percentage.setVisibility(View.GONE);
            if (Integer.valueOf(currentItem.getRise()) < 0) {
                holder.rise.setTextColor(Color.parseColor("#e10000"));
                Picasso.get().load(R.drawable.arrow_down_right).into(holder.arrow);
            } else {
                holder.rise.setText("+" + currentItem.getRise());
                holder.rise.setTextColor(Color.parseColor("#0b996a"));
                Picasso.get().load(R.drawable.arrow_up_right).into(holder.arrow);
            }
        }

        */


        //holder.date.setText(DateFormat.getDateInstance().format(currentItem.getDate()));

       /* long msDiff = Calendar.getInstance().getTimeInMillis() - testCalendar.getTimeInMillis();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        */

    }

    public void setVisibility(boolean val){
        isVisible = val;
        this.notifyDataSetChanged();
    }


    public void updateList(ArrayList<ChartData> list){
        Collections.reverse(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }


   /* private String getDate(long time) {
        Date date = new Date(time*1000L); // *1000 is to convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy "); // the format of your date
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        return sdf.format(date);
    }

    */




    public long Daybetween(String date1,String date2,String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.ENGLISH);
        Date Date1 = null,Date2 = null;
        try{
            Date1 = sdf.parse(date1);
            Date2 = sdf.parse(date2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return (Date2.getTime() - Date1.getTime())/(24*60*60*1000);
    }


    public String changeDate(String date1,String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("dd MMM yyyy");
        String yourFormatedDateString = sdf.format(date);

        return yourFormatedDateString;

    }
}