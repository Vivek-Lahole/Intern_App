package com.farmigo.app.Adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Models.Commodities;
import com.farmigo.app.Models.Fav_commodities;
import com.farmigo.app.Models.mandis;
import com.farmigo.app.R;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ExampleViewHolder> {
    private ArrayList<Commodities> mExampleList;
    private OnItemClickListener mListener;
    private OnCardClickListener mcardListener;
    private boolean isVisible = false;


    DecimalFormat decim = new DecimalFormat("#,###.##");

    Date todayDate = Calendar.getInstance().getTime();
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    String todayString = formatter.format(todayDate);
   // String s=String.valueOf(Daybetween("2020-10-20",todayString,"yyyy-mm-dd"));

    public interface OnItemClickListener {
        void onItemClick(int position);
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
       // public ImageView mImageView;
        public TextView commodity;
        public TextView price;
        public TextView apmc;
        public ConstraintLayout top;
        public ConstraintLayout middle;
        public TextView date;
        public TextView rise;
        public TextView type;
        public ImageView img;
        public ImageView arrow;
        public TextView percentage;
        public TextView days,addtext;
        private CardView cardView;
        private LinearLayout addcrop;
        private ImageView bookmark1,bookmark2;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener,final OnCardClickListener listener2) {
            super(itemView);
            //mImageView = itemView.findViewById(R.id.imageView9);
            commodity = itemView.findViewById(R.id.textView77);

            price = itemView.findViewById(R.id.textView26);
            apmc = itemView.findViewById(R.id.textView21);
            cardView = itemView.findViewById(R.id.cardView3);
            top = itemView.findViewById(R.id.constraintLayout7);
            middle = itemView.findViewById(R.id.constraintLayoutmiddle);
            date = itemView.findViewById(R.id.textView36);
            rise = itemView.findViewById(R.id.textView41);
            type = itemView.findViewById(R.id.textView23);
            img = itemView.findViewById(R.id.imageView9);
            arrow = itemView.findViewById(R.id.imageView11);
            percentage = itemView.findViewById(R.id.textView42);
            days = itemView.findViewById(R.id.textView38);
            addcrop = itemView.findViewById(R.id.addcrop);
            bookmark1=itemView.findViewById(R.id.bookmark1);
            bookmark2=itemView.findViewById(R.id.bookmark2);
            addtext=itemView.findViewById(R.id.addtext);


            addcrop.setOnClickListener(new View.OnClickListener() {
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

    public CommodityAdapter(ArrayList<Commodities> exampleList) {
        mExampleList = exampleList;
    }
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.commodity_card, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener,mcardListener);
        return evh;
    }
    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        Commodities currentItem = mExampleList.get(position);
       // holder.mImageView.setImageResource(currentItem.getImageResource());
        Picasso.get().load(currentItem.getImg()).into(holder.img);
        holder.commodity.setText(GetFormattedString(currentItem.getName()));
        holder.price.setText(decim.format(Long.valueOf(currentItem.getPrice())));
        //holder.price.setText(currentItem.getPrice());
       // holder.apmc.setText(currentItem.getApmc_name());
        holder.top.setBackgroundColor(Color.parseColor(currentItem.getColor1()));
        holder.middle.setBackgroundColor(Color.parseColor(currentItem.getColor2()));
        //holder.rise.setText(currentItem.getRise());
        //holder.date.setText(getDate(Long.valueOf(currentItem.getDate())));
        //Log.d("onBindViewHolder", "onBindViewHolder: "+currentItem.getDate()+currentItem.getName());
        holder.date.setText(changeDate(currentItem.getDate(),"yyyy-MM-dd", currentItem.getName()));
        holder.type.setText(currentItem.getType());
        holder.days.setText(String.valueOf(Daybetween(currentItem.getDate(),todayString,"yyyy-MM-dd"))+" days ago");
        //isVisible=currentItem.getBookmark();
        if(currentItem.getBookmark()){
            //holder.rise.setVisibility(View.GONE);
           // holder.removeBagItem.setVisibility(View.VISIBLE);
            holder.bookmark1.setVisibility(View.GONE);
            holder.bookmark2.setVisibility(View.VISIBLE);

            holder.addtext.setText("Remove");

        }else{
           // holder.removeBagItem.setVisibility(View.GONE);
            holder.bookmark1.setVisibility(View.VISIBLE);
            holder.bookmark2.setVisibility(View.GONE);

            holder.addtext.setText("Add");
        }

        if(isVisible){
            //holder.rise.setVisibility(View.GONE);
            // holder.removeBagItem.setVisibility(View.VISIBLE);
            holder.percentage.setVisibility(View.VISIBLE);
            if (Integer.valueOf(currentItem.getPercentage()) < 0) {
                holder.rise.setText(currentItem.getPercentage());
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
                holder.rise.setText(currentItem.getRise());
                holder.rise.setTextColor(Color.parseColor("#e10000"));
                Picasso.get().load(R.drawable.arrow_down_right).into(holder.arrow);
            } else {
                holder.rise.setText("+" + currentItem.getRise());
                holder.rise.setTextColor(Color.parseColor("#0b996a"));
                Picasso.get().load(R.drawable.arrow_up_right).into(holder.arrow);
            }
        }

    }

    public void setVisibility(boolean val){
        isVisible = val;
        this.notifyDataSetChanged();
    }

    public String GetFormattedString(String s) {

        if (s.contains("(")) {
            return s.substring(0, s.indexOf("("));
        } else {
            return  s;
        }
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

    public void updateList(ArrayList<Commodities> list){
        mExampleList = list;
        notifyDataSetChanged();
    }


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


    public String changeDate(String date1,String pattern,String name)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        Log.d("date1", "changeDate: "+date1+' '+name);
        try {
            date = sdf.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("dd MMM yy");
        String yourFormatedDateString = sdf.format(date);

        return yourFormatedDateString;

    }

    public Commodities getItem(int position) {
        return mExampleList.get(position);
    }
}