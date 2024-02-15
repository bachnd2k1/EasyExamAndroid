package com.practice.easyexam.app.view.statistical;

import static com.practice.easyexam.app.view.history.host.HistoryHostActivity.ID_ROOM;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.view.tracking.TrackingActivity;
import com.practice.easyexam.app.view.tracking.TrackingViewModel;
import com.practice.easyexam.app.view.tracking.UserTrackingAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class ParticipantFragment extends Fragment {
    private RecyclerView rvParticipant;
    private ParticipantAdapter adapter;
    private List<RecordTest> recordList = new ArrayList<>();
    private StatisticalViewModel viewModel;
    public ParticipantFragment() {

    }
    public static ParticipantFragment newInstance(List<RecordTest> recordList) {
        ParticipantFragment fragment = new ParticipantFragment();
        Bundle args = new Bundle();
        args.putSerializable(ID_ROOM, (Serializable) recordList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recordList = (List<RecordTest>) getArguments().getSerializable(ID_ROOM);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_participant, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(StatisticalViewModel.class);
        rvParticipant = v.findViewById(R.id.rvParticipant);
        adapter = new ParticipantAdapter();
        adapter.updateData(recordList);

        rvParticipant.setLayoutManager(new LinearLayoutManager(getContext()));
        rvParticipant.setAdapter(adapter);
        rvParticipant.addItemDecoration(new MyItemDecoration(ContextCompat.getColor(getContext(), R.color.blue_black)));

        Double value = adapter.getAccuracy();

//        double averageAccuracy = accuracyList.stream()
//                .mapToDouble(Double::doubleValue)
//                .average()
//                .orElseThrow(() -> new RuntimeException("Accuracy list is empty"));
        Log.d("accuracyList",value+"a");
        viewModel.setAccuracyAvg(value);
        return v;
    }

    private class MyItemDecoration extends RecyclerView.ItemDecoration {
        private final int itemSpacing = 5;
        private final Paint dividerPaint;

        public MyItemDecoration(@ColorInt int color) {
            dividerPaint = new Paint();
            dividerPaint.setColor(color);
            dividerPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            if (parent.getAdapter().getItemViewType(position) == ParticipantAdapter.VIEW_TYPE_ITEM) {
                outRect.bottom = itemSpacing;
            }
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 1; i < parent.getChildCount(); i++) {
                View child = parent.getChildAt(i);
                int top = child.getTop() - itemSpacing;
                int bottom = child.getTop();
                c.drawRect(left, top, right, bottom, dividerPaint);
            }
        }
    }
}