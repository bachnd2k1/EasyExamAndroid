package com.practice.easyexam.app.view.statistical;

import static com.practice.easyexam.app.view.history.host.HistoryHostActivity.ID_ROOM;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestionsFragment extends Fragment {

    private StatisticalViewModel viewModel;
    private RecyclerView recyclerView;
    private QuestionStatisticalAdapter adapter;
    private Room room;
    private ArrayList<Question> questionList = new ArrayList<>();

    public QuestionsFragment() {
        // Required empty public constructor
    }

    public static QuestionsFragment newInstance(Room room) {
        QuestionsFragment fragment = new QuestionsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ID_ROOM, room);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            room = (Room) getArguments().getSerializable(ID_ROOM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question1, container, false);
        recyclerView = view.findViewById(R.id.rvQuestion);
        viewModel = new ViewModelProvider(requireActivity()).get(StatisticalViewModel.class);
        adapter = new QuestionStatisticalAdapter(questionList, requireContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        viewModel.getQuestionListLiveData().observe(requireActivity(), new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questionList) {
                adapter.updateData(questionList);
            }
        });
        viewModel.getAnswerCountMap().observe(requireActivity(), new Observer<Map<String, Map<String, Integer>>>() {
            @Override
            public void onChanged(Map<String, Map<String, Integer>> answerCountMap) {
                adapter.updateCountMap(answerCountMap);
            }
        });
        viewModel.getQuestionsByIDTest(room.getIdTest());
        return view;
    }
}