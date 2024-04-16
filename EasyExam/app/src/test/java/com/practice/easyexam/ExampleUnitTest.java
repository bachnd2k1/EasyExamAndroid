package com.practice.easyexam;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.easyexam.app.model.RecordTest;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.Question;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(MockitoJUnitRunner.class)
public class ExampleUnitTest {
    String json = "{\n" +
            "  \"name\": \"test1\",\n" +
            "  \"questionArrayList\": [\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"42\",\n" +
            "        \"44\",\n" +
            "        \"46\",\n" +
            "        \"48\"\n" +
            "      ],\n" +
            "      \"correctNum\": 4,\n" +
            "      \"question\": \"3 con gà đẻ 3 quả trứng trong 3 ngày. Vậy trong 12 ngày, 12 con gà đẻ được bao nhiêu quả trứng?\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"30\",\n" +
            "        \"36\",\n" +
            "        \"38\",\n" +
            "        \"42\"\n" +
            "      ],\n" +
            "      \"correctNum\": 4,\n" +
            "      \"question\": \"Có bao nhiêu chấm trên hai con xúc xắc?\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"29\",\n" +
            "        \"39\",\n" +
            "        \"49\",\n" +
            "        \"59\"\n" +
            "      ],\n" +
            "      \"correctNum\": 4,\n" +
            "      \"question\": \"Một người cho nước chảy vào bể chứa. Cứ mỗi phút, lượng nước trong bể lại tăng gấp đôi. Nước chảy trong một giờ thì đầy bể. Vậy nước chiếm nửa bể vào phút thứ mấy?\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"0\",\n" +
            "        \"1\",\n" +
            "        \"10\",\n" +
            "        \"100\"\n" +
            "      ],\n" +
            "      \"correctNum\": 4,\n" +
            "      \"question\": \"1/2 của 2/3 của 3/4 của 4/5 của 5/6 của 6/7 của 7/8 của 8/9 của 9/10 của 1.000 là bao nhiêu?\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"Muối\",\n" +
            "        \"Giấm\",\n" +
            "        \"Đường\",\n" +
            "        \"Chất tẩy trắng\"\n" +
            "      ],\n" +
            "      \"correctNum\": 1,\n" +
            "      \"question\": \"Tên thường gọi của Sodium Chloride là gì?\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"answers\": [\n" +
            "        \"Liverpool\",\n" +
            "        \"Arsenal\",\n" +
            "        \"Manchestercity\",\n" +
            "        \"Chelsea\"\n" +
            "      ],\n" +
            "      \"correctNum\": 2,\n" +
            "      \"question\": \"Đội bóng nào tại Premier League có biệt danh là “Những khẩu thần công”?\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    Test exam;
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    static {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }


    @Mock
    private Observer<List<RecordTest>> observer;
    private Map<String, Map<String, Boolean>> questionsAnswerMap;
    @Captor
    private ArgumentCaptor<List<RecordTest>> captor;
    @org.junit.Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @org.junit.Test
    public void testMapExam() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            exam = objectMapper.readValue(json, Test.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        questionsAnswerMap = getExam(exam);
        System.out.println(questionsAnswerMap);
    }

    @org.junit.Test
    public void testCast() {
        String json = "{\"id\":\"8c3b9fc6-1953-4beb-b282-4e858d3f8a2c\",\"name\":\"2\",\"questionArrayList\":[{\"correctNum\":3,\"expanded\":false,\"idQuestion\":\"89ea2b60-0dc2-459f-a427-cb3964fcff48\",\"idTest\":\"ecaca7a9-0d18-4289-a894-05c9ce3b93bd\",\"question\":\"123123213,12412412412,213123214,124124421142\"}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Test test = objectMapper.readValue(json, Test.class);
            System.out.println("Test ID: " + test.id);
            System.out.println("Test Name: " + test.getName());
            System.out.println("Number of Questions: " + test.getQuestionArrayList().size());
            System.out.println("First Question: " + test.getQuestionArrayList().get(0).getQuestion());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @org.junit.Test
    public void testWord() {
        String fileName = "sample.docx";
        ArrayList<Question> questions = new ArrayList<>();

        File file = new File(fileName);

        try (FileInputStream fis = new FileInputStream(file)) {
            XWPFDocument document = new XWPFDocument(fis);

            String questionText = null;
            ArrayList<String> answers = new ArrayList<>();

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                String text = paragraph.getText().trim();

                if (text.startsWith("Câu ")) {
                    // Save the previous question and its answers
                    if (questionText != null && !answers.isEmpty()) {
                        questions.add(new Question(questionText, answers,1));
                        answers.clear();
                    }
                    questionText = text.split(":")[1].trim();
                } else if (text.matches("^[A-D]\\.\\s.*")) {
                    String[] items = text.split("\\s*[A-Z]\\.\\s*");
//                    items = Arrays.copyOfRange(items, 1, items.length);
                    for (String item : items) {
                        answers.add(item);
                    }
                }
            }

            // Add the last question to the list
            if (questionText != null && !answers.isEmpty()) {
                questions.add(new Question(questionText, answers,1));
            }

            exam = new Test();
            exam.setQuestionArrayList(questions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @org.junit.Test
    public void testJson() {
//        AtomicReference<String> fileName = new AtomicReference<>("sample.docx");
//        Utils.checkUrl("https://cdn.pixabay.com/photo/2014/09/08/09/24/solar-system-439046_1280.jpg",isPng -> {
//            if (isPng) {
//                 fileName.set("sample.docx" + isPng);
//                System.out.println("fileName " + isPng);
//            } else {
//                fileName.set("sample.docx1" + isPng + "a");
//                System.out.println("fileName " + isPng);
//            }
//        });
    }

//    @org.junit.Test
//    public void testMap() {
//        StatisticalViewModel viewModel = new StatisticalViewModel();
//
//        // Observe LiveData using the mock Observer
//        viewModel.getRecordTestLiveData().observeForever(observer);
//
//        // Trigger the method that updates LiveData
//        viewModel.getRecordTestInRoom("BB351SOP");
//
//        // Wait for the LiveData to be updated (you might need to adjust the timeout)
//        try {
//            Thread.sleep(1000); // 1 second timeout
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Verify that onChanged was called and capture the argument
//        Mockito.verify(observer).onChanged(captor.capture());
//        List<RecordTest> capturedList = captor.getValue();
//        Log.d("Data",capturedList.size()+"");
//    }

    public  Map<String, Map<String,Boolean>> getExam(Test exam) {
        HashMap<String,Map<String,Boolean>> questions = new HashMap<>();
        for (int i = 0; i < exam.getQuestionArrayList().size(); i++) {
            Question question = exam.getQuestionArrayList().get(i);
            HashMap<String,Boolean> answer = new HashMap<>();;
            for (int j = 0; j< question.getAnswers().size(); j++) {
                if (j == question.getCorrectNum() - 1 ) {
                    answer.put(question.getAnswers().get(j),true);
                } else {
                    answer.put(question.getAnswers().get(j),false);
                }
            }
            questions.put(question.getQuestion(),answer);
        }
        return questions;
    }
}

