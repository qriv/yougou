package com.gsh.app.ugou.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.activity.ActivityBase;

/**
 * @author Tan Chunmao
 */
public class SplashActivity extends ActivityBase {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View splashView = View.inflate(this, R.layout.ugou_activity_splash, null);
        setContentView(splashView);
        Animation alpha = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        splashView.startAnimation(alpha);
        alpha.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        entry();
                    }
                }, 500);
            }

            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void entry() {
        go.name(MainActivity.class).goAndFinishCurrent();
    }

}
