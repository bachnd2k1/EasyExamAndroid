package com.practice.easyexam.app.view.examing;

import static com.practice.easyexam.app.view.waiting.WaitingActivity.ROOM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ExaminingActivity extends AppCompatActivity {
    private TextView tvQuestion, tvQuestionNumber, tvTimerCounter;
    private Button btnNext,btnPrev;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    TextView tvTitle;
    String json = "";
    Test exam;
    Room room;
    User user;
    private int currentQuestionIndex = 0;
    private List<String> questions;
    private int correctQuestion = 0;
    private LinkedHashMap<String, Map<String, Boolean>> questionsAnswerMap;
    private CountDownTimer countDownTimer;
    long timeLeftInMillis = 0;

    ExaminingViewModel viewModel;
    List<String> answers = new ArrayList<>();
    List<String> correctAnswers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_quiz_new);
        initView();
//        json = getIntent().getStringExtra(TEST);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new ExaminingViewModel();
            }
        }).get(ExaminingViewModel.class);
        SharedPref sharedPref = SharedPref.getInstance();
        user =  sharedPref.getUser(this);
        room = (Room) getIntent().getSerializableExtra(ROOM);
//        ObjectMapper objectMapper = new ObjectMapper();
//        Log.d("json",json);
//        try {
//            exam = objectMapper.readValue(json, Test.class);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        timeLeftInMillis = room.getTime() * 60000;
        countDownTimer = new CountDownTimer(60000 * 10, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;

                tvTimerCounter.setText(String.format("%d:%d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                // Timer countdown has finished.
            }
        };

        countDownTimer.start();

        questionsAnswerMap = (LinkedHashMap<String, Map<String, Boolean>>) getExam(exam);

        questions = new ArrayList<>(questionsAnswerMap.keySet());
        Collections.shuffle(questions);
        if (questionsAnswerMap.size() == 1) {
            btnNext.setText("Finish");
        }
        viewModel.getInsertionResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(ExaminingActivity.this, "Add RecordTest" + aBoolean, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.createRecordTest(room,user,String.valueOf(questions.size() - 1));
        viewModel.getCurrentQuestionIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newIndex) {
//                Toast.makeText(ExaminingActivity.this, "INDEX"+ newIndex, Toast.LENGTH_SHORT).show();
                btnPrev.setBackgroundResource(newIndex > 0 ? R.drawable.bg_button_exam : R.drawable.bg_button_exam_not_select);
            }
        });
        displayData();


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                if (btnNext.getText().equals(getString(R.string.next))) {
                        answers.add(currentQuestionIndex, String.valueOf(radioButton.getText()));
                        currentQuestionIndex++;
                        viewModel.setCurrentQuestionIndex(currentQuestionIndex);
                        displayNextQuestions();
                } else {
                    answers.add(currentQuestionIndex, String.valueOf(radioButton.getText()));
                    correctAnswers = getCorrectAnswers(exam.getQuestionArrayList());
                    correctQuestion = (int) IntStream.range(0, correctAnswers.size())
                            .filter(i -> correctAnswers.get(i).equals(answers.get(i)))
                            .count();
//                    Intent intentResult = new Intent(ExaminingActivity.this, FinalResultActivity.class);
//                    intentResult.putExtra(Constants.SUBJECT, exam.getName());
//                    intentResult.putExtra(Constants.CORRECT, correctQuestion);
//                    intentResult.putExtra(Constants.INCORRECT, questions.size() - correctQuestion);
//                    intentResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intentResult);
//                    finish();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioButton radioButton = findViewById(radioGroup.getCheckedRadioButtonId());
                if (currentQuestionIndex > 0) {
                    if (radioButton != null) {
                        answers.add(currentQuestionIndex, String.valueOf(radioButton.getText()));
                    }
                    currentQuestionIndex -= 1 ;
                    viewModel.setCurrentQuestionIndex(currentQuestionIndex);
                    displayNextQuestions();
                }
            }
        });
    }

    public ArrayList<String> getCorrectAnswers(ArrayList<Question> questionArrayList) {
        ArrayList<String> correctAnswersList = new ArrayList<>();

        for (Question question : questionArrayList) {
            ArrayList<String> answers = question.getAnswers();
            int correctNum = question.getCorrectNum();

            if (correctNum >= 0 && correctNum < answers.size()) {
                correctAnswersList.add(answers.get(correctNum - 1));
            }
        }
        return correctAnswersList;
    }

    private void displayNextQuestions() {
        setAnswersToRadioButton();
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1));
//        Log.d("exam.getQuestionArrayList().size()", exam.getQuestionArrayList().size() + "");
        if (currentQuestionIndex == exam.getQuestionArrayList().size() - 1){
            btnNext.setText(getText(R.string.finish));
        } else {
            btnNext.setText(getText(R.string.next));
        }
    }

//    private void displayPrevQuestions() {
//        setAnswersToRadioButton();
//        tvQuestion.setText(questions.get(currentQuestionIndex));
//        tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1));
//
//        if (currentQuestionIndex == exam.getQuestionArrayList().size() - 1){
//            btnNext.setText(getText(R.string.next));
//        }
//    }


    private void displayData() {
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1));

        setAnswersToRadioButton();
    }

    private void setAnswersToRadioButton() {
        radioGroup.clearCheck();
        ArrayList<String> questionKey = new ArrayList(questionsAnswerMap.get(questions.get(currentQuestionIndex)).keySet());
        radioButton1.setText(questionKey.get(0));
        radioButton2.setText(questionKey.get(1));
        radioButton3.setText(questionKey.get(2));
        radioButton4.setText(questionKey.get(3));
    }

    public Map<String, Map<String, Boolean>> getExam(Test exam) {
        LinkedHashMap<String, Map<String, Boolean>> questions = new LinkedHashMap<>();
        for (int i = 0; i < exam.getQuestionArrayList().size(); i++) {
            Question question = exam.getQuestionArrayList().get(i);
            LinkedHashMap<String, Boolean> answer = new LinkedHashMap<>();
            for (int j = 0; j < question.getAnswers().size(); j++) {
                if (j == question.getCorrectNum() - 1) {
                    answer.put(question.getAnswers().get(j), true);
                } else {
                    answer.put(question.getAnswers().get(j), false);
                }
            }
            questions.put(question.getQuestion(), answer);
        }
        return questions;
    }

    private void initView() {
        tvTitle = findViewById(R.id.textView26);
        tvQuestion = findViewById(R.id.textView78);
        tvQuestionNumber = findViewById(R.id.textView18);
        tvTimerCounter = findViewById(R.id.tvTimerCounter);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
    }
}