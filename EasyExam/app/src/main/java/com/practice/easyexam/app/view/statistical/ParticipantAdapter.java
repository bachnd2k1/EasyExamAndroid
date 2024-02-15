package com.practice.easyexam.app.view.statistical;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.customview.AccuracyView;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.RecordTest;

import java.util.ArrayList;
import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_HEADER = 0;
    public static final int VIEW_TYPE_ITEM = 1;

    private Double accuracy = 0.0;
    private List<RecordTest> dataList;
    ArrayList<Question> questions;
    ArrayList<Double> accuracyList = new ArrayList<>();

//    public ParticipantAdapter(List<RecordTest> dataList) {
//        this.dataList = dataList;
//    }

    public ParticipantAdapter() {
    }

    public Double getAccuracy() {
        return calculateAverage(accuracyList);
    }

    private static double calculateAverage(ArrayList<Double> list) {
        if (list == null || list.isEmpty()) {
            return 0.0;
        }

        // Calculate the sum
        double sum = 0.0;
        for (Double number : list) {
            sum += number;
        }

        // Calculate the average
        return sum / list.size();
    }

    void updateData(List<RecordTest> dataList) {
        if (dataList != null && !dataList.isEmpty()) {
//            // Create a new ArrayList with the same data
//            List<RecordTest> newDataList = new ArrayList<>(dataList);
//
//            // Duplicate the first item and add it to the new list
//            RecordTest firstItem = dataList.get(0);
//            newDataList.add(0, firstItem);

            // Update the accuracyList
            accuracyList.clear();
            for (RecordTest recordTest : dataList) {
                Gson gson = new Gson();
                questions = gson.fromJson(recordTest.getQuestions(), new TypeToken<ArrayList<Question>>() {
                }.getType());

                int numCorrect = Integer.parseInt(recordTest.getCorrectQuestion());
                double decimalValue = (double) numCorrect / questions.size();
                double percentage = decimalValue * 100;

                accuracyList.add(percentage);
            }

            // Assign the new ArrayList to the dataList
            this.dataList = dataList;

            // Notify the adapter that the data has changed
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                View headerView = inflater.inflate(R.layout.header_participant_view, parent, false);
                return new HeaderViewHolder(headerView);
            case VIEW_TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.row_participant_view, parent, false);
                return new MyViewHolder(itemView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                break;
            case VIEW_TYPE_ITEM:
                RecordTest data = dataList.get(position - 1); // Subtract 1 to skip the header
                MyViewHolder viewHolder = (MyViewHolder) holder;
                Gson gson = new Gson();
                questions = gson.fromJson(data.getQuestions(), new TypeToken<ArrayList<Question>>() {
                }.getType());
                viewHolder.nameTextView.setText(data.getNameStudent());
                viewHolder.pointTextView.setText(data.getPoint());

                int numCorrect = Integer.parseInt(data.getCorrectQuestion());
                double decimalValue = (double) numCorrect / questions.size();
                double percentage = decimalValue * 100;
                viewHolder.accuracyView.setAccuracyPercentage((int) Math.round(percentage));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size() + 1; // Add 1 for the header
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, pointTextView;
        AccuracyView accuracyView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            accuracyView = itemView.findViewById(R.id.accuracyView);
            pointTextView = itemView.findViewById(R.id.optionsTextView);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        // Add TextViews for header titles if needed

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views if needed
        }
    }
}
