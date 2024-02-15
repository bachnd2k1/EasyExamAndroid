package com.practice.easyexam.app.view.statistical;

import static com.practice.easyexam.app.view.history.host.HistoryHostActivity.ID_ROOM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.utils.ExcelExporter;
import com.practice.easyexam.app.view.login.LoginActivity;
import com.practice.easyexam.app.view.summary.SummaryExamViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticalActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private List<RecordTest> recordList = new ArrayList<>();
    private ImageView imgBack, imgMore;
    private TextView tv_num_user, tv_accuracy, tv_question;
    private Room room;
    private StatisticalViewModel viewModel;
    private Gson gson;
    private PopupMenu popupMenu;
    private Integer value = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistical);
        initView();
        room = (Room) getIntent().getSerializableExtra(ID_ROOM);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new StatisticalViewModel();
            }
        }).get(StatisticalViewModel.class);
        viewModel.getRecordTestInRoom(room.getIdRoom());
        gson = new Gson();
//        viewModel.getMaxNumberUserInRoom(idRoom);
        viewModel.getRecordTestLiveData().observe(this, new Observer<List<RecordTest>>() {
            @Override
            public void onChanged(List<RecordTest> recordTests) {
                recordList = recordTests;
                tv_num_user.setText(recordTests.size() + "/" + room.getNumOfUser());
                Gson gson = new Gson();
                List<Question> questions = gson.fromJson(recordTests.get(0).getQuestions(), new TypeToken<ArrayList<Question>>() {
                }.getType());
                tv_question.setText(questions.size() + "");
                viewPager.setAdapter(new ViewPagerAdapter(StatisticalActivity.this, recordTests, room));
                if (viewPager.getAdapter() != null) {
                    new TabLayoutMediator(tabLayout, viewPager,
                            (tab, position) -> {
                                switch (position) {
                                    case 0:
                                        tab.setText("Participant");
                                        break;
                                    case 1:
                                        tab.setText("Questions");
                                        break;
                                }
                            }).attach();
                }
            }
        });

        viewModel.getAccuracyAvg().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                tv_accuracy.setText(Math.round(aDouble) + " %");
            }
        });

        viewModel.getPermissionLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                value = integer;
                Toast.makeText(StatisticalActivity.this, String.valueOf(integer), Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getPermissionView(room.getIdRoom());
    }

    private void initView() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tv_num_user = findViewById(R.id.tv_num_user);
        tv_accuracy = findViewById(R.id.tv_accuracy);
        tv_question = findViewById(R.id.tv_question);
        imgBack = findViewById(R.id.imgBack);
        imgMore = findViewById(R.id.imgMore);
        imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showPopupMenu(View v) {
        if (value == 0) {
            popupMenu = new PopupMenu(this, v, Gravity.END, 0, R.style.PopupMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_statistical, popupMenu.getMenu());
        } else {
            popupMenu = new PopupMenu(this, v, Gravity.END, 0, R.style.PopupMenu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_statistical_not_view, popupMenu.getMenu());
        }

        popupMenu.setForceShowIcon(true);
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle clicks on menu items
            switch (item.getItemId()) {
                case R.id.menu_view:
                    if (value == 0) {
                        viewModel.setPermissionView(room.getIdRoom(),1);
                    } else  {
                        viewModel.setPermissionView(room.getIdRoom(),0);
                    }
//                    viewModel.getPermissionView(room.getIdRoom());
                    return true;
                case R.id.menu_export:
                    ExcelExporter.exportToExcel(this, recordList, room.getNameRoom());
                    return true;
                default:
                    return false;

            }
        });

        // Show the PopupMenu
        popupMenu.show();
    }

}