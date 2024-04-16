package com.practice.easyexam.app.view.history.user;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.RecordItem;
import com.practice.easyexam.app.model.RecordTest;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.AttemptViewHolder> {

    private List<RecordItem> attempts;
    onClickItemHistory onClickItemHistory;

    public HistoryAdapter(List<RecordItem> attempts) {
        this.attempts = attempts;
    }
    void updateData(List<RecordItem> attempts) {
        this.attempts = attempts;
        notifyDataSetChanged();
    }


    public void setOnClickItemHistory(onClickItemHistory onClickItem) {
        this.onClickItemHistory = onClickItem;
    }

    @NonNull
    @Override
    public AttemptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_history, parent, false);
        return new AttemptViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull AttemptViewHolder holder, int position) {

        RecordItem item = attempts.get(position);
        Log.d("RecordItemAdapter",item.getRecordTest().toString()+"");
        RecordTest recordTest = item.getRecordTest();
        holder.tvSubject.setText(String.valueOf(item.getNameRoom()));
        holder.tvEarned.setText(String.valueOf(recordTest.getPoint()));
        holder.tvDate.setText(recordTest.getDay());

        holder.cvParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickItemHistory.onClickItem(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        return attempts.size();
    }

    public static class AttemptViewHolder extends RecyclerView.ViewHolder {

        public TextView tvSubject,tvEarned,tvDate;
        public CardView cvParent;

        public AttemptViewHolder(View v) {
            super(v);
            tvSubject = v.findViewById(R.id.textView23);
            tvEarned = v.findViewById(R.id.textView24);
            tvDate = v.findViewById(R.id.textView25);
            cvParent = v.findViewById(R.id.cvItemHistory);
        }
    }
}

interface onClickItemHistory {
    void onClickItem(RecordItem item);
}
