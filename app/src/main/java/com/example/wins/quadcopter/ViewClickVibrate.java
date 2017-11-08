package com.example.wins.quadcopter;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by kqq03 on 2017/10/27.
 */

public class ViewClickVibrate implements OnClickListener{
    /** 按钮震动时间 */
    private final int VIBRATE_TIME = 60;


    @Override
    public void onClick(View v) {
        // TODO 根据设置中的标记判断是否执行震动
        VibrateHelp.vSimple(v.getContext(), VIBRATE_TIME);
    }
}