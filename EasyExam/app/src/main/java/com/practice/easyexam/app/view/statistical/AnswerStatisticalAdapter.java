package com.practice.easyexam.app.view.statistical;

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
import java.util.HashMap;
import java.util.Map;

public class AnswerStatisticalAdapter extends  RecyclerView.Adapter<AnswerStatisticalAdapter.AdapterAfterCreateViewHolder>{

    ArrayList<String> listAnswer;
    Map<String, Integer> innerMap;
    int correctAnswer = -1;

    String [] alphabet;
    public AnswerStatisticalAdapter(Map<String, Integer> innerMap, ArrayList<String> listAnswer, int correctAnswer) {
        this.innerMap = innerMap;
        this.listAnswer = listAnswer;
        this.correctAnswer = correctAnswer;
        this.alphabet = Utils.getCharArr();
    }

    public AnswerStatisticalAdapter(ArrayList<String> listAnswer, int correctAnswer) {
        this.listAnswer = listAnswer;
        this.correctAnswer = correctAnswer;
        this.alphabet = Utils.getCharArr();
    }

    public void updateMap(Map<String, Integer> innerMap) {
        this.innerMap = innerMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdapterAfterCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_answer_statistical, parent, false);
        return new AdapterAfterCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterAfterCreateViewHolder holder, int position) {
        Log.d("alphabet",alphabet.length + "a");
        String answer = listAnswer.get(position);
        if (innerMap != null) {
            int count = innerMap.get(answer);
            holder.tvNumUser.setText(String.valueOf(count));
        }
        holder.tvAnswer.setText(alphabet[position]+". " + answer);
        Log.d("correct",correctAnswer + "anc");
        holder.tvAnswer.setBackgroundResource(position == correctAnswer ? R.drawable.bg_answer_correct_user: R.drawable.bg_answer_default );
    }

    @Override
    public int getItemCount() {
        return listAnswer.size();
    }

    public static class AdapterAfterCreateViewHolder extends RecyclerView.ViewHolder {

        public TextView tvAnswer,tvNumUser;

        public AdapterAfterCreateViewHolder(View v) {
            super(v);
            tvAnswer = v.findViewById(R.id.tvAnswer);
            tvNumUser = v.findViewById(R.id.tvNumUser);
        }
    }
}
