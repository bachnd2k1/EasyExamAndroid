package com.practice.easyexam.app.view.waiting;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.ApiResponse;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.UpdateResponse;
import com.practice.easyexam.app.model.User;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WaitingViewModel extends ViewModel {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<Room> roomMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Question>> questionLiveData = new MutableLiveData<>();

    private MutableLiveData<String> stateLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> numUserLiveData = new MutableLiveData<>();

    private MutableLiveData<Boolean> insertionResult = new MutableLiveData<>();

    private MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private MyApiService apiService;
    private List<Question> questions = new ArrayList<>();


    public List<Question> getQuestions() {
        return questions;
    }

    public LiveData<Boolean> getInsertionResult() {
        return insertionResult;
    }


    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public MutableLiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public LiveData<List<Question>> getQuestionLiveData() {
        return questionLiveData;
    }

    public LiveData<Room> getRoomLiveData() {
        return roomMutableLiveData;
    }


    public LiveData<String> getStateLiveData() {
        return stateLiveData;
    }

    public LiveData<Integer> getNumberInUser() {
        return numUserLiveData;
    }


    public WaitingViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public Bitmap generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 512, 512, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            return qrBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void getAllQuestionInRoom(Room room) {
        compositeDisposable.add(apiService.getQuestions(room.getIdRoom(), room.getIdTest())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<QuestionResponse>() {
                    @Override
                    public void onSuccess(QuestionResponse response) {
                        List<Question> questions = response.getQuestions();
                        for (Question question : questions) {
                            Log.d("QuestionResponse", question.getQuestion() + question.getAnswers());
                        }
                        if (questions != null && !questions.isEmpty()) {
                            questionLiveData.setValue(questions);
                            setQuestions(questions);
                        } else {
                            Log.e("QuestionResponse", "Test list is null or empty.");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("QuestionResponse", "Error: " + e.getMessage());
                    }
                }));
    }

    public void deleteRoom(String idRoom) {
        apiService.deleteRoom(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ApiResponse>() {
                    @Override
                    public void onSuccess(@NonNull ApiResponse apiResponse) {
                        if (apiResponse.getMessage().equals("done")) {
                            Log.d("ApiResponse","success");
                        } else {
                            Log.d("ApiResponse","fail");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d("ApiResponse","error");
                    }
                });
    }


    public void startRoom(String idRoom) {
        compositeDisposable.add(apiService.updateState(idRoom, Utils.STATE_EXAMINING)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            String s = response.getMessage();
                            stateLiveData.setValue(Utils.STATE_EXAMINING);
                            Log.d("responseState", s);
                        },
                        throwable -> {
                            Log.e("response", "Error occurred while update room", throwable);
                        }
                ));
    }

    public void createRecordTest(Room room, User user, String answer) {
        compositeDisposable.add(apiService.addRecordTest(room.getIdRoom(), room.getIdTest(), user.getIdStudent(), user.getName(), "1",answer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            // Check if the response is successful
                            Log.d("server", response + "");
                            if (response.getMessage().equals("done")) {
                                // Handle the success response
                                insertionResult.setValue(true);
                                Log.d("responseRecordTest", "responseRecordTest inserted successfully: " + response.getMessage());
                            } else {
                                // Handle the error response
                                insertionResult.setValue(false);
                                Log.e("responseRecordTest", "Error occurred while inserting data: " + response.getMessage());
                            }
                        },
                        throwable -> {
                            // Handle the error response
                            insertionResult.setValue(false);
                            Log.e("responseRecordTest", "Error occurred while inserting data", throwable);
                        }
                ));
    }


    public void getStateRoom(String idRoom) {
        apiService.getState(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Handle subscription
                    }

                    @Override
                    public void onSuccess(JsonObject response) {
                        Log.d("responseUPDATE", response.toString());
                        if (response.has("state")) {
                            String state = response.get("state").getAsString();
                            String message = response.get("message").getAsString();
                            if (message.equals(Utils.RESPONSE_DONE)) {
                                stateLiveData.setValue(state);
                            }
                        } else if (response.has("error")) {
                            String error = response.get("error").getAsString();
                            stateLiveData.setValue("");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle error
                        e.printStackTrace();
                    }
                });
    }

    public  void saveNumUserInRoom(Room room) {
        apiService.saveNumInRoom(room.getIdRoom(),room.getCurrentNumUser(),room.getStartTime())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UpdateResponse>() {
                    @Override
                    public void onSuccess(UpdateResponse response) {
                        if (response.getMessage().equals("done")) {
                            updateResult.setValue(true);
                        } else {
                            updateResult.setValue(true);
                        }
                        Log.d("updateExamResponse", response.getMessage() + "response");
                    }

                    @Override
                    public void onError(Throwable e) {
                        updateResult.setValue(false);
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                });
    }


    public void getCurrentNumberInRoom(String idRoom) {
        apiService.getCurrentNumberInRoom(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Handle subscription
                    }

                    @Override
                    public void onSuccess(JsonObject response) {
                        Log.d("responseNumUPDATE", response.toString());
                        if (response.has("currentNumUser")) {
                            int number = response.get("currentNumUser").getAsInt();
                            String message = response.get("message").getAsString();
                            if (message.equals(Utils.RESPONSE_DONE)) {
                                numUserLiveData.setValue(number);
                            }
                        } else if (response.has("error")) {
                            String error = response.get("error").getAsString();
                            numUserLiveData.setValue(-1);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle error
                        e.printStackTrace();
                    }
                });
    }

    public void updateNumberInRoom(String idRoom) {
        apiService.updateNumInRoom(idRoom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<JsonObject>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        // Handle subscription
                    }

                    @Override
                    public void onSuccess(JsonObject response) {
                        Log.d("responseNum", response.toString());
//                        if (response.has("currentNumUser")) {
//                            int number = response.get("currentNumUser").getAsInt();
//                            String message = response.get("message").getAsString();
//                            if (message.equals(Utils.RESPONSE_DONE)) {
//                                numUserLiveData.setValue(number);
//                            }
//                        } else if (response.has("error")) {
//                            String error = response.get("error").getAsString();
//                            numUserLiveData.setValue(-1);
//                        }
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
