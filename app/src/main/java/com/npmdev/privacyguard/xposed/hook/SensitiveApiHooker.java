package com.npmdev.privacyguard.xposed.hook;

import com.npmdev.privacyguard.xposed.XpUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SensitiveApiHooker {

    // 类名字符串
    private static final String AudioRecord = android.media.AudioRecord.class.getName();
    private static final String MediaRecorder = android.media.MediaRecorder.class.getName();
    private static final String Camera = android.hardware.Camera.class.getName();
    private static final String CameraManager = android.hardware.camera2.CameraManager.class.getName();

    public static void handle(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // 设置被Hook的类的方法，以及对用户的提示信息
            HookMethod(lpparam, AudioRecord, lpparam.classLoader, "startRecording", "开启了录音");
            HookMethod(lpparam, AudioRecord, lpparam.classLoader, "stop", "停止了录音");
            HookMethod(lpparam, AudioRecord, lpparam.classLoader, "getState", "获取了录音状态");
            HookMethod(lpparam, MediaRecorder, lpparam.classLoader, "setOutputFile", "输出了录音文件");
            HookMethod(lpparam, MediaRecorder, lpparam.classLoader, "start", "开启了录音/录像");
            HookMethod(lpparam, MediaRecorder, lpparam.classLoader, "stop", "停止了录音/录像");
            HookMethod(lpparam, Camera, lpparam.classLoader, "open", "拍了一张照");
            HookMethod(lpparam, Camera, lpparam.classLoader, "startPreview", "调用了相机");
            HookMethod(lpparam, Camera, lpparam.classLoader, "open", "开启了相机");
            HookMethod(lpparam, Camera, lpparam.classLoader, "openLegacy", "开启了相机");
            HookMethod(lpparam, CameraManager, lpparam.classLoader, "openCamera", "请求开启相机");

        } catch (Throwable e) {
            XposedBridge.log(SensitiveApiHooker.class.getSimpleName() + "failed to hook" + e.getMessage());
        }
    }

    public static void HookMethod(XC_LoadPackage.LoadPackageParam lpparam, String className, ClassLoader classLoader, String method, final String tip) {
        final Class<?> aClass;
        aClass = XposedHelpers.findClass(className, lpparam.classLoader);
        XposedBridge.hookAllMethods(aClass, method, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                XposedBridge.log(lpparam.packageName + " call " + className + "." + method);
                XpUtil.showToast(tip);
            }
        });
    }
}
