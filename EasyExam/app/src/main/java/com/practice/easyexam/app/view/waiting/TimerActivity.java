package com.practice.easyexam.app.view.waiting;

import static com.practice.easyexam.app.view.waiting.WaitingActivity.ROOM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.examing.ExaminingTestActivity;
import com.practice.easyexam.app.view.tracking.TrackingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimerActivity extends AppCompatActivity {

    private WaitingViewModel viewModel;
    private CountDownTimer countDownTimer;
    private int countdown = 5; // Initial countdown value

    Room room;
    Intent intent;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
//        json = getIntent().getStringExtra(TEST);
        room = (Room) getIntent().getSerializableExtra(ROOM);
        Log.d("room",room.getNameRoom()+"abcd");

        SharedPref sharedPref = SharedPref.getInstance();
        user =  sharedPref.getUser(this);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new WaitingViewModel();
                    }
                })
                .get(WaitingViewModel.class);
//        viewModel.getAllQuestionInRoom(room);
//        viewModel.getQuestionLiveData().observe(this ,list -> {
//            if (!list.isEmpty()) {
//                Utils.showToast(this,R.string.value);
//            } else {
//                Utils.showToast(this,R.string.empty_test);
//            }
//        });


        if (user.getRole().equals(Constants.STUDENT)) {
            viewModel.getInsertionResult().observe(this, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    Toast.makeText(TimerActivity.this, "Add RecordTest" + aBoolean, Toast.LENGTH_SHORT).show();
                }
            });
            viewModel.getQuestionLiveData().observe(this, new Observer<List<Question>>() {
                @Override
                public void onChanged(List<Question> questionList) {
                    if (room.getState().equals(Utils.STATE_WAITING)) {
                        ArrayList<Integer> arrayList = new ArrayList<>(Collections.nCopies(questionList.size(), -1));
                        String answerDefault = formatArrayList(arrayList);
                        viewModel.createRecordTest(room,user,answerDefault);
                    }
                }
            });
            viewModel.getAllQuestionInRoom(room);
        }
        Log.d("TimeROOM",room.getIdRoom()+"@");
        startCountdown();
    }

    private static String formatArrayList(ArrayList<Integer> list) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            result.append(list.get(i));
            if (i < list.size() - 1) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(5000, 1000) { // 5 seconds total, tick every 1 second
            @Override
            public void onTick(long millisUntilFinished) {
                countdown--;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                User user = SharedPref.getInstance().getUser(TimerActivity.this);
                if (user.getRole().equals(Constants.STUDENT)) {
                    intent = new Intent(TimerActivity.this, ExaminingTestActivity.class);
                }
                else {
//                    intent = new Intent(TimerActivity.this, ExaminingTestActivity.class);
                    intent = new Intent(TimerActivity.this, TrackingActivity.class);
                }
//                intent.putExtra(TEST,json);
                intent.putExtra(ROOM,room);
                startActivity(intent);
                finish(); // Close this activity
            }
        };
        countDownTimer.start();
    }

    private void updateTimerText() {
        TextView timerTextView = findViewById(R.id.timerTextView);
        timerTextView.setText(String.valueOf(countdown));

        if (countdown > 0) {
            timerTextView.setText(String.valueOf(countdown));
        } else {
            timerTextView.setText("Ready!");
        }
    }
}