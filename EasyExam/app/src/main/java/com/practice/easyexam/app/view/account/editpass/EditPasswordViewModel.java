package com.practice.easyexam.app.view.account.editpass;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.Question;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EditPasswordViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<String> responseLiveData = new MutableLiveData<>();
    private MyApiService apiService;
    public LiveData<String> getResponse() {
        return responseLiveData;
    }

    public EditPasswordViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void updatePassword(String currentPassword, String newPassword, String idUser) {
        apiService.updatePassword(currentPassword, newPassword, idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    responseLiveData.setValue(response.getMessage());
                }, throwable -> {
                    Log.d("UpdatePasswordError", "Error updating password", throwable);
                    responseLiveData.setValue("Error");
                });
    }


}
