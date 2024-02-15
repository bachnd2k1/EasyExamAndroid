package com.practice.easyexam.app.view.examing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;


import java.util.List;

public class AnswerChoiceAdapter  extends  RecyclerView.Adapter<AnswerChoiceAdapter.AnswerViewHolder> {
    private List<String> answers;
    private String[] data;
    private int selectedItem = -1;
    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_select_ans, parent, false);
        return new AnswerChoiceAdapter.AnswerViewHolder(view);
    }

    public AnswerChoiceAdapter(List<String> answers) {
        this.answers = answers;
        this.data = Utils.getCharArr();
    }

    public void updateData(List<String> newData,int selectedItem) {
        this.answers = newData;
        this.selectedItem = selectedItem;
        notifyDataSetChanged();
    }

    public  int getSelectItem() { return selectedItem; }
//    public void setSelectedItem(int selectedItem) {
//        this.selectedItem = selectedItem;
//        notifyDataSetChanged();
//    }
    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
//        holder.tvAns.setText(questions.get(position));
        holder.itemView.setOnClickListener(v -> {
            int previousSelectedItem = selectedItem;
            selectedItem = position;
            notifyItemChanged(previousSelectedItem);
            notifyItemChanged(selectedItem);
            onClickListener.onItemClick(selectedItem);
        });
        boolean isSelected = position == selectedItem;
        holder.bindData(answers.get(position), isSelected,data[position]);
    }

    @Override
    public int getItemCount() {
        return answers.size();
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAns;

        public AnswerViewHolder(View v) {
            super(v);
            tvAns = v.findViewById(R.id.tvAns);
        }

        public void bindData(String answer, boolean isSelected, String ch) {
            tvAns.setText(ch + ". " + answer);
            int backgroundColor = isSelected ? R.drawable.bg_ans_selected : R.drawable.bg_ans_unselected;
            int textColor = isSelected ? ContextCompat.getColor(tvAns.getContext(), R.color.white) : ContextCompat.getColor(tvAns.getContext(), R.color.blue_black);

            tvAns.setBackgroundResource(backgroundColor);
            tvAns.setTextColor(textColor);
        }
    }

    interface OnClickListener {
        void onItemClick(int position);
    }
}
