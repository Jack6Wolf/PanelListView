package com.star.panellist;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

/**
 * 两个横向SrollView同步滑动
 *
 * @author jack
 * @since 2021/1/12 10:15
 */
public class HorScrollView extends HorizontalScrollView {

    /**
     * 自定义的监听器
     */
    private OnHorizontalScrollListener listener;

    public HorScrollView(Context context) {
        super(context);
    }

    public HorScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnHorizontalScrollListener(OnHorizontalScrollListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // 通知自定义的listener
        if (listener != null) {
            listener.onHorizontalScrolled(this, l, t, oldl, oldt);
        }
        Log.e("JACK", "l:" + l + ",t:" + t + ",oldl:" + oldl + ",oldt:" + oldt);
    }

    /**
     * 内部接口，用来监听系统的onScrollChangedListener监听到的数据
     */
    interface OnHorizontalScrollListener {
        void onHorizontalScrolled(HorScrollView view, int l, int t, int oldl, int oldt);
    }
}
