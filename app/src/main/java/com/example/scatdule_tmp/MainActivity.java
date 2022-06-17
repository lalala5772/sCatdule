package com.example.scatdule_tmp;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import android.content.pm.Signature;

import com.bumptech.glide.Glide;
import com.example.scatdule_tmp.Adapter.ToDoAdapter;
import com.example.scatdule_tmp.Model.ToDoModel;
import com.example.scatdule_tmp.Utils.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity implements OnDialogCloseListner{
    private static final String TAG = "MainActivity";

    SQLiteDatabase sqliteDB ;
    public static Context context_main;
    public MediaPlayer mediaPlayer; // 음악재생

    TextView days_info; //오늘의 날짜 정보를 가져올 객체

    private RecyclerView mRecyclerview;
    private FloatingActionButton fab;
    private DataBaseHelper myDB;
    private List<ToDoModel> mList;
    private ToDoAdapter adapter;

    public boolean exp_flag = false;
    public static int exp = 0;
    public static int level = 0;

    private AlarmManager alarmManager;
    private GregorianCalendar mCalender;

    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //알림
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        mCalender = new GregorianCalendar();

        Log.v("HelloAlarmActivity", mCalender.getTime().toString());

        setContentView(R.layout.activity_main);


        // 로그에 해시 키 값 띄우기
        Log.d("getKeyHash", "" + getKeyHash(MainActivity.this));

        context_main=this;

        //세팅 버튼 기능 구현
        ImageButton setting_btn = (ImageButton) findViewById(R.id.settings);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });

        mediaPlayer = MediaPlayer.create(this, R.raw.matt);
        mediaPlayer.setLooping(true); //무한재생
        mediaPlayer.start();
        //-----------------------------------------------------------------------------------------------

        ImageView cat = (ImageView) findViewById(R.id.cat);
        Glide.with(this).load(R.raw.orange_tabby_sit_tale).into(cat);


        //-----------------------------------------------------------------------------------------------

        setDays_info(); //오늘의 날짜 가져오는 함수 선언
        set_progress(exp); //경험치 함수 선언


        //    ---------------------------------------------------------------------------------------------
        //    todolist 기능 구현 코드
        mRecyclerview = findViewById(R.id.recyclerview);
        fab = findViewById(R.id.fab);
        myDB = new DataBaseHelper(MainActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB , MainActivity.this);

        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerview.setAdapter(adapter);

        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerview);


    }



    //      ----------------------------------------------------------------------------------------------
    //      오늘의 날짜 정보 가져오는 코드

    public void setDays_info(){
        days_info = findViewById(R.id.days_info);

        Date currentTime = Calendar.getInstance().getTime();
        String date_text = new SimpleDateFormat("yyyy-MM-dd    EE", Locale.getDefault()).format(currentTime);

        days_info.setText(date_text);

    }

    //      ----------------------------------------------------------------------------------------------
    //      경험치 바 구현 코드
    public void set_progress(int exp){
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        progress.setMax(100);
        progress.setProgress(exp);
    }

    //해시 키 가져오기
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onDialogClose(DialogInterface dialogInterface) {
        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }

    public void musicStop(){
        mediaPlayer.stop();
        mediaPlayer=null;
    }


}