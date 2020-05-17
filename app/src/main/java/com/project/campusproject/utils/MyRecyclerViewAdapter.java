package com.project.campusproject.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.project.campusproject.R;
import com.project.campusproject.data.ReportProblem;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<ReportProblem> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<ReportProblem> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReportProblem animal = mData.get(position);
        holder.latitude.setText("Latitude " + String.valueOf(animal.getLatitude()));
        holder.longitude.setText("Longitude " +String.valueOf(animal.getLongitude()));
        if(animal.getDrainage()!=null){
            if(animal.getDrainage().getStatus() == 1){
                holder.drainageIssueLayout.setVisibility(View.VISIBLE);
                holder.drainageIssueTv.setText(animal.getDrainage().getDesc());
                if(animal.getDrainage().getDesc().isEmpty()){
                    holder.drainageIssueTv.setText("nil");
                }
            }else {
                holder.drainageIssueLayout.setVisibility(View.GONE);
            }

        }else {
            holder.drainageIssueTv.setText("nil");
            holder.drainageIssueLayout.setVisibility(View.GONE);
        }
        if(animal.getElectrical()!=null){
            if(animal.getElectrical().getStatus() == 1){
                holder.electricIssueLayout.setVisibility(View.VISIBLE);
                holder.electricIssueTv.setText(animal.getElectrical().getDesc());
                if(animal.getElectrical().getDesc().isEmpty()){
                    holder.electricIssueTv.setText("nil");
                }
            }else {
                holder.electricIssueLayout.setVisibility(View.GONE);
            }



        }else {
            holder.electricIssueTv.setText("nil");
            holder.electricIssueLayout.setVisibility(View.GONE);
        }
        if(animal.getGarbage()!=null){
            if(animal.getGarbage().getStatus() == 1){
                holder.garbageIssueLayout.setVisibility(View.VISIBLE);
                holder.garbageIssueTv.setText(animal.getGarbage().getDesc());
                if(animal.getGarbage().getDesc().isEmpty()){
                    holder.garbageIssueTv.setText("nil");
                }
            }else {
                holder.garbageIssueLayout.setVisibility(View.GONE);
            }


        }else {
            holder.garbageIssueLayout.setVisibility(View.GONE);
            holder.garbageIssueTv.setText("nil");
        }
        if(animal.getOther()!=null){
            if(animal.getOther().getStatus() == 1){
                holder.otherIssueLayout.setVisibility(View.VISIBLE);
                holder.otherIssueTv.setText(animal.getOther().getDesc());
                if(animal.getOther().getDesc().isEmpty()){
                    holder.otherIssueTv.setText("nil");
                }
            }else {
                holder.otherIssueLayout.setVisibility(View.GONE);
            }


        }else {
            holder.otherIssueLayout.setVisibility(View.GONE);
            holder.otherIssueTv.setText("nil");
        }

        holder.resolved.setOnClickListener(view -> {
            if (mClickListener != null) mClickListener.onItemClick(view, position,animal);
        });

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView drainageIssueTv;
        TextView electricIssueTv;
        TextView garbageIssueTv;
        TextView otherIssueTv;

        AppCompatButton resolved;

        TextView longitude;
        TextView latitude;

        LinearLayout drainageIssueLayout;
        LinearLayout electricIssueLayout;
        LinearLayout garbageIssueLayout;
        LinearLayout otherIssueLayout;

        ViewHolder(View itemView) {
            super(itemView);
            resolved = itemView.findViewById(R.id.resolved);

            longitude = itemView.findViewById(R.id.longitude);
            latitude = itemView.findViewById(R.id.latitude);

            drainageIssueLayout = itemView.findViewById(R.id.drainageIssueLayout);
            electricIssueLayout = itemView.findViewById(R.id.electricIssueLayout);
            garbageIssueLayout = itemView.findViewById(R.id.garbageIssueLayout);
            otherIssueLayout = itemView.findViewById(R.id.otherIssueLayout);

            drainageIssueTv = itemView.findViewById(R.id.drainageIssueTv);
            electricIssueTv = itemView.findViewById(R.id.electricIssueTv);
            garbageIssueTv = itemView.findViewById(R.id.garbageIssueTv);
            otherIssueTv = itemView.findViewById(R.id.otherIssueTv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    // convenience method for getting data at click position
    ReportProblem getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position,ReportProblem reportProblem);
    }
}