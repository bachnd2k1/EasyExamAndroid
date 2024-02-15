package com.practice.easyexam.app.view.statistical;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.ApiResponse;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.RecordsResponse;
import com.practice.easyexam.app.model.Room;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StatisticalViewModel extends ViewModel {
    private MyApiService apiService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<RecordTest>> recordTestLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> permissionLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> setPermissionLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String, Map<String, Integer>>> answerCountMap = new MutableLiveData<>();
    private MutableLiveData<Double> accuracyAvg = new MutableLiveData<>();
    private MutableLiveData<List<Question>> questionLiveData = new MutableLiveData<>();

    private MutableLiveData<String> numUserLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Question>> questionListLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> getSetPermissionLiveData() {
        return setPermissionLiveData;
    }

    public MutableLiveData<Double> getAccuracyAvg() {
        return accuracyAvg;
    }

    public void setAccuracyAvg(Double value) {
        accuracyAvg.setValue(value);
    }

    public MutableLiveData<String> getNumUserLiveData() {
        return numUserLiveData;
    }

    public MutableLiveData<Integer> getPermissionLiveData() {
        return permissionLiveData;
    }

    public MutableLiveData<Map<String, Map<String, Integer>>> getAnswerCountMap() {
        return answerCountMap;
    }

    public MutableLiveData<List<Question>> getQuestionLiveData() {
        return questionLiveData;
    }

    public MutableLiveData<List<RecordTest>> getRecordTestLiveData() {
        return recordTestLiveData;
    }

    public MutableLiveData<List<Question>> getQuestionListLiveData() {
        return questionListLiveData;
    }

    public StatisticalViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void getRecordTestInRoom(String idRoom) {
        apiService.getRecordTestsInRoom(idRoom).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<RecordsResponse>() {
            @Override
            public void onSuccess(RecordsResponse response) {
                List<RecordTest> recordTests = response.getData();
                if (recordTests != null && !recordTests.isEmpty()) {
                    Log.d("ResponseList", "Test" + recordTests.size());
                    recordTestLiveData.setValue(recordTests);
                    calculateNumberAnswer(recordTests);
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

    public void getPermissionView(String idRoom) {
        apiService.getPermissionView(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse<Integer>>() {
            @Override
            public void onSuccess(ApiResponse<Integer> response) {
                permissionLiveData.setValue(response.getData());
            }

            @Override
            public void onError(Throwable e) {
                Log.e("Response", "Error: " + e.getMessage());
            }
        });
    }

    public void setPermissionView(String idRoom,int isViewed) {
        apiService.setPermissionView(idRoom,isViewed)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse<String>>() {
                    @Override
                    public void onSuccess(ApiResponse<String> response) {
//                        permissionLiveData.setValue(response.getData());
                        permissionLiveData.setValue(isViewed);
                        Log.d("ResponsePermissionView", response.getMessage());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                });
    }




    public void getAllQuestionInRoom(Room room) {
        compositeDisposable.add(apiService.getQuestions(room.getIdRoom(), room.getIdTest()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<QuestionResponse>() {
            @Override
            public void onSuccess(QuestionResponse response) {
                List<Question> questions = response.getQuestions();
                for (Question question : questions) {
                    Log.d("QuestionResponse", question.getQuestion() + question.getAnswers());
                }
                if (questions != null && !questions.isEmpty()) {
                    questionLiveData.setValue(questions);
                } else {
                    Log.d("QuestionResponse", "Test list is null or empty.");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("QuestionResponse", "Error: " + e.getMessage());
            }
        }));
    }


    public void calculateNumberAnswer(List<RecordTest> recordTests) {
        Map<String, Map<String, Integer>> answerSelectionCountMap = getAnswerSelectionCountMap(recordTests);

        for (Map.Entry<String, Map<String, Integer>> entry : answerSelectionCountMap.entrySet()) {
            String questionId = entry.getKey();
            Map<String, Integer> innerMapValues = entry.getValue();

            Log.d("AnswerSelectionCountMap", "Question ID: " + questionId);

            innerMapValues.forEach((answer, count) ->
                    Log.d("AnswerSelectionCountMap", "  Answer: " + answer + ", Count: " + count));
        }
        answerCountMap.setValue(answerSelectionCountMap);
    }



    private Map<String, Map<String, Integer>> getAnswerSelectionCountMap(List<RecordTest> recordTests) {
        RecordTest firstRecord = recordTests.stream().findFirst().orElse(null);
        Map<String, Map<String, Integer>> answerSelectionCountMap = getMap(firstRecord);

        for (RecordTest recordTest : recordTests) {
            ArrayList<Question> questionList = parseQuestions(recordTest);

            List<Integer> answers = Arrays.stream(recordTest.getAnswer().split("\\s*,\\s*"))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            for (int i = 0; i < questionList.size(); i++) {
                Question question = questionList.get(i);
                Map<String, Integer> innerMap = answerSelectionCountMap.get(question.getIdQuestion());
                int index = answers.get(i);
                String answer = question.getAnswers().get(index);
                innerMap.compute(answer, (existingAnswer, count) -> (count == null) ? 0 : count + 1);
            }
        }

        return answerSelectionCountMap;
    }

    private Map<String, Map<String, Integer>> getMap(RecordTest recordTest) {
        Map<String, Map<String, Integer>> answerSelectionCountMap = new HashMap<>();
        ArrayList<Question> questionList = parseQuestions(recordTest);

        for (Question question : questionList) {
            System.out.println("Question ID: " + question.getIdQuestion());
            Map<String, Integer> innerMap = new HashMap<>();

            question.getAnswers().forEach(answer ->
                    innerMap.compute(answer, (existingAnswer, count) -> (count == null) ? 0 : count + 1));

            answerSelectionCountMap.put(question.getIdQuestion(), innerMap);
        }

        return answerSelectionCountMap;
    }

    private ArrayList<Question> parseQuestions(RecordTest recordTest) {
        String questionsJson = recordTest.questions;
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<Question>>() {}.getType();
        return gson.fromJson(questionsJson, listType);
    }


    public void getQuestionsByIDTest(String idtest) {
        Log.d("idtest", idtest + "");
        compositeDisposable.add(apiService.getQuestionByIDTest(idtest).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableSingleObserver<QuestionResponse>() {
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


    public void getMaxNumberUserInRoom(String idRoom) {
        apiService.getNumUserByIDRoom(idRoom).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<JsonObject>() {
            @Override
            public void onSubscribe(Disposable d) {
                // Handle subscription
            }

            @Override
            public void onSuccess(JsonObject response) {
                Log.d("responseUPDATE", response.toString());
                String message = response.get("message").getAsString();
                if (message.equals("done")) {
                    numUserLiveData.setValue(response.get("currentNumUser").getAsString());
                } else {
                    numUserLiveData.setValue("0");
                }
            }

            @Override
            public void onError(Throwable e) {
                // Handle error
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
