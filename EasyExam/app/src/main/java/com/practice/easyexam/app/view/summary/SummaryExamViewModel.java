package com.practice.easyexam.app.view.summary;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.RecordsResponse;
import com.practice.easyexam.app.model.Room;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SummaryExamViewModel extends ViewModel {
    MyApiService apiService;
    private MutableLiveData<List<RecordTest>> recordTestLiveData = new MutableLiveData<>();

    public MutableLiveData<List<RecordTest>> getRecordTestLiveData() {
        return recordTestLiveData;
    }
    public SummaryExamViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void getRecordTestInRoom(String idRoom) {
        apiService.getRecordTestsInRoom(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RecordsResponse>() {
                    @Override
                    public void onSuccess(RecordsResponse response) {
                        List<RecordTest> recordTests = response.getData();
                        if (recordTests != null && !recordTests.isEmpty()) {
                            Log.d("ResponseList", "Test" + recordTests.size());
                            recordTestLiveData.setValue(recordTests);
                        } else {
                            Log.e("Response", "Test list is null or empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                });

    }
}
