package com.practice.easyexam.app.view.createdTests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Test;

import java.util.List;

public class CreatedTestsAdapter extends RecyclerView.Adapter<CreatedTestsAdapter.ItemViewHolder> {
    private List<Test> itemList;
    private OnItemListener onItemListener;

    public CreatedTestsAdapter(List<Test> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public void updateData(List<Test> items) {
        this.itemList = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Test item = itemList.get(position);
        holder.itemNameTextView.setText(item.getName());
        holder.itemDateTextView.setText(item.getCreateDate());
        if (onItemListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(item);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView,itemDateTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.tvName);
            itemDateTextView = itemView.findViewById(R.id.tvCreateDate);
        }
    }

    interface OnItemListener {
        void onItemClick(Test test);
    }
}

