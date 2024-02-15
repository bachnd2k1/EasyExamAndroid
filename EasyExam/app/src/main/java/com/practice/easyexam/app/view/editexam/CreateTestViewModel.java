package com.practice.easyexam.app.view.editexam;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.ListQuestionResponse;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.data.local.ExamDatabase;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class CreateTestViewModel extends ViewModel {

    private ExamDatabase appDatabase;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<Boolean> insertionResult = new MutableLiveData<>();

    private MutableLiveData<Boolean> checkNameResult = new MutableLiveData<>();

    MutableLiveData<Boolean> insertResult = new MutableLiveData<>();


    private MyApiService apiService;

    public CreateTestViewModel(ExamDatabase appDatabase) {
        this.appDatabase = appDatabase;
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public LiveData<Boolean> getInsertionResult() {
        return insertResult;
    }

    public LiveData<Boolean> getCheckNameResult() {
        return checkNameResult;
    }


    public void insertItem(Test item) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        item.setCreateDate(formatter.format(date));
        Disposable disposable = appDatabase.examDao().insertExam(item).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            // Insertion successful
            insertionResult.setValue(true);
            Log.d("CreateTestViewModel", "inserted successfully");
        }, throwable -> {
            // Handle error if insertion fails
            insertionResult.setValue(false);
            Log.e("CreateTestViewModel", "Error inserting : " + throwable.getMessage());
        });
        compositeDisposable.add(disposable);
    }

    public void insertExam(Test exam, User user) {
        Log.d("question", exam.id + "original");
        compositeDisposable.add(apiService.addTest(exam.id, exam.getName(), exam.getCreateDate(), user.getEmail())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    // Handle successful response
                    ArrayList<Question> questions = exam.getQuestionArrayList();
                    questions.forEach(question -> question.setIdTest(exam.id));
                    addQuestionToExam(questions);
                }, throwable -> {
                    // Handle network failure or other errors
                    if (throwable instanceof HttpException) {
                        ResponseBody errorBody = ((HttpException) throwable).response().errorBody();
                        if (errorBody != null) {
                            try {
                                String errorResponse = errorBody.string();
                                Log.e("response123", "Error Response: " + errorResponse);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Log.e("response123", "Error: " + throwable.getMessage());
                    }
                    insertResult.setValue(false);
                }));
    }


    public  void updateExam(Test exam, User user) {
        compositeDisposable.add(apiService.updateTest(exam.id, exam.getName(), exam.getCreateDate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<com.practice.easyexam.app.model.Response>() {
                    @Override
                    public void onSuccess(com.practice.easyexam.app.model.Response response) {
                        if (response.getMessage().equals("done")) {
                            insertResult.setValue(true);
                        } else {
                            insertResult.setValue(false);
                        }
                        Log.d("updateExamResponse", response.getMessage() + "response");
                    }

                    @Override
                    public void onError(Throwable e) {
                        insertResult.setValue(false);
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                }));
    }


//    private void addQuestionToExam(ArrayList<Question> questions) {
//        compositeDisposable.add(apiService.addQuestionToTest(questions).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
//            // Handle successful response
//            Log.d("addQuestion","size:" + questions.size());
//            Log.d("response123", "Questions inserted successfully");
//            insertResult.setValue(true);
//            // You can perform any further processing if needed
//        }, throwable -> {
//            // Handle network failure or other errors
//            Log.e("response123", "Error: " + throwable.getMessage());
//            insertResult.setValue(false);
//        }));
//    }

    public void addQuestionToExam(ArrayList<Question> questions) {
        apiService.addQuestion(questions)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ListQuestionResponse>() {
                    @Override
                    public void onSuccess(ListQuestionResponse response) {
                        if (!response.getError()) {
                            insertResult.setValue(true);
                        } else {
                            insertResult.setValue(false);
                        }
                        Log.d("ListQuestionResponse", response.getMessage() + "response");
                    }

                    @Override
                    public void onError(Throwable e) {
                        insertResult.setValue(false);
                        Log.e("ListQuestionResponse", "Error: " + e.getMessage());
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}


