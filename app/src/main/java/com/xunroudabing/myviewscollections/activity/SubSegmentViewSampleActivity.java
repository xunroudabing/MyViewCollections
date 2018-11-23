package com.xunroudabing.myviewscollections.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xunroudabing.myviewscollections.R;
import com.xunroudabing.myviewscollections.views.hicon.subsegment.SubSegmentView;

/**
 * 子区时距图控件
 * Created by HanZheng(305058709@qq.com) on 2018-11-23.
 */

public class SubSegmentViewSampleActivity extends AppCompatActivity {
    SubSegmentView mView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }
    protected void initViews(){
        setContentView(R.layout.activity_subsegment);
        mView = findViewById(R.id.subsegmentView);
        mView.playDemo();
    }
}
