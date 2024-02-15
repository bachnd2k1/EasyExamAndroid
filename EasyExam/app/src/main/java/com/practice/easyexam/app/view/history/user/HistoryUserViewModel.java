package com.practice.easyexam.app.view.history.user;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.RecordItem;
import com.practice.easyexam.app.model.RecordUserResponse;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.RoomResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryUserViewModel extends ViewModel {
    private MyApiService apiService;
    private MutableLiveData<List<RecordItem>> recordItemLiveData = new MutableLiveData<>();

    public MutableLiveData<List<RecordItem>> getRecordMutableLiveData() {
        return recordItemLiveData;
    }

    public HistoryUserViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void getRecordOfUser(String idUser) {
        apiService.getRecordsOfUser(idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RecordUserResponse>() {
                    @Override
                    public void onSuccess(RecordUserResponse response) {
                        Log.d("RecordResponse", response+ "");
                        List<RecordItem> roomList = response.getData();

                        if (!roomList.isEmpty()) {
                            Log.d("RecordResponse", roomList.size() + "");
                            recordItemLiveData.setValue(roomList);
                        } else {
                            recordItemLiveData.setValue(new ArrayList<>());
                            Log.e("RecordResponse", "Room list is empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("RecordResponse", "Error: " + e.getMessage());
                    }
                });
    }
}