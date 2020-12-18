package com.npmdev.privacyguard.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.coniy.fileprefs.FileSharedPreferences;
import com.npmdev.privacyguard.BuildConfig;
import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.R;
import com.npmdev.privacyguard.databinding.FragmentHomeBinding;
import com.npmdev.privacyguard.util.CommonUtil;

public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    private static final String TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.topbar.setTitle(R.string.home_title);
        binding.topbar.addRightImageButton(R.drawable.ic_setting_24, R.id.topbar_right_button_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_Home_To_Settings);
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 在子线程中初始化UI
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 检查相应功能模块是否开启,通过开关状态展现在UI上,设置开关状态变化的监听回调
                initSerialProtectionView();
                initInstalledAppProtectionView();
                initSensitiveProtectionView();
            }
        });
    }
    /**
     * 设备序列号保护卡片
     */
    void initSerialProtectionView() {
        SharedPreferences sp = CommonUtil.getPrefs(getContext());
        // 是否开启
        boolean isEnable = sp.getBoolean(Constant.ENABLE_SERIAL_PROTECTION, false);
        // 设置开关状态变化监听回调
        binding.serialProtection.addOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i(TAG, "initSerialProtectionView: onCheckedChanged: " + isChecked);
                sp.edit().putBoolean(Constant.ENABLE_SERIAL_PROTECTION, isChecked).commit();
                if (isChecked) {
                    onSerialProtectionEnabled(sp);
                } else {
                    binding.serialProtection.setDetail("当应用请求获取IMEI等设备唯一序列号时，随即一个随机的字符串");
                }
                FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);
            }
        });
        // 设置开关状态
        binding.serialProtection.getSwitcher().setChecked(isEnable);
    }
    /**
     * 禁止读取本机应用卡片
     */
    void initInstalledAppProtectionView() {
        SharedPreferences sp = CommonUtil.getPrefs(getContext());
        // 是否开启
        boolean isEnable = sp.getBoolean(Constant.ENABLE_BAN_READ_INSTALLED_APPLICATION, false);
        // 设置开关状态变化监听回调
        binding.banReadingApps.addOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // 当开关状态变化时,立即保存,并设置xml文件为全局可读
                sp.edit().putBoolean(Constant.ENABLE_BAN_READ_INSTALLED_APPLICATION, isChecked).commit();
                FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);
            }
        });
        // 设置开关状态
        binding.banReadingApps.getSwitcher().setChecked(isEnable);
    }
    /**
     * 敏感API调用监控卡片
     */
    void initSensitiveProtectionView() {
        SharedPreferences sp = CommonUtil.getPrefs(getContext());
        boolean isEnable = sp.getBoolean(Constant.SENSITIVE_API_PROTECTION, false);
        binding.sensitiveApi.addOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sp.edit().putBoolean(Constant.SENSITIVE_API_PROTECTION, isChecked).commit();
                FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);
            }
        });
        binding.sensitiveApi.getSwitcher().setChecked(isEnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.serialProtection.getSwitcher().setOnCheckedChangeListener(null);
        binding = null;
    }

    @SuppressLint({"CommitPrefEdits", "ApplySharedPref"})
    void onSerialProtectionEnabled(SharedPreferences sp) {
        String IMEI = CommonUtil.getIMEI();
        String IMSI = CommonUtil.getIMSI();
        String MAC = CommonUtil.getMAC();
        sp.edit().putString(Constant.IMEI, IMEI).commit();
        sp.edit().putString(Constant.IMSI, IMSI).commit();
        sp.edit().putString(Constant.WLAN_MAC, MAC).commit();
        FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);

        StringBuilder sb = new StringBuilder();
        sb.append(binding.serialProtection.getDetail());
        sb.append("\nIMEI: ").append(IMEI).append("\nIMSI: ").append(IMSI).append("\n网卡MAC地址: ").append(MAC);
        binding.serialProtection.setDetail(sb.toString());
    }
}