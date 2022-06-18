package com.example.scatdule_tmp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton setting_btn = (ImageButton) findViewById(R.id.backButton);
        ImageButton setting_btn2 = (ImageButton) findViewById(R.id.home);
        Button notification_btn = (Button) findViewById(R.id.notificationSettings_text);

        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        setting_btn2.setOnClickListener(new View.OnClickListener() {
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




        Button logout_btn = (Button) findViewById(R.id.logoutButton);
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

    }
}
