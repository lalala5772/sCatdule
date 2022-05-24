package com.example.scatdule_tmp;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    TextView days_info; //오늘의 날짜 정보를 가져올 객체
    Fragment mainFragment; //todolist(주기능을)를 구현하기 위한 객체
    EditText inputToDo;
    Context context;


    public boolean exp_flag = false;
    public static int exp = 50;
    public static NoteDatabase noteDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//      세팅 버튼 기능 구현
        ImageButton setting_btn = (ImageButton) findViewById(R.id.settings);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(intent);
            }
        });


        setDays_info(); //오늘의 날짜 가져오는 함수 선언
        set_progress(exp); //경험치 함수 선언



//        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox) ;
//        checkBox.setOnClickListener(new CheckBox.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (((CheckBox)v).isChecked()) {
//                    if(exp_flag)
//                    exp+=20;
//                    exp%=100;
//                    set_progress(exp);
//                    exp_flag=true;
//
//                } else {
//                    if(exp_flag){
//                        exp-=20;
//                        exp%=100;
//                        set_progress(exp);
//                        exp_flag = false;
//                    }
//                }
//            }
//        }) ;

        //    ---------------------------------------------------------------------------------------------
        //    todolist 기능 구현 코드
        mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.container,mainFragment).commit();

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                saveToDo();
                Toast.makeText(getApplicationContext(),"추가되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });
        openDatabase();

    }
    private void saveToDo(){
        inputToDo = findViewById(R.id.inputToDo);

        //EditText에 적힌 글을 가져오기
        String todo = inputToDo.getText().toString();

        //테이블에 값을 추가하는 sql구문 insert...
        String sqlSave = "insert into " + NoteDatabase.TABLE_NOTE + " (TODO) values (" +
                "'" + todo + "')";

        //sql문 실행
        NoteDatabase database = NoteDatabase.getInstance(context);
        database.execSQL(sqlSave);

        //저장과 동시에 EditText 안의 글 초기화
        inputToDo.setText("");
    }


    public void openDatabase() {
        // open database
        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }

        noteDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = noteDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (noteDatabase != null) {
            noteDatabase.close();
            noteDatabase = null;
        }
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

}