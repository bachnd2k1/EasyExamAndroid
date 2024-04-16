package com.practice.easyexam.app.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.practice.easyexam.app.model.RecordTest;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExporter {
    public static void exportToExcel(Context context, List<RecordTest> userList, String nameRoom) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");

        // Create header row
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Name");
        headerRow.createCell(2).setCellValue("Point");

        // Populate data rows
        for (int i = 0; i < userList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            RecordTest user = userList.get(i);
            row.createCell(0).setCellValue(user.getIdStudent());
            row.createCell(1).setCellValue(user.getNameStudent());
            row.createCell(2).setCellValue(user.getPoint());
        }

        // Save the workbook to a file
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String fileName = generateUniqueFileName(downloadsDir, nameRoom, "xlsx");

            File file = new File(downloadsDir, fileName);
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();

            // Use MediaScanner to scan the file and make it visible in the Downloads app
            scanFile(context, file);

            // Show a notification
            showNotification(context, file);

            Toast.makeText(context, "Exporting and downloading...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting Excel file", Toast.LENGTH_SHORT).show();
        }
    }

    private static String generateUniqueFileName(File directory, String baseName, String extension) {
        String fileName = baseName + "." + extension;
        File file = new File(directory, fileName);

        int counter = 1;
        while (file.exists()) {
            fileName = baseName + "(" + counter + ")." + extension;
            file = new File(directory, fileName);
            counter++;
        }

        return fileName;
    }

    private static void scanFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    private static void showNotification(Context context, File file) {
        NotificationHelper.showNotification(context, file);
    }

}