package com.andruid.magic.makeitspeak.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.andruid.magic.makeitspeak.R;
import com.andruid.magic.makeitspeak.activity.DetailsActivity;
import com.andruid.magic.makeitspeak.activity.MainActivity;
import com.andruid.magic.makeitspeak.database.AudioText;

import static com.andruid.magic.makeitspeak.data.Constants.CHANNEL_ID;
import static com.andruid.magic.makeitspeak.data.Constants.CHANNEL_NAME;
import static com.andruid.magic.makeitspeak.data.Constants.KEY_AUDIO_TEXT;

public class NotificationUtil {
    public static NotificationCompat.Builder buildProgressNotification(Context context, String fileName){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager==null)
            return null;
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(fileName)
                .setOngoing(true)
                .setContentText("Converting text to audio...")
                .setProgress(100, 0, true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }

    public static NotificationCompat.Builder buildCompletedNotification(Context context,
                                                                        AudioText audioText){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager==null)
            return null;
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra(KEY_AUDIO_TEXT, audioText);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle(audioText.getName()+".mp3")
                .setContentText("Audio file created")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
    }
}