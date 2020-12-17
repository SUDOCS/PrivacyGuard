package com.npmdev.privacyguard.xposed.hook;

import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.npmdev.privacyguard.BuildConfig;
import com.npmdev.privacyguard.xposed.XpUtil;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class ApplicationPackageHooker {
    private static final String ClassName = "android.app.ApplicationPackageManager";


    public static void handle(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // 排除系统应用和自身
            if (lpparam.appInfo.flags == ApplicationInfo.FLAG_SYSTEM || BuildConfig.APPLICATION_ID.equals(lpparam.packageName)) {
                return;
            }
            // 设置被Hook的类的方法
            HookMethod(lpparam, ClassName, lpparam.classLoader, "getInstalledApplications");
            HookMethod(lpparam, ClassName, lpparam.classLoader, "getInstalledPackages");

        } catch (Throwable e) {
            Log.d(DeviceSerialHooker.class.getSimpleName(), "failed to hook" + e.getMessage());
        }
    }

    public static void HookMethod(XC_LoadPackage.LoadPackageParam lpparam, String className, ClassLoader classLoader, String method) {
        findAndHookMethod(className, classLoader, method, int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                XpUtil.showToast("已阻止", "读取应用列表");
                // 返回空列表
                List<ApplicationInfo> xp = new ArrayList<>();
                xp.clear();
                param.setResult(xp);
            }
        });
    }
}
