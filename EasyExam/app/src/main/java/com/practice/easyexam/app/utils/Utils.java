package com.practice.easyexam.app.utils;

import static java.util.Locale.getDefault;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Question;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Utils {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ID_LENGTH = 8;

    public static ArrayList<Question> listQuestion = new ArrayList<>();
    public static String RESPONSE_DONE = "done";
    public static String RESPONSE_ERROR = "ERROR";
    public static String STATE_WAITING = "Waiting";
    public static String STATE_EXAMINING = "Examining";
    public static String STATE_FINISHED = "Finished";
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String formatDate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatter.format(calendar.getTime());
    }

    public static void showToast(Context context, int value) {
        Toast.makeText(context,value,Toast.LENGTH_LONG).show();
    }

    public static ArrayList<Question> convertToArrayList(String jsonString) {
        // Create a Gson object
        Gson gson = new Gson();

        // Define the type for the ArrayList using TypeToken
        Type questionListType = new TypeToken<ArrayList<Question>>() {}.getType();

        // Convert JSON string to ArrayList<Question>
        return gson.fromJson(jsonString, questionListType);
    }

    public static Bitmap getBitmapFromString(String encodedImage) {
        byte[] decodedBytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return formatter.format(date);
    }

    public static String compressAndResizeImage(String base64Image, int newWidth, int newHeight, int compressionQuality) {
        try {
            // Decode Base64 string into byte array
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

            // Create Bitmap from byte array
            Bitmap originalBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            // Resize the image
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);

            // Compress the image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, compressionQuality, outputStream);

            // Encode the compressed and resized image back to Base64 string
            byte[] compressedBytes = outputStream.toByteArray();
            return Base64.encodeToString(compressedBytes, Base64.DEFAULT);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static Bitmap decodeBase64String(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }


    public static String[] getCharArr() {
        String[] alphabetArray = new String[26];

        // Fill the array with alphabet characters
        char currentChar = 'A';
        for (int i = 0; i < 26; i++) {
            alphabetArray[i] = String.valueOf(currentChar);
            currentChar++;
        }
        return alphabetArray;
    }

    public static String generateRandomID() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        Random random = new Random();

        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static boolean isImageUrl(String urlString) {
        // Define regular expression patterns for PNG and JPEG image URLs
        String pngPattern = ".*\\.png$";
        String jpegPattern = ".*\\.jpe?g$";

        // Check if the URL matches either the PNG or JPEG pattern
        return urlString.matches(pngPattern) || urlString.matches(jpegPattern);
    }

    public static void checkUrl(String imageUrl, UrlCheckListener listener) {
        new Thread(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();

                String contentType = connection.getContentType();
                boolean isImage = contentType != null && (contentType.equals("image/png") || contentType.equals("image/jpeg"));

                // Use a Handler to post the result back to the main thread
                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onUrlCheckResult(isImage);
                });
            } catch (IOException e) {
                e.printStackTrace();
                // Handle the exception, e.g., notify the listener about the failure
                new Handler(Looper.getMainLooper()).post(() -> {
                    listener.onUrlCheckResult(false);
                });
            }
        }).start();
    }

    public interface UrlCheckListener {
        void onUrlCheckResult(boolean isImage);
    }





    public static Map<String,String> getMathQuestions(){
        HashMap<String,String> questions = new HashMap<>();
        questions.put("1+1","2");
        questions.put("2+2","4");
        questions.put("3+3","6");
        questions.put("4+4","8");
        questions.put("5+5","10");
        questions.put("6+6","12");
        questions.put("7+7","14");
        questions.put("8+8","16");
        questions.put("9+9","18");
        questions.put("10+10","20");
        questions.put("11+11","22");
        questions.put("12+12","24");
        questions.put("13+13","26");
        questions.put("14+14","28");
        questions.put("15+15","30");

        return questions;
    }

    public static Map<String,String> getRandomMathQuestions(int SIZE){
        HashMap<String,String> questionsMap = new HashMap<>();
        Map<String,String> originalQuestion = getMathQuestions();
        int originalSize =  originalQuestion.size();
        ArrayList<String> keyList = new ArrayList<String>(originalQuestion.keySet());

        while (questionsMap.size()<=SIZE){
            Random random = new Random();
            int randomNumber = random.nextInt(originalSize);
            String question = keyList.get(randomNumber);
            if (!questionsMap.containsKey(question)){
                questionsMap.put(question,originalQuestion.get(question));
            }
        }
        return questionsMap;
    }




    public static Map<String,Map<String,Boolean>> getLiteratureQuestions(){
        HashMap<String,Map<String,Boolean>> questions = new HashMap<>();

        HashMap<String,Boolean> answer1 = new HashMap<>();
        answer1.put("Maya Angelou",true);
        answer1.put("Robert Hass",false);
        answer1.put("Jessica Hagdorn",false);
        answer1.put("Micheal Palmer",false);
        questions.put("Which American writer published ‘A brave and startling truth’ in 1996",answer1);

        HashMap<String,Boolean> answer2 = new HashMap<>();
        answer2.put("Acrostic ",true);
        answer2.put("Haiku",false);
        answer2.put("Epic",false);
        answer2.put("Alliterative",false);
        questions.put("What is a poem called whose first letters of each line spell out a word?",answer2);

        HashMap<String,Boolean> answer3 = new HashMap<>();
        answer3.put("Limerick",true);
        answer3.put("Quartet",false);
        answer3.put("Sextet",false);
        answer3.put("Palindrome",false);
        questions.put("What is a funny poem of five lines called?",answer3);

        HashMap<String,Boolean> answer4 = new HashMap<>();
        answer4.put("Robert Greene",true);
        answer4.put("John Milton",false);
        answer4.put("Philip Sidney",false);
        answer4.put("Christopher Marlowe",false);
        questions.put("Who succeeded Lyly?",answer4);

        HashMap<String,Boolean> answer5 = new HashMap<>();
        answer5.put("Hamlet",true);
        answer5.put("Cymbeline",false);
        answer5.put("Titus Andronicus",false);
        answer5.put("Pericles, Prince of Tyre",false);
        questions.put("Which famous Shakespeare play does the quote,”Neither a borrower nor a lender be” come from?",answer5);

        HashMap<String,Boolean> answer6 = new HashMap<>();
        answer6.put("16th",true);
        answer6.put("17th",false);
        answer6.put("14th",false);
        answer6.put("15th",false);
        questions.put("In which century was Shakespeare born?",answer6);

        HashMap<String,Boolean> answer7 = new HashMap<>();
        answer7.put("A thief",true);
        answer7.put("A clerk",false);
        answer7.put("A teacher",false);
        answer7.put("A dentist",false);
        questions.put("Who is Mr. Tench in The Power and the Glory?",answer7);

        HashMap<String,Boolean> answer8 = new HashMap<>();
        answer8.put("Coleridge",true);
        answer8.put("Wordsworth",false);
        answer8.put("Lamb",false);
        answer8.put("Shelley",false);
        questions.put("Who said ‘Keats was a Greek’?",answer8);

        HashMap<String,Boolean> answer9 = new HashMap<>();
        answer9.put("Gertrude",true);
        answer9.put("Beatrice",false);
        answer9.put("Margaret",false);
        answer9.put("Rosalind",false);
        questions.put("Which of the following is Hamlet’s mother?",answer9);

        HashMap<String,Boolean> answer10 = new HashMap<>();
        answer10.put("Stingy",true);
        answer10.put("Rude",false);
        answer10.put("Unintelligent",false);
        answer10.put("Fanatic",false);
        questions.put("Which of the following was Elizabeth known as?",answer10);

        HashMap<String,Boolean> answer11 = new HashMap<>();
        answer11.put("Keats",true);
        answer11.put("Blake",false);
        answer11.put("Tennyson",false);
        answer11.put("Shelley",false);
        questions.put("For whom it is said: “sensuousness is a paramount bias of his genius”:",answer11);

        HashMap<String,Boolean> answer12 = new HashMap<>();
        answer12.put("Maud",true);
        answer12.put("Ulysses",false);
        answer12.put("Break, Break, Break",false);
        answer12.put("Crossing the Bar",false);
        questions.put("Which of the following poems by Tennyson is a monodrama?",answer12);

        HashMap<String,Boolean> answer13 = new HashMap<>();
        answer13.put("Southey",true);
        answer13.put("Tennyson",false);
        answer13.put("Byron",false);
        answer13.put("Wordsworth",false);
        questions.put("Which one of the following poets was appointed Poet Laureate in the year 1813?",answer13);

        HashMap<String,Boolean> answer14 = new HashMap<>();
        answer14.put("Wordsworth",true);
        answer14.put("Keats",false);
        answer14.put("Byron",false);
        answer14.put("Blake",false);
        questions.put("Who believed that poetry is the spontaneous overflow of emotions?",answer14);

        HashMap<String,Boolean> answer15 = new HashMap<>();
        answer15.put("Dickens",true);
        answer15.put("George Eliot",false);
        answer15.put("Hardy",false);
        answer15.put("None of the above",false);
        questions.put("Moral choice is everything in the works of:",answer15);

        return questions;
    }

    public static Map<String,Map<String,Boolean>> getGeographyQuestions(){
        HashMap<String,Map<String,Boolean>> questions = new HashMap<>();

        HashMap<String,Boolean> answer1 = new HashMap<>();
        answer1.put("Mustard",false);
        answer1.put("Linseed",false);
        answer1.put("Groundnut ",true);
        answer1.put("coconut",false);
        questions.put("The scarcity or crop failure of which of the following can cause a serious edible oil crisis in India?",answer1);

        HashMap<String,Boolean> answer2 = new HashMap<>();
        answer2.put("old mountains",true);
        answer2.put("young mountains",false);
        answer2.put("fold mountains",false);
        answer2.put("block mountains",false);
        questions.put("The pennines (Europe), Appalachians (America) and the Aravallis (India) are examples of",answer2);

        HashMap<String,Boolean> answer3 = new HashMap<>();
        answer3.put("5",false);
        answer3.put("13",true);
        answer3.put("8",false);
        answer3.put("10",false);
        questions.put("The number of major ports in India is",answer3);

        HashMap<String,Boolean> answer4 = new HashMap<>();
        answer4.put("Gondak",false);
        answer4.put("Kosi",false);
        answer4.put("Sutlej",false);
        answer4.put("Krishna",true);
        questions.put("Which of the following is a peninsular river of India?",answer4);

        HashMap<String,Boolean> answer5 = new HashMap<>();
        answer5.put("1730 hours",false);
        answer5.put("0630 hours",true);
        answer5.put("midnight ,GMT",false);
        answer5.put("None of the above",false);
        questions.put("When it is noon IST at Allahabad in India, the time at Greenwich, London, will be",answer5);

        HashMap<String,Boolean> answer6 = new HashMap<>();
        answer6.put("USA",false);
        answer6.put("Canada",true);
        answer6.put("Australia",false);
        answer6.put("India",false);
        questions.put("Which country has the largest coast line?",answer6);

        HashMap<String,Boolean> answer7 = new HashMap<>();
        answer7.put("Alluvial soils",true);
        answer7.put("Black soils",false);
        answer7.put("Laterite soils",false);
        answer7.put("Red soils",false);
        questions.put("Which of the following types of soil are mostly confined to river basins and coastal plains of India?",answer7);

        HashMap<String,Boolean> answer8 = new HashMap<>();
        answer8.put("USA",true);
        answer8.put("India",false);
        answer8.put("Australia",false);
        answer8.put("France",false);
        questions.put("Which of the following countries leads in the production of aluminium and its products in the world?",answer8);

        HashMap<String,Boolean> answer9 = new HashMap<>();
        answer9.put("Coconut",true);
        answer9.put("Cotton",false);
        answer9.put("Sugarcane",false);
        answer9.put("Rice",false);
        questions.put("Which of the following crops is regarded as a plantation crop?",answer9);

        HashMap<String,Boolean> answer10 = new HashMap<>();
        answer10.put("33.3%",true);
        answer10.put("30.3%",false);
        answer10.put("38.3%",false);
        answer10.put("42.3%",false);
        questions.put("The proportion of forest to the total national geographical area of India as envisaged by National Forest Policy is",answer10);

        HashMap<String,Boolean> answer11 = new HashMap<>();
        answer11.put("Gandhi Sagar",true);
        answer11.put("Hirakud",false);
        answer11.put("Periyar",false);
        answer11.put("Tungabhadra",false);
        questions.put("Which of the following dams has generations of power more than irrigation as its main purpose?",answer11);

        HashMap<String,Boolean> answer12 = new HashMap<>();
        answer12.put("The lease Himalayas and the Indo Gangetic plain",true);
        answer12.put("The foot hills and the Indo Gangetic plain",false);
        answer12.put("The greater Himalayas and the lesser Himalayas",false);
        answer12.put("Indo-Gangetic plains and the peninsula",false);
        questions.put("The outer Himalayas lie between",answer12);

        HashMap<String,Boolean> answer13 = new HashMap<>();
        answer13.put("Aravalis",true);
        answer13.put("Vindhyas",false);
        answer13.put("Satpuras",false);
        answer13.put("Nilgiri hills",false);
        questions.put("The oldest mountains in India are",answer13);

        HashMap<String,Boolean> answer14 = new HashMap<>();
        answer14.put("1/6",true);
        answer14.put("1/3",false);
        answer14.put("1/10",false);
        answer14.put("1/50",false);
        questions.put("Approximately what fraction of the world’s population lives in India?",answer14);

        HashMap<String,Boolean> answer15 = new HashMap<>();
        answer15.put("Second",true);
        answer15.put("Third",false);
        answer15.put("Fourth",false);
        answer15.put("Fifth",false);
        questions.put("India has the _________ largest population on Earth",answer15);

        return questions;
    }

    public static Map<String,Map<String,Boolean>> getRandomLiteratureAndGeographyQuestions(Context context, String subject, int SIZE){
        Map<String,Map<String,Boolean>> questionsMap = new HashMap<>();
        Map<String, Map<String, Boolean>> originalQuestion;
        if (subject.equals(context.getString(R.string.geography))){
            originalQuestion = getGeographyQuestions();
        }else{
            originalQuestion = getLiteratureQuestions();
        }

        int originalSize =  originalQuestion.size();
        ArrayList<String> keyList = new ArrayList<String>(originalQuestion.keySet());

        while (questionsMap.size()<=SIZE){
            Random random = new Random();
            int randomNumber = random.nextInt(originalSize);
            String question = keyList.get(randomNumber);
            if (!questionsMap.containsKey(question)){
                questionsMap.put(question,originalQuestion.get(question));
            }
        }
        return questionsMap;
    }

    public static boolean isValidCode(String code) {
        if (code == null || code.length() != ID_LENGTH) {
            return false;
        }

        for (int i = 0; i < ID_LENGTH; i++) {
            char ch = code.charAt(i);
            if (CHARACTERS.indexOf(ch) == -1) {
                // Character not found in CHARACTERS string
                return false;
            }
        }

        return true;
    }
}
