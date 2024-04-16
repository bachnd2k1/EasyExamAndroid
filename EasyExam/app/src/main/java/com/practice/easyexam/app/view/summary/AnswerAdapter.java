package com.practice.easyexam.app.view.summary;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;


import java.util.ArrayList;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.AdapterAfterCreateViewHolder> {

    ArrayList<String> listAnswer;
    int correctAnswer = -1;
    int answerIndex = -1;
    String[] alphabet;

    public AnswerAdapter(ArrayList<String> listAnswer, int correctAnswer, int answerIndex) {
        this.listAnswer = listAnswer;
        this.correctAnswer = correctAnswer;
        this.alphabet = Utils.getCharArr();
        this.answerIndex = answerIndex;
    }

    @NonNull
    @Override
    public AdapterAfterCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_answer_after_create, parent, false);
        return new AdapterAfterCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAfterCreateViewHolder holder, int position) {
        Log.d("alphabet", alphabet.length + "a");
        String answer = listAnswer.get(position);
        holder.tvAnswer.setText(alphabet[position] + ". " + answer);
        Log.d("correct", correctAnswer + "|" + answerIndex );

        int backgroundResId = (position == correctAnswer  && position == answerIndex )
                ? R.drawable.bg_answer_correct_user
                : (position == correctAnswer )
                ? R.drawable.bg_answer_correct_user
                : (position == answerIndex )
                ? R.drawable.bg_ans_incorrect_user
                : R.drawable.bg_answer_default;

        holder.tvAnswer.setBackgroundResource(backgroundResId);


//        if (position == correctAnswer - 1 && position == answerIndex - 1) {
//            // Both red and green background if position is both the correct answer and the user's answer
//            holder.tvAnswer.setBackgroundResource(R.drawable.bg_answer_correct_user);
//        } else if (position == correctAnswer - 1) {
//            // Green background if position is the correct answer
//            holder.tvAnswer.setBackgroundResource(R.drawable.bg_answer_correct_user);
//        } else if (position == answerIndex - 1) {
//            // Red background if position is only the user's answer
//            holder.tvAnswer.setBackgroundResource(R.drawable.bg_ans_incorrect_user);
//        } else {
//            // Default background for other positions
//            holder.tvAnswer.setBackgroundResource(R.drawable.bg_answer_default);
//        }
    }

    @Override
    public int getItemCount() {
        return listAnswer.size();
    }

    public static class AdapterAfterCreateViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAnswer;

        public AdapterAfterCreateViewHolder(View v) {
            super(v);
            tvAnswer = v.findViewById(R.id.tvAnswer);
        }
    }
}

