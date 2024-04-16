package com.practice.easyexam.app.model;

import static com.practice.easyexam.app.view.history.host.HistoryHostAdapter.VIEW_TYPE_CONTENT;

public class ContentQuestionItem implements HistoryItem {
    private Test test;

    public ContentQuestionItem(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }

    @Override
    public int getItemType() {
        return VIEW_TYPE_CONTENT;
    }

    @Override
    public String getDate() {
        return test.getCreateDate();
    }
}
