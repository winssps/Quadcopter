package com.example.wins.quadcopter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by kqq03 on 2017/10/27.
 */

/**
 * ImageView比较特殊，如果想让ImageView有点击效果，设置android:background是没用的，因为一般都会被图片挡住。我的解决办法是重写或者监听触摸事件，然后设置ImageView的滤色。
 */
public class ClickImageView extends ImageView {
    public ClickImageView(Context context) {
        super(context);
    }
    public ClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ClickImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /**
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.setColorFilter(0x99000000);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                this.setColorFilter(null);
                break;
        }
        return super.onTouchEvent(event);
    }**/
}
