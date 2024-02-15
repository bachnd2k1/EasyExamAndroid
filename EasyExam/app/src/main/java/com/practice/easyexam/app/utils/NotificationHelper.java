package com.practice.easyexam.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class NotificationHelper {

    private static final String CHANNEL_ID = "EasyExam";
    private static final int NOTIFICATION_ID = 1;

    public static void showNotification(Context context, File file) {
        createNotificationChannel(context);

        // Create an intent to open the file
        Intent openFileIntent = new Intent(Intent.ACTION_VIEW);
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        openFileIntent.setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent openFilePendingIntent = PendingIntent.getActivity(
                context,
                0,
                openFileIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("File Downloaded")
                .setContentText("File saved to Downloads: " + file.getName())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(openFilePendingIntent)
                .setAutoCancel(true); // Remove the notification when clicked

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "MyAppChannel";
            String description = "MyApp Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}