package com.practice.easyexam.app.view.history.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Attempt;
import com.practice.easyexam.app.data.local.UserDatabase;
import com.practice.easyexam.app.data.local.UserDatabaseClient;
import com.practice.easyexam.app.data.local.UserWithAttempts;
import com.practice.easyexam.app.data.local.SharedPref;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private List<UserWithAttempts> userWithAttempts;
    ArrayList<Attempt> attempts = new ArrayList<>();
    private TextView tvTotalPoints, tvTotalQuestions;
    String email = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rvHistory = findViewById(R.id.rvHistory);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestionsHistory);
        tvTotalPoints = findViewById(R.id.tvOverAllPointsHistory);

        findViewById(R.id.imageViewHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        email = SharedPref.getInstance().getUser(this).getEmail();
        GetAllUserAttemptTask getAllUserAttemptTask = new GetAllUserAttemptTask(email);
        getAllUserAttemptTask.execute();
    }


    class GetAllUserAttemptTask extends AsyncTask<Void, Void, Void> {

        private final String email;
        ArrayList<Attempt> attempts = new ArrayList<>();

        public GetAllUserAttemptTask(String email) {
            this.email = email;
        }

        public ArrayList<Attempt> getAttempts() {
            return attempts;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            UserDatabase databaseClient = UserDatabaseClient.getInstance(getApplicationContext());
            attempts = (ArrayList<Attempt>) databaseClient.userDao().getUserAndAttemptsWithSameEmail(email);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            int overallPoints = 0;

            for (Attempt userWithAttempts : attempts) {
                overallPoints += userWithAttempts.getEarned();
            }

            tvTotalQuestions.setText(String.valueOf(attempts.size()));
            tvTotalPoints.setText(String.valueOf(overallPoints));

            Collections.sort(attempts, new AttemptCreatedTimeComparator());

//            HistoryAdapter adapter = new HistoryAdapter(attempts);
//            adapter.setOnClickItemHistory(new onClickItemHistory() {
//                @Override
//                public void onClickItem(String name) {
//                    QueryExamTask queryExamTask = new QueryExamTask(name,getApplicationContext());
//                    queryExamTask.execute();
//
////                    ExamDatabase databaseClient = UserDatabaseClient.getInstanceExam(HistoryActivity.this);
////                    Exam exam = databaseClient.examDao().getExam(name);
////                    if (exam != null) {
////                        Log.d("exam",exam.getName() );
////                    }
////                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
////                    Intent intent = new Intent(this, ExamActivity.class);
////                    intent.putExtra("jsonQrCode", json);
////                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    context.startActivity(intent);
//                }
//            });
//            rvHistory.setAdapter(adapter);
        }
    }

    public class AttemptCreatedTimeComparator implements Comparator<Attempt> {

        @Override
        public int compare(Attempt attempt, Attempt t1) {
            return String.valueOf(t1.getCreatedTime()).compareTo(String.valueOf(attempt.getCreatedTime()));
        }
    }


}