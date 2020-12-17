package com.npmdev.privacyguard.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.SwitchCompat;

import com.npmdev.privacyguard.Constant;
import com.npmdev.privacyguard.databinding.ComponentSwitcherCardBinding;

/**
 * 自定义组合组件
 * 主界面带有switch的卡片
 */
public class SwitcherCard extends RelativeLayout {

    ComponentSwitcherCardBinding binding;

    public SwitcherCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SwitcherCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public SwitcherCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs);
    }

    void initView(Context context) {
        initView(context, null);
    }

    // 根据XML的配置初始化组件的标题等内容
    void initView(Context context, AttributeSet attrs) {
        binding = ComponentSwitcherCardBinding.inflate(LayoutInflater.from(context), this, true);
        if (attrs != null) {
            binding.title.setText(attrs.getAttributeValue(Constant.CUSTOM_NAMESPACE, "title"));
            binding.detailText.setText(attrs.getAttributeValue(Constant.CUSTOM_NAMESPACE, "detail"));
        }

        binding.switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    ///////////////////////////////////////////////////////////////////
    // 对外提供获取详细描述、修改详细描述、接口
    public String getDetail() {
        if (binding != null) {
            return binding.detailText.getText().toString();
        } else {
            return null;
        }
    }

    public void setDetail(String detail) {
        if (binding != null) {
            binding.detailText.setText(detail);
        }
    }

    public void addOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        if (binding != null) {
            binding.switcher.setOnCheckedChangeListener(listener);
        }
    }

    public SwitchCompat getSwitcher() {
        if (binding != null) {
            return binding.switcher;
        } else {
            return null;
        }
    }
    ///////////////////////////////////////////////////////////////////
}
