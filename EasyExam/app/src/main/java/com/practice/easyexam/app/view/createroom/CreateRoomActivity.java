package com.practice.easyexam.app.view.createroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.practice.easyexam.R;
import com.practice.easyexam.app.customview.CustomSpinner;
import com.practice.easyexam.app.customview.CustomSpinnerAdapter;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.data.local.ExamDatabase;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.createdTests.CreatedTestsViewModel;
import com.practice.easyexam.app.view.waiting.WaitingActivity;

import java.util.ArrayList;
import java.util.List;

public class CreateRoomActivity extends AppCompatActivity implements CustomSpinner.OnSpinnerEventsListener{
    public static final String ROOM = "ROOM";
    TextInputEditText edtNumberOfUser, edtTime, edtRoomName;
    CustomSpinner spinnerTest;
    MaterialToolbar toolbar;
    TextView tvCreate;
    private CreatedTestsViewModel viewModel;
    private int selectedPosition = 0;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        initView();
        ExamDatabase appDatabase = UserDatabaseClient.getInstanceExam(getBaseContext());
        // Load items from the database when the fragment is created or resumed
        user = SharedPref.getInstance().getUser(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new CreatedTestsViewModel(appDatabase);
            }
        }).get(CreatedTestsViewModel.class);
        viewModel.getAllTestByIdCreate(user.getEmail());
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerTest.setAdapter(adapter);

        List<String> data = new ArrayList<>();
        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, data);
        spinnerTest.setSpinnerEventsListener(this);
        spinnerTest.setAdapter(adapter);

        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                adapter.setSelectedItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        viewModel.getNameListLiveData().observe(this, items -> {
            if (items != null) {
                adapter.updateData(items);
            } else {
                data.add(String.valueOf(R.string.empty_test));
                adapter.updateData(data);
            }
            adapter.notifyDataSetChanged(); // Notify the adapter that data has change
        });

        viewModel.getInsertRoomLiveData().observe(this, bool -> {
            if (bool) {
                if (room != null) {
                    Utils.showToast(this, R.string.create_room_success);
                    Intent i = new Intent(this, WaitingActivity.class);
                    Log.d("CreateROOM", room.getIdRoom() + "@");
                    i.putExtra(ROOM, room);
                    startActivity(i);
                    finish();
                }
            } else {
                Utils.showToast(this, R.string.create_room_fail);
            }

        });

//        viewModel.getItemListLiveData().observe(this, tests -> {
//            adapter.clear();
//            if (tests != null) {
//                if (tests != null && !tests.isEmpty()) {
//                    adapter.addAll(viewModel.getListName(tests)); // Add the new data to the adapter
//                    viewModel.setListTest(tests);
//                }
//            } else {
//                adapter.add(String.valueOf(R.string.empty_test));
//            }
//            adapter.notifyDataSetChanged(); // Notify the adapter that data has change
//        });


        spinnerTest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position; // Store the selected position in the variable
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtRoomName.getText().toString().isEmpty() && !edtRoomName.getText().toString().isEmpty() && !edtTime.getText().toString().isEmpty()) {
                    String name = edtRoomName.getText().toString();
                    int numOfUser = Integer.parseInt(edtNumberOfUser.getText().toString());
                    int time = Integer.parseInt(edtTime.getText().toString());
                    if (isValid(name, numOfUser, time)) {
                        room = getRoom(name, numOfUser, time);
                        viewModel.createRoom(room);
                    }
                } else {
                    Utils.showToast(CreateRoomActivity.this  , R.string.request_fill);
                }

            }
        });
    }

    public boolean isValid(String name, int numOfUser, int time) {
        return !name.isEmpty() && numOfUser > 0 && time > 0;
    }

    public Room getRoom(String name, int numOfUser, int time) {
        List<Test> tests = viewModel.getTests();
        String idTest = tests.get(selectedPosition).id;
        return new Room(Utils.generateRandomID(), name, numOfUser, time, idTest, user.getEmail(), Utils.getCurrentDate());
    }

    private void initView() {
        edtRoomName = findViewById(R.id.tietUsername);
        edtNumberOfUser = findViewById(R.id.tietNumOfUser);
        edtTime = findViewById(R.id.tietSelectTime);
        spinnerTest = findViewById(R.id.spinnerTestSelection);
        tvCreate = findViewById(R.id.btnSave);
        toolbar = findViewById(R.id.tool_bar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onPopupWindowOpened(Spinner spinner) {
        spinnerTest.setBackground(getResources().getDrawable(R.drawable.bg_spinner_up));
    }

    @Override
    public void onPopupWindowClosed(Spinner spinner) {
        spinnerTest.setBackground(getResources().getDrawable(R.drawable.bg_spinner_down));
    }
}