package com.xunroudabing.myviewscollections.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xunroudabing.myviewscollections.R;
import com.xunroudabing.myviewscollections.views.hicon.schemechart.SchemeChart;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 方案环图
 */
public class SchemeChartSampleActivity extends AppCompatActivity {
    SchemeChart chart1, chart2;
    int i = 0;
    Disposable mDisposable;
    static final String TAG = SchemeChartSampleActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mDisposable != null) {
            mDisposable.dispose();
        }
    }

    protected void initViews() {
        setContentView(R.layout.activity_schemechart);
        chart1 = findViewById(R.id.schemeChart1);
        chart1.playDemo();
        chart2 = findViewById(R.id.schemeChart2);
        chart2.playDemo();
        final int mRC = chart2.getRC();
        mDisposable = Flowable.interval(1000, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        i++;
                        if (i++ > mRC) {
                            i = 0;
                        }
                        chart2.setRCD(mRC - i, mRC);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG,throwable.toString());
                    }
                });
    }
}
