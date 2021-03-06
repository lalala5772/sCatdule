package com.example.scatdule_tmp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.scatdule_tmp.Constants.KOREA_TIMEZONE;
import static com.example.scatdule_tmp.Constants.SCATDULE_EVENT_TIME;

public class WorkerScatdule extends Worker {
    public WorkerScatdule(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper mNotificationHelper = new NotificationHelper(getApplicationContext());
        long currentMillis = Calendar.getInstance(TimeZone.getTimeZone(KOREA_TIMEZONE), Locale.KOREA).getTimeInMillis();

        // 알림 범위(08:00-09:00, 20:00-21:00)에 해당하는지 기준 설정
        Calendar eventCal = NotificationHelper.getScheduledCalender(SCATDULE_EVENT_TIME);
        long NotifyMinRange = eventCal.getTimeInMillis();

        eventCal.add(Calendar.HOUR_OF_DAY, Constants.NOTIFICATION_INTERVAL_HOUR);
        long NotifyMaxRange = eventCal.getTimeInMillis();


        // 현재 시각이  알림 범위에 해당하는지
        boolean isNotifyRange = NotifyMinRange <= currentMillis && currentMillis <= NotifyMaxRange;
        // 현재 시각이 알림 범위에 해당여부
        boolean isEventANotifyAvailable = isNotifyRange;


        if (isEventANotifyAvailable) {
            // 현재 시각이 알림 범위에 해당하면 알림 생성
            mNotificationHelper.createNotification(Constants.WORK_SCATDULE_NAME);
        } else {
            // 그 외의 경우 가장 빠른 A 이벤트 예정 시각까지의 notificationDelay 계산하여 딜레이 호출
            long notificationDelay = NotificationHelper.getNotificationDelay(Constants.WORK_SCATDULE_NAME);
            OneTimeWorkRequest workRequest =
                    new OneTimeWorkRequest.Builder(WorkerScatdule.class)
                            .setInitialDelay(notificationDelay, TimeUnit.MILLISECONDS)
                            .build();
            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);
        }
        return Result.success();
    }
}