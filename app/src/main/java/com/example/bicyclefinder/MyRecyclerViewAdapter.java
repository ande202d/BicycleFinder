package com.example.bicyclefinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private List<Bike> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    MyRecyclerViewAdapter(Context context, List<Bike> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

//    public void updateData(List<Bike> data){
//        mData.clear();
//        mData.addAll(data);
//        notifyDataSetChanged();
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.rv_bike_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {
        Bike bike = mData.get(position);
        holder.myTextViewFrameNumber.setText(bike.getFrameNumber());
        holder.myTextViewPlace.setText(bike.getPlace());
        holder.myTextViewDate.setText(bike.getDate());
        holder.myTextViewMissing.setText(bike.getMissingFound());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    Bike getItem(int id){ return mData.get(id);}

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextViewFrameNumber;
        TextView myTextViewPlace;
        TextView myTextViewDate;
        TextView myTextViewMissing;

        ViewHolder(View itemView) {
            super(itemView);
            myTextViewFrameNumber = itemView.findViewById(R.id.rvBikeRowFrameNumber);
            myTextViewPlace = itemView.findViewById(R.id.rvBikeRowPlace);
            myTextViewDate = itemView.findViewById(R.id.rvBikeRowDate);
            myTextViewMissing = itemView.findViewById(R.id.rvBikeRowMissing);
            itemView.setOnClickListener(this);
            //myTextView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
