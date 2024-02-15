package com.practice.easyexam.app.view.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.easyexam.app.data.remote.MyApiService;
import com.practice.easyexam.app.data.remote.RetrofitClient;
import com.practice.easyexam.app.model.LoginResponse;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.model.UserResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<List<User>> userMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<String> accessTokenLiveData = new MutableLiveData<>();
    private MutableLiveData<String> refreshTokenLiveData = new MutableLiveData<>();
//    private MutableLiveData<String> roleLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoginAgain = new MutableLiveData<>();
    private MyApiService apiService;

    public MutableLiveData<String> getRefreshTokenLiveData() {
        return refreshTokenLiveData;
    }

    public MutableLiveData<Boolean> getIsLoginAgain() {
        return isLoginAgain;
    }

    public void setIsLoginAgain(MutableLiveData<Boolean> isLoginAgain) {
        this.isLoginAgain = isLoginAgain;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

//    public MutableLiveData<String> getRoleLiveData() {
//        return roleLiveData;
//    }


    public MutableLiveData<List<User>> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<String> getAccessTokenLiveData() {
        return accessTokenLiveData;
    }


    public LoginViewModel() {
        apiService = RetrofitClient.getInstance().create(MyApiService.class);
    }

    public void fetchAllUser() {
        compositeDisposable.add(apiService.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserResponse>() {
                    @Override
                    public void onSuccess(UserResponse response) {

                        if (response.isSuccess()) {
                            userMutableLiveData.setValue(response.getUsers());
                        } else {
                            userMutableLiveData.setValue(new ArrayList<User>());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Response", "Error: " + e.getMessage());
                    }
                }));
    }

    public void login(String email, String password) {
        apiService.login(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        Log.d("Response", "response: " + response.getAccessToken() + "|"+response.getRefreshToken());
                        accessTokenLiveData.setValue(response.getAccessToken());
                        if (response.isSuccess()) {
                            refreshTokenLiveData.setValue(response.getRefreshToken());
                            userLiveData.setValue(getUserFromToken(response.getAccessToken()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        accessTokenLiveData.setValue("");
                    }
                });
    }

    public void authentication(String token) {
        if (isTokenExpired(token)) {
            isLoginAgain.setValue(true);
        } else {
            isLoginAgain.setValue(false);
            userLiveData.setValue(getUserFromToken(token));
        }
    }

    public  User getUserFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return getUserDefault() ;
        }
        try {
            Algorithm algorithm = Algorithm.HMAC256("key");
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            User user = new User(
                    jwt.getClaim("name").asString(),
                    jwt.getClaim("email").asString(),
                    jwt.getClaim("password").asString(),
                    jwt.getClaim("idClass").asString(),
                    jwt.getClaim("role").asString(),
                    jwt.getClaim("idStudent").asString()
            );
            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return getUserDefault();
        }
    }

    private boolean isTokenExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return  jwt.getExpiresAt().before(new Date());
    }

    private User getUserDefault() {
        return new User("","","","","","");
    }

}
