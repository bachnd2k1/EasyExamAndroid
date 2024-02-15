package com.practice.easyexam.app.view.editexam;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;
//import com.practice.easyexam.app.utils.Utils;

import java.util.ArrayList;

public class AnswerAfterCreateAdapter extends  RecyclerView.Adapter<AnswerAfterCreateAdapter.AdapterAfterCreateViewHolder>{

    ArrayList<String> listAnswer;
    int correctAnswer = -1;
    String [] alphabet;
    public AnswerAfterCreateAdapter(ArrayList<String> listAnswer, int correctAnswer) {
        this.listAnswer = listAnswer;
        this.correctAnswer = correctAnswer;
        this.alphabet = Utils.getCharArr();
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
        Log.d("alphabet",alphabet.length + "a");
        String answer = listAnswer.get(position);
        holder.tvAnswer.setText(alphabet[position]+". " + answer);
        Log.d("correct",correctAnswer + "anc");
        holder.tvAnswer.setBackgroundResource(position == correctAnswer ? R.drawable.bg_answer_correct_user: R.drawable.bg_answer_default );
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
