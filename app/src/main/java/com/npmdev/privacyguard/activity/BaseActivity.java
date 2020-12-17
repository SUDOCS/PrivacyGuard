package com.npmdev.privacyguard.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.qmuiteam.qmui.arch.QMUIActivity;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.List;

public class BaseActivity extends QMUIActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAppearance();
    }

    void initAppearance() {

        // 状态栏沉浸
        QMUIStatusBarHelper.translucent(this);
        // 设置状态栏图标为暗色
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        // 设置导航栏背景颜色
        getWindow().setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAppearance();
    }
}
