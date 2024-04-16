package com.practice.easyexam.app.view.waiting;

import static com.practice.easyexam.app.data.remote.RetrofitClient.IP;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.utils.DialogUtils;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.tracking.TrackingActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WaitingActivity extends AppCompatActivity {

    public static final String TEST = "QUESTION";
    public static final String ROOM = "ROOM";
    private WaitingViewModel viewModel;
    TextView tvCode, tvNumberUser, btnStart;
    ImageView imageViewBack;
    ImageView imgCode, imgCopy;
    String code;
    Runnable runnable;
    Handler handler;
    int delay = 5000;
    Room room;
    User user;
    private WebSocket webSocket;
    private String state = "";
    int userCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting);
        initView();
        instantiateWebSocket();
        handler = new Handler();
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new WaitingViewModel();
                    }
                })
                .get(WaitingViewModel.class);

        room = (Room) getIntent().getSerializableExtra(ROOM);
        Log.d("ROOM", room.getIdRoom());
        user = SharedPref.getInstance().getUser(this);
        code = room.getIdRoom();
        tvCode.setText(code);
        tvNumberUser.setText("0/" + room.getNumOfUser());
        imgCode.setImageBitmap(viewModel.generateQRCode(code));
        if (user.getRole().equals(Constants.STUDENT)) {
//            viewModel.updateNumberInRoom(room.getIdRoom());
            btnStart.setVisibility(View.GONE);
        }
        viewModel.getAllQuestionInRoom(room);
//        Log.d("Test",  (ArrayList<Question>) viewModel.getQuestions() + "anceqw");
        viewModel.getStateLiveData().observe(this, state -> {
            if (state.equals(Utils.STATE_EXAMINING)) {
                Intent i = new Intent(this, TimerActivity.class);
//                Test test = new Test(room.getNameRoom(), (ArrayList<Question>) viewModel.getQuestions());
//                Log.d("Test","anceqw" +  viewModel.getQuestions().size());
//                Gson gson = new Gson();
//                String json = gson.toJson((ArrayList<Question>) viewModel.getQuestions());
//                i.putExtra(TEST,json);
                i.putExtra(ROOM, room);
                startActivity(i);
                finish();
            } else {
                viewModel.getStateRoom(room.getIdRoom());
            }
        });

//        viewModel.getAllQuestionInRoom(room);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!viewModel.getQuestions().isEmpty()) {
                    Log.d("roomId", room.getIdRoom());
                    room.setCurrentNumUser(userCount);
                    webSocket.send(Utils.STATE_EXAMINING);
                    Date currentTime = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    String formattedTime = formatter.format(currentTime);
                    room.setStartTime(formattedTime);
                    viewModel.saveNumUserInRoom(room);
                    viewModel.startRoom(room.getIdRoom());
                    if (runnable != null) {
                        handler.removeCallbacks(runnable);
                        runnable = null;
                    }
                } else {
                    Utils.showToast(WaitingActivity.this, R.string.empty_test);
                }
            }
        });
        runnable = new Runnable() {
            @Override
            public void run() {
//                viewModel.getStateRoom(room.getIdRoom());
//                viewModel.getCurrentNumberInRoom(room.getIdRoom());
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(runnable, delay);
    }

    private void instantiateWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://" + IP + ":8080").build();
        SocketListener socketListener = new SocketListener(this);
        webSocket = client.newWebSocket(request, socketListener);
    }

    public class SocketListener extends WebSocketListener {
        public WaitingActivity activity;

        public SocketListener(WaitingActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Connection Established!", Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        public void onMessage(WebSocket webSocket, final String text) {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("WebSocket", text);
                    try {
                        JSONObject jsonObject = new JSONObject(text);

                        if (jsonObject.has("userCount")) {
                            userCount = jsonObject.getInt("userCount");
                            tvNumberUser.setText(userCount - 1 + "/" + room.getNumOfUser());
                        } else {
                            state = jsonObject.getString("state");
                            if (state.equals(Utils.STATE_EXAMINING)) {
                                Intent i = new Intent(WaitingActivity.this, TimerActivity.class);
                                i.putExtra(ROOM, room);
                                startActivity(i);
                                finish();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
        }


        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
        }


        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
        }


        @Override
        public void onFailure(WebSocket webSocket, final Throwable t, @Nullable final Response response) {
            super.onFailure(webSocket, t, response);
            Toast.makeText(WaitingActivity.this, response.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DialogUtils.showNotificationDialog(
                WaitingActivity.this,
                getString(R.string.notification),
                getString(R.string.cancel_exam),
                true,
                (dialogInterface, i) -> {
                    viewModel.deleteRoom(room.getIdRoom());
                    finish();
                }
        );
    }

    private void initView() {
        imageViewBack = findViewById(R.id.imgBack);
        tvCode = findViewById(R.id.codeTextView);
        tvNumberUser = findViewById(R.id.tvNumUser);
        imgCode = findViewById(R.id.imgCode);
        imgCopy = findViewById(R.id.copyButton);
        btnStart = findViewById(R.id.btnStart);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showNotificationDialog(
                        WaitingActivity.this,
                        getString(R.string.notification),
                        getString(R.string.cancel_exam),
                        true,
                        (dialogInterface, i) -> {
//                            viewModel.finishRoom(room.getIdRoom());
                            if (user.getRole().equals(Constants.TEACHER)) {
                                viewModel.deleteRoom(room.getIdRoom());
                            }
                            finish();
                        }
                );
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocket.close(1000, "Activity Paused");
    }
}