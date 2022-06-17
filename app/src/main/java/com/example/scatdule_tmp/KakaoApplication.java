package com.example.scatdule_tmp;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Debug;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    private Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, "8c01720fccf706f0c6e904ec2d7e4632");
        NotificationHelper.createNotificationChannel(getApplicationContext());
        NotificationHelper.refreshScheduledNotification(getApplicationContext());
    }
}

