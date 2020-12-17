package com.npmdev.privacyguard.xposed.hook;

import android.net.wifi.WifiInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.xposed.XpUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class DeviceSerialHooker {

    public static void handle(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Log.d(DeviceSerialHooker.class.getSimpleName(), lpparam.packageName);
            XSharedPreferences pre = XpUtil.getPrefs();
            // 设置被Hook的类名及方法，并给出随机非真实的返回值
            HookMethod(lpparam, TelephonyManager.class.getName(), lpparam.classLoader, "getDeviceId", pre.getString(Constant.IMEI, ""), "读取了IMEI");
            HookMethod(lpparam, TelephonyManager.class.getName(), lpparam.classLoader, "getSubscriberId", pre.getString(Constant.IMSI, ""), "读取了IMSI");
            HookMethod(lpparam, WifiInfo.class.getName(), lpparam.classLoader, "getMacAddress", pre.getString(Constant.WLAN_MAC, ""), "读取了MAC地址");

        } catch (Throwable e) {
            Log.d(DeviceSerialHooker.class.getSimpleName(), "failed to hook" + e.getMessage());
        }
    }

    public static void HookMethod(XC_LoadPackage.LoadPackageParam lpparam, String className, ClassLoader classLoader, String method, final String result, final String tip) {
        findAndHookMethod(className, classLoader, method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                XposedBridge.log(lpparam.packageName + " " + method + ": " + result);
                // 提示用户
                XpUtil.showToast(tip);
                // 返回随机值
                param.setResult(result);
            }
        });
    }
}
