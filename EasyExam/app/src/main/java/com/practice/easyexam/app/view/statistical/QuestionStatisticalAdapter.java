package com.practice.easyexam.app.view.statistical;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.editexam.AnswerAfterCreateAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionStatisticalAdapter extends RecyclerView.Adapter<QuestionStatisticalAdapter.AdapterExamAfterCreateViewHolder> {

    ArrayList<Question> questionArrayList;
    AnswerStatisticalAdapter answerAfterCreateAdapter;
    Map<String, Map<String, Integer>> answerCountMap;
    Context context;


    public QuestionStatisticalAdapter(ArrayList<Question> questionArrayList, Context context) {
        this.questionArrayList = questionArrayList;
        this.context = context;
    }


    public void updateData(List<Question> questionArrayList){
        this.questionArrayList = (ArrayList<Question>) questionArrayList;
        notifyDataSetChanged();
    }

    public void updateCountMap(Map<String, Map<String, Integer>> answerCountMap){
        this.answerCountMap = answerCountMap;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public AdapterExamAfterCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_question_statistical, parent, false);
        return new AdapterExamAfterCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExamAfterCreateViewHolder holder, int position) {
        Question question = questionArrayList.get(position);
        Log.d("QuestionAdapter",question.getIdQuestion() + "|" + question.getIdTest());
        answerAfterCreateAdapter = new AnswerStatisticalAdapter(question.getAnswers(), question.getCorrectNum());
        if (answerCountMap != null) {
            Map<String, Integer> innerMap = answerCountMap.get(question.getIdQuestion());
            answerAfterCreateAdapter.updateMap(innerMap);
        }
        int index = position + 1;
        boolean isExpanded = questionArrayList.get(position).isExpanded();
        holder.tvQuestion.setText("Câu "+ index  +": "+question.getQuestion());
        holder.rvAnswer.setAdapter(answerAfterCreateAdapter);
        holder.rvAnswer.setLayoutManager(new LinearLayoutManager(context));
        if (question.getImage() != null) {
            holder.imgQuestion.setImageBitmap(Utils.getBitmapFromString(question.getImage()));
            holder.imgQuestion.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        } else {
            holder.imgQuestion.setVisibility(View.GONE);
        }
        holder.rvAnswer.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Question question = questionArrayList.get(position);
                question.setExpanded(!question.isExpanded());
                notifyItemChanged(position);
            }
        });

    }



    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }

    public static class AdapterExamAfterCreateViewHolder extends RecyclerView.ViewHolder {
        public TextView tvQuestion;
        public ImageView imgQuestion;
        public RecyclerView rvAnswer;

        public AdapterExamAfterCreateViewHolder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            rvAnswer = v.findViewById(R.id.rvAnswer);
            imgQuestion = v.findViewById(R.id.imageQuestion);
        }
    }
}

interface OnItemOptionListener {
    void onItemOptionSelected(int menuId, Question question,int index);
}

