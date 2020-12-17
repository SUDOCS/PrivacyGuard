package com.npmdev.privacyguard.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.coniy.fileprefs.FileSharedPreferences;
import com.npmdev.privacyguard.BuildConfig;
import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.R;
import com.npmdev.privacyguard.databinding.FragmentAppListBinding;
import com.npmdev.privacyguard.util.CommonUtil;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.List;

/**
 * 设置中的APP勾选列表界面
 */
public class AppListFragment extends Fragment {

    FragmentAppListBinding binding;
    QMUIGroupListView.Section appGroup;

    public AppListFragment() {
        // Required empty public constructor
    }

    public static AppListFragment newInstance() {
        AppListFragment fragment = new AppListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppListBinding.inflate(inflater, container, false);
        // 设置标题
        binding.topbar.setTitle(R.string.app_list_title);
        // 设置左上角反扭按钮和点击事件
        binding.topbar.addLeftImageButton(R.drawable.ic_back_24, R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AppListFragment.this).popBackStack();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toast.makeText(getContext(), "正在加载应用列表", Toast.LENGTH_SHORT).show();
        // 在子线程读取已安装应用，更新UI
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadAppList();
            }
        }, 1000);

//        initView();
    }

    /**
     * 列表项点击事件
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof QMUICommonListItemView) {
                QMUICommonListItemView item = (QMUICommonListItemView) v;
                if (((QMUICommonListItemView) v).getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM) {
                    CheckBox checkBox = (CheckBox) item.getAccessoryContainerView().getChildAt(0);
                    checkBox.setChecked(!checkBox.isChecked());
                }
            }
        }
    };

    /**
     * 加载已安装的用户应用，构建更新视图
     */
    void loadAppList() {
        appGroup = QMUIGroupListView.newSection(getContext()).setTitle("勾选应用");

        SharedPreferences sp = CommonUtil.getPrefs(getContext());
        PackageManager pm = getContext().getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            String packageName = packageInfo.packageName;
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                QMUICommonListItemView appItem = binding.appList.createItemView(packageInfo.applicationInfo.loadLabel(pm).toString());
                appItem.setOrientation(QMUICommonListItemView.VERTICAL);
                appItem.setDetailText(packageName);
                appItem.setImageDrawable(packageInfo.applicationInfo.loadIcon(pm));
                appItem.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                CheckBox checkBox = new CheckBox(getContext());
                boolean is = sp.getBoolean(packageName, false);
                checkBox.setChecked(is);
                // 设置checkbox的状态变化事件回调
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        toggleAppInHook(packageName, isChecked);
                    }
                });
                appItem.addAccessoryCustomView(checkBox);
                // 加入View以及点击事件
                appGroup.addItemView(appItem, onClickListener);
            } else {
                // 系统应用
            }
        }
        binding.loadingView.setVisibility(View.INVISIBLE);
        appGroup.addTo(binding.appList);
    }

    /**
     * 当checkbox状态变化，修改相应应用的记录
     * @param packageName  被点击的应用的包名
     * @param isChecked 是否选中
     */
    @SuppressLint("ApplySharedPref")
    void toggleAppInHook(String packageName, boolean isChecked) {
        System.out.println("set: " + packageName + " to " + isChecked);
        CommonUtil.getPrefs(getContext()).edit().putBoolean(packageName, isChecked).commit();
        FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);
//        CommonUtil.setWorldReadable(getActivity().getApplicationInfo().dataDir);
    }
}