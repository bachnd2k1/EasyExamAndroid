package com.practice.easyexam.app.view.account.editpass;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.data.local.UserDatabase;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.view.statistical.StatisticalViewModel;

public class EditPasswordActivity extends AppCompatActivity {

    private EditText etOldPassword,etNewPassword,etConfirmNewPassword;
    private EditPasswordViewModel viewModel;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);
        viewModel = new ViewModelProvider(EditPasswordActivity.this).get(EditPasswordViewModel.class);
        etOldPassword = findViewById(R.id.tietPassword);
        etNewPassword = findViewById(R.id.tietPasswordNewPass);
        etConfirmNewPassword = findViewById(R.id.tietPasswordConfirmNewPass);
        Button btnSavePassword = findViewById(R.id.btnChangePassword);
        MaterialToolbar toolbar = findViewById(R.id.tool_bar);
        SharedPref sharedPref = SharedPref.getInstance();
        user =  sharedPref.getUser(this);
        viewModel.getResponse().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(EditPasswordActivity.this,s,Toast.LENGTH_LONG).show();
                finish();
            }
        });
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String oldPassword = etOldPassword.getText().toString();
                String newPassword = etNewPassword.getText().toString();
                String confirmNewPassword = etConfirmNewPassword.getText().toString();

                if (!validateInput(oldPassword,newPassword,confirmNewPassword)) return;
                viewModel.updatePassword(oldPassword, newPassword, user.getIdStudent());
            }
        });


    }

    private void changePassword(String oldPassword,String newPassword) {

        User user = SharedPref.getInstance().getUser(this);
        if (!user.getPassword().equals(oldPassword)){
            Toast.makeText(this, "Please enter the right password", Toast.LENGTH_SHORT).show();
            return;
        }
        user.setPassword(newPassword);
        UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask(user);
        updatePasswordTask.execute();

    }

    private boolean validateInput(String oldPassword, String newPassword, String confirmNewPassword) {

        if (oldPassword.isEmpty()){
            Toast.makeText(this, getString(R.string.old_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (newPassword.isEmpty()){
            Toast.makeText(this, getString(R.string.old_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (confirmNewPassword.isEmpty()){
            Toast.makeText(this, getString(R.string.old_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!confirmNewPassword.equals(newPassword)){
            Toast.makeText(this, getString(R.string.password_must_be_same), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (oldPassword.equals(newPassword)){
            Toast.makeText(this, getString(R.string.new_password_must_be_different), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    class UpdatePasswordTask extends AsyncTask<Void, Void, Void> {

        private final User user;

        public UpdatePasswordTask(User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserDatabase databaseClient = UserDatabaseClient.getInstance(getApplicationContext());
            databaseClient.userDao().updateUser(user);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(EditPasswordActivity.this, "Password Updated Successfully!", Toast.LENGTH_SHORT).show();
            SharedPref.getInstance().setUser(EditPasswordActivity.this,user);
            finish();
        }
    }

}