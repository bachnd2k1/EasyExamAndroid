package com.practice.easyexam.app.view.history.user;

import static com.practice.easyexam.app.view.history.host.HistoryHostActivity.ID_ROOM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.RecordItem;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.view.history.host.HistoryHostAdapter;
import com.practice.easyexam.app.view.history.host.HistoryHostViewModel;
import com.practice.easyexam.app.view.summary.SummaryExamActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryUserActivity extends AppCompatActivity {
    public static final String ID_STUDENT = "ID_STUDENT" ;
    public static final String QUESTION = "QUESTION";
    RecyclerView rvExam;
    HistoryAdapter adapter;
    List<RecordItem> recordItems = new ArrayList<>();
    TextView tvTotalTest;
    HistoryUserViewModel viewModel;
    ImageView imgBack,imgFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_user);
        rvExam = findViewById(R.id.rvExam);
        imgBack = findViewById(R.id.imgBack);
        imgFilter = findViewById(R.id.imgFilter);
        tvTotalTest = findViewById(R.id.tvTotalTest);


        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new HistoryUserViewModel();
            }
        }).get(HistoryUserViewModel.class);
        User user = SharedPref.getInstance().getUser(this);
        viewModel.getRecordOfUser(user.getIdStudent());
        adapter = new HistoryAdapter(recordItems);
        adapter.setOnClickItemHistory (recordItem -> {
            int isViewed = recordItem.getRecordTest().getIsViewed();
            if (isViewed == 0) {
                Toast.makeText(this,R.string.not_allow_to_view,Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, SummaryExamActivity.class);
                intent.putExtra(QUESTION, (Serializable) recordItem);
                startActivity(intent);
            }
        });
        rvExam.setLayoutManager(new LinearLayoutManager(this));
        rvExam.setAdapter(adapter);
        viewModel.getRecordMutableLiveData().observe(this, new Observer<List<RecordItem>>() {
            @Override
            public void onChanged(List<RecordItem> recordItems) {
                if (recordItems.size() > 0 ) {
                    tvTotalTest.setText(String.valueOf(recordItems.size()));
                    adapter.updateData(recordItems);
                }
            }
        });
    }
}