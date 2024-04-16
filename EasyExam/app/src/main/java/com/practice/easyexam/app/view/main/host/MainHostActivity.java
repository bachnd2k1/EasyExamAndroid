package com.practice.easyexam.app.view.main.host;

import static com.practice.easyexam.app.view.createroom.CreateRoomActivity.ROOM;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_ERROR;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_FORMULA;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC;
import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
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
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.practice.easyexam.R;
import com.practice.easyexam.app.data.local.SharedPref;
import com.practice.easyexam.app.model.User;
import com.practice.easyexam.app.model.Test;
import com.practice.easyexam.app.model.Question;
import com.practice.easyexam.app.utils.QuizParser;
import com.practice.easyexam.app.utils.Utils;
import com.practice.easyexam.app.view.account.AccountActivity;
import com.practice.easyexam.app.view.createdTests.CreatedTestsActivity;
import com.practice.easyexam.app.view.createroom.CreateRoomActivity;
import com.practice.easyexam.app.view.editexam.CreateTestActivity;
import com.practice.easyexam.app.view.history.host.HistoryHostActivity;
import com.practice.easyexam.app.view.login.LoginActivity;
import com.practice.easyexam.app.utils.OptionalDialog;
import com.practice.easyexam.app.view.main.user.MainUserViewModel;
import com.practice.easyexam.app.view.waiting.WaitingActivity;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainHostActivity extends AppCompatActivity implements OptionalDialog.OptionalDialogListener {
    public static final int REQ_CODE_IMPORT_CSV_FILE = 11;
    public static final int REQ_CODE_IMPORT_DOC_FILE = 12;
    public static final int REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT = 405;
    public static final String TYPE_TEST = "TYPE_TEST";
    public static final String TYPE_JOIN_ROOM = "TYPE_JOIN_ROOM";
    public static final String PATH = "PATH";
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private CardView cvCreateExam;
    private CardView cvCreateTest;
    private CardView cvCreatedTests;
    private CardView cvEditPassword;
    private CardView cvLogout;
    SharedPref sharedPref;
    MainUserViewModel viewModel;
    String path;
    Test exam = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_host);
        viewModel = new ViewModelProvider(this,
                new ViewModelProvider.Factory() {
                    @Override
                    public <T extends ViewModel> T create(Class<T> modelClass) {
                        return (T) new MainUserViewModel();
                    }
                })
                .get(MainUserViewModel.class);
        viewModel.getRoomLiveData().observe(this, item -> {
            if (item == null) {
                Utils.showToast(MainHostActivity.this, R.string.empty_test);
            } else {
                Log.d("itemRoom", item.getNameRoom());
                Intent i = new Intent(this, WaitingActivity.class);
                i.putExtra(ROOM, item);
                startActivity(i);
            }
        });
        TextView tvUsername = findViewById(R.id.tvUsernameHome);
        cvCreateExam = findViewById(R.id.cvStartQuiz);
        cvCreateTest = findViewById(R.id.cvCreate);
        cvCreatedTests = findViewById(R.id.cvHistory);
        cvEditPassword = findViewById(R.id.cvEditPassword);
        cvLogout = findViewById(R.id.cvLogout);
        sharedPref = SharedPref.getInstance();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            tvUsername.setText("Hello, " + personName);
        } else {
            User user = sharedPref.getUser(this);
            tvUsername.setText(user.getName() + " - " + user.getIdStudent());
        }


        cvCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainHostActivity.this, CreateRoomActivity.class));
            }
        });

        cvCreateTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] examOptions = getApplicationContext().getResources().getStringArray(R.array.option_choice_exam);
                showOptionalDialog(examOptions, TYPE_TEST);
            }
        });

        cvCreatedTests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainHostActivity.this, CreatedTestsActivity.class));
            }
        });

        cvEditPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainHostActivity.this, HistoryHostActivity.class));
            }
        });

        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sharedPref.clearSharedPref(MainHostActivity.this);
//                Intent intent = new Intent(MainHostActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
                Intent intent = new Intent(MainHostActivity.this, AccountActivity.class);
                startActivity(intent);

            }
        });
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
//                    viewModel.queryRoomByID(code);
                } else {
                    Utils.showToast(MainHostActivity.this, R.string.empty_code);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showOptionalDialog(String[] options, String dialogTag) {
        OptionalDialog optionalDialog = new OptionalDialog(options, dialogTag);
        optionalDialog.show(getSupportFragmentManager(), dialogTag);
    }
    private boolean checkStoragePermissionImport() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT);
                return false;
            }
        }

        return true;
    }


    private void selectCSVFile() {
        String[] mimeTypes = {
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.ms-excel"
        };
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String mimeTypesStr = "";
        for (String mimeType : mimeTypes) {
            mimeTypesStr += mimeType + "|";
        }
        intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        startActivityForResult(intent, REQ_CODE_IMPORT_CSV_FILE);
    }

    private void selectDocxFile() {
        String[] mimeTypes = {
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/msword"
        };

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Combine all MIME types into a single string
        StringBuilder mimeTypesStr = new StringBuilder();
        for (String mimeType : mimeTypes) {
            mimeTypesStr.append(mimeType).append("|");
        }
//        intent.setType(mimeTypesStr.toString());
        intent.setType("*/*");
        startActivityForResult(intent, REQ_CODE_IMPORT_DOC_FILE);
    }


    private boolean isDocxFile(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return "docx".equalsIgnoreCase(fileExtension);
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
        Uri uri = data.getData();
        if (requestCode == REQ_CODE_IMPORT_CSV_FILE && resultCode == RESULT_OK) {
            path = getDriveFilePath(uri, MainHostActivity.this);
            Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
            intent.putExtra(PATH,path);
            startActivity(intent);
        }
//        else if (requestCode == REQ_CODE_IMPORT_DOC_FILE && resultCode == RESULT_OK) {
//            if (isDocxFile(uri)) {
//                String path = getDriveFilePath(uri, MainHostActivity.this);
//                readWord(path);
//            } else {
//                Toast.makeText(this, "Please select a .docx file", Toast.LENGTH_SHORT).show();
//            }
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_READ_EXTERNAL_STORAGE_IMPORT && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            selectCSVFile();
        }
    }

//    @Override
//    public void onPositiveButtonClicked(int position) {
//        switch (position) {
//            case 0:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    selectCSVFile();
//                } else {
//                    if (checkStoragePermissionImport()) {
//                        selectCSVFile();
//                    }
//                }
//                break;
//            case 1:
////                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////                    selectDocxFile();
////                } else {
////                    if (checkStoragePermissionImport()) {
////                        selectDocxFile();
////                    }
////                }
//                Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
//                startActivity(intent);
//                break;
//        }
//    }

//    @Override
//    public void onNegativeButtonClicked() {
//
//    }


    @Override
    public void onPositiveButtonClicked(String dialogTag, int position) {
        if (dialogTag.equals(TYPE_TEST)) {
            switch (position) {
                case 0:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        selectCSVFile();
                    } else {
                        if (checkStoragePermissionImport()) {
                            selectCSVFile();
                        }
                    }
                    break;
                case 1:
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    selectDocxFile();
//                } else {
//                    if (checkStoragePermissionImport()) {
//                        selectDocxFile();
//                    }
//                }
                    Intent intent = new Intent(getApplicationContext(), CreateTestActivity.class);
                    startActivity(intent);
                    break;
            }
        } else if (dialogTag.equals(TYPE_JOIN_ROOM)) {
            // Handle positive button click for the second dialog
            Log.d("YourActivity", "Dialog 2 - Positive button clicked, position: " + position);
        }

    }

    @Override
    public void onNegativeButtonClicked(String dialogTag) {

    }
}
