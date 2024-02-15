package com.practice.easyexam.app.view.history.host;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.view.examing.ExaminingViewModel;
import com.practice.easyexam.app.view.statistical.StatisticalActivity;
import com.practice.easyexam.app.view.summary.SummaryExamActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryHostActivity extends AppCompatActivity {
    HistoryHostViewModel viewModel;
    public static final String ID_ROOM = "ID_ROOM";
    RecyclerView rvExam;
    HistoryHostAdapter adapter;
    List<Room> rooms = new ArrayList<>();
    ImageView imgBack;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_host);
        rvExam = findViewById(R.id.rvExam);
        imgBack = findViewById(R.id.imgBack);
        searchView = findViewById(R.id.searchView);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new HistoryHostViewModel();
            }
        }).get(HistoryHostViewModel.class);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        User user = SharedPref.getInstance().getUser(this);
        viewModel.getRoomFinish(user.getEmail());
        adapter = new HistoryHostAdapter(rooms);
        adapter.setOnClickItemHistory (room -> {
            Intent intent = new Intent(this, StatisticalActivity.class);
            intent.putExtra(ID_ROOM,room);
            startActivity(intent);
        });
        rvExam.setLayoutManager(new LinearLayoutManager(this));
        rvExam.setAdapter(adapter);
        viewModel.getRoomMutableLiveData().observe(this, new Observer<List<Room>>() {
            @Override
            public void onChanged(List<Room> rooms) {
                adapter.updateData(rooms);
            }
        });
        searchView.setIconifiedByDefault(false); // Ensure it's not iconified by default
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Call filter method in the adapter
                adapter.getFilter().filter(newText);
                return true;
            }
        });

    }
}