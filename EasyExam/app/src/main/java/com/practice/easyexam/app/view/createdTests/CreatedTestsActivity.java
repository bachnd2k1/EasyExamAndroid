package com.practice.easyexam.app.view.createdTests;

import static com.practice.easyexam.app.view.main.host.MainHostActivity.TYPE_JOIN_ROOM;
import static com.practice.easyexam.app.view.main.host.MainHostActivity.TYPE_TEST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.ExamDatabase;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.utils.OptionalDialog;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.view.editexam.CreateTestActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreatedTestsActivity extends AppCompatActivity implements OptionalDialog.OptionalDialogListener  {
    public static final String EXAM = "EXAM";
    private CreatedTestsViewModel viewModel;
    private CreatedTestAdapter adapter;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private TextView btnCreateNow;
    private MaterialToolbar toolbar;
    User user;
    Test testTmp = new Test();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_created_test);
        initView();
        ExamDatabase appDatabase = UserDatabaseClient.getInstanceExam(getBaseContext());
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new CreatedTestsViewModel(appDatabase);
                    }
                })
                .get(CreatedTestsViewModel.class);
        adapter = new CreatedTestAdapter(new ArrayList<>());
        adapter.setOnItemListener(new OnItemListener() {
            @Override
            public void onItemClick(Test test) {
                Log.d("TESTDATA",test.toString());
                testTmp = test;
                viewModel.getQuestionsByIDTest(test.id);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new CreatedTestsViewModel(appDatabase);
            }
        }).get(CreatedTestsViewModel.class);
        user = SharedPref.getInstance().getUser(this);


        viewModel.getTestsLiveData().observe(this, new Observer<List<Test>>() {
            @Override
            public void onChanged(List<Test> tests) {
                linearLayout.setVisibility(tests.size() > 0 ? View.GONE : View.VISIBLE);
                adapter.updateData(tests);
            }
        });

        viewModel.getQuestionListLiveData().observe(this, new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questionList) {
                if (!questionList.isEmpty()) {
                    testTmp.setQuestionArrayList((ArrayList<Question>) questionList);
                    Intent intent = new Intent(CreatedTestsActivity.this,CreateTestActivity.class);
                    intent.putExtra(EXAM, (Serializable) testTmp);
                    startActivity(intent);
//                    finish();
                } else {
                    Toast.makeText(CreatedTestsActivity.this,"EMPTY DATA",Toast.LENGTH_LONG).show();
                }
            }
        });
        btnCreateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] examOptions =  getApplicationContext().getResources().getStringArray(R.array.option_choice_exam);
                showOptionalDialog(examOptions,TYPE_TEST);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.getAllTestByIdCreate(user.getEmail());
    }

    void initView() {
        linearLayout = findViewById(R.id.layoutEmpty);
        recyclerView = findViewById(R.id.rvTest);
        btnCreateNow = findViewById(R.id.btnCreateNow);
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showOptionalDialog(String[] options, String dialogTag) {
        OptionalDialog optionalDialog = new OptionalDialog(options, dialogTag);
        optionalDialog.show(getSupportFragmentManager(), dialogTag);
    }


//    @Override
//    public void onPositiveButtonClicked(int position) {
//        switch (position) {
//            case 0:
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                    selectCSVFile();
////                } else {
////                    if (checkStoragePermissionImport()) {
////                        selectCSVFile();
////                    }
////                }
//                break;
//            case 1:
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                    selectDocxFile();
////                } else {
////                    if (checkStoragePermissionImport()) {
////                        selectDocxFile();
////                    }
////                }
//                Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
//                startActivity(intent);
//                finish();
//                break;
//        }
//    }
//
//    @Override
//    public void onNegativeButtonClicked() {
//
//    }

    @Override
    public void onPositiveButtonClicked(String dialogTag, int position) {
        if (dialogTag.equals(TYPE_TEST)) {
            switch (position) {
                case 0:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                        selectCSVFile();
//                    } else {
//                        if (checkStoragePermissionImport()) {
//                            selectCSVFile();
//                        }
//                    }
                    break;
                case 1:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    selectDocxFile();
//                } else {
//                    if (checkStoragePermissionImport()) {
//                        selectDocxFile();
//                    }
//                }
                    Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
                    startActivity(intent);
                    break;
            }
        } else if (dialogTag.equals(TYPE_JOIN_ROOM)) {
            // Handle positive button click for the second dialog
            Log.d("YourActivity", "Dialog 2 - Positive button clicked, position: " + position);
        }

    }

    @Override
    public void onNegativeButtonClicked(String dialogTag) {
        if (dialogTag.equals(TYPE_TEST)) {
            // Handle negative button click for the first dialog
            Log.d("YourActivity", "Dialog 1 - Negative button clicked");

            // If you want to show the second dialog after the first one is dismissed,
            // you can call showOptionalDialog(examOptions2, "dialog2") here.
        } else if (dialogTag.equals(TYPE_JOIN_ROOM)) {
            // Handle negative button click for the second dialog
            Log.d("YourActivity", "Dialog 2 - Negative button clicked");
        }
    }
}