package com.example.scatdule_tmp;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.user.UserApiClient;
import com.lakue.lakuepopupactivity.PopupActivity;
import com.lakue.lakuepopupactivity.PopupGravity;
import com.lakue.lakuepopupactivity.PopupType;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SettingActivity extends AppCompatActivity {
    private ImageButton back_btn;
    private ImageButton home_btn;
    private SplashActivity splashActivity;
    private Switch soundbtn;
    public static boolean soundcheck = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton setting_btn = (ImageButton) findViewById(R.id.backButton);
        ImageButton helpview_btn = (ImageButton) findViewById(R.id.help_text);
        ImageButton notification_btn = (ImageButton) findViewById(R.id.notificationSettings_text);


        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SpinerActivity.class);
                startActivity(intent);
            }
        });

        ImageButton logout_btn = (ImageButton) findViewById(R.id.logoutButton);
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        finish();
                        ((MainActivity)MainActivity.context_main).musicStop();

                        //데이터 저장
                        FirebaseUser.saveUserInfo(Constants.id,Constants.level,Constants.exp);
                        return null;
                    }
                });
            }
        });


        helpview_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
            }
        });


        soundbtn = findViewById(R.id.sound_btn);
        if(soundcheck) soundbtn.setChecked(true);
        else soundbtn.setChecked(false);
        soundbtn.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    soundbtn.setChecked(true);
                    soundcheck = true;
                    ((MainActivity)MainActivity.context_main).musicStart();
                }
                else{
                    soundbtn.setChecked(false);
                    soundcheck = false;
                    ((MainActivity)MainActivity.context_main).musicStop();
                }
            }
        });

    }

}
