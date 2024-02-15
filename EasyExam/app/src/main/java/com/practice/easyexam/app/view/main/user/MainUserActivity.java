package com.practice.easyexam.app.view.main.user;

import static com.practice.easyexam.app.view.createroom.CreateRoomActivity.ROOM;
import static com.practice.easyexam.app.view.main.host.MainHostActivity.TYPE_JOIN_ROOM;
import static com.practice.easyexam.app.view.main.host.MainHostActivity.TYPE_TEST;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.utils.Constants;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.editexam.CreateTestActivity;
import com.practice.easyexam.app.view.account.editpass.EditPasswordActivity;
import com.practice.easyexam.app.view.history.user.HistoryUserActivity;
import com.practice.easyexam.app.view.login.LoginActivity;
import com.practice.easyexam.app.utils.OptionalDialog;
import com.practice.easyexam.app.view.scanqr.InputCodeActivity;
import com.practice.easyexam.app.view.waiting.TimerActivity;
import com.practice.easyexam.app.view.waiting.WaitingActivity;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.io.IOException;


public class MainUserActivity extends AppCompatActivity implements OptionalDialog.OptionalDialogListener {
    public static final int REQ_CODE_IMPORT_FILE = 11;
    public static final int REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT = 405;
    private CardView cvStartQuiz;
    private CardView cvCreate;
    private CardView cvHistory;
    private CardView cvEditPassword;
    private CardView cvLogout;
    SharedPref sharedPref;
    Question question = null;
    MainUserViewModel viewModel;
    private ActivityResultLauncher<ScanOptions> barcodeLauncher;
    ArrayList<Question> listQuestion = new ArrayList<Question>();
    Test exam = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvUsername = findViewById(R.id.tvUsernameHome);

        cvStartQuiz = findViewById(R.id.cvStartQuiz);
        cvCreate = findViewById(R.id.cvCreate);
        cvHistory = findViewById(R.id.cvHistory);
        cvEditPassword = findViewById(R.id.cvEditPassword);
        cvLogout = findViewById(R.id.cvLogout);
        User user = SharedPref.getInstance().getUser(this);
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @Override
            public <T extends ViewModel> T create(Class<T> modelClass) {
                return (T) new MainUserViewModel();
            }
        }).get(MainUserViewModel.class);

        viewModel.getRoomLiveData().observe(this, item -> {
            if (item.getIdTest() == null) {
                Utils.showToast(MainUserActivity.this, R.string.incorrect_id_room);
            } else {
                Log.d("itemRoom", item.getNameRoom());
//                if (item.getState().equals(Utils.STATE_WAITING)) {
//                    Intent i = new Intent(this, WaitingActivity.class);
//                    i.putExtra(ROOM, item);
//                    startActivity(i);
//                } else {
//                    Intent i = new Intent(MainUserActivity.this, TimerActivity.class);
//                    i.putExtra(ROOM, item);
//                    startActivity(i);
//                }
                Class<?> targetActivity = item.getState().equals(Utils.STATE_WAITING) ? WaitingActivity.class : TimerActivity.class;
                Intent i = new Intent(this, targetActivity).putExtra(ROOM, item);
                startActivity(i);
            }
        });
        barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null) {
                Toast.makeText(MainUserActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                byte[] bytes = result.getContents().getBytes(StandardCharsets.UTF_8);
                byte[] decompress = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    decompress = Base64.getDecoder().decode(bytes);
                }
                viewModel.queryRoomByID(new String(decompress));
                // Start the appropriate activity or do other processing
            }
        });


        tvUsername.setText(user.getName() + " - " + user.getIdStudent());


        cvStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] examOptions = getApplicationContext().getResources().getStringArray(R.array.option_choice_join_exam);
                showOptionalDialog(examOptions, TYPE_JOIN_ROOM);
            }
        });

        cvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainUserActivity.this, HistoryUserActivity.class));
            }
        });

        cvEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainUserActivity.this, EditPasswordActivity.class));
                checkStoragePermissionImport();
                selectCSVFile();
            }
        });

        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
//
//                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(MainUserActivity.this, gso);
//                googleSignInClient.signOut();
                sharedPref.clearSharedPref(MainUserActivity.this);
                Intent intent = new Intent(MainUserActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkStoragePermissionImport() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT);
                return false;
            }
        }
        return true;
    }

    private void showOptionalDialog(String[] options, String dialogTag) {
        OptionalDialog optionalDialog = new OptionalDialog(options, dialogTag);
        optionalDialog.show(getSupportFragmentManager(),dialogTag);
    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_layout);

        EditText edtCode = dialog.findViewById(R.id.codeEditText);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button saveButton = dialog.findViewById(R.id.saveButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle save logic here
                String code = edtCode.getText().toString();
                if (!code.isEmpty()) {
                    viewModel.queryRoomByID(code);
                } else {
                    Utils.showToast(MainUserActivity.this, R.string.empty_code);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void selectCSVFile() {
        String[] mimeTypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String mimeTypesStr = "";
        for (String mimeType : mimeTypes) {
            mimeTypesStr += mimeType + "|";
        }
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, REQ_CODE_IMPORT_FILE);
    }


    public void readExcel(String excelFilePath) throws IOException, InvalidFormatException {
        File file = new File(excelFilePath);
        Workbook workBook = WorkbookFactory.create(file);
        Sheet sheet = workBook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        initExam(iterator);
        passData();
    }

    public void passData() {
        Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
        if (exam != null) {
            intent.putExtra("EXAM", (Serializable) exam);
            startActivity(intent);
        }
    }

    void initExam(Iterator<Row> iterator) {
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();
            question = new Question();
            ArrayList<String> listAnswer = new ArrayList<>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                Object cellValue = getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                if (cell.getColumnIndex() == 0) {
                    question.setQuestion((String) cellValue);
                } else if (cell.getColumnIndex() == 5) {
                    question.setCorrectNum((Integer) getCellValue(cell));
                } else {
                    listAnswer.add(String.valueOf(cellValue));
                }
                question.setAnswers(listAnswer);
            }
            if (question.getQuestion() != null) {
                listQuestion.add(question);
            }
        }
        exam = new Test();
//        exam.setQuestionArrayList(listQuestion);
    }


    private static Object getCellValue(Cell cell) {
        int cellType = cell.getCellType();
        Object cellValue = cell;
        switch (cellType) {
            case CELL_TYPE_BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case CELL_TYPE_FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case CELL_TYPE_NUMERIC:
                cellValue = (int) cell.getNumericCellValue();
                break;
            case CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case CELL_TYPE_BLANK:
            case CELL_TYPE_ERROR:
                break;
            default:
                break;
        }

        return cellValue;
    }


    private static String getDriveFilePath(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getCacheDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_IMPORT_FILE && resultCode == RESULT_OK) {
            // Handle file import
            Uri uri = data.getData();
            String path = getDriveFilePath(uri, MainUserActivity.this);
            try {
                readExcel(path);
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
        } else if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle QR code scan result
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    // Handle canceled scan
                    Toast.makeText(this, "Cancelling to scan QR Code", Toast.LENGTH_LONG).show();
                } else {
                    // Handle successful scan
                    String qrCodeContents = result.getContents();
                    Log.d("decompress", qrCodeContents);
                    viewModel.queryRoomByID(qrCodeContents);
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            selectCSVFile();
        }
    }

    private void startQRCodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);  // Allow both portrait and landscape scanning
        integrator.initiateScan();
    }




    @Override
    public void onPositiveButtonClicked(String dialogTag, int position) {
        if (dialogTag.equals(TYPE_JOIN_ROOM)) {
            switch (position) {
                case 0:
//                    ScanOptions options = new ScanOptions();
//                    barcodeLauncher.launch(options);
//                    startActivity(new Intent(MainUserActivity.this, InputCodeActivity.class));
                    startQRCodeScanner();
                    break;
                case 1:
                    showCustomDialog();
                    break;
            }
        }
    }

    @Override
    public void onNegativeButtonClicked(String dialogTag) {
        if (dialogTag.equals(TYPE_JOIN_ROOM)) {
        }
    }
}