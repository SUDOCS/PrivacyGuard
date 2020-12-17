package com.npmdev.privacyguard.xposed.hook;

import android.app.PendingIntent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;

import com.npmdev.privacyguard.xposed.XpUtil;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class LocationHooker {
    private static final String TAG = LocationHooker.class.getSimpleName();

    private static final String ClassName = "android.location.LocationManager";

    // 北京的经纬度
    private static final double BEIJING_LATITUDE = 39.92;
    private static final double BEIJING_LONGITUDE = 116.46;

    private static XC_LoadPackage.LoadPackageParam lpparam;

    public static void handle(XC_LoadPackage.LoadPackageParam lpp) {
        lpparam = lpp;
        addGpsStatusListener();
        addNmeaListener();
        addProximityAlert();
        getBestProvider();
        getLastKnownLocation();
        getProviders();
    }

    // 当被Hook应用调用addGpsStatusListener方法
    private static void addGpsStatusListener() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener", GpsStatus.Listener.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                if (param.args[0] != null) {
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 1);
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 3);

                    XpUtil.showToast("正监听GPS定位");
                }
            }
        });
    }

    //添加监听
    private static void addNmeaListener() {
        try {
            final Class<?> LocationManager;
            LocationManager = XposedHelpers.findClass(ClassName, lpparam.classLoader);
            XposedBridge.hookAllMethods(LocationManager, "addNmeaListener", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    param.args[0] = null;
                    //param.setResult(false);
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log(lpparam.packageName + TAG + e.toString());
        }
    }

    //报告有关网络状态的额外信息
    private static void addProximityAlert() {
        try {
            findAndHookMethod(ClassName, lpparam.classLoader, "addProximityAlert", double.class, double.class, float.class, long.class, PendingIntent.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    param.args[0] = 0;
                    param.args[1] = 0;
                }
            });
        } catch (NoSuchMethodError e) {
            XposedBridge.log(lpparam.packageName + TAG + e.toString());
        }
    }

    //最好的定位提供商 指定gps
    private static void getBestProvider() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                param.setResult("gps");
            }
        });
    }

    //提供商 位置
    private static void getLastLocation() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(BEIJING_LATITUDE);
                l.setLongitude(BEIJING_LONGITUDE);
                l.setAccuracy(100f);
                l.setTime(0);
                param.setResult(l);
                XpUtil.showToast("已将", "定位结果设置为北京");
            }
        });
    }

    //最近一次位置
    private static void getLastKnownLocation() {
        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(BEIJING_LATITUDE);
                l.setLongitude(BEIJING_LONGITUDE);
                l.setAccuracy(100f);
                l.setTime(0);
                param.setResult(l);
                XpUtil.showToast("已将", "定位结果设置为北京");
            }
        });
    }

    //定位提供商
    private static void getProviders() {
        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("gps");
                param.setResult(arrayList);
            }
        });
    }
}
