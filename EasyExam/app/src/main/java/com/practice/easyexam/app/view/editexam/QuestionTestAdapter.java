package com.practice.easyexam.app.view.editexam;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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

import com.bumptech.glide.Glide;
import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.Question;

import java.util.ArrayList;

public class QuestionTestAdapter extends RecyclerView.Adapter<QuestionTestAdapter.AdapterExamAfterCreateViewHolder> {

    ArrayList<Question> questionArrayList;
    AnswerAfterCreateAdapter answerAfterCreateAdapter;
    Context context;
    OnItemOptionListener onItemOptionListener;

    public QuestionTestAdapter(ArrayList<Question> questionArrayList, Context context) {
        this.questionArrayList = questionArrayList;
        this.context = context;
    }

    public void setOnItemOptionListener(OnItemOptionListener onItemOptionListener) {
        this.onItemOptionListener = onItemOptionListener;
    }

    @NonNull
    @Override
    public AdapterExamAfterCreateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_after_create_answer, parent, false);
        return new AdapterExamAfterCreateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterExamAfterCreateViewHolder holder, int position) {
        Question question = questionArrayList.get(position);
        Log.d("QuestionAdapter",question.toString());
        answerAfterCreateAdapter = new AnswerAfterCreateAdapter(question.getAnswers(), question.getCorrectNum());
        int index = position + 1;
        boolean isExpanded = questionArrayList.get(position).isExpanded();
        holder.tvQuestion.setText("Câu "+ index  +": "+question.getQuestion());
        holder.rvAnswer.setAdapter(answerAfterCreateAdapter);
        holder.rvAnswer.setLayoutManager(new LinearLayoutManager(context));

        if (question.getImage() != null) {
            if (Utils.isImageUrl(question.getImage())) {
                Glide.with(context)
                        .load(question.getImage())
                        .into(holder.imgQuestion);
            } else  {
                holder.imgQuestion.setImageBitmap(Utils.getBitmapFromString(question.getImage()));
            }
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

        holder.imgMore.setOnClickListener(v -> showMenu(v,position));
    }

    private void showMenu(View v, final int pos) {
        PopupMenu popup = new PopupMenu(v.getContext(), v, Gravity.END, 0, R.style.PopupMenu);
        popup.setOnMenuItemClickListener(item -> {
            if (onItemOptionListener != null) {
                onItemOptionListener.onItemOptionSelected(item.getItemId(), questionArrayList.get(pos),pos);
            }
            return false;
        });

        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_more, popup.getMenu());
        popup.show();
    }

    void addQuestion(Question question) {
        questionArrayList.add(question);
        notifyDataSetChanged();
    }

    void updateAnswer() {
        if (answerAfterCreateAdapter != null) {
            answerAfterCreateAdapter.notifyDataSetChanged();
        }
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
