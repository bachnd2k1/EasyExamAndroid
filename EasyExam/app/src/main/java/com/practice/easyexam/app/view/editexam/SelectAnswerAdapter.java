package com.practice.easyexam.app.view.editexam;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;


import java.util.ArrayList;

public class SelectAnswerAdapter extends RecyclerView.Adapter<SelectAnswerAdapter.SelectViewHolder> {
    private String[] data;
    private int itemCount = 4;
    private Context context;

    private int selectedItem = 0;

    ArrayList<String> answers;

    private onSelectItem onSelectItem;

    public void setData(String[] data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnSelectItem(SelectAnswerAdapter.onSelectItem onSelectItem) {
        this.onSelectItem = onSelectItem;
    }

    //    public SelectAnswerAdapter(String[] data, Context context) {
//        this.data = data;
//        this.context = context;
//    }
    public SelectAnswerAdapter(Context context) {
        this.data = Utils.getCharArr();
    }

    public void setItemCount(ArrayList<String> answers) {
        this.answers = answers;
        this.itemCount = answers.size();
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_answer, parent, false);
        return new SelectViewHolder(view);
    }

    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {
            int previousSelectedItem = selectedItem;
            selectedItem = position;

            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItem);

            if (onSelectItem != null) {
                onSelectItem.onClickItem(position);
            }
        });

        boolean isSelected = position == selectedItem;
        holder.bindData(data[position], isSelected);
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public static class SelectViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvAns);

        }

        public void bindData(String answer, boolean isSelected) {
            textView.setText(answer);
            int backgroundColor = isSelected ? R.drawable.bg_ans_selected : R.drawable.bg_ans_unselected;
            int textColor = isSelected ? ContextCompat.getColor(textView.getContext(), R.color.white) : ContextCompat.getColor(textView.getContext(), R.color.blue_black);

            textView.setBackgroundResource(backgroundColor);
            textView.setTextColor(textColor);
        }
    }

    public void addItem() {
        if (itemCount < data.length) {
            itemCount++;
            notifyItemInserted(itemCount - 1);
        }
    }

    public void subtractItem() {
        if (itemCount > 1) {
            itemCount--;
            notifyItemRemoved(itemCount);
        }
    }

    public interface onSelectItem {
        void onClickItem(int position);
    }
}