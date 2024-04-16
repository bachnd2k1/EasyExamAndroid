package com.practice.easyexam.app.view.editexam;

import static com.practice.easyexam.app.view.editexam.CreateExamNormalActivity.CODE_NUMBER;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.practice.easyexam.R;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.model.Question;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateQuestionActivity extends AppCompatActivity {
    public static String CODE_QUESTION = "QUESTION";
    public static String CODE_EDIT = "EDIT";
    public static final String URI_KEY = "URI_KEY";
    TextView btnSave;
    RecyclerView rvQuestion;
    QuestionAdapter questionAdapter;
    Question question;

    int position = 1;

    private int PICK_IMAGE_REQUEST = 1;
    int index = -1 ;
    private Uri filePath;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);
        btnSave = findViewById(R.id.btnSave);
        rvQuestion = findViewById(R.id.rvQuestion);
        rvQuestion.setNestedScrollingEnabled(false);
        questionAdapter = new QuestionAdapter(this);
        questionAdapter.setSelectImage(new QuestionAdapter.SelectImage() {
            @Override
            public void onSelectImage() {
                showFileChooser();
            }
        });
        rvQuestion.setLayoutManager(new LinearLayoutManager(this));
        rvQuestion.setAdapter(questionAdapter);
        if (getIntent().getExtras() != null) {
            question = (Question) getIntent().getSerializableExtra("object");
            Log.d("QUESTION_PASS",question.toString());
            index = getIntent().getIntExtra("index", -1);
            questionAdapter.setQues(question);
            if (question.getImage() != null) {
                Bitmap b = Utils.getBitmapFromString(question.getImage());
                questionAdapter.setImage(b);
            }
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get all text from the InputAnswerAdapter through the QuestionAdapter
                String allText = questionAdapter.getAllTextFromInputAnswers();
                List<String> answer = new ArrayList<>(Arrays.asList(allText.split("\\s+")));
                String ques = questionAdapter.getQuestion();
                int correctAns = questionAdapter.getCorrectAns();
                Bitmap bitmap1 = questionAdapter.getImageBitmap();
                if (!ques.isEmpty() || answer.size() < 3) {
                    if (question == null) {
                        question = new Question(ques, (ArrayList<String>) answer,correctAns);
                    } else {
//                        question.setIdQuestion();
                        question.setQuestion(ques);
                        question.setAnswers((ArrayList<String>) answer);
                        question.setCorrectNum(correctAns);
                    }
//                    if (bitmap1 != null) {
//                        String image = getStringImage(bitmap1);
//                        Log.d("B64string",image + "");
//                        question.setImage(image);
//                    }
                    Log.d("QUESTION_PASS_BACK",question.toString());
                    final Intent data = new Intent();
                    Bundle bundle = new Bundle();
                    if (index < 0) {
                        bundle.putSerializable(CODE_QUESTION,question);
                    } else {
                        bundle.putSerializable(CODE_EDIT,question);
                        bundle.putInt(CODE_NUMBER,index);
                        Log.d("position_normal",index +"");
                    }
                    if (filePath != null) {
                        bundle.putParcelable(URI_KEY, filePath);
                    }
                    data.putExtras(bundle);
                    setResult(Activity.RESULT_OK, data);
                    finish();
                } else {
                    Toast.makeText(CreateQuestionActivity.this, R.string.warning_empty, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                int newHeight = 300; // Set your desired height
                int compressionQuality = 50; // Set your desired compression quality (0-100)
                int screenWidth = getScreenWidth();
                // Set newWidth to screen width - 20
                int newWidth = screenWidth - 15;
                String b64Image = Utils.getStringImage(bitmap);
                String compress = Utils.compressAndResizeImage(b64Image, newWidth, newHeight, compressionQuality);
                if (questionAdapter != null) {
                    questionAdapter.setImage(Utils.decodeBase64String(compress));
                }
//                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}