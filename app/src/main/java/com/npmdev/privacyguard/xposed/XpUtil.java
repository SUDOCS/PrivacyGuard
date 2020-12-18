package com.npmdev.privacyguard.xposed;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.npmdev.privacyguard.Constant;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class XpUtil {
    private static final String TAG = XpUtil.class.getSimpleName();

    private static String lastMsg = "";


    private static XSharedPreferences prefs;

    /**
     * 初始化 XSharePreference 并打印测试信息
     */
    public static void prefsInit() {
        prefs = new XSharedPreferences("com.npmdev.privacyguard", "main_prefs");
        prefs.makeWorldReadable();

        XposedBridge.log(TAG + "preferences init");
        XposedBridge.log("LIST_MODE: " + prefs.getInt(Constant.LIST_MODE, -1));
    }

    /**
     * 获取 XSharePreference 实例
     */
    public static XSharedPreferences getPrefs() {
        if (prefs == null) {
            prefs = new XSharedPreferences("com.npmdev.privacyguard", "main_prefs");
            prefs.makeWorldReadable();
        } else {
            // 载入刷新
            prefs.reload();
        }
        return prefs;
    }

    /**
     * 根据包名，和Hook作用模式，返回相应的APP是否在HOOK作用名单之内
     *
     * @param packageName 包名
     */
    public static Boolean isAppInLimited(String packageName) {
        int mode = getPrefs().getInt(Constant.LIST_MODE, 0);
        boolean isInList = getPrefs().getBoolean(packageName, false);
        if (mode == 0) {
            return isInList;
        } else {
            return !isInList;
        }
    }


    /**
     * 全局弹出Taost
     */
    public static void showToast(String latter) {
        showToast("", latter);
    }

    /**
     * 全局弹出Taost
     */
    @SuppressLint("CheckResult")
    public static void showToast(String former, String latter) {
        // 在looper prepare()之后
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 获取当前应用的Context
                Context ctx = AndroidAppHelper.currentApplication().getApplicationContext();
                ApplicationInfo info = AndroidAppHelper.currentApplication().getApplicationInfo();
                if (ctx != null) {
                    PackageManager pm = ctx.getPackageManager();
                    if (info != null && pm != null) {
                        // 提示中指明产生事件的应用
                        String msg = former + "[" + info.loadLabel(pm) + "]" + latter;
                        // 避免一直弹重复的提示
                        if (!msg.equals(lastMsg)) {
                            Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
                            lastMsg = msg;
                        }
                    }
                }
            }
        });
    }
}
