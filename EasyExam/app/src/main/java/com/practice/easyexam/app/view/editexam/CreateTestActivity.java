package com.practice.easyexam.app.view.editexam;

import static com.practice.easyexam.app.view.createdTests.CreatedTestsActivity.EXAM;

import static com.practice.easyexam.app.view.editexam.CreateExamNormalActivity.CODE_NUMBER;
import static com.practice.easyexam.app.view.editexam.CreateQuestionActivity.CODE_EDIT;
import static com.practice.easyexam.app.view.editexam.CreateQuestionActivity.CODE_QUESTION;
import static com.practice.easyexam.app.view.editexam.CreateQuestionActivity.URI_KEY;
import static com.practice.easyexam.app.view.main.host.MainHostActivity.PATH;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.data.local.ExamDatabase;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.utils.DialogUtils;
import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.QuizParser;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.tracking.TrackingActivity;

import java.io.IOException;
import java.util.ArrayList;


public class CreateTestActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_ADD = 100;
    private static final int REQUEST_CODE_EDIT = 101;

    private CreateTestViewModel viewModel;
    private RecyclerView rvExamAfterCreate;
    private MaterialToolbar materialToolbar;
    private QuestionTestAdapter afterCreateAdapter;
    private TextView tvAdd;
    private Test exam;
    private LinearLayout layoutEmpty;
    private String path;
    private ArrayList<Question> questionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_create_exam);
        ExamDatabase appDatabase = UserDatabaseClient.getInstanceExam(getBaseContext());
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new CreateTestViewModel(appDatabase);
                    }
                })
                .get(CreateTestViewModel.class);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        rvExamAfterCreate = findViewById(R.id.rvExam);
        materialToolbar = findViewById(R.id.tool_bar);
        tvAdd = findViewById(R.id.btnAdd);
        setSupportActionBar(materialToolbar);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                DialogUtils.showNotificationDialog(
                        CreateTestActivity.this,
                        getString(R.string.notification),
                        getString(R.string.cancel_adding_question),
                        true,
                        (dialogInterface, i) -> {
                            finish();
                        }
                );
            }
        });
        materialToolbar.inflateMenu(R.menu.menu_after_create_exam);
        exam = (Test) getIntent().getSerializableExtra(EXAM);
        if (exam != null) {
            questionList.addAll(exam.getQuestionArrayList());
            updateEmptyLayoutVisibility();
        }
        path = getIntent().getStringExtra(PATH);
        if (path != null) {
            questionList = (ArrayList<Question>) QuizParser.parseQuiz(path);
            updateEmptyLayoutVisibility();
        }
        afterCreateAdapter = new QuestionTestAdapter(questionList, this);
        rvExamAfterCreate.setAdapter(afterCreateAdapter);
        rvExamAfterCreate.setLayoutManager(new LinearLayoutManager(this));

        afterCreateAdapter.setOnItemOptionListener((menuId, item,position) -> {
            if (menuId == R.id.menu_delete) {
                DialogUtils.showNotificationDialog(
                        CreateTestActivity.this,
                        getString(R.string.notification),
                        getString(R.string.delete_question),
                        true,
                        (dialogInterface, i) -> {
                            afterCreateAdapter.questionArrayList.remove(item);
                            afterCreateAdapter.notifyDataSetChanged();
                            updateEmptyLayoutVisibility();
                        }
                );
            } else if (menuId == R.id.menu_edit) {
                Intent intent = new Intent(this, CreateQuestionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("object",item);
                bundle.putInt("index",position);
                Log.d("position_edit",position +"");
                intent.putExtras(bundle);
                Log.d("QUESTION_CURRENT",item.toString());
                startActivityForResult(intent, REQUEST_CODE_EDIT);
            }
        });

        viewModel.getInsertionResult().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isInsertionSuccessful) {
                if (isInsertionSuccessful) {
                    Utils.showToast(getBaseContext(),R.string.insert_exam_successfully);
                } else {
                    Utils.showToast(getBaseContext(),R.string.insert_exam_fail);
                }
                finish();
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTestActivity.this, CreateQuestionActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_after_create_exam, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                DialogUtils.showRenameDialog(
                        CreateTestActivity.this,
                        name -> {
                            if (name.isEmpty()) {
                                Toast.makeText(
                                        CreateTestActivity.this,
                                        R.string.warning_name_empty,
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                User user = SharedPref.getInstance().getUser(this);
                                if (questionList.size() > 0) {
                                    if (exam == null) {
                                        exam = new Test();
                                        exam.setName(name);
                                        exam.setQuestionArrayList(questionList);
                                        exam.setCreateDate(Utils.getCurrentDate());
                                        viewModel.insertExam(exam,user);
                                    } else {
                                        exam.setName(name);
                                        exam.setQuestionArrayList(questionList);
                                        exam.setCreateDate(Utils.getCurrentDate());
//                                        viewModel.updateExam(exam,user);
                                        viewModel.addQuestionToExam(exam.getQuestionArrayList());
                                        for (Question question: questionList) {
                                            Log.d("ITEM_QUESTION",question.getQuestion());
                                        }
                                    }
                                } else {
                                    Toast.makeText(this,R.string.empty_question,Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        view -> {
                            finish();
                        },
                        exam == null ? "" : exam.getName());
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateEmptyLayoutVisibility() {
        if (questionList.size() > 0) {
            layoutEmpty.setVisibility(View.INVISIBLE);
        } else {
            layoutEmpty.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_ADD:
                handleAddResult(resultCode, data);
                break;
            case REQUEST_CODE_EDIT:
                handleEditResult(resultCode, data);
                break;
            // Add more cases if there are additional request codes
        }
    }

    private void handleAddResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Question question = getQuestionFromIntent(data, CODE_QUESTION);
            Uri uri = getUriFromIntent(data, URI_KEY);

            if (question != null) {
                handleImageBitmap(uri, question);
                questionList.add(question);
                afterCreateAdapter.updateAnswer();
                afterCreateAdapter.notifyDataSetChanged();
                updateEmptyLayoutVisibility();
                showToast("Size: " + questionList.size());
            }
        } else {
            showToast("Error");
        }
    }

    private void handleEditResult(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Question question = getQuestionFromIntent(data, CODE_EDIT);
            Uri uri = getUriFromIntent(data, URI_KEY);
            int index = data.getIntExtra(CODE_NUMBER, -1);
            if (uri != null) {
                handleImageBitmap(uri, question);
            }

            if (question != null && index >= 0 && index < questionList.size()) {
                Log.d("QUESTION_BACK",question.toString());
                questionList.set(index, question);
                afterCreateAdapter.notifyDataSetChanged();
            }
        } else {
            showToast("ErrorEdit");
        }
    }

    private Question getQuestionFromIntent(Intent data, String key) {
        return (Question) data.getExtras().getSerializable(key);
    }

    private Uri getUriFromIntent(Intent data, String key) {
        return data.getExtras().getParcelable(key);
    }

    private void handleImageBitmap(Uri uri, Question question) {
        if (uri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                String b64 = Utils.getStringImage(bitmap);
                question.setImage(b64);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}