package com.practice.easyexam.app.customview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.practice.easyexam.R;

import java.util.List;

public class CustomSpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<String> data;
    private int selectedItem = -1;

    public CustomSpinnerAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    public void updateData(List<String> data) {
        this. data = data;
        notifyDataSetChanged();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(data.get(position));
//        if (position == selectedItem) {
//            textView.setBackgroundColor(context.getResources().getColor(R.color.peach));
//        } else {
//            textView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
//        }


        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.custom_spinner_item, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(data.get(position));

        return convertView;
    }
}