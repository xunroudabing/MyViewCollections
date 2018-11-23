package com.xunroudabing.myviewscollections.views.hicon.subsegment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.xunroudabing.myviewscollections.R;
import com.xunroudabing.myviewscollections.utils.SysUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 子区时距图控件
 *
 * @author hanzheng QQ:305058709 2016-9-5
 */
public class SubSegmentView extends SurfaceView implements Callback, Runnable,
        IGestureListener {

    static final int ACTION_DRAW = 100;
    static final int ACTION_PLAY = 101;
    static final String TAG = SubSegmentView.class.getSimpleName();
    final int PHASE_RECT_MARGIN = 2;// 周期矩形文字MARGIN dp
    final int PHASE_RECT_WIDTH = 16;// 周期文字矩形宽度 dp
    final int PHASE_RECT_HEIGHT = 10;// 周期文字矩形高度 dp
    final int GREEN_WIDTH = 5;// 绿色矩形宽度 dp
    final int MARKER_HEIGHT = 5;// 刻画的标尺高度 dp
    final int X_OFFSET = 10;// x轴0值偏移量 dp
    final int X_SPLIT = 200;// x轴间隔 米
    final int Y_SPLIT = 20;// y轴间隔 秒
    final int MARGIN_LEFT = 30;// dp
    final int MARGIN_BOTTOM = 30;// dp
    final int MARGIN_TOP = 30;// dp
    final int DRAW_PERIOD = 500;
    List<SubSegmentInfo> mData;
    boolean isRunning = false;
    Paint mPaintBlack, mTextPaintBlack, mPaintPink, mPaintGreen,
            mPaintLightGreen, mDashPaint, mPaintClear, mPaintOrange;
    // 触摸相关
    private SubSegmentInfo mCurrentInfo;
    private boolean isDragging = false;
    private float mLastMotionX, mLastMotionY;
    private float mInitialMotionX, mInitialMotionY;
    private float mScrollValue;
    private int mMarginLeft;
    private int mMarginBottom;
    private int mMarginTop;
    private int mPhaseRectMargin;
    private int mPhaseRectWidth;// 周期文字矩形宽度 单位像素
    private int mPhaseRectHeight;// 周期文字矩形高度 单位像素
    private int mAxisLength;// x轴与y轴长度
    private int mGreenWidth = 5; // 绿色矩形宽度 单位像素
    private int mCycleTime = 120;// 周期长 单位秒
    private int mTotalTime = 180;// 时间总长 单位秒 由mCycleTime计算得出
    private int mTotalDistance;// 数据总长 单位米
    private int mMarkerHeight = 5;// 刻线高度 单位像素
    private int mXOffset = 10;// x轴起点偏移 单位像素
    private int mXSplit = 200;// x轴间隔 单位米
    private int mYSplit = 20;// y轴间隔 单位秒
    private RefreshHandler mRefreshHandler = new RefreshHandler();
    /**
     * 当前View的尺寸
     */
    private int mWidth;
    private int mHeight;
    private ScheduledExecutorService mScheduledExecutor;
    private RectF mGamePanelRect = new RectF();
    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;

    public SubSegmentView(Context context) {
        this(context, null);
    }

    public SubSegmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubSegmentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);// 设置画布 背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常亮
        this.setKeepScreenOn(true);
        initResources();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        draw();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        isRunning = false;
        mScheduledExecutor.shutdown();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mGamePanelRect.set(0, 0, w, h);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int w = MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY);
        int h = MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY);
        super.onMeasure(w, h);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        if (!isRunning) {
            return;
        }
        long start = System.currentTimeMillis();
        draw();
        long end = System.currentTimeMillis();

        try {
            if (end - start < 50) {
                Thread.sleep(50 - (end - start));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        float x = event.getX();
        float y = event.getY();
        Log.i(TAG, "x=" + x + " y=" + y);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "ACTION_DOWN");
                SubSegmentInfo info = contains(x, y);
                if (info != null) {
                    mInitialMotionX = mLastMotionX = x;
                    mInitialMotionY = mLastMotionY = y;
                    mCurrentInfo = info;
                    isDragging = true;
                    startDrag();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "ACTION_MOVE");
                float diff = y - mLastMotionY;
                // float scrollvalue = mLastMotionY - mInitialMotionY;
                onDrag(diff);
                mLastMotionX = x;
                mLastMotionY = y;
                Log.i(TAG, "diff:" + diff);
                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "ACTION_UP");
                isDragging = false;
                endDrag();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void startDrag() {
        // TODO Auto-generated method stub
        play();
    }

    @Override
    public void onDrag(float value) {
        // TODO Auto-generated method stub
        int val = 1;
        // 向下滑
        if (value > 0) {
            val = -1;
        }
        // 向上滑
        else {
            val = 1;
        }
        if (mCurrentInfo != null) {
            mCurrentInfo.forphase += val;
            mCurrentInfo.revphase += val;
            if (mCurrentInfo.forphase >= mCycleTime) {
                mCurrentInfo.forphase = 0;
            }
            if (mCurrentInfo.revphase >= mCycleTime) {
                mCurrentInfo.revphase = 0;
            }
            if (mCurrentInfo.forphase < 0) {
                mCurrentInfo.forphase = mCycleTime;
            }
            if (mCurrentInfo.revphase < 0) {
                mCurrentInfo.revphase = mCycleTime;
            }
        }
    }

    @Override
    public void endDrag() {
        // TODO Auto-generated method stub
        pause();
    }

    public void postDraw() {
        draw();
    }

    protected void initResources() {
        mScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        int screenWidth = SysUtils.getScreenWidth(getContext());
        int screenHeight = SysUtils.getScreenHeight(getContext());
        mWidth = Math.min(screenWidth, screenHeight);
        mHeight = mWidth;
        mMarginLeft = SysUtils.dip2px(getContext(), MARGIN_LEFT);
        mMarginBottom = SysUtils.dip2px(getContext(), MARGIN_BOTTOM);
        mMarginTop = SysUtils.dip2px(getContext(), MARGIN_TOP);
        mXOffset = SysUtils.dip2px(getContext(), X_OFFSET);
        mMarkerHeight = SysUtils.dip2px(getContext(), MARKER_HEIGHT);
        mGreenWidth = SysUtils.dip2px(getContext(), GREEN_WIDTH);
        mPhaseRectWidth = SysUtils.dip2px(getContext(), PHASE_RECT_WIDTH);
        mPhaseRectHeight = SysUtils.dip2px(getContext(), PHASE_RECT_HEIGHT);
        mPhaseRectMargin = SysUtils.dip2px(getContext(), PHASE_RECT_MARGIN);
        mPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlack.setColor(getResources().getColor(R.color.black));
        mPaintBlack.setStrokeWidth(2F);
        mPaintPink = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintPink.setColor(0XFFCB589D);
        mPaintPink.setStrokeWidth(2F);
        mTextPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintBlack.setColor(getResources().getColor(R.color.black));
        mTextPaintBlack.setTextSize(20F);
        mPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGreen.setColor(getResources().getColor(R.color.green));
        mPaintLightGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLightGreen.setColor(getResources().getColor(R.color.wave_green));
        mDashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PathEffect effect = new DashPathEffect(new float[]{10, 5, 5, 5}, 0);
        mDashPaint = new Paint();
        mDashPaint.setColor(getResources().getColor(R.color.gray_light));
        mDashPaint.setStyle(Style.STROKE);
        mDashPaint.setStrokeWidth(2F);
        mDashPaint.setAntiAlias(true);
        mDashPaint.setPathEffect(effect);
        mPaintClear = new Paint();
        mPaintClear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mPaintOrange = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOrange.setColor(getResources().getColor(R.color.orange));
    }

    /**
     * 显示demo图表
     */
    public void playDemo() {
        List<SubSegmentInfo> mData = new LinkedList<SubSegmentInfo>();
        SubSegmentInfo i1 = new SubSegmentInfo();
        i1.name = "1一代机128.46";
        i1.cycleType = 1;
        i1.distance = 0;
        i1.forphase = 100;
        i1.revphase = 100;
        i1.forgreen = 48;
        i1.revgreen = 48;
        i1.forspeed = 36;
        i1.revspeed = 0;
        SubSegmentInfo i2 = new SubSegmentInfo();
        i2.name = "2一代机128.48_2";
        i2.distance = 500;
        i2.forphase = 33;
        i2.revphase = 64;
        i2.forgreen = 17;
        i2.revgreen = 17;
        i2.forspeed = 40;
        i2.revspeed = 50;
        SubSegmentInfo i3 = new SubSegmentInfo();
        i3.name = "3一代机128.48";
        i3.distance = 400;
        i3.forphase = 78;
        i3.revphase = 78;
        i3.forgreen = 42;
        i3.revgreen = 42;
        i3.forspeed = 0;
        i3.revspeed = 60;
        mData.add(i1);
        mData.add(i2);
        mData.add(i3);

        setBaseData(mData);
    }

    /**
     * 设置周期长 单位秒
     *
     * @param cycletime
     */
    public void setCycleTime(int cycletime) {
        mCycleTime = cycletime;
    }

    /**
     * 设置基础数据
     *
     * @param list
     */
    public void setBaseData(List<SubSegmentInfo> list) {
        mData = list;
        caculateParms();
    }

    // 开始绘制动画
    protected void play() {
        isRunning = true;
        mRefreshHandler.sendEmptyMessage(ACTION_PLAY);
    }

    // 暂停绘制动画
    protected void pause() {
        isRunning = false;
    }

    protected void playsample() {
        isRunning = true;
        mScheduledExecutor.scheduleAtFixedRate(this, 0, DRAW_PERIOD,
                TimeUnit.MILLISECONDS);
    }

    protected void caculatesample() {
        SubSegmentInfo info = mData.get(0);
        info.forphase += 10;
        if (info.forphase >= mCycleTime) {
            info.forphase = 0;
        }
    }

    protected void sample1() {
        mData = new ArrayList<SubSegmentInfo>();

        SubSegmentInfo i1 = new SubSegmentInfo();
        i1.name = "一代机128.46";
        i1.distance = 0;
        i1.forphase = 0;
        i1.revphase = 0;
        i1.forgreen = 48;
        i1.revgreen = 48;

        SubSegmentInfo i2 = new SubSegmentInfo();
        i2.name = "一代机128.48";
        i2.distance = 500;
        i2.forphase = 33;
        i2.revphase = 64;
        i2.forgreen = 17;
        i2.revgreen = 17;

        SubSegmentInfo i3 = new SubSegmentInfo();
        i3.name = "一代机128.48";
        i3.distance = 400;
        i3.forphase = 78;
        i3.revphase = 78;
        i3.forgreen = 42;
        i3.revgreen = 42;

        mData.add(i1);
        mData.add(i2);
        mData.add(i3);

    }

    // 触摸相关
    protected SubSegmentInfo contains(float x, float y) {
        if (mData == null) {
            return null;
        }
        int y1 = mMarginTop;
        int y2 = y1 + mAxisLength;
        for (SubSegmentInfo info : mData) {
            RectF rect = new RectF(info.x1, y1, info.x2, y2);
            if (rect.contains(x, y)) {
                return info;
            }
        }
        return null;
    }

    // 计算参数
    protected void caculateParms() {
        int totalDistance = 0;
        for (SubSegmentInfo item : mData) {
            totalDistance += item.distance;
        }
        int i = totalDistance / 1000 + 1;
        mXSplit = i * X_SPLIT;// x轴一段是多少米 单位米
        int j = Math.round(mCycleTime / 120F);
        j = Math.max(j, 1);
        mYSplit = j * Y_SPLIT;// y轴间隔是多少秒 单位秒
        mTotalDistance = 5 * mXSplit; // 总长度 单位米
        mTotalTime = 9 * mYSplit; // y轴总长 单位秒
        mAxisLength = mHeight - mMarginTop - mMarginBottom;// x轴y轴长度
        caculatePhase();
    }

    // 计算相位相关参数
    private void caculatePhase() {
        // 计算粉色竖线位置
        int distance = 0;
        int x = mMarginLeft + mXOffset;// x轴0值起点
        int a = mAxisLength - mXOffset;// x轴长度
        for (SubSegmentInfo info : mData) {
            distance += info.distance;
            int info_x = x + distance * a / mTotalDistance;
            info.x1 = info_x;
            info.x2 = info_x + 20;
        }
    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                // drawSomething..
                drawAxis();
                drawPhase();
                drawGreen();
                drawMixGreenText();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    // 画坐标轴
    private void drawAxis() {
        mCanvas.drawPaint(mPaintClear);
        int x1 = mMarginLeft;
        int y1 = mMarginTop;
        int x2 = mMarginLeft;
        int y2 = y1 + mAxisLength;
        // Y轴
        mCanvas.drawLine(x1, y1, x2, y2, mPaintBlack);

        int x3 = mMarginLeft;
        int y3 = y2;
        int x4 = x3 + mAxisLength;
        int y4 = y2;
        // X轴
        mCanvas.drawLine(x3, y3, x4, y4, mPaintBlack);

        int x = x3 + mXOffset;// x轴0值起点
        int a = mAxisLength - mXOffset;// x轴长度

        float x_span = a / 5;// x轴刻度间隔
        // 画x轴刻度
        for (int i = 0; i < 5; i++) {
            float mark_x = x + i * x_span;
            int mark_y = y2;
            mCanvas.drawLine(mark_x, mark_y, mark_x, mark_y + mMarkerHeight,
                    mPaintBlack);
            // 画刻度文字
            int val = i * mXSplit;
            mCanvas.drawText(String.valueOf(val), mark_x - 10, mark_y
                    + mMarkerHeight + 15, mTextPaintBlack);
        }
        // 画y轴刻度
        for (int i = 0; i < 9; i++) {
            int val = i * mYSplit;
            float mark_x = x1;
            float mark_y = y2 - mAxisLength * i / 9;
            mCanvas.drawLine(mark_x, mark_y, mark_x - mMarkerHeight, mark_y,
                    mPaintBlack);
            // 画刻度文字
            mCanvas.drawText(String.valueOf(val), mark_x - mMarkerHeight - 35,
                    mark_y + 5, mTextPaintBlack);
        }
        // 画周期虚线
        int i = 1;
        int y5 = y2;
        while (y5 > mMarginTop) {
            y5 = y2 - mCycleTime * i * mAxisLength / mTotalTime;
            mCanvas.drawLine(x1, y5, x1 + mAxisLength, y5, mDashPaint);
            i++;
        }

    }

    // 画相位
    private void drawPhase() {
        if (mData == null) {
            return;
        }
        if (mData.size() <= 0) {
            return;
        }

        int y1 = mMarginTop;// 最顶端的y坐标
        int y2 = y1 + mAxisLength;// 最底端的y坐标
        // 画粉色竖线
        for (SubSegmentInfo info : mData) {
            // 第一条
            mCanvas.drawLine(info.x1, y2, info.x1, y1, mPaintPink);
            // 第二条
            mCanvas.drawLine(info.x2, y2, info.x2, y1, mPaintPink);
        }

        // 计算绿色矩形
        for (SubSegmentInfo info : mData) {
            info.rect1.clear();
            info.rect2.clear();
            float mGreenHeight1 = info.forgreen * mAxisLength / mTotalTime;
            float mGreenHeight2 = info.revgreen * mAxisLength / mTotalTime;

            float info1_bottom_y = y2;// 正向矩形底部y坐标
            float info1_left_x = info.x1 - mGreenWidth / 2;
            int i = -1;
            int cycletime = mCycleTime;
            if (info.cycleType == 1) {
                cycletime = cycletime / 2;
            }
            while (info1_bottom_y > y1) {
                info1_bottom_y = y2 - (info.forphase + cycletime * i++)
                        * mAxisLength / mTotalTime;
                float info1_top_y = info1_bottom_y - mGreenHeight1;// 正向矩形顶部y坐标
                // 超出y轴最顶端
                if (info1_bottom_y <= y1) {
                    break;
                }
                // 在y轴最低端下面不绘制，忽略
                if (info1_top_y >= y2) {
                    continue;
                }
                info1_bottom_y = Math.min(info1_bottom_y, y2);
                info1_top_y = Math.max(info1_top_y, y1);// 如果超出y轴范围则进行截取
                RectF rect1 = new RectF(info1_left_x, info1_top_y, info1_left_x
                        + mGreenWidth, info1_bottom_y);
                info.rect1.add(rect1);
                mCanvas.drawRect(rect1, mPaintGreen);
            }

            float info2_bottom_y = y2;// 反向矩形底部y坐标
            float info2_left_x = info.x2 - mGreenWidth / 2;
            int j = -1;
            while (info2_bottom_y > y1) {
                info2_bottom_y = y2 - (info.revphase + cycletime * j++)
                        * mAxisLength / mTotalTime;
                float info2_top_y = info2_bottom_y - mGreenHeight2;
                if (info2_bottom_y <= y1) {
                    break;
                }
                if (info2_top_y >= y2) {
                    continue;
                }
                info2_bottom_y = Math.min(info2_bottom_y, y2);
                info2_top_y = Math.max(info2_top_y, y1);// 超出y轴范围则截取
                RectF rect2 = new RectF(info2_left_x, info2_top_y, info2_left_x
                        + mGreenWidth, info2_bottom_y);
                info.rect2.add(rect2);
                mCanvas.drawRect(rect2, mPaintGreen);
            }

        }
    }

    // 画绿波
    private void drawGreen() {
        if (mData == null) {
            return;
        }
        if (mData.size() <= 0) {
            return;
        }
        for (int i = 0; i < mData.size(); i++) {
            SubSegmentInfo info = mData.get(i);
            if (i < mData.size() - 1) {
                SubSegmentInfo info_next = mData.get(i + 1);
                drawGreen(info, info_next);
            }
        }

    }

    private void drawGreen(SubSegmentInfo info, SubSegmentInfo info_next) {
        for (RectF rect : info.rect1) {
            int speed = info.forspeed;
            speed = speed * 1000 / 3600;// km/h换算成m/s
            int distance = info_next.distance;
            float mGreenHeight1 = info.forgreen * mAxisLength / mTotalTime;
            float span = (distance * mAxisLength) / (speed * mTotalTime);
            float y_bottom = rect.bottom - span;
            float y_top = rect.top - span;
            if ((y_bottom - y_top) < mGreenHeight1) {
                y_bottom = y_top + mGreenHeight1;
            }
            for (RectF rect_next : info_next.rect1) {
                // 无交集
                if (rect_next.top >= y_bottom || rect_next.bottom <= y_top) {

                } else {
                    float y1 = 0F;
                    float y2 = 0F;
                    // 有交集
                    if (rect_next.top > y_top && rect_next.top < y_bottom) {
                        y1 = rect_next.top;
                        y2 = Math.min(rect_next.bottom, y_bottom);
                    } else if (rect_next.bottom > y_top
                            && rect_next.bottom < y_bottom) {
                        y1 = Math.max(rect_next.top, y_top);
                        y2 = rect_next.bottom;
                    }
                    Path path = new Path();
                    path.moveTo(info.x1, y2 + span);
                    path.lineTo(info_next.x1, y2);
                    path.lineTo(info_next.x1, y1);
                    path.lineTo(info.x1, y1 + span);
                    mCanvas.drawPath(path, mPaintLightGreen);

                    int formix = (int) Math.abs(((y1 - y2) * mTotalTime / mAxisLength));
                    info.formix = formix;
                }
            }
        }

        for (RectF rect : info_next.rect2) {
            int speed = info_next.revspeed;
            speed = speed * 1000 / 3600;// km/h换算成m/s
            int distance = info_next.distance;
            float mGreenHeight1 = info_next.revgreen * mAxisLength / mTotalTime;
            float span = (distance * mAxisLength) / (speed * mTotalTime);
            float y_bottom = rect.bottom - span;
            float y_top = rect.top - span;
            if ((y_bottom - y_top) < mGreenHeight1) {
                y_bottom = y_top + mGreenHeight1;
            }
            for (RectF rect_next : info.rect2) {
                // 无交集
                if (rect_next.top >= y_bottom || rect_next.bottom <= y_top) {

                } else {
                    float y1 = 0F;
                    float y2 = 0F;
                    // 有交集
                    if (rect_next.top > y_top && rect_next.top < y_bottom) {
                        y1 = rect_next.top;
                        y2 = Math.min(rect_next.bottom, y_bottom);
                    } else if (rect_next.bottom > y_top
                            && rect_next.bottom < y_bottom) {
                        y1 = Math.max(rect_next.top, y_top);
                        y2 = rect_next.bottom;
                    }
                    Path path = new Path();
                    path.moveTo(info_next.x2, y2 + span);
                    path.lineTo(info.x2, y2);
                    path.lineTo(info.x2, y1);
                    path.lineTo(info_next.x2, y1 + span);
                    mCanvas.drawPath(path, mPaintLightGreen);
                    int revmix = (int) Math.abs(((y1 - y2) * mTotalTime / mAxisLength));
                    info_next.revmix = revmix;
                }
            }
        }
    }

    //绘制相位数值文字
    protected void drawMixGreenText() {
        if (mData == null) {
            return;
        }
        if (mData.size() <= 0) {
            return;
        }

        int y1 = mMarginTop;// 最顶端的y坐标
        int y3 = y1 - mPhaseRectMargin;
        // 画周期文字
        for (SubSegmentInfo info : mData) {
            Rect rect1 = new Rect(info.x1 + mPhaseRectMargin - mPhaseRectWidth,
                    y3 - mPhaseRectHeight, info.x1 + mPhaseRectMargin, y3);
            Rect rect2 = new Rect(info.x2 - mPhaseRectMargin, y3
                    - mPhaseRectHeight, info.x2 - mPhaseRectMargin
                    + mPhaseRectWidth, y3);
            mCanvas.drawRect(rect1, mPaintOrange);
            mCanvas.drawRect(rect2, mPaintGreen);

            int y4 = y3 - mPhaseRectHeight - mPhaseRectMargin;
            Rect rect3 = new Rect(info.x1 + mPhaseRectMargin - mPhaseRectWidth,
                    y4 - mPhaseRectHeight, info.x1 + mPhaseRectMargin, y4);
            Rect rect4 = new Rect(info.x2 - mPhaseRectMargin, y4
                    - mPhaseRectHeight, info.x2 - mPhaseRectMargin
                    + mPhaseRectWidth, y4);
            mCanvas.drawRect(rect3, mPaintOrange);
            mCanvas.drawRect(rect4, mPaintGreen);

            FontMetricsInt fontMetrics = mTextPaintBlack
                    .getFontMetricsInt();
            int baseline = (rect1.bottom + rect1.top
                    - fontMetrics.bottom - fontMetrics.top) / 2;
            mTextPaintBlack.setTextAlign(Paint.Align.LEFT);
            mCanvas.drawText(String.valueOf(info.forphase), rect1.left + 1, baseline,
                    mTextPaintBlack);
            mCanvas.drawText(String.valueOf(info.formix), rect2.left + 1, baseline,
                    mTextPaintBlack);

            baseline = (rect3.bottom + rect3.top
                    - fontMetrics.bottom - fontMetrics.top) / 2;
            mCanvas.drawText(String.valueOf(info.revphase), rect3.left + 1, baseline,
                    mTextPaintBlack);
            mCanvas.drawText(String.valueOf(info.revmix), rect4.left + 1, baseline,
                    mTextPaintBlack);
        }
    }

    class RefreshHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case ACTION_DRAW:
                    draw();

                    break;
                case ACTION_PLAY:
                    draw();
                    if (isRunning) {
                        sendEmptyMessageDelayed(ACTION_PLAY, 100);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
