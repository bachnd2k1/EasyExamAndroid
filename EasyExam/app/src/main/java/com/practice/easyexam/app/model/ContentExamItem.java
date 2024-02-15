package com.practice.easyexam.app.model;

import static com.practice.easyexam.app.view.history.host.HistoryHostAdapter.VIEW_TYPE_CONTENT;

public class ContentExamItem implements HistoryItem {
    private Room room;

    public ContentExamItem(Room room) {
        this.room = room;
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public String getDate() {
        // Assuming getCreateTime() returns a date string
        return room.getCreateTime();
    }
}
