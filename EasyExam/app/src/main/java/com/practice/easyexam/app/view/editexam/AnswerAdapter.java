package com.practice.easyexam.app.view.editexam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;

import java.util.ArrayList;

public class AnswerAdapter extends  RecyclerView.Adapter<AnswerAdapter.AnswerViewHolder>{
    ArrayList<String> listAnswer;
    int selectedPosition = -1;
    ItemClickListener itemClickListener;

    public AnswerAdapter(ArrayList<String> listAnswer, ItemClickListener itemClickListener) {
        this.listAnswer = listAnswer;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_answer, parent, false);
        return new AnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerViewHolder holder, int position) {
        holder.radioButton.setText(listAnswer.get(position));

        // Checked selected radio button
        holder.radioButton.setChecked(position
                == selectedPosition);

        // set listener on radio button
        holder.radioButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            CompoundButton compoundButton,
                            boolean b)
                    {
                        // check condition
                        if (b) {
                            // When checked
                            // update selected position
                            selectedPosition
                                    = holder.getAdapterPosition();
                            // Call listener
                            itemClickListener.onClick(
                                    holder.radioButton.getText()
                                            .toString());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listAnswer.size();
    }

    public static class AnswerViewHolder extends RecyclerView.ViewHolder {

        public RadioButton radioButton;

        public AnswerViewHolder(View v) {
            super(v);
            radioButton = v.findViewById(R.id.radio_button);
        }
    }
}
