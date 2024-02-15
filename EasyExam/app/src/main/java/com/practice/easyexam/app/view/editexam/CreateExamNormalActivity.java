package com.practice.easyexam.app.view.editexam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;

import java.util.ArrayList;

public class CreateExamNormalActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String CODE_NUMBER = "NUMBER";
    EditText edtQuestion, edtAnswer1, edtAnswer2, edtAnswer3, edtAnswer4;
    TextView tvAns1, tvAns2, tvAns3, tvAns4, btnSave;
    int position = 1;
    public static String CODE_QUESTION = "QUESTION";
    public static String CODE_EDIT = "EDIT";
    int index = -1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam_normall);
        edtQuestion = findViewById(R.id.edtQuestion);
        edtAnswer1 = findViewById(R.id.edtAnswer1);
        edtAnswer2 = findViewById(R.id.edtAnswer2);
        edtAnswer3 = findViewById(R.id.edtAnswer3);
        edtAnswer4 = findViewById(R.id.edtAnswer4);
        tvAns1 = findViewById(R.id.tvAns1);
        tvAns2 = findViewById(R.id.tvAns2);
        tvAns3 = findViewById(R.id.tvAns3);
        tvAns4 = findViewById(R.id.tvAns4);
        btnSave = findViewById(R.id.btnSave);
        tvAns1.setOnClickListener(this);
        tvAns2.setOnClickListener(this);
        tvAns3.setOnClickListener(this);
        tvAns4.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        if (getIntent().getExtras() != null) {
            Question question = (Question) getIntent().getSerializableExtra("object");
            index = getIntent().getIntExtra("index",-1);
            edtQuestion.setText(question.getQuestion());
            edtAnswer1.setText(question.getAnswers().get(0));
            edtAnswer2.setText(question.getAnswers().get(1));
            edtAnswer3.setText(question.getAnswers().get(2));
            edtAnswer4.setText(question.getAnswers().get(3));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tvAns1:
                tvAns1.setBackgroundResource(R.drawable.bg_ans_selected);
                tvAns2.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns3.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns4.setBackgroundResource(R.drawable.bg_ans_unselected);

                tvAns1.setTextColor(getResources().getColor(R.color.white));
                tvAns2.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns3.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns4.setTextColor(getResources().getColor(R.color.blue_black));

                position = 1;
                break;
            case R.id.tvAns2:
                tvAns1.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns2.setBackgroundResource(R.drawable.bg_ans_selected);
                tvAns3.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns4.setBackgroundResource(R.drawable.bg_ans_unselected);

                tvAns1.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns2.setTextColor(getResources().getColor(R.color.white));
                tvAns3.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns4.setTextColor(getResources().getColor(R.color.blue_black));

                position = 2;
                break;
            case R.id.tvAns3:
                tvAns1.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns2.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns3.setBackgroundResource(R.drawable.bg_ans_selected);
                tvAns4.setBackgroundResource(R.drawable.bg_ans_unselected);

                tvAns1.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns2.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns3.setTextColor(getResources().getColor(R.color.white));
                tvAns4.setTextColor(getResources().getColor(R.color.blue_black));

                position = 3;
                break;
            case R.id.tvAns4:
                tvAns1.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns2.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns3.setBackgroundResource(R.drawable.bg_ans_unselected);
                tvAns4.setBackgroundResource(R.drawable.bg_ans_selected);

                tvAns1.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns2.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns3.setTextColor(getResources().getColor(R.color.blue_black));
                tvAns4.setTextColor(getResources().getColor(R.color.white));

                position = 4;
                break;
            case R.id.btnSave:
                if (edtAnswer1.getText().length() > 0
                        && edtAnswer2.getText().length() > 0
                        && edtAnswer3.getText().length() > 0
                        && edtAnswer4.getText().length() > 0
                        && edtQuestion.getText().length() > 0) {

                    ArrayList<String> listAnswer = new ArrayList<>();
                    Question question = new Question();
                    listAnswer.add(edtAnswer1.getText().toString());
                    listAnswer.add(edtAnswer2.getText().toString());
                    listAnswer.add(edtAnswer3.getText().toString());
                    listAnswer.add(edtAnswer4.getText().toString());
                    question.setQuestion(edtQuestion.getText().toString());
                    question.setAnswers(listAnswer);
                    question.setCorrectNum(position);


                    final Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    if (index < 0) {
                        bundle.putSerializable(CODE_QUESTION,question);
                    } else {
                        bundle.putSerializable(CODE_EDIT,question);
                        bundle.putInt(CODE_NUMBER,index);
                        Log.d("position_normal",index +"");
                    }
                    data.putExtras(bundle);
                    setResult(Activity.RESULT_OK, data);
                    finish();

                } else {
                    Toast.makeText(CreateExamNormalActivity.this, R.string.warning_empty, Toast.LENGTH_LONG).show();
                }

                break;
        }

    }
    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }
}

