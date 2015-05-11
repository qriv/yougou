package com.gsh.app.client.mall.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.gsh.app.client.mall.MallApplication;
import com.gsh.app.client.mall.R;
import com.gsh.app.client.mall.User;

/**
 * Created by taosj on 15/2/2.
 */
public class SplashActivity extends ActivityBase {
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View splashView = View.inflate(this, R.layout.activity_splash, null);

        View image_container = splashView.findViewById(R.id.image_container);
        image_container.getLayoutParams().height = getWindowManager().getDefaultDisplay().getWidth();
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
//        Runnable r2 = () -> toast("Hello, lambda");
//        r2.run();
    }

    private void entry() {
        User user = User.load(User.class);
        if (user != null && !user.communityChoosed())
            go.name(CommunitySettingActivity.class).goAndFinishCurrent();
        else
            go.name(MainActivity.class).goAndFinishCurrent();
    }
}
