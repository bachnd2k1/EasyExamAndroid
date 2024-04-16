package com.practice.easyexam.app.view.editexam;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;


import java.util.ArrayList;
import java.util.Arrays;

public class InputAnswerAdapter extends RecyclerView.Adapter<InputAnswerAdapter.InputAnswerViewHolder> {
    private String[] data;
    private String[] editTextValues;
    private int itemCount;
    ArrayList<String> answers;


//    public InputAnswerAdapter(String[] data) {
//        this.data = data;
//        this.editTextValues = new String[data.length];
//        Arrays.fill(editTextValues, null);
//    }


    public InputAnswerAdapter() {
        this.data = Utils.getCharArr();
        this.editTextValues = new String[data.length];
        Arrays.fill(editTextValues, null);
        this.itemCount = 4;
    }

    public void setAns(ArrayList<String> answers) {
        this.answers = answers;
        this.itemCount = answers.size();
    }

    public String getAllText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String value : editTextValues) {
            if (value != null) {
                stringBuilder.append(value).append(" ");
            }
        }
        return stringBuilder.toString().trim();
    }

    @NonNull
    @Override
    public InputAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_input_answer, parent, false);
        return new InputAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputAnswerViewHolder holder, int position) {
        if (answers == null) {
            holder.editText.setHint(data[position]);
        } else {
            if (position >= answers.size()) {
                holder.editText.setHint(data[position]);
            } else {
                holder.editText.setText(answers.get(position));
            }
        }
        holder.bindData(data[position]);
        holder.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                editTextValues[position] = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                editTextValues[position] = editable.toString();
            }
        });
//        if (editTextValues[position].isEmpty()) {
            editTextValues[position] = holder.editText.getText().toString();
//        }
    }

    @Override
    public int getItemCount() {
        return itemCount;
    }

    public  class InputAnswerViewHolder extends RecyclerView.ViewHolder {
        EditText editText;

        public InputAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.edtAnswer1);
        }

        public void bindData(String answer) {
            editText.setHint(answer);

        }
    }
    public void addItem() {
        if (itemCount < data.length) {
            itemCount++;
            notifyItemInserted(itemCount - 1);
        }
    }

    public void subtractItem() {
        if (itemCount > 1) {
            itemCount--;
            notifyItemRemoved(itemCount);
        }
    }

}

