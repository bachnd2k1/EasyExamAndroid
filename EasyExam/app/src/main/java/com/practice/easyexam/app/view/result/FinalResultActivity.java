package com.practice.easyexam.app.view.result;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.view.main.user.MainUserActivity;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.main.user.MainUserTestActivity;

import java.util.Calendar;

public class FinalResultActivity extends AppCompatActivity {

    private TextView tvSubject, tvCorrect, tvIncorrect, tvEarned, tvOverallStatus, tvDate;
    int correctAnswer = 0;
    int incorrectAnswer = 0;
    int correct = 0;
    int incorrect = 0;
    String subject = "";
    String email = "";
    int earnedPoints = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);

        Intent intent = getIntent();
        correctAnswer = intent.getIntExtra(Constants.CORRECT, 0);
        incorrectAnswer = intent.getIntExtra(Constants.INCORRECT, 0);
        correct = correctAnswer;
        incorrect = incorrectAnswer;
        subject = intent.getStringExtra(Constants.SUBJECT);
        email = SharedPref.getInstance().getUser(this).getEmail();

        double point = correct * 10 / (correct + incorrect);
        earnedPoints = (int) point;
        tvSubject = findViewById(R.id.textView16);
        tvCorrect = findViewById(R.id.textView19);
        tvIncorrect = findViewById(R.id.textView27);
        tvOverallStatus = findViewById(R.id.textView29);
        tvDate = findViewById(R.id.textView30);

        tvSubject.setText(subject);
        tvCorrect.setText(String.valueOf(correct));
        tvIncorrect.setText(String.valueOf(incorrect));
        tvOverallStatus.setText(String.valueOf(earnedPoints));
        tvDate.setText(Utils.getCurrentDate());



        findViewById(R.id.imageViewFinalResultQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinalResultActivity.this, MainUserTestActivity.class);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.btnFinishQuiz).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinalResultActivity.this, MainUserTestActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainUserTestActivity.class);
        startActivity(intent);
        finish();
    }
}