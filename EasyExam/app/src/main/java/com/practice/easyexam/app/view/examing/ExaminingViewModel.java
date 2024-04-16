package com.practice.easyexam.app.view.examing;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.RecordResponse;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.UpdateResponse;
import com.practice.easyexam.app.model.User;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExaminingViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<Question>> questionLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> insertionResult = new MutableLiveData<>();
    private MutableLiveData<Integer> currentQuestionIndex = new MutableLiveData<>();

    private MutableLiveData<RecordTest> recordTestResult = new MutableLiveData<>();
    private MyApiService apiService;


    public MutableLiveData<List<Question>> getQuestionLiveData() {
        return questionLiveData;
    }

    public MutableLiveData<RecordTest> getRecordTestResult() {
        return recordTestResult;
    }

    public void setRecordTestResult(MutableLiveData<RecordTest> recordTestResult) {
        this.recordTestResult = recordTestResult;
    }

    public LiveData<Integer> getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int index) {
        currentQuestionIndex.setValue(index);
    }

    public LiveData<Boolean> getInsertionResult() {
        return insertionResult;
    }

    public ExaminingViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void getAllQuestionInRoom(Room room) {
        compositeDisposable.add(apiService.getQuestions(room.getIdRoom(), room.getIdTest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<QuestionResponse>() {
                    @Override
                    public void onSuccess(QuestionResponse response) {
                        List<Question> questions = response.getQuestions();
                        if (questions != null && !questions.isEmpty()) {
                            questionLiveData.setValue(questions);
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


    public void createRecordTest(Room room, User user, String numQuestion) {
//        compositeDisposable.add(apiService.addRecordTest(room.getIdRoom(), room.getIdTest(), user.getIdClass(), user.getName(),"1")
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(() -> {
//                    insertionResult.setValue(true);
//                    Log.d("responseRecordTest", "responseRecordTest" );
//                }, throwable -> {
//                    // Handle network failure or other errors
//                    if (throwable instanceof HttpException) {
//                        ResponseBody errorBody = ((HttpException) throwable).response().errorBody();
//                        if (errorBody != null) {
//                            try {
//                                String errorResponse = errorBody.string();
//                                Log.e("response123", "Error Response: " + errorResponse);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        Log.e("response123", "Error: " + throwable.getMessage());
//                    }
//                    insertionResult.setValue(false);
//                }));
    }


    public void  updateQuestionToRecord(String questions,String idRoom, String idUser) {
        compositeDisposable.add(apiService.updateQuestionToRecord(questions,idRoom,idUser)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // Check if the response is successful
                            Log.d("server", response + "");
                            if (response.getMessage().equals("done")) {
                                // Handle the success response
                                insertionResult.setValue(true);
                                Log.d("updateQuestionToRecord", "updateQuestionToRecord successfully: " + response.getMessage());
                            } else {
                                // Handle the error response
                                insertionResult.setValue(false);
                                Log.e("updateQuestionToRecord", "Error occurred while updateQuestionToRecord: " + response.getMessage());
                            }
                        },
                        throwable -> {
                            // Handle the error response
                            insertionResult.setValue(false);
                            Log.e("updateQuestionToRecord", "Error occurred while updateQuestionToRecord", throwable);
                        }
                ));
    }

    public void updateRecordTest(RecordTest recordTest) {
        apiService.updateRecord(recordTest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateResponse>() {
                    @Override
                    public void onSuccess(UpdateResponse response) {
                        String recordTest = response.getMessage();
                        Log.d("messagerecordTest",recordTest+"");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("messagerecordTest", "Error: " + e.getMessage());
                    }
                });
    }

    public void getRecordTestUser(Room room, User user) {
        Log.d("getRecordTestUser",room.getIdRoom() + "|" + user.getIdStudent());
        apiService.getRecordTestByID(room.getIdRoom(), user.getIdStudent())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<RecordResponse>() {
                    @Override
                    public void onSuccess(RecordResponse response) {
                        RecordTest recordTest = response.getData();
                        if (recordTest != null) {
                            recordTestResult.setValue(recordTest);
                            Log.d("ResponseRecordTest", recordTest.getIdRoom() + "a");
                        } else {
                            Log.e("ResponseRecordTest", "Test list is null or empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("ResponseRecordTest", "Error: " + e.getMessage());
                    }
                });
    }
}

