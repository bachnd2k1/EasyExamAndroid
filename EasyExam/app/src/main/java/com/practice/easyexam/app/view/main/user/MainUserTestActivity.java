package com.practice.easyexam.app.view.main.user;

import static com.practice.easyexam.app.view.createroom.CreateRoomActivity.ROOM;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.account.AccountActivity;
import com.practice.easyexam.app.view.history.user.HistoryUserActivity;
import com.practice.easyexam.app.view.login.LoginActivity;
import com.practice.easyexam.app.view.waiting.TimerActivity;
import com.practice.easyexam.app.view.waiting.WaitingActivity;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;

public class MainUserTestActivity extends AppCompatActivity {
    private TextView textView2, tvUsername, btnJoinExam, textView5;
    private EditText codeEditText;
    private CardView cvHistory, cvAccount;
    SharedPref sharedPref;
    MainUserViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user_test);
        initView();
        sharedPref = SharedPref.getInstance();
        User user = sharedPref.getUser(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new MainUserViewModel();
            }
        }).get(MainUserViewModel.class);

        viewModel.getRoomLiveData().observe(this, item -> {
            if (item.getIdTest() == null) {
                Utils.showToast(MainUserTestActivity.this, R.string.incorrect_id_room);
            } else {
                Log.d("itemRoom", item.getNameRoom());
                Class<?> targetActivity = item.getState().equals(Utils.STATE_WAITING) ? WaitingActivity.class : TimerActivity.class;
                Intent i = new Intent(this, targetActivity).putExtra(ROOM, item);
                startActivity(i);
            }
        });

        tvUsername.setText(user.getName() + " - " + user.getIdStudent());

        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserTestActivity.this, HistoryUserActivity.class));
            }
        });

        cvAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sharedPref.clearSharedPref(MainUserTestActivity.this);
                Intent intent = new Intent(MainUserTestActivity.this, AccountActivity.class);
                startActivity(intent);
//                finish();
            }
        });

        btnJoinExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle save logic here
                String code = codeEditText.getText().toString();
                if (!code.isEmpty()) {
                    viewModel.queryRoomByID(code);
                } else {
                    Utils.showToast(MainUserTestActivity.this, R.string.empty_code);
                }
            }
        });

        codeEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (codeEditText.getRight() - codeEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // The drawable on the right of the EditText is clicked
                        startQRCodeScanner();
                        return true;
                    }
                }
                return false;
            }
        });
    }


    private void startQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }


    private void initView() {
        tvUsername = findViewById(R.id.tvUsernameHome);
        btnJoinExam = findViewById(R.id.tvJoin);
        codeEditText = findViewById(R.id.codeEditText);
        cvHistory = findViewById(R.id.cvStartQuiz);
        cvAccount = findViewById(R.id.cvCreate);
        textView5 = findViewById(R.id.textView5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle QR code scan result
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    // Handle canceled scan
                    Toast.makeText(this, "Cancelling to scan QR Code", Toast.LENGTH_LONG).show();
                } else {
                    // Handle successful scan
                    String qrCodeContents = result.getContents();
                    Log.d("decompress", qrCodeContents);
                    viewModel.queryRoomByID(qrCodeContents);
                }
            }
        }
    }
}