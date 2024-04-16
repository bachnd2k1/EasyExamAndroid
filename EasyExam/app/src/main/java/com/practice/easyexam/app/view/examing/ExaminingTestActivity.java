package com.practice.easyexam.app.view.examing;

import static com.practice.easyexam.app.view.waiting.WaitingActivity.ROOM;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.view.result.FinalResultActivity;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExaminingTestActivity extends AppCompatActivity {
    private RecyclerView rvAnswer;
    private TextView tvQuestion, tvQuestionNumber, tvTimerCounter;
    private Button btnNext,btnPrev;
    private TextView tvTitle;
    private int currentQuestionIndex = 0;
    private List<String> questions = new ArrayList<>();
    private int correctQuestion = 0;
    private LinkedHashMap<String, Map<String, Boolean>> questionsAnswerMap = new LinkedHashMap<>();
    private CountDownTimer countDownTimer;
    long timeLeftInMillis = 0;
    private ExaminingViewModel viewModel;
    private ImageView imgQuestion;
    List<Integer> correctAns = new ArrayList<>();
    List<Integer> arrAns;
    Question question = new Question();
    ArrayList<String> questionKey = new ArrayList<>();
    Room room;
    User user;
    RecordTest outerRecordTest = new RecordTest();
    List<Question> questionList = new ArrayList<>();
    AnswerChoiceAdapter answerChoiceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_quiz_test);
        initView();

        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new ExaminingViewModel();
            }
        }).get(ExaminingViewModel.class);
        SharedPref sharedPref = SharedPref.getInstance();
        user =  sharedPref.getUser(this);
        room = (Room) getIntent().getSerializableExtra(ROOM);
        viewModel.getRecordTestUser(room,user);
        viewModel.getQuestionLiveData().observe(this, new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questionList1) {
                questionList = questionList1;
                Log.d("questionList",questionList.size()+"");
                questionsAnswerMap = (LinkedHashMap<String, Map<String, Boolean>>) getExam(questionList);
//                arrAns = new ArrayList<>(Collections.nCopies(questionsAnswerMap.size(), -1));
                questions = new ArrayList<>(questionsAnswerMap.keySet());
                questionKey = new ArrayList(questionsAnswerMap.get(questions.get(currentQuestionIndex)).keySet());
                answerChoiceAdapter = new AnswerChoiceAdapter(questionKey);
                rvAnswer.setLayoutManager(new LinearLayoutManager(ExaminingTestActivity.this));
                rvAnswer.setAdapter(answerChoiceAdapter);
                answerChoiceAdapter.setOnClickListener(new AnswerChoiceAdapter.OnClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        arrAns.set(currentQuestionIndex, position);
                        saveStateRecordTest();
                    }
                });
                displayData();
                Gson gson = new Gson();
                String json = gson.toJson((ArrayList<Question>) questionList);
                if (outerRecordTest != null) {
                    viewModel.updateQuestionToRecord(json,outerRecordTest.getIdRoom(),outerRecordTest.getIdStudent());
                }
                if (questionsAnswerMap.size() == 1) {
                    btnNext.setText(R.string.finish);
                }
                viewModel.setCurrentQuestionIndex(currentQuestionIndex);
            }
        });

        viewModel.getRecordTestResult().observe(this, new Observer<RecordTest>() {
            @Override
            public void onChanged(RecordTest recordTest) {
                Log.d("recordTest.getAnswer()",recordTest.getAnswer()+"");
                List<String> stringList = Arrays.asList(recordTest.getAnswer().split(","));
                arrAns = stringList.stream()
                        .map(String::trim) // Trim leading and trailing whitespaces
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                outerRecordTest = recordTest;
                if (recordTest.getQuestions() != null) {
//                    String currentTime = recordTest.getTime();
                    String currentTime = getCurrentTime();
                    String startTime = room.startTime;
                    String endTime = getEndingTime(startTime,room.getTime());
                    Log.d("timeLeftInMillisstartTime&currentTime ",startTime + "ms" + "|" + currentTime+"|" +endTime);
                    timeLeftInMillis = room.getTime() * 60000 - calculateTimeDifferenceInMillis(startTime, currentTime) + 5;
                    Log.d("room.getTime()",room.getTime()+"");
//                    timeLeftInMillis = calculateTimeDifferenceInMillis(currentTime,endTime);
                    Log.d("timeLeftInMillis1",calculateTimeDifferenceInMillis(currentTime,endTime) + "||" + calculateTimeDifferenceInMillis(startTime, currentTime));
                    currentQuestionIndex = Integer.parseInt(recordTest.getCurrentQuestion());
                    String question = recordTest.getQuestions();
                    Gson gson = new Gson();
                    Type listType = new TypeToken<ArrayList<Question>>() {}.getType();
                    questionList = gson.fromJson(question, listType);
                    questionsAnswerMap = (LinkedHashMap<String, Map<String, Boolean>>) getExam(questionList);
                    questions = new ArrayList<>(questionsAnswerMap.keySet());
                    questionKey = new ArrayList(questionsAnswerMap.get(questions.get(currentQuestionIndex)).keySet());
                    answerChoiceAdapter = new AnswerChoiceAdapter(questionKey);
                    rvAnswer.setLayoutManager(new LinearLayoutManager(ExaminingTestActivity.this));
                    rvAnswer.setAdapter(answerChoiceAdapter);
                    answerChoiceAdapter.setOnClickListener(new AnswerChoiceAdapter.OnClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            arrAns.set(currentQuestionIndex, position);
                            saveStateRecordTest();
                        }
                    });
                    viewModel.setCurrentQuestionIndex(currentQuestionIndex);
                    displayData();
                } else  {
                    if (outerRecordTest != null) {
                        currentQuestionIndex = Integer.parseInt(recordTest.getCurrentQuestion()) - 1;
                        timeLeftInMillis = room.getTime() * 60000;
                        viewModel.getAllQuestionInRoom(room);
                    }
                }

                Log.d("timeLeftInMillis",timeLeftInMillis + "ms");

                countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        int minutes = (int) (millisUntilFinished / 1000) / 60;
                        int seconds = (int) (millisUntilFinished / 1000) % 60;
                        String formattedMinutes = String.format("%02d", minutes);
                        String formattedSeconds = String.format("%02d", seconds);
                        tvTimerCounter.setText(String.format("%s:%s", formattedMinutes, formattedSeconds));
                    }
                    @Override
                    public void onFinish() {
                        outerRecordTest.setState(Constants.STATE_FINISH);
                        saveRecordTest();
                        openResultActivity();
                    }
                };

                countDownTimer.start();
                Log.d("outerRecordTest345",outerRecordTest.toString());
            }
        });





        viewModel.getCurrentQuestionIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newIndex) {
                btnPrev.setBackgroundResource(newIndex > 0 ? R.drawable.bg_button_exam : R.drawable.bg_button_exam_not_select);
                questionKey = new ArrayList(questionsAnswerMap.get(questions.get(newIndex)).keySet());
                if (answerChoiceAdapter != null) {
                    answerChoiceAdapter.updateData(questionKey,arrAns.get(newIndex));
                }
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnNext.getText().equals(getString(R.string.next))) {
                    currentQuestionIndex++;
                    viewModel.setCurrentQuestionIndex(currentQuestionIndex);
                    saveStateRecordTest();
                    displayNextQuestions();
                } else {
                    outerRecordTest.setState(Constants.STATE_FINISH);
                    saveRecordTest();
                    openResultActivity();
                }
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex -= 1 ;
                    viewModel.setCurrentQuestionIndex(currentQuestionIndex);
                    saveStateRecordTest();
                    displayNextQuestions();
                }
            }
        });
    }

    public static long calculateTimeDifferenceInMillis(String startTimeString, String endTimeString) {
        SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");
        try {
            // Convert time strings to Date objects
            Date startTime = TIME_FORMATTER.parse(startTimeString);
            Date endTime = TIME_FORMATTER.parse(endTimeString);

            // Calculate the time difference in milliseconds
            return endTime.getTime() - startTime.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1; // Invalid time format, you might want to handle this differently
        }
    }


    private String getEndingTime(String currentTime, int timeTest) {
        try {
            // Parse current time string
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(currentTime));

            // Add timeTest minutes
            calendar.add(Calendar.MINUTE, timeTest);

            // Format the result
            return sdf.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        // Get the current time
        Date currentTime = new Date();

        // Format the current time using the SimpleDateFormat object
        return sdf.format(currentTime);
    }

    private void saveStateRecordTest() {
        correctAns = getArrCorrectAnswers(questionList);
        correctQuestion = (int) IntStream.range(0, correctAns.size())
                .filter(i -> correctAns.get(i).equals(arrAns.get(i)))
                .count();
        Log.d("outerRecordTest",outerRecordTest.toString());
        outerRecordTest.setCorrectQuestion(String.valueOf(correctQuestion));
        outerRecordTest.setCurrentQuestion(String.valueOf(currentQuestionIndex));
        double point = (double) (correctQuestion * 10) / arrAns.size();
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedResult = df.format(point);
        outerRecordTest.setPoint(formattedResult);
        outerRecordTest.setAnswer(listToString(arrAns));
        viewModel.updateRecordTest(outerRecordTest);
    }
    private void saveRecordTest() {
        correctAns = getArrCorrectAnswers(questionList);
        correctQuestion = (int) IntStream.range(0, correctAns.size())
                .filter(i -> correctAns.get(i).equals(arrAns.get(i)))
                .count();
        Log.d("outerRecordTest",outerRecordTest.toString());
        outerRecordTest.setCorrectQuestion(String.valueOf(correctQuestion));
        outerRecordTest.setCurrentQuestion(String.valueOf(currentQuestionIndex));
        double point = (double) (correctQuestion * 10) / arrAns.size();
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedResult = df.format(point);
        outerRecordTest.setPoint(formattedResult);
//        outerRecordTest.setState(Constants.STATE_FINISH);
        outerRecordTest.setAnswer(listToString(arrAns));
        Date currentTime = new Date();

        // Define the desired time format
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

        String formattedTime = formatter.format(currentTime);
        outerRecordTest.setTime(formattedTime);

        viewModel.updateRecordTest(outerRecordTest);
        Log.d("outerRecordTest123",outerRecordTest.toString());
//        openResultActivity();
    }

    public void openResultActivity() {
        Intent intentResult = new Intent(ExaminingTestActivity.this, FinalResultActivity.class);
        intentResult.putExtra(Constants.SUBJECT, room.getNameRoom());
        intentResult.putExtra(Constants.CORRECT, correctQuestion);
        intentResult.putExtra(Constants.INCORRECT, questions.size() - correctQuestion);
        intentResult.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentResult);
        finish();
    }
    private static String listToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));

            if (i < list.size() - 1) {
                sb.append(", "); // Add a comma and space between elements
            }
        }

        return sb.toString();
    }
    private static ArrayList<Question> convertToArrayList(String jsonString) {
        // Create a Gson object
        Gson gson = new Gson();

        // Define the type for the ArrayList using TypeToken
        Type questionListType = new TypeToken<ArrayList<Question>>() {}.getType();

        // Convert JSON string to ArrayList<Question>
        return gson.fromJson(jsonString, questionListType);
    }
    private void displayData() {
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1));
        question = questionList.get(currentQuestionIndex);
        if (question.getImage() != null) {
            imgQuestion.setVisibility(View.VISIBLE);
            Bitmap bitmap = Utils.getBitmapFromString(question.getImage());
            imgQuestion.setImageBitmap(bitmap);
        } else {
            imgQuestion.setVisibility(View.GONE);
        }
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

    public ArrayList<Integer> getArrCorrectAnswers(List<Question> questionArrayList) {
        ArrayList<Integer> correctAnswersList = new ArrayList<>();

        for (Question question : questionArrayList) {
            ArrayList<String> answers = question.getAnswers();
            int correctNum = question.getCorrectNum();
            Log.d("correctNum", "Correct Answer:" + correctNum);

            if (correctNum >= 0 && correctNum < answers.size()) {
                correctAnswersList.add(correctNum);
            }
        }
        return correctAnswersList;
    }

    public Map<String, Map<String, Boolean>> getExam(List<Question> questionList) {
        LinkedHashMap<String, Map<String, Boolean>> questions = new LinkedHashMap<>();
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
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

    private void displayNextQuestions() {
        tvQuestion.setText(questions.get(currentQuestionIndex));
        tvQuestionNumber.setText("Current Question: " + (currentQuestionIndex + 1));
        question = questionList.get(currentQuestionIndex);
        if (currentQuestionIndex == questionList.size() - 1){
            btnNext.setText(getText(R.string.finish));
        } else {
            btnNext.setText(getText(R.string.next));
        }
        if (question.getImage() != null) {
            imgQuestion.setVisibility(View.VISIBLE);
            Bitmap bitmap = Utils.getBitmapFromString(question.getImage());
            imgQuestion.setImageBitmap(bitmap);
        } else {
            imgQuestion.setVisibility(View.GONE);
        }

    }


    private void initView() {
        imgQuestion = findViewById(R.id.imgQuestion);
        tvTitle = findViewById(R.id.textView26);
        tvQuestion = findViewById(R.id.textView78);
        tvQuestionNumber = findViewById(R.id.textView18);
        tvTimerCounter = findViewById(R.id.tvTimerCounter);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        rvAnswer = findViewById(R.id.rvAnswer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveRecordTest();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
//        saveRecordTest();
//        outerRecordTest.setState(Constants.STATE_TERMINATE);
//        viewModel.updateRecordTest(outerRecordTest);
    }
}