package com.example.scatdule_tmp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.work.WorkManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class SpinerActivity extends Activity {

    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mCalender = new GregorianCalendar();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_spiner);

        final String[] time = {"-알림 시간 선택-", "00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00"};

        RadioButton allow = (RadioButton) findViewById(R.id.radioButton3);
        RadioButton block = (RadioButton) findViewById(R.id.radioButton4);
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        Button con = (Button) findViewById(R.id.confirmation);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, time);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        if (allow.isChecked()) {
            spinner.setVisibility(View.VISIBLE);
        } else if (block.isChecked()) {
            spinner.setVisibility(View.INVISIBLE);
        }

        con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allow.isChecked()) {
                    if (spinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(getApplicationContext(), "시간을 선택해 주세요 ", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Constants.SCATDULE_EVENT_TIME=(spinner.getSelectedItemPosition()-1)*3;
                        Toast.makeText(getApplicationContext(), spinner.getSelectedItem().toString() + "에 알림 설정됨.", Toast.LENGTH_SHORT).show();
                        finish();
                        //알림 설정
                        boolean isChannelCreated = NotificationHelper.isNotificationChannelCreated(getApplicationContext());
                        if (isChannelCreated) {
                            PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, true);
                            setAlarm(WorkManager.getInstance(getApplicationContext()));
                        } else {
                            NotificationHelper.createNotificationChannel(getApplicationContext());
                        }
                    }
                } else {
                    finish();
                    // 알림이 가지 않게 설정
                    PreferenceHelper.setBoolean(getApplicationContext(), Constants.SHARED_PREF_NOTIFICATION_KEY, false);
                    cancelAlarm(WorkManager.getInstance(getApplicationContext()));
                }

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        return;
    }

    private void setAlarm2(String hour) {
        //AlarmReceiver에 값 전달
        Intent receiverIntent = new Intent(SpinerActivity.this, AlarmRecevier.class);
        //오류가 날 예정
        PendingIntent pendingIntent = PendingIntent.getBroadcast(SpinerActivity.this, 0, receiverIntent, PendingIntent.FLAG_IMMUTABLE);

        //String from = hour+":00"; //임의로 날짜와 시간을 지정

        Date nowDate = new Date();
        Log.i("tlqkf", nowDate.toString());
        String from = "22:31:00";
        //날짜 포맷을 바꿔주는 소스코드
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date datetime = null;
        try {
            datetime = dateFormat.parse(from);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datetime);

        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
    }
    private void setAlarm(final WorkManager workManager) {
        NotificationHelper.setScheduledNotification(workManager);
    }
    private void cancelAlarm(final WorkManager workManager) {
        workManager.cancelAllWork();
    }
}