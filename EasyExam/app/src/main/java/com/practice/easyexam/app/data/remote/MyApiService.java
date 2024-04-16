package com.practice.easyexam.app.data.remote;

import com.google.gson.JsonObject;
import com.practice.easyexam.app.model.ApiResponse;
import com.practice.easyexam.app.model.ListQuestionResponse;
import com.practice.easyexam.app.model.LoginResponse;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.model.QuestionResponse;
import com.practice.easyexam.app.model.RecordResponse;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.RecordUserResponse;
import com.practice.easyexam.app.model.RecordsResponse;
import com.practice.easyexam.app.model.ResponseServer;
import com.practice.easyexam.app.model.Room;
import com.practice.easyexam.app.model.RoomResponse;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.TestResponse;
import com.practice.easyexam.app.model.UpdateResponse;
import com.practice.easyexam.app.model.UserResponse;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MyApiService {
    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Test/addTest.php")
    Completable addTest(
            @Field("id") String id,
            @Field("name") String name,
            @Field("createDate") String createDate,
            @Field("idCreate") String idCreate
    );



    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Test/updateTest.php")
    Single<com.practice.easyexam.app.model.Response> updateTest(
            @Field("id") String id,
            @Field("name") String name,
            @Field("createDate") String createDate
    );


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Room/saveNumberInRoom.php")
    Single<UpdateResponse> saveNumInRoom(
            @Field("idRoom") String id,
            @Field("currentNumUser") int userCount,
            @Field("startTime") String startTime
    );




    //    Completable updateTest(
//            @Field("id") String id,
//            @Field("name") String name,
//            @Field("createDate") String createDate
//    );



    @Headers({"Accept: application/json"})
    @POST("Question/addQuestion.php")
    Completable addQuestionToTest(@Body ArrayList<Question> questions);

    @Headers({"Accept: application/json"})
    @POST("Question/addQuestion.php")
    Single<ListQuestionResponse> addQuestion(@Body ArrayList<Question> questions);


    @Headers({"Accept: application/json"})
    @POST("Question/addQuestion1.php")
    Single<ListQuestionResponse> addQuestion1(@Body Test test);

    @Headers({"Accept: application/json"})
    @POST("Room/addRoom.php")
    Observable<Response<ResponseBody>> insertRoom(@Body Room room);

    @FormUrlEncoded
    @Headers({"Accept: application/json", "Content-Type: application/x-www-form-urlencoded"})
    @POST("RecordTest/addRecordTest.php")
    Observable<com.practice.easyexam.app.model.Response> addRecordTest(
            @Field("idRoom") String idRoom,
            @Field("idQuestion") String idQuestion,
            @Field("idStudent") String idStudent,
            @Field("nameStudent") String nameStudent,
            @Field("currentQuestion") String numberQuestion,
            @Field("answer") String answer
    );

    @FormUrlEncoded
    @Headers({"Accept: application/json", "Content-Type: application/x-www-form-urlencoded"})
    @POST("RecordTest/addQuestionToRecord.php")
    Observable<com.practice.easyexam.app.model.Response> updateQuestionToRecord(
            @Field("questions") String questions,
            @Field("idRoom") String idRoom,
            @Field("idStudent") String idStudent
    );


//    @FormUrlEncoded
////    @Headers({"Accept: application/json"})
////    @POST("RecordTest/updateRecordTest.php")
////    Completable updateRecordTest(
////            @Field("idRoom") String idRoom,
////            @Field("idStudent") String idStudent,
////            @Field("point") String point,
////            @Field("time") String time,
////            @Field("correctQuestion") String correctQuestion,
////            @Field("idStudent") String currentQuestion,
////            @Field("idStudent") String answer
////    );

    @POST("RecordTest/updateRecordTest.php")
    @Headers({"Accept: application/json"})
    Single<UpdateResponse> updateRecord(@Body RecordTest recordTest);




    @FormUrlEncoded
    @POST("RecordTest/getRecordTestUser.php")
    Single<RecordResponse> getRecordTestByID(
            @Field("idRoom") String idRoom,
            @Field("idStudent") String idStudent
    );


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Question/getQuestionByIDTest.php")
    Single<QuestionResponse> getQuestionByIDTest(
            @Field("idTest") String idTest
    );

    @DELETE("Room/deleteRoomByID.php")
    Single<ApiResponse> deleteRoom(@Query("idRoom") String idRoom);


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Test/getAllTestByIDUser.php")
    Single<TestResponse> getAllTestByIdCreate(
            @Field("idCreate") String idCreate
    );


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Test/getTestByIDExam.php")
    Single<QuestionResponse> getQuestions(
            @Field("idRoom") String idRoom,
            @Field("idTest") String idTest
    );


    @Headers({"Accept: application/json"})
    @GET("User/getUsers.php")
    Single<UserResponse> getUsers();

    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("User/login.php")
    Single<LoginResponse> login (
            @Field("email")  String email,
            @Field("password")  String password);


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("Room/getRoomByID.php")
    Single<ApiResponse<Room>> getRoom(
            @Field("idRoom") String idRoom,
            @Field("idStudent") String idStudent
    );

    @GET("Room/getNumUserByIDRoom.php")
    Single<JsonObject> getNumUserByIDRoom(@Query("idRoom") String idRoom);

    @GET("Room/getStateByIDRoom.php")
    Single<JsonObject> getState(@Query("idRoom") String idRoom);


    @GET("Room/getNumUserByIDRoom.php")
    Single<JsonObject> getCurrentNumberInRoom(@Query("idRoom") String idRoom);

    @GET("Room/updateNumberInRoom.php")
    Single<JsonObject> updateNumInRoom(@Query("idRoom") String idRoom);


    @FormUrlEncoded
    @Headers({"Accept: application/json"})
    @POST("RecordTest/getAllRecordTest.php")
    Single<RecordsResponse> getRecordTestsInRoom(@Field("idRoom") String idRoom);

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("Room/updateStateRoom.php")
    Observable<ResponseServer<Room>> updateState(
            @Field("idRoom") String idRoom,
            @Field("state") String state);


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("Room/getRoomsFinished.php")
    Single<RoomResponse> getRooms(@Field("idCreateUser") String idCreateUser);

    @FormUrlEncoded
    @POST("RecordTest/getAllRecordOfUser.php")
    Single<RecordUserResponse> getRecordsOfUser(@Field("idStudent") String idStudent);

    @GET("Room/getPermissionView.php")
    Single<ApiResponse<Integer>> getPermissionView(@Query("idRoom") String idRoom);


    @FormUrlEncoded
    @POST("Room/setPermissionView.php")
    Single<ApiResponse<String>> setPermissionView(@Field("idRoom") String idRoom,@Field("isViewed") int isViewed );

    @FormUrlEncoded
    @POST("User/updatePassword.php")
    Single<com.practice.easyexam.app.model.Response> updatePassword(
            @Field("currentPassword") String currentPassword,
            @Field("newPassword") String newPassword,
            @Field("userId") String userId
    );


}
