package com.star.panellist;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * 控制表头/内容表格适配器
 *
 * @author jack
 * @since 2021/1/12 10:14
 */

public abstract class TableListAdapter {

    private static final String TAG = "TableListAdapter";

    private Context context;

    /**
     * 两个横向滑动layout
     */
    //行头
    private HorScrollView mhsvRow;
    //内容横向滑动
    private HorScrollView mhsvContent;

    /**
     * 整个页面的所有布局
     */
    private TableListLayout plRoot;//外层的根布局
    private TextView tvTitle;//表的title
    private TextView startTv;//表的表头
    private LinearLayout llRow;//上方的表头
    private ListView lvColumn;//左方的列头
    private ListView lvContent;//中间部分
    private LinearLayout llContentItem;//中间的内容部分(lv_content)的子布局

    /**
     * 标题的高,同时也是表行头的高
     */
    private int titleHeight = 100;
    private int columnWidth = 100;

    private String title = "";
    private String start = "";
    private int titleBackgroundResource;
    private int startBackgroundResource;
    private int titleTextSize;
    private int rowColumnTextSize;
    private List<String> rowDataList;
    private List<String> columnDataList;
    //标题和横向表头字体的颜色
    private String titleColor = "#000000";
    //横向表头的背景色
    private String rowColor = "#666666";
    private String columnColor = "#666666";
    //横向表头的分格线
    private Drawable rowDivider;
    private int initPosition = 0;//列表显示的初始值，默认第一条数据显示在最上面
    private BaseAdapter contentAdapter;//listview的adapter
    /**
     * 是否显示列头
     */
    private boolean isShowColumn = false;
    private ColumnAdapter columnAdapter;

    /**
     * 两个监听器，分别控制水平和垂直方向上的同步滑动
     */
    private HorizontalScrollListener horizontalScrollListener = new HorizontalScrollListener();
    private VerticalScrollListener verticalScrollListener = new VerticalScrollListener();
    //表格每列的宽度 单位：v
    private List<Integer> itemWidthList;

    /**
     * constructor
     *
     * @param lvContent 内容的ListView
     */
    public TableListAdapter(Context context, TableListLayout tableListLayout, ListView lvContent) {
        this.context = context;
        this.plRoot = tableListLayout;
        this.lvContent = lvContent;
    }

    public List<Integer> getItemWidthList() {
        return itemWidthList;
    }

    /**
     * 设置表每列的宽度
     */
    public void setItemWidthList(List<Integer> itemWidthList) {
        this.itemWidthList = parseDpList2PxList(itemWidthList);
    }

    /**
     * 设置表的标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 设置表的开始页面
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * 设置表标题的背景
     */
    public void setTitleBackgroundResource(int resourceId) {
        this.titleBackgroundResource = resourceId;
    }

    /**
     * 设置标题字体大小
     */
    public void setTitleTextSize(int titleTextSize) {
        this.titleTextSize = titleTextSize;
    }

    /**
     * 设置表头字体大小
     */
    public void setRowColumnTextSize(int rowColumnTextSize) {
        this.rowColumnTextSize = rowColumnTextSize;
    }

    /**
     * 设置表头背景
     */
    public void setStartBackgroundResource(int resourceId) {
        this.startBackgroundResource = resourceId;
    }

    /**
     * 设置表头和表标题的高度
     */
    public void setTitleHeight(int titleHeight) {
        this.titleHeight = dp2px(titleHeight);
    }

    /**
     * 设置表头宽度
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = dp2px(columnWidth);
    }

    /**
     * 横向表头的分割线
     */
    public void setRowDivider(Drawable rowDivider) {
        this.rowDivider = rowDivider;
    }

    /**
     * 设置标题和横向表头的字体色
     */
    public void setTitleColor(String titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * 设置横向表头的背景色
     */
    public void setRowColor(String rowColor) {
        this.rowColor = rowColor;
    }

    /**
     * 设置竖向表头的背景色
     */
    public void setColumnColor(String columnColor) {
        this.columnColor = columnColor;
    }

    /**
     * 设置content的初始position
     */
    public void setInitPosition(int initPosition) {
        this.initPosition = initPosition;
    }

    /**
     * 返回中间内容部分的ListView
     */
    public ListView getContentListView() {
        return lvContent;
    }

    /**
     * 在该方法中返回contentList的adapter
     *
     * @return content部分的adapter
     */
    protected abstract BaseAdapter getContentAdapter();

    /**
     * 初始化总Adapter，加载数据到视图
     */
    void initAdapter() {
        contentAdapter = getContentAdapter();
        if (contentAdapter == null) {
            throw new NullPointerException("You Must Set BaseContentAdapter!");
        }
        reorganizeViewGroup();
        mhsvRow.setOnHorizontalScrollListener(horizontalScrollListener);
        mhsvContent.setOnHorizontalScrollListener(horizontalScrollListener);
        if (isShowColumn)
            lvContent.setOnScrollListener(verticalScrollListener);
    }

    /**
     * 更新ContentList数据后需要调用此方法来刷新列表
     */
    public void notifyDataSetChanged() {
        // 先刷新lv_content的数据，然后根据判断决定是否要刷新表头的数据
        contentAdapter.notifyDataSetChanged();
    }


    /**
     * 整理重组整个表的布局
     * 主要包含4个部分
     * 1. title
     * 2. row
     * 3. column
     * 4. content
     */
    private void reorganizeViewGroup() {
        lvContent.setAdapter(contentAdapter);
        lvContent.setVerticalScrollBarEnabled(true);

        // clear root viewGroup
        plRoot.removeView(lvContent);

        // 1. 添加title (TextView --> TableListAdapter)
        tvTitle = new TextView(context);
        tvTitle.setText(title);
        if (titleBackgroundResource != 0) {
            tvTitle.setBackgroundResource(titleBackgroundResource);
        }
        if (titleTextSize != 0)
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, titleTextSize);
        tvTitle.getPaint().setFakeBoldText(true);
        tvTitle.setGravity(Gravity.CENTER);
        tvTitle.setTextColor(Color.parseColor(titleColor));
        tvTitle.setId(IdiUtils.generateViewId());//设置一个随机id，这样可以保证不冲突
        RelativeLayout.LayoutParams lpTvTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, titleHeight);
        plRoot.addView(tvTitle, lpTvTitle);

        if (isShowColumn) {
            startTv = new TextView(context);
            startTv.setText(start);
            startTv.setTextColor(Color.parseColor(titleColor));
            if (startBackgroundResource != 0)
                startTv.setBackgroundResource(startBackgroundResource);
            if (rowColumnTextSize != 0)
                startTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowColumnTextSize);
            startTv.getPaint().setFakeBoldText(true);
            startTv.setGravity(Gravity.CENTER);
            startTv.setId(IdiUtils.generateViewId());//设置一个随机id，这样可以保证不冲突
            RelativeLayout.LayoutParams lpTvStart = new RelativeLayout.LayoutParams(columnWidth, titleHeight);
            lpTvStart.addRule(RelativeLayout.BELOW, tvTitle.getId());
            plRoot.addView(startTv, lpTvStart);
        }

        // 2. 添加行（LinearLayout --> MyHorizontalScrollView --> TableListAdapter）
        llRow = new LinearLayout(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        llRow.setLayoutParams(lp);
        mhsvRow = new HorScrollView(context);
        mhsvRow.setHorizontalScrollBarEnabled(false);
        mhsvRow.setOverScrollMode(View.OVER_SCROLL_NEVER);//去除滑动到边缘时出现的阴影
        mhsvRow.addView(llRow);//暂时先不给ll_row添加子view，等布局画出来了再添加
        mhsvRow.setId(IdiUtils.generateViewId());
        RelativeLayout.LayoutParams lpMhsvRow = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, titleHeight);
        //在title的下面
        lpMhsvRow.addRule(RelativeLayout.BELOW, tvTitle.getId());
        if (isShowColumn)
            lpMhsvRow.addRule(RelativeLayout.RIGHT_OF, startTv.getId());
        plRoot.addView(mhsvRow, lpMhsvRow);

        // 3. column （ListView --> TableListAdapter）
        if (isShowColumn) {
            lvColumn = new ListView(context);
            lvColumn.setBackgroundColor(Color.parseColor(columnColor));
            lvColumn.setSelector(R.drawable.selector_bg);
            lvColumn.setId(IdiUtils.generateViewId());
            lvColumn.setVerticalScrollBarEnabled(false);//去掉滚动条
            RelativeLayout.LayoutParams lpLvColumn = new RelativeLayout.LayoutParams(columnWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            lpLvColumn.addRule(RelativeLayout.BELOW, startTv.getId());
            plRoot.addView(lvColumn, lpLvColumn);
            lvColumn.setOnScrollListener(verticalScrollListener);
        }

        // 4. content (ListView --> MyHorizontalScrollView  --> TableListAdapter)
        mhsvContent = new HorScrollView(context);
        mhsvContent.setHorizontalScrollBarEnabled(false);
        mhsvContent.setOverScrollMode(View.OVER_SCROLL_NEVER);//去除滑动到边缘时出现的阴影
        mhsvContent.addView(lvContent);//因为 lv_content 在 xml 文件中已经设置了 layout 为 match_parent，所以这里add时不需要再加 LayoutParameter 对象
        RelativeLayout.LayoutParams lpSrl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lpSrl.addRule(RelativeLayout.BELOW, mhsvRow.getId());
        if (isShowColumn)
            lpSrl.addRule(RelativeLayout.RIGHT_OF, lvColumn.getId());
        plRoot.addView(mhsvContent, lpSrl);

        // 发一个消息出去。当布局渲染完成之后会执行消息内容，此时
        plRoot.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "post--lv_content = " + lvContent.toString());
                llContentItem = (LinearLayout) lvContent.getChildAt(0);//获得content的第一个可见item
                initRowLayout();
                initColumnLayout();
                // 当ListView绘制完成后设置初始位置，否则ll_contentItem会报空指针
                lvContent.setSelection(initPosition);
            }
        });
    }

    /**
     * 初始化列头的布局，必须在所有的布局都载入完之后才能调用
     * must be called in pl_root.post();
     */
    private void initColumnLayout() {
        if (isShowColumn) {
            lvColumn.setAdapter(getColumnAdapter());
            if (lvContent != null) {
                lvColumn.setDivider(lvContent.getDivider());
                lvColumn.setDividerHeight(dp2px(0.5f));
            }
        }
    }

    /**
     * 返回纵向表头的适配器
     * 可重写
     */
    protected BaseAdapter getColumnAdapter() {
        if (columnAdapter == null) {
            columnAdapter = new ColumnAdapter(context, getColumnDataList());
        }
        return columnAdapter;
    }


    /**
     * 初始化横向表头的布局，必须在所有的布局都载入完之后才能调用
     * must be called in pl_root.post();
     */
    private void initRowLayout() {
        Integer[] widthArray = new Integer[getRowDataList().size()];
        if (itemWidthList != null) {
            for (int i = 0; i < widthArray.length; i++) {
                widthArray[i] = itemWidthList.get(i);
            }
        } else {
            if (llContentItem != null) {
                for (int i = 0; i < widthArray.length; i++) {
                    widthArray[i] = llContentItem.getChildAt(i).getWidth();
                }
            } else {
                Log.w(TAG, "You Must Set itemWidthList ");
            }
        }
        //设置横向表头
        addRowTitle(widthArray);
    }

    /**
     * 设置横向表头
     */
    private void addRowTitle(Integer[] widthArray) {
        List<String> rowDataList = getRowDataList();
        int rowCount = rowDataList.size();
        //分隔线的设置，如果content的item设置了分割线，那row使用相同的分割线，除非单独给row设置了分割线
        if (rowDivider == null) {
            if (llContentItem != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    llRow.setDividerDrawable(llContentItem.getDividerDrawable());
                    llRow.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                }
            }
        } else {
            llRow.setDividerDrawable(rowDivider);
            llRow.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        }
        //横向表头每一个item的宽度都取决于content的item的宽度
        for (int i = 0; i < rowCount; i++) {
            TextView rowItem = new TextView(context);
            rowItem.setText(rowDataList.get(i));//设置文字
            rowItem.getPaint().setFakeBoldText(true);
            rowItem.setWidth(widthArray[i]);//设置宽度
            rowItem.setHeight(titleHeight);//设置高度
            rowItem.setGravity(Gravity.CENTER);
            rowItem.setTextColor(Color.parseColor(titleColor));
            rowItem.setBackgroundColor(Color.parseColor(rowColor));
            if (rowColumnTextSize != 0)
                rowItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, rowColumnTextSize);
            llRow.addView(rowItem);
        }
    }

    /**
     * 返回横向表头的内容列表
     */
    private List<String> getRowDataList() {
        if (rowDataList == null) {
            try {
                throw new Exception("you must set your column data list by calling setColumnDataList(List<String>)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rowDataList;
    }

    /**
     * 设置横向表头的标题（！！必须调用！！）
     *
     * @param rowDataList data list of row layout, must be a List<String>
     */
    public void setRowDataList(List<String> rowDataList) {
        this.rowDataList = rowDataList;
    }

    public List<String> getColumnDataList() {
        return columnDataList;
    }

    /**
     * 设置竖向的列头标题
     */
    public void setColumnDataList(List<String> columnDataList) {
        this.columnDataList = columnDataList;
    }

    public boolean isShowColumn() {
        return isShowColumn;
    }

    /**
     * 是否显示列头
     */
    public void setShowColumn(boolean showColumn) {
        isShowColumn = showColumn;
    }

    private int dp2px(float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private List<Integer> parseDpList2PxList(List<Integer> itemWidthList) {
        List<Integer> itemWidthListInPx = new ArrayList<>();
        for (int i = 0; i < itemWidthList.size(); i++) {
            itemWidthListInPx.add(dp2px(itemWidthList.get(i)));
        }
        return itemWidthListInPx;
    }

    /**
     * HorizontalScrollView的滑动监听（水平方向同步控制）
     */
    private class HorizontalScrollListener implements HorScrollView.OnHorizontalScrollListener {
        @Override
        public void onHorizontalScrolled(HorScrollView view, int l, int t, int oldl, int oldt) {
            if (view == mhsvContent) {
                mhsvRow.scrollTo(l, t);
            } else {
                mhsvContent.scrollTo(l, t);
            }
        }
    }

    private class VerticalScrollListener implements AbsListView.OnScrollListener {
        int scrollState;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (!isShowColumn)
                return;
            this.scrollState = scrollState;
            //正在滑动中或滑动停止
            if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                View subView = view.getChildAt(0);
                if (subView != null && view == lvContent) {
                    int top = subView.getTop();
                    int position = view.getFirstVisiblePosition();
                    lvColumn.setSelectionFromTop(position, top);
                } else if (subView != null && view == lvColumn) {
                    int top = subView.getTop();
                    int position = view.getFirstVisiblePosition();
                    lvContent.setSelectionFromTop(position, top);
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!isShowColumn)
                return;
            //判断滑动是否终止，以停止自动对齐，否则该方法会一直被调用，影响性能
            if (scrollState == SCROLL_STATE_IDLE) {
                return;
            }
            View subView = view.getChildAt(0);
            if (subView != null && view == lvContent) {
                int top = subView.getTop();
                lvColumn.setSelectionFromTop(firstVisibleItem, top);
            } else if (subView != null && view == lvColumn) {
                int top = subView.getTop();
                lvContent.setSelectionFromTop(firstVisibleItem, top);
            }
        }
    }

    /**
     * 默认的columnAdapter
     * 之所以重写是为了根据content的item之高度动态设置column的item之高度
     */
    private class ColumnAdapter extends ArrayAdapter<String> {
        private List<String> columnDataList;

        ColumnAdapter(@NonNull Context context, @NonNull List<String> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            columnDataList = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = new TextView(context);
                ((TextView) view).setGravity(Gravity.CENTER);
                if (rowColumnTextSize != 0)
                    ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_SP, rowColumnTextSize);
                if (llContentItem != null)
                    ((TextView) view).setHeight(llContentItem.getHeight());
            } else {
                view = convertView;
            }
            ((TextView) view).setText(columnDataList.get(position));
            return view;
        }
    }
}