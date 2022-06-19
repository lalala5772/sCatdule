package com.example.scatdule_tmp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


import static com.example.scatdule_tmp.Constants.SCATDULE_EVENT_TIME;
import static com.example.scatdule_tmp.Constants.WORK_SCATDULE_NAME;
import static com.example.scatdule_tmp.Constants.KOREA_TIMEZONE;
import static com.example.scatdule_tmp.Constants.NOTIFICATION_CHANNEL_ID;

public class NotificationHelper {
    private Context mContext;
    private static final Integer WORK_SCATDULE_CODE = 2;
    NotificationHelper(Context context) {
        mContext = context;
    }

    public static void setScheduledNotification(WorkManager workManager) {
        setScatduleNotifySchedule(workManager);
    }

    private static void setScatduleNotifySchedule(WorkManager workManager) {
        // Event 발생시 WorkerScatdule.class 호출
        // 알림 활성화 시점에서 반복 주기 이전에 있는 가장 빠른 알림 생성
        OneTimeWorkRequest aWorkerOneTimePushRequest = new OneTimeWorkRequest.Builder(WorkerScatdule.class).build();
        // 가장 가까운 알림시각까지 대기 후 실행, 24시간 간격 반복 5분 이내 완료
        PeriodicWorkRequest aWorkerPeriodicPushRequest =
                new PeriodicWorkRequest.Builder(WorkerScatdule.class, 24, TimeUnit.HOURS, 5, TimeUnit.MINUTES)
                        .build();
        try {
            // workerScatdule 정보 조회
            List<WorkInfo> aWorkerNotifyWorkInfoList = workManager.getWorkInfosForUniqueWorkLiveData(WORK_SCATDULE_NAME).getValue();
            for (WorkInfo workInfo : aWorkerNotifyWorkInfoList) {
                // worker의 동작이 종료된 상태라면 worker 재등록
                if (workInfo.getState().isFinished()) {
                    workManager.enqueue(aWorkerOneTimePushRequest);
                    workManager.enqueueUniquePeriodicWork(WORK_SCATDULE_NAME, ExistingPeriodicWorkPolicy.KEEP, aWorkerPeriodicPushRequest);
                }
            }
        } catch (NullPointerException nullPointerException) {
            // 알림 worker가 생성된 적이 없으면 worker 생성
            workManager.enqueue(aWorkerOneTimePushRequest);
            workManager.enqueueUniquePeriodicWork(WORK_SCATDULE_NAME, ExistingPeriodicWorkPolicy.KEEP, aWorkerPeriodicPushRequest);
        }
    }

    // 현재시각이 알림 범위에 해당하지 않으면 딜레이 리턴
    public static long getNotificationDelay(String workName) {
        long pushDelayMillis = 0;
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(KOREA_TIMEZONE), Locale.KOREA);
        long currentMillis = cal.getTimeInMillis();
        if (workName.equals(WORK_SCATDULE_NAME)) {
            if (cal.get(Calendar.HOUR_OF_DAY) >= SCATDULE_EVENT_TIME) {
                Calendar nextDayCal = getScheduledCalender(SCATDULE_EVENT_TIME);
                nextDayCal.add(Calendar.DAY_OF_YEAR, 1);
                pushDelayMillis = nextDayCal.getTimeInMillis() - currentMillis;

            } else if (cal.get(cal.get(Calendar.HOUR_OF_DAY)) < SCATDULE_EVENT_TIME) {
                pushDelayMillis = getScheduledCalender(SCATDULE_EVENT_TIME).getTimeInMillis() - currentMillis;
            }
        }
        return pushDelayMillis;
    }

    //푸시알림 생성 및 동작 정의
    public void createNotification(String workName) {
        // 클릭 시 MainActivity 호출
        Intent intent = new Intent(mContext, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT); // 대기열에 이미 있다면 MainActivity가 아닌 앱 활성화
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Notificatoin을 이루는 공통 부분 정의
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.cat) // 기본 제공되는 이미지
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true); // 클릭 시 Notification 제거

        // 매개변수가 WorkerSCATCULE라면
        if (workName.equals(WORK_SCATDULE_NAME)) {
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, WORK_SCATDULE_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
            notificationBuilder.setContentTitle("Scatdule").setContentText("to-do-list를 완료하여 고양이를 키우세요!")
                    .setContentIntent(pendingIntent);
            if (notificationManager != null) {
                notificationManager.notify(WORK_SCATDULE_CODE, notificationBuilder.build());
            }
        }
    }

    public static Boolean isNotificationChannelCreated(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                return notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null;
            }
            return true;
        } catch (NullPointerException nullException) {
            Toast.makeText(context, "푸시 알림 기능에 문제가 발생했습니다. 앱을 재실행해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 푸시 알림 허용 및 사용자에 의해 알림이 꺼진 상태가 아니라면 푸시 알림 백그라운드 갱신
    public static void refreshScheduledNotification(Context context) {
        try {
            Boolean isNotificationActivated = PreferenceHelper.getBoolean(context, Constants.SHARED_PREF_NOTIFICATION_KEY);
            if (isNotificationActivated) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                boolean isNotifyAllowed;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    int channelImportance = notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID).getImportance();
                    isNotifyAllowed = channelImportance != NotificationManager.IMPORTANCE_NONE;
                } else {
                    isNotifyAllowed = NotificationManagerCompat.from(context).areNotificationsEnabled();
                }
                if (isNotifyAllowed) {
                    NotificationHelper.setScheduledNotification(WorkManager.getInstance(context));
                }
            }
        } catch (NullPointerException nullException) {
            Toast.makeText(context, "푸시 알림 기능에 문제가 발생했습니다. 앱을 재실행해주세요.", Toast.LENGTH_SHORT).show();
            nullException.printStackTrace();
        }
    }

    // 한번 실행 시 이후 재호출해도 동작 안함
    public static void createNotificationChannel(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                // NotificationChannel 초기화
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, context.getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel
                notificationChannel.setDescription("푸시알림");
                notificationChannel.enableLights(true); // 화면활성화 설정
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500}); // 진동패턴 설정
                notificationChannel.enableVibration(true); // 진동 설정
                notificationManager.createNotificationChannel(notificationChannel); // channel 생성
            }
        } catch (NullPointerException nullException) {
            // notificationManager null 오류 raise
            Toast.makeText(context, "푸시 알림 채널 생성에 실패했습니다. 앱을 재실행하거나 재설치해주세요.", Toast.LENGTH_SHORT).show();
            nullException.printStackTrace();
        }
    }

    public static Calendar getScheduledCalender(Integer scheduledTime) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(KOREA_TIMEZONE), Locale.KOREA);
        cal.set(Calendar.HOUR_OF_DAY, scheduledTime);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal;
    }
}
