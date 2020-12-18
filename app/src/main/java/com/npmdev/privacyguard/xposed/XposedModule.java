package com.npmdev.privacyguard.xposed;


import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.xposed.hook.ApplicationPackageHooker;
import com.npmdev.privacyguard.xposed.hook.DeviceSerialHooker;
import com.npmdev.privacyguard.xposed.hook.LocationHooker;
import com.npmdev.privacyguard.xposed.hook.SensitiveApiHooker;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Xposed入口
 */
public class XposedModule implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private final String TAG = "Xposed.PrivacyGuard ";

    @Override
    public void initZygote(StartupParam startupParam) {
        XposedBridge.log(TAG + "initZygote");
        XpUtil.prefsInit();
    }

    /**
     * 在一个应用被启动时调用
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {

        // 查看当前应用是否在Hook名单内
        if (XpUtil.isAppInLimited(lpparam.packageName)) {
            // 在名单内

            // 看设备序列号保护是否开启
            if (XpUtil.getPrefs().getBoolean(Constant.ENABLE_SERIAL_PROTECTION, false)) {
                DeviceSerialHooker.handle(lpparam);
            }

            // 看敏感API调用监控是否开启
            if (XpUtil.getPrefs().getBoolean(Constant.SENSITIVE_API_PROTECTION, false)) {
                SensitiveApiHooker.handle(lpparam);
                LocationHooker.handle(lpparam);
            }

            // 看禁止读取已安装应用是开启
            if (XpUtil.getPrefs().getBoolean(Constant.ENABLE_BAN_READ_INSTALLED_APPLICATION, false)) {
                ApplicationPackageHooker.handle(lpparam);
            }
        }
    }
}
