package com.practice.easyexam.app.view.summary;

import static com.practice.easyexam.app.view.history.host.HistoryHostActivity.ID_ROOM;
import static com.practice.easyexam.app.view.history.user.HistoryUserActivity.ID_STUDENT;
import static com.practice.easyexam.app.view.history.user.HistoryUserActivity.QUESTION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.RecordItem;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.view.tracking.TrackingActivity;
import com.practice.easyexam.app.view.tracking.TrackingViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SummaryExamActivity extends AppCompatActivity {
    SummaryExamViewModel viewModel;
    RecordItem recordItem;
    RecordTest recordTest;
    ArrayList<Question> questions = new ArrayList<>();
    List<Integer> answers;
    SummaryAdapter adapter;
    TextView tvCorrectQuestion,tvPoint,tvTitle;
    ImageView imgBack;
    RecyclerView rvExam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_exam);
        tvCorrectQuestion = findViewById(R.id.tvCorrectQuestion);
        tvPoint = findViewById(R.id.tvPoint);
        rvExam = findViewById(R.id.rvExam);
        tvTitle = findViewById(R.id.tvNameTest);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recordItem = (RecordItem) getIntent().getSerializableExtra(QUESTION);
        recordTest = recordItem.getRecordTest();
        tvTitle.setText(recordItem.getNameRoom());
        tvPoint.setText(recordTest.getPoint());
        tvCorrectQuestion.setText(recordTest.getCorrectQuestion());
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new SummaryExamViewModel();
            }
        }).get(SummaryExamViewModel.class);
        answers  = Arrays.stream(recordTest.getAnswer().split(", "))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Gson gson = new Gson();
        questions = gson.fromJson(recordTest.getQuestions(), new TypeToken<ArrayList<Question>>(){}.getType());
        adapter = new SummaryAdapter(questions,this, (ArrayList<Integer>) answers);
        rvExam.setLayoutManager(new LinearLayoutManager(this));
        rvExam.setAdapter(adapter);
        Log.d("JSON_QUESTION",questions.size()+"");
    }
}