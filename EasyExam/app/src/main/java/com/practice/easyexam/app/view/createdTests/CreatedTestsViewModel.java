package com.practice.easyexam.app.view.createdTests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.data.local.ExamDatabase;
import com.practice.easyexam.app.model.TestResponse;
import com.practice.easyexam.app.model.UpdateResponse;
import com.practice.easyexam.app.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

public class CreatedTestsViewModel extends ViewModel {
    private ExamDatabase appDatabase;
    private MyApiService apiService;
    private List<Test> tests = new ArrayList<>();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<Boolean> insertionResult = new MutableLiveData<>();
    private MutableLiveData<List<Test>> testsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Test>> itemListLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> nameListLiveData = new MutableLiveData<>();

    private MutableLiveData<List<Question>> questionListLiveData = new MutableLiveData<>();

    public CreatedTestsViewModel(ExamDatabase appDatabase) {
        this.appDatabase = appDatabase;
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void setTests(List<Test> tests) {
        testsLiveData.setValue(tests);
    }

    // Method to observe the tests LiveData from the activity
    public LiveData<Boolean> getInsertRoomLiveData() {
        return insertionResult;
    }

    public MutableLiveData<List<Question>> getQuestionListLiveData() {
        return questionListLiveData;
    }


    public LiveData<List<Test>> getTestsLiveData() {
        return testsLiveData;
    }


    public LiveData<List<String>> getNameListLiveData() {
        return nameListLiveData;
    }

    public LiveData<List<Test>> getItemListLiveData() {
        return itemListLiveData;
    }

    public void queryAllExam() {
//        Disposable disposable = appDatabase.examDao().getListExam()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map(exams -> {
//                    List<String> nameList = new ArrayList<>();
//                    for (Test exam : exams) {
//                        nameList.add(exam.getName());
//                    }
//                    nameListLiveData.setValue(nameList);
//                    return exams;
//                })
//                .subscribe(items -> {
//                    itemListLiveData.setValue(items);
//                });
//        compositeDisposable.add(disposable);
    }

    public void getAllTestByIdCreate(String idCreate) {
        compositeDisposable.add(apiService.getAllTestByIdCreate(idCreate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<TestResponse>() {
                    @Override
                    public void onSuccess(TestResponse response) {
                        List<Test> tests = response.getTests();
                        List<String> nameList = new ArrayList<>();
                        if (tests != null && !tests.isEmpty()) {
                            testsLiveData.setValue(tests);
                            for (Test exam : tests) {
                                nameList.add(exam.getName());
                            }
                            setListTest(tests);
                            nameListLiveData.setValue(nameList);
                        } else {
                            Log.e("Response", "Test list is null or empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                }));
    }

//    public  void saveNumUserInRoom(Room room) {
//        apiService.saveNumInRoom(room.getIdRoom(),room.getNumOfUser())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<UpdateResponse>() {
//                    @Override
//                    public void onSuccess(UpdateResponse response) {
//                        if (response.getMessage().equals("done")) {
//                            updateResult.setValue(true);
//                        } else {
//                            updateResult.setValue(true);
//                        }
//                        Log.d("updateExamResponse", response.getMessage() + "response");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        updateResult.setValue(false);
//                        Log.e("Response", "Error: " + e.getMessage());
//                    }
//                });
//    }





    public void getQuestionsByIDTest(String idtest) {
        Log.d("idtest", idtest + "");
        compositeDisposable.add(apiService.getQuestionByIDTest(idtest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<QuestionResponse>() {
                    @Override
                    public void onSuccess(QuestionResponse response) {
                        List<Question> questions = response.getQuestions();
                        questionListLiveData.setValue(questions);
                    }

                    @Override
                    public void onError(Throwable e) {
                        questionListLiveData.setValue(new ArrayList<>());
                        Log.e("getQuestionsByIDTest", "Error: " + e.getMessage());
                    }
                }));
    }

    public void createRoom(Room room) {
        compositeDisposable.add(apiService.insertRoom(room)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // Check if the response is successful
                            Log.d("server", response + "");
                            if (response.isSuccessful()) {
                                // Handle the success response
                                insertionResult.setValue(true);
                                Log.d("responseRoom", "Room inserted successfully: " + response.body().string());
                            } else {
                                // Handle the error response
                                insertionResult.setValue(false);
                                Log.e("responseRoom", "Error occurred while inserting data: " + response.errorBody().string());
                            }
                        },
                        throwable -> {
                            // Handle the error response
                            insertionResult.setValue(false);
                            Log.e("responseRoom", "Error occurred while inserting data", throwable);
                        }
                ));

    }

    public List<String> getListName(List<Test> tests) {
        List<String> nameList = new ArrayList<>();
        for (Test exam : tests) {
            nameList.add(exam.getName());
        }
        return nameList;
    }

    public void setListTest(List<Test> tests) {
        this.tests = tests;
    }

    public List<Test> getTests() {
        return tests;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
