package com.practice.easyexam.app.view.statistical;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.Room;

import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private String idRoom = "";
    private Room room;
    private List<RecordTest> recordList;
    public ViewPagerAdapter(AppCompatActivity activity,List<RecordTest> recordList, Room room) {
        super(activity);
//        this.idRoom = idRoom;\
        this.room = room;
        this.recordList = recordList;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return  ParticipantFragment.newInstance(recordList);
            case 1:
                return  QuestionsFragment.newInstance(room);
            default:
                throw new IllegalArgumentException("Invalid position");
        }
    }
}
