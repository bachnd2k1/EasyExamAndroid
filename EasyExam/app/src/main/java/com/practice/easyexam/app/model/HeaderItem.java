package com.practice.easyexam.app.model;

import static com.practice.easyexam.app.view.history.host.HistoryHostAdapter.VIEW_TYPE_HEADER;

public class HeaderItem implements HistoryItem {
    private String date;

    public HeaderItem(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_HEADER;
    }
}
