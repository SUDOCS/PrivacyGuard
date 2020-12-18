package com.npmdev.privacyguard.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.coniy.fileprefs.FileSharedPreferences;
import com.npmdev.privacyguard.BuildConfig;
import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.R;
import com.npmdev.privacyguard.databinding.FragmentSettingsBinding;
import com.npmdev.privacyguard.util.CommonUtil;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

public class SettingsFragment extends Fragment {

    FragmentSettingsBinding binding;

    int listModeVal = 0;
    QMUICommonListItemView listMode;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.topbar.setTitle(R.string.settings_title);
        binding.topbar.addLeftImageButton(R.drawable.ic_back_24, R.id.qmui_topbar_item_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(SettingsFragment.this).popBackStack();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initGroupViewHeader();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listModeVal = CommonUtil.getPrefs(getContext()).getInt(Constant.LIST_MODE, 0);
                updateListModeDetail(listModeVal);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener onHookModeClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showModelSheet();
        }
    };

    View.OnClickListener onHookEntranceClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            NavHostFragment.findNavController(SettingsFragment.this).navigate(R.id.action_Settings_To_AppList);
        }
    };

    void initGroupViewHeader() {
        QMUICommonListItemView hookEntrance = binding.settingGroup.createItemView("设置Hook对象");
        hookEntrance.setOrientation(QMUICommonListItemView.VERTICAL);
        hookEntrance.setDetailText("选择需要Hook的App名单");
        hookEntrance.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);


        listMode = binding.settingGroup.createItemView("设置名单控制模式");
        listMode.setOrientation(QMUICommonListItemView.VERTICAL);
        updateListModeDetail(listModeVal);

        QMUIGroupListView
                .newSection(getContext())
                .setTitle("Hook设置")
                .addItemView(hookEntrance, onHookEntranceClick)
                .addItemView(listMode, onHookModeClick)
                .addTo(binding.settingGroup);
    }

    /**
     * 弹出Hook模式选择
     */
    void showModelSheet() {
        QMUIBottomSheet.BottomListSheetBuilder builder = new QMUIBottomSheet.BottomListSheetBuilder(getActivity());
        builder.setGravityCenter(false)
                .setTitle("选则控制模式")
                .setAddCancelBtn(true)
                .setAllowDrag(true)
                .setNeedRightMark(true)
                .setCheckedIndex(listModeVal)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @SuppressLint("ApplySharedPref")
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        // 点击后保存选择结果
                        dialog.dismiss();
                        Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
                        listModeVal = position;
                        CommonUtil.getPrefs(getContext()).edit().putInt(Constant.LIST_MODE, position).commit();
                        FileSharedPreferences.makeWorldReadable(BuildConfig.APPLICATION_ID, Constant.PREF_FILENAME);
//                        CommonUtil.setWorldReadable(getActivity().getApplicationInfo().dataDir);
                        updateListModeDetail(position);
                    }
                });
        builder.addItem("仅Hook选中的App");
        builder.addItem("排除选中的App");
        builder.build().show();
    }

    void updateListModeDetail(int val) {
        if (listMode != null) {
            if (val == 0) {
                listMode.setDetailText("仅Hook选中的App");
            } else {
                listMode.setDetailText("排除选中的App");
            }
        }
    }
}