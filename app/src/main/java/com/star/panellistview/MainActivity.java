package com.star.panellistview;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.star.panellist.TableListAdapter;
import com.star.panellist.TableListLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TableListLayout pl_root;
    private ListView lv_content;
    private TableListAdapter adapter;
    private List<List<String>> contentList = new ArrayList<>();
    private List<Integer> itemWidthList = new ArrayList<>();
    private List<String> rowDataList = new ArrayList<>();
    private List<String> columnDataList = new ArrayList<>();

    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pl_root = findViewById(R.id.id_pl_root);
        lv_content = findViewById(R.id.id_lv_content);

        //生成一份横向表头的内容
        rowDataList.add("第一列");
        rowDataList.add("第二列");
        rowDataList.add("第三列");
        rowDataList.add("第四列");
        rowDataList.add("第五列");
        rowDataList.add("第六列");
        rowDataList.add("第七列");
        //初始化content数据
        for (int i = 1; i <= 50; i++) {
            List<String> data = new ArrayList<>();
            data.add("第" + i + "行第一个");
            data.add("第" + i + "行第二个");
            data.add("第" + i + "行第三个");
            if (i == 9 && !flag) {
                data.add("第" + i + "行第四个dsadaddddddadaddddddddddddddddd");
                flag = true;
            } else {
                data.add("第" + i + "行第四个");
            }
            data.add("第" + i + "行第五个");
            data.add("第" + i + "行第六个");
            data.add("第" + i + "行第七个");
            contentList.add(data);
            columnDataList.add("第" + i + "行");
        }
        //初始化 content 部分每一个 item 的每个数据的宽度
        itemWidthList.add(50);
        itemWidthList.add(200);
        itemWidthList.add(100);
        itemWidthList.add(100);
        itemWidthList.add(100);
        itemWidthList.add(100);
        itemWidthList.add(100);

        adapter = new TableListAdapter(this, pl_root, lv_content) {
            @Override
            protected BaseAdapter getContentAdapter() {
                return new ContentAdapter(MainActivity.this, contentList);
            }
        };
        adapter.setRowDataList(rowDataList);
        adapter.setTitle("表");
        adapter.setStart("行\\列");
        adapter.setColumnWidth(100);
        adapter.setShowColumn(true);
        adapter.setStartBackgroundResource(R.color.colorPrimary);
        adapter.setColumnDataList(columnDataList);
        adapter.setItemWidthList(itemWidthList);
        pl_root.setAdapter(adapter);
    }

    private class ContentAdapter extends ArrayAdapter<List<String>> {

        public ContentAdapter(@NonNull Context context, List<List<String>> objects) {
            super(context, R.layout.defaultcontentitem, objects);
        }

        @Override
        public int getCount() {
            int count = super.getCount();
            Log.d("JACK", "getCount: " + count);
            return count;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            List<String> itemData = getItem(position);
            if (convertView == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.defaultcontentitem, parent, false);
                viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }
            for (int i = 0; i < itemData.size(); i++) {
                viewHolder.contentTextViewList.get(i).setText(itemData.get(i));
            }
            return view;
        }

        class ViewHolder {
            List<TextView> contentTextViewList = new ArrayList<>(10);

            ViewHolder(View view) {
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content1));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content2));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content3));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content4));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content5));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content6));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content7));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content8));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content9));
                contentTextViewList.add((TextView) view.findViewById(R.id.id_tv_content10));
                for (int i = 0; i < 10; i++) {
                    try {
                        contentTextViewList.get(i).setWidth(adapter.getItemWidthList().get(i));
                    } catch (Exception e) {
                        contentTextViewList.get(i).setWidth(0);
                    }
                }
            }
        }
    }
}