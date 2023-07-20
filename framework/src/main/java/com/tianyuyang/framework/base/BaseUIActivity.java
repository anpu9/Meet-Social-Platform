package com.tianyuyang.framework.base;

import android.os.Bundle;

import com.tianyuyang.framework.utils.SystemUI;

/**
 * FileName: BaseUIActivity
 * Founder: tianyuyang
 * Profile: UI 基类
 */
public class BaseUIActivity extends BaseActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SystemUI.fixSystemUI(this);
    }
}
