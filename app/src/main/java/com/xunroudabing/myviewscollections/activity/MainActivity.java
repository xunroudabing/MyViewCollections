package com.xunroudabing.myviewscollections.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.xunroudabing.myviewscollections.R;
import com.xunroudabing.myviewscollections.views.hicon.schemechart.SchemeChart;

/**
 * Created by HanZheng(305058709@qq.com) on 2018-11-23.
 */

public class MainActivity extends ListActivity {
    BaseAdapter adapter;
    ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListView = getListView();
        Bind();
    }

    static final String[] NAMES = { "子区时距图","方案环图"};
    static final Class<?>[] CLAZZ = { SubSegmentViewSampleActivity.class , SchemeChartSampleActivity.class};

    protected void Bind() {
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, NAMES);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), CLAZZ[arg2]);
                startActivity(intent);
            }
        });
    }
}
