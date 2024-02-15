package com.practice.easyexam.app.view.tracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.RecordTest;

import java.util.List;

public class UserTrackingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private List<RecordTest> dataList;

    public UserTrackingAdapter(List<RecordTest> dataList) {
        this.dataList = dataList;
    }

    void updateData(List<RecordTest> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.header_layout, parent, false);
                return new HeaderViewHolder(headerView);
            case VIEW_TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.row_layout, parent, false);
                return new MyViewHolder(itemView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                break;
            case VIEW_TYPE_ITEM:
                RecordTest data = dataList.get(position - 1); // Subtract 1 to skip the header
                MyViewHolder viewHolder = (MyViewHolder) holder;
                viewHolder.idTextView.setText(String.valueOf(data.getId()));
                viewHolder.nameTextView.setText(data.getNameStudent());
                viewHolder.stateTextView.setText(data.getState());
//                viewHolder.optionsTextView.setText(data.getPoint());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1; // Add 1 for the header
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView, nameTextView, stateTextView, optionsTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.idTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            stateTextView = itemView.findViewById(R.id.stateTextView);
            optionsTextView = itemView.findViewById(R.id.optionsTextView);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Add TextViews for header titles if needed

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views if needed
        }
    }
}
