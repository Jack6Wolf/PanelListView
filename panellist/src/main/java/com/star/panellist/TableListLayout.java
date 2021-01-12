package com.star.panellist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 表格布局的父布局
 *
 * @author jack
 * @since 2021/1/12 10:14
 */
public class TableListLayout extends RelativeLayout {

    private TableListAdapter adapter;

    public TableListLayout(Context context) {
        super(context);
    }

    public TableListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TableListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TableListAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(TableListAdapter adapter) {
        this.adapter = adapter;
        adapter.initAdapter();
    }
}
