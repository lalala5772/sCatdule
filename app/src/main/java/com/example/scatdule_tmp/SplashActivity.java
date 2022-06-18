package com.example.scatdule_tmp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class SplashActivity extends AppCompatActivity {
    public boolean isLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView gif_image = (ImageView) findViewById(R.id.gif_image);
        /*Glide.with(this).load(R.drawable.startview3).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(gif_image);*/
        Glide.with(this).asGif().load(R.drawable.startview3).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        //do whatever after specified number of loops complete
                        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                            @Override
                            public Unit invoke(User user, Throwable throwable) {
                                // 로그인이 되어있으면
                                if (user != null) {
                                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(main);
                                    finish();
                                } else {
                                    // 로그인이 되어 있지 않다면 위와 반대로
                                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                                    startActivity(main);
                                    finish();
                                }
                                return null;
                            }
                        });

                    }
                });
                return false;
            }
        }).into(gif_image);



        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isLogin==true)
                {
                    Intent main = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(main);
                }
                else
                {
                    Intent main = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(main);
                }
                finish();
            }
            //로딩이 다 안됐는데 넘어감
        }, 3000);*/

    }
}
