package com.npmdev.privacyguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.npmdev.privacyguard.R;
import com.npmdev.privacyguard.databinding.ActivitySplashBinding;

/**
 * 启动
 */
public class SplashActivity extends BaseActivity {

    ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 绑定视图
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 新建动画
                Animation trans = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_anim);
                trans.setFillAfter(true);
                trans.setFillEnabled(true);
                trans.setDuration(1000);
                trans.setRepeatCount(1);
                trans.setInterpolator(new DecelerateInterpolator(2));
                // 让图片logo网上平移
                binding.imageView.startAnimation(trans);
                trans.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 当动画结束后，等待500毫秒进入主界面
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                // 结束本Activity
                                finish();
                            }
                        }, 500);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });

    }
}