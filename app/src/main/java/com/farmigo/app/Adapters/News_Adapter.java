package com.farmigo.app.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.farmigo.app.Helpers.RoundedCornersTransform;
import com.farmigo.app.Models.News;
import com.farmigo.app.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class News_Adapter extends RecyclerView.Adapter<News_Adapter.ExampleViewHolder> {
    private ArrayList<News> mExampleList;
    private OnItemClickListener mListener;
    private boolean isVisible = true;




    public interface OnItemClickListener {
        void onItemClick(View view,int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView src;
        public TextView heading;
        public ImageView img;


        public ExampleViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);


            date = itemView.findViewById(R.id.textView75);
            src = itemView.findViewById(R.id.textView74);
            heading = itemView.findViewById(R.id.textView76);
            img = itemView.findViewById(R.id.imageView14);

            itemView.setOnClickListener(new View.OnClickListener() {
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


    public News_Adapter(ArrayList<News> exampleList) {
        mExampleList = exampleList;
    }


    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        News currentItem = mExampleList.get(position);

        Picasso.get().load(currentItem.getImg()).transform(new RoundedCornersTransform()).into(holder.img);

        holder.src.setText(currentItem.getSrc());
        holder.date.setText(changeDate(currentItem.getDate(),"yyyy-MM-dd"));
        holder.heading.setText(currentItem.getHeading());
    }


    public void setVisibility(boolean val){
        isVisible = val;
        this.notifyDataSetChanged();
    }


    public void updateList(ArrayList<News> list){
        mExampleList= list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
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

    public News getItem(int position) {
        return mExampleList.get(position);
    }


}