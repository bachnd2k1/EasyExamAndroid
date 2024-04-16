package com.practice.easyexam.app.view.login;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.view.main.host.MainHostActivity;
import com.practice.easyexam.app.view.account.register.RegisterActivity;
import com.practice.easyexam.app.data.local.UserDatabase;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.view.main.user.MainUserActivity;
import com.practice.easyexam.app.view.main.user.MainUserTestActivity;


import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername, etPassword;
    private TextView tvSignUp;
    private Button btnLogin;
    private LoginViewModel loginViewModel;
    SharedPref sharedPref;

    @Override
    protected void onStart() {
        super.onStart();
        SharedPref sharedPref = SharedPref.getInstance();
        String token = sharedPref.getRefreshToken(this);
        if (!token.isEmpty()) {
            loginViewModel.authentication(token);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_EasyExam);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.tietUsername);
        etPassword = findViewById(R.id.tiePassword);
        tvSignUp = findViewById(R.id.tvSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        sharedPref = SharedPref.getInstance();
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

//        loginViewModel.getUserLiveData().observe(this, new Observer<List<User>>() {
//            @Override
//            public void onChanged(List<User> users) {
//                User user = users.stream()
//                        .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
//                        .findFirst()
//                        .orElse(null);
//
//                if (user == null) {
//                    Toast.makeText(LoginActivity.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SharedPref sharedPref = SharedPref.getInstance();
//                sharedPref.setUser(LoginActivity.this, user);
//
//                switch (user.getRole()) {
//                    case Constants.STUDENT:
//                        startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
//                        finish();
//                        break;
//                    case Constants.TEACHER:
//                        startActivity(new Intent(LoginActivity.this, MainHostActivity.class));
//                        finish();
//                        break;
//                    default:
//                        // Handle the case where the user's role is invalid.
//                }
//            }
//        });
//        loginViewModel.getIsLoginAgain().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(Boolean aBoolean) {
//                sharedPref.clearSharedPref(LoginActivity.this);
//            }
//        });

        loginViewModel.getAccessTokenLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String token) {
                if (token.isEmpty()) {
                    Toast.makeText(LoginActivity.this,R.string.invalid_user, Toast.LENGTH_SHORT).show();
                }
            }
        });

//        loginViewModel.getRoleLiveData().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(String role) {
//                switch (role) {
//                    case Constants.STUDENT:
//                        startActivity(new Intent(LoginActivity.this, MainUserActivity.class));
//                        finish();
//                        break;
//                    case Constants.TEACHER:
//                        startActivity(new Intent(LoginActivity.this, MainHostActivity.class));
//                        finish();
//                        break;
//                    default:
////                        Toast.makeText(LoginActivity.this, R.string.err, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        loginViewModel.getUserLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.getName().isEmpty()) {return;}
                sharedPref.setUser(LoginActivity.this,user);
                switch (user.getRole()) {
                    case Constants.STUDENT:
                        startActivity(new Intent(LoginActivity.this, MainUserTestActivity.class));
                        finish();
                        break;
                    case Constants.TEACHER:
                        startActivity(new Intent(LoginActivity.this, MainHostActivity.class));
                        finish();
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, R.string.err, Toast.LENGTH_SHORT).show();
                }
            }
        });


        loginViewModel.getRefreshTokenLiveData().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String token) {
                sharedPref.setRefreshToken(LoginActivity.this,token);
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (!validaInputs(email, password)) return;
                loginViewModel.login(email,password);
            }
        });


        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private boolean validaInputs(String username, String password) {
        if (username.isEmpty()) {
            Toast.makeText(this, getString(R.string.username_cannot_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, getString(R.string.password_cannot_empty), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}


