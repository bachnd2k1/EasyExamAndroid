package com.practice.easyexam.app.view.tracking;

import static com.practice.easyexam.app.view.waiting.WaitingActivity.ROOM;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.UserData;
import com.practice.easyexam.app.utils.DialogUtils;
import com.practice.easyexam.app.view.examing.ExaminingViewModel;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserTrackingAdapter adapter;
    private List<RecordTest> dataList = new ArrayList<>();
    private TrackingViewModel viewModel;
    private ImageView imgBack;
    private CountDownTimer countDownTimer;
    long timeLeftInMillis = 0;
    TextView tvTimerCounter, tvNameRoom, tvCode;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        tvTimerCounter = findViewById(R.id.tvTimer);
        tvNameRoom = findViewById(R.id.tvNameRoom);
        tvCode = findViewById(R.id.tvCode);
        imgBack = findViewById(R.id.imgBack);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new TrackingViewModel();
            }
        }).get(TrackingViewModel.class);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtils.showNotificationDialog(
                        TrackingActivity.this,
                        getString(R.string.notification),
                        getString(R.string.early_end),
                        true,
                        (dialogInterface, i) -> {
                            viewModel.finishRoom(room.getIdRoom());
                            finish();
                        }
                );
            }
        });
        room = (Room) getIntent().getSerializableExtra(ROOM);
        tvNameRoom.setText(room.getNameRoom());
        tvCode.setText(room.getIdRoom());
        timeLeftInMillis = room.getTime() * 60000;
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
                viewModel.finishRoom(room.getIdRoom());
                showNotificationDialog(getString(R.string.notification), getString(R.string.finish_exam), false);
//                DialogUtils.showNotificationDialog(
//                        TrackingActivity.this,
//                        getString(R.string.notification),
//                        getString(R.string.finish_exam),
//                        false,
//                        (dialogInterface, i) -> {
//                            viewModel.finishRoom(room.getIdRoom());
//                            finish();
//                        }
//                );
            }
        };
        countDownTimer.start();
        adapter = new UserTrackingAdapter(dataList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addItemDecoration(new MyItemDecoration());
        viewModel.getRecordTestLiveData().observe(this, new Observer<List<RecordTest>>() {
            @Override
            public void onChanged(List<RecordTest> recordTests) {

                adapter.updateData(recordTests);
            }
        });
        viewModel.getRecordTestInRoom(room);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        Log.d("TrackingTestROOM", room.getIdRoom() + "@");
    }

    private void showNotificationDialog(String title, String content, boolean isShowNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        viewModel.finishRoom(room.getIdRoom());
                        finish();
                    }
                })
                .setCancelable(false);

        // Add negative button if isShowNegative is true
        if (isShowNegative) {
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Handle negative button click if needed
                }
            });
        }
        builder.show();
    }

    private void refreshData() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewModel.getRecordTestInRoom(room);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        private final int headerSpacing = 20; // Adjust as needed
        private final int itemSpacing = 10;   // Adjust as needed

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (position == 0 || parent.getAdapter().getItemViewType(position) == UserTrackingAdapter.VIEW_TYPE_HEADER) {
                // Add space between VIEW_TYPE_HEADER and VIEW_TYPE_ITEM
                outRect.bottom = headerSpacing;
            } else {
                // Add space between items in VIEW_TYPE_ITEM
                outRect.bottom = itemSpacing;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
