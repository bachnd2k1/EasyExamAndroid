package com.practice.easyexam.app.view.summary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.Question;

import java.util.ArrayList;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.AdapterExamAfterCreateViewHolder> {

    ArrayList<Question> questionArrayList;
    ArrayList<Integer> answers;
    AnswerAdapter answerAfterCreateAdapter;

    Context context;
    OnItemOptionListener onItemOptionListener;

    public SummaryAdapter(ArrayList<Question> questionArrayList, Context context,  ArrayList<Integer> answers) {
        this.questionArrayList = questionArrayList;
        this.context = context;
        this.answers = answers;
    }

    public void setOnItemOptionListener(OnItemOptionListener onItemOptionListener) {
        this.onItemOptionListener = onItemOptionListener;
    }

    @NonNull
    @Override
    public AdapterExamAfterCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_summary, parent, false);
        return new AdapterExamAfterCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryAdapter.AdapterExamAfterCreateViewHolder holder, int position) {
        Question question = questionArrayList.get(position);
//        Log.d("QuestionAdapter",question.getIdQuestion() + "|" + question.getIdTest() + "|" + question.getCorrectNum());
        answerAfterCreateAdapter = new AnswerAdapter(question.getAnswers(), question.getCorrectNum(),answers.get(position));
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
        public ImageView imgMore;
        public ImageView imgQuestion;
        public RecyclerView rvAnswer;

        public AdapterExamAfterCreateViewHolder(View v) {
            super(v);
            tvQuestion = v.findViewById(R.id.tvQuestion);
            rvAnswer = v.findViewById(R.id.rvAnswer);
            imgMore = v.findViewById(R.id.imgMore);
            imgQuestion = v.findViewById(R.id.imageQuestion);
        }
    }
}

interface OnItemOptionListener {
    void onItemOptionSelected(int menuId, Question question,int index);
}
