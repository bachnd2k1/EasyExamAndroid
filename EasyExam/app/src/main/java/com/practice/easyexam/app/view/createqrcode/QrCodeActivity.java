package com.practice.easyexam.app.view.createqrcode;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.practice.easyexam.R;
import com.practice.easyexam.app.model.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Hashtable;

public class QrCodeActivity extends AppCompatActivity {
    ImageView imageCode;
    String json = "";
    Test exam;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        imageCode = findViewById(R.id.imgCode);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
           json = extras.getString("jsonString");
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                exam = objectMapper.readValue(json, Test.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MultiFormatWriter mWriter = new MultiFormatWriter();
        try {
            Hashtable hints = new Hashtable();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            byte[] compressed = Base64.getEncoder().encode(bytes);
            String data = new String(compressed);
            Log.d("json",data);
            BitMatrix mMatrix = mWriter.encode(data, BarcodeFormat.QR_CODE, 1000, 1000,hints);
            BarcodeEncoder mEncoder = new BarcodeEncoder();
            Bitmap mBitmap = mEncoder.createBitmap(mMatrix);//creating bitmap of code
            imageCode.setImageBitmap(mBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}