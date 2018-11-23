package com.xunroudabing.myviewscollections.views.hicon.schemechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.xunroudabing.myviewscollections.R;

public class SchemePoint {
    static final String TAG = SchemePoint.class.getSimpleName();
    int mRC = 0;
    int mRCD = 0;
    int mSchemeWidth;
    int mSchemeHeight;
    Paint mPaint;
    RectF mRect;
    Context mContext;

    public SchemePoint(Context context, int schemeWidth, int schemeHeight, RectF bgRect) {
        mContext = context;
        mSchemeWidth = schemeWidth;
        mSchemeHeight = schemeHeight;
        mRect = bgRect;
        mPaint = new Paint();
        mPaint.setColor(mContext.getResources().getColor(R.color.black));
        mPaint.setStrokeWidth(2F);
    }

    /**
     * @param rc  总周期
     * @param rcd 当前周期
     */
    public void setData(int rc, int rcd) {
        mRC = rc;
        mRCD = rcd;
    }

    /**
     * 绘制自己
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        if (mRC <= 0) {
            return;
        }
        int start_x = (mSchemeWidth * (mRC - mRCD) / mRC) + (int) mRect.left;
        int end_y = mSchemeHeight;
        canvas.drawLine(start_x, 0, start_x, end_y, mPaint);
    }
}
