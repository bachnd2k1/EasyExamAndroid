package com.practice.easyexam.app.view.main.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.ApiResponse;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.RoomResponse;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainUserViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<Room> roomMutableLiveData = new MutableLiveData<>();
    private MyApiService apiService;

    public LiveData<Room> getRoomLiveData() {
        return roomMutableLiveData;
    }

    public MainUserViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void queryRoomByID(String id) {
        compositeDisposable.add(apiService.getRoom(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse<Room>>() {
                    @Override
                    public void onSuccess(ApiResponse<Room> response) {
                        Log.d("RoomResponse", response+ "");
                        Room room = response.getData();
                        roomMutableLiveData.setValue(room == null ? new Room() : room);
//                        if (room != ) {
//                            Room room = roomList.get(0); // Assuming you only expect one room
//                            Log.d("ResponseRoom", room.getIdRoom() + "");
//                            roomMutableLiveData.setValue(room);
//                        } else {
//                            Log.e("Response", "Room list is empty.");
//                            roomMutableLiveData.setValue(new Room());
//                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                        roomMutableLiveData.setValue(new Room());
                    }
                }));
    }
}
