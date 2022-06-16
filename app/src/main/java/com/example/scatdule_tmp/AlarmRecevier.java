package com.example.scatdule_tmp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class AlarmRecevier extends BroadcastReceiver {

    public AlarmRecevier(){ }

    NotificationManager manager;
    NotificationCompat.Builder builder;

    //오레오 이상은 반드시 채널을 설정해줘야 Notification이 작동함
    private static String CHANNEL_ID = "channel1";
    private static String CHANNEL_NAME = "Channel1";


    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        builder = null;
        manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            manager.createNotificationChannel(
                    new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            );
            builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(context);
        }

        //알림창 클릭 시 activity 화면 부름

        Intent intent2 = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent2, PendingIntent.FLAG_IMMUTABLE);

        //알림창 제목
        builder.setContentTitle("sCatdule");
        //알림창 내용
        builder.setContentText("to-do-list를 완료하여 고양이를 키우세요!");
        //알림창 아이콘
        builder.setSmallIcon(R.drawable.cat);
        //알림창 터치시 자동 삭제
        builder.setAutoCancel(true);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        manager.notify(1,notification);

    }
}