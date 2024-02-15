package com.practice.easyexam.app.view.history.host;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.RoomResponse;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HistoryHostViewModel extends ViewModel {
    private MyApiService apiService;
    private MutableLiveData<List<Room>> roomMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Room>> getRoomMutableLiveData() {
        return roomMutableLiveData;
    }

    public HistoryHostViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void getRoomFinish(String idHost) {
        apiService.getRooms(idHost)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RoomResponse>() {
                    @Override
                    public void onSuccess(RoomResponse response) {
                        Log.d("RoomResponse", response+ "");
                        List<Room> roomList = response.getData(); // Assuming you have a method like getData() in RoomResponse

                        if (!roomList.isEmpty()) {
                            Log.d("ResponseRoom", roomList.size() + "");
                            roomMutableLiveData.setValue(roomList);
                        } else {
                            Log.e("Response", "Room list is empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                });
    }
}
