package com.example.scatdule_tmp;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(this,"8c01720fccf706f0c6e904ec2d7e4632");
    }
}
