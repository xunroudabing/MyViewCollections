package com.xunroudabing.myviewscollections.views.hicon.schemechart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.xunroudabing.myviewscollections.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 方案环图
 *
 * @author hanzheng QQ:305058709 2015-12-15
 */
public class SchemeChart extends SurfaceView implements Callback, Runnable {
    static final long DRAW_PERIOD = 500;// 绘图间隔时间 ms
    static final String TAG = SchemeChart.class.getSimpleName();
    static int ROW_HEIGHT = 80;
    static int ROW_MARGIN = 40;
    boolean drawCurrency = true;// 是否绘制屏障
    int mRC;// 总周期
    int mRCD;// 当前周期
    List<Cycle> mCycles = new ArrayList<Cycle>();
    Paint mPaintGreen, mPaintYellow, mPaintRed, mPaintBlack, mPaintStroke,
            mPaintClear, mPaintCurrency, mTextPaintRed;
    SchemePoint mSchemePoint;// 指针
    Bitmap mBufferBitmap;
    int TOTAL_HEIGHT = 300;
    /**
     * 当前View的尺寸
     */
    private int mWidth;
    private int mHeight;
    private RectF mGamePanelRect = new RectF();
    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private ScheduledExecutorService mScheduledExecutor;
    /**
     * 线程的控制开关
     */
    private boolean isRunning = true;

    public SchemeChart(Context context) {
        this(context, null);
    }

    public SchemeChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SchemeChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        isRunning = true;
        mScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutor.scheduleAtFixedRate(this, 0, DRAW_PERIOD,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 通知关闭线程
        isRunning = false;
        mScheduledExecutor.shutdown();
        mScheduledExecutor = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h = MeasureSpec.makeMeasureSpec(TOTAL_HEIGHT, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, h);
    }

    ;

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "width=" + w + " height=" + h + "paddingLeft:"
                + getPaddingLeft() + "paddingRight:" + getPaddingRight());
        mWidth = w - getPaddingLeft() - getPaddingRight();
        mHeight = h;
        mGamePanelRect.set(0 + getPaddingLeft(), 0, w - getPaddingRight(), h);
        mSchemePoint = new SchemePoint(getContext(), mWidth, mHeight,
                mGamePanelRect);
        mBufferBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        createBufferBitmap();
    }

    /**
     * 生成静态图,需在setBaseData方法后调用
     */
    public void postDraw() {
        draw();
    }

    /**
     * 设置是否绘制屏障
     *
     * @param draw
     */
    public void setDrawCurrency(boolean draw) {
        drawCurrency = draw;
    }
    public void playDemo(){
        String json = "{\"CycleBase\": [1, 2, 3], \"CurCycle\": 1, \"CycleData\": [{\"Cycle\": \"1\", \"Phase\": [{\"Channel\": \"3\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\": \"1\", \"Direction\": \"5\", \"RTime\": \"0\", \"Currency\": \"16\"}, {\"Channel\": \"2\", \"MGTime\": \"7\", \"GTime\": \"41\", \"YTime\": \"3\", \"PName\": \"9\", \"Direction\": \"1\", \"RTime\": \"3\", \"Currency\": \"16\"}, {\"Channel\": \"7\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\": \"2\", \"Direction\": \"37\", \"RTime\": \"0\", \"Currency\": \"32\"}, {\"Channel\": \"6\", \"MGTime\": \"7\", \"GTime\": \"69\", \"YTime\": \"3\", \"PName\": \"10\", \"Direction\": \"40\", \"RTime\": \"3\", \"Currency\": \"32\"}, {\"Channel\": \"9,12,20\", \"MGTime\": \"7\", \"GTime\": \"51\", \"YTime\": \"3\", \"PName\": \"3\", \"Direction\": \"65,66,45\", \"RTime\": \"3\", \"Currency\": \"1088\"}, {\"Channel\": \"13,16\", \"MGTime\": \"7\", \"GTime\": \"45\", \"YTime\": \"3\", \"PName\": \"4\", \"Direction\": \"97,104\", \"RTime\": \"3\", \"Currency\": \"2176\"}]}, {\"Cycle\": \"2\", \"Phase\": [{\"Channel\": \"1,4,17\", \"MGTime\": \"7\", \"GTime\": \"50\", \"YTime\": \"3\", \"PName\": \"5\", \"Direction\": \"8,2,109\", \"RTime\": \"3\", \"Currency\": \"257\"}, {\"Channel\": \"5,8\", \"MGTime\": \"7\", \"GTime\": \"78\", \"YTime\": \"3\", \"PName\": \"6\", \"Direction\": \"33,40\", \"RTime\": \"3\", \"Currency\": \"514\"}, {\"Channel\": \"11\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\": \"7\", \"Direction\": \"69\", \"RTime\": \"0\", \"Currency\": \"4\"}, {\"Channel\": \"10\", \"MGTime\": \"7\", \"GTime\": \"42\", \"YTime\": \"3\", \"PName\": \"11\", \"Direction\": \"72\", \"RTime\": \"3\", \"Currency\": \"4\"}, {\"Channel\": \"15\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\": \"8\", \"Direction\": \"101\", \"RTime\": \"0\", \"Currency\": \"8\"}, {\"Channel\": \"14\", \"MGTime\": \"7\", \"GTime\": \"36\", \"YTime\": \"3\", \"PName\": \"12\", \"Direction\": \"98\", \"RTime\": \"3\", \"Currency\": \"8\"}]}]}";
        try {
            JSONObject object = new JSONObject(json);
            JSONArray data = object.getJSONArray("CycleData");
            setBaseData(data);
            postDraw();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    /**
     * 设置基础数据
     * {\"CycleBase\": [1, 2, 3], \"CurCycle\": 1, \"CycleData\": [{\"Cycle\": \"1\", \"Phase\":
     * [{\"Channel\": \"3\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\":
     * \"1\", \"Direction\": \"5\", \"RTime\": \"0\", \"Currency\": \"16\"}, {\"Channel\": \"2\",
     * \"MGTime\": \"7\", \"GTime\": \"41\", \"YTime\": \"3\", \"PName\": \"9\", \"Direction\":
     * \"1\", \"RTime\": \"3\", \"Currency\": \"16\"}, {\"Channel\": \"7\", \"MGTime\": \"6\",
     * \"GTime\": \"9\", \"YTime\": \"3\", \"PName\": \"2\", \"Direction\": \"37\", \"RTime\":
     * \"0\", \"Currency\": \"32\"}, {\"Channel\": \"6\", \"MGTime\": \"7\", \"GTime\": \"69\",
     * \"YTime\": \"3\", \"PName\": \"10\", \"Direction\": \"40\", \"RTime\": \"3\", \"Currency\":
     * \"32\"}, {\"Channel\": \"9,12,20\", \"MGTime\": \"7\", \"GTime\": \"51\", \"YTime\": \"3\",
     * \"PName\": \"3\", \"Direction\": \"65,66,45\", \"RTime\": \"3\", \"Currency\": \"1088\"},
     * {\"Channel\": \"13,16\", \"MGTime\": \"7\", \"GTime\": \"45\", \"YTime\": \"3\", \"PName\":
     * \"4\", \"Direction\": \"97,104\", \"RTime\": \"3\", \"Currency\": \"2176\"}]}, {\"Cycle\":
     * \"2\", \"Phase\": [{\"Channel\": \"1,4,17\", \"MGTime\": \"7\", \"GTime\": \"50\",
     * \"YTime\": \"3\", \"PName\": \"5\", \"Direction\": \"8,2,109\", \"RTime\": \"3\",
     * \"Currency\": \"257\"}, {\"Channel\": \"5,8\", \"MGTime\": \"7\", \"GTime\": \"78\",
     * \"YTime\": \"3\", \"PName\": \"6\", \"Direction\": \"33,40\", \"RTime\": \"3\",
     * \"Currency\": \"514\"}, {\"Channel\": \"11\", \"MGTime\": \"6\", \"GTime\": \"9\",
     * \"YTime\": \"3\", \"PName\": \"7\", \"Direction\": \"69\", \"RTime\": \"0\", \"Currency\":
     * \"4\"}, {\"Channel\": \"10\", \"MGTime\": \"7\", \"GTime\": \"42\", \"YTime\": \"3\",
     * \"PName\": \"11\", \"Direction\": \"72\", \"RTime\": \"3\", \"Currency\": \"4\"},
     * {\"Channel\": \"15\", \"MGTime\": \"6\", \"GTime\": \"9\", \"YTime\": \"3\", \"PName\":
     * \"8\", \"Direction\": \"101\", \"RTime\": \"0\", \"Currency\": \"8\"}, {\"Channel\":
     * \"14\", \"MGTime\": \"7\", \"GTime\": \"36\", \"YTime\": \"3\", \"PName\": \"12\",
     * \"Direction\": \"98\", \"RTime\": \"3\", \"Currency\": \"8\"}]}]}
     *
     * @param CycleData
     */
    public void setBaseData(JSONArray CycleData) {
        mCycles = createCycles(CycleData);
        mRC = caculateRC(CycleData);
        if (mCycles.size() > 0) {
            int height = mCycles.size() * (ROW_HEIGHT + ROW_MARGIN)
                    + ROW_MARGIN;
            if (height != TOTAL_HEIGHT) {
                TOTAL_HEIGHT = height;
                requestLayout();
            } else {
                createBufferBitmap();
            }
        }
    }

    /**
     * 设置当前周期
     *
     * @param rcd
     */
    public void setRCD(int rcd, int rc) {
        mRCD = rcd;
        if (mSchemePoint != null) {
            mSchemePoint.setData(mRC, rcd);
        }
    }

    /**
     * 获取总周期
     * @return
     */
    public int getRC(){
        return  mRC;
    }
    /**
     * 暂停绘制
     */
    public void pause() {
        isRunning = false;
    }

    /**
     * 当前是否暂停绘制
     *
     * @return
     */
    public boolean isPaused() {
        return !isRunning;
    }

    /**
     * 开始绘制
     */
    public void start() {
        isRunning = true;
    }

    // 初始化资源
    private void initResources() {
        ROW_HEIGHT = getResources().getDimensionPixelSize(R.dimen.schemechartRowHeight);
        ROW_MARGIN = getResources().getDimensionPixelSize(R.dimen.schemechartRowMargin);
        TOTAL_HEIGHT = getResources().getDimensionPixelSize(R.dimen.schemechartRowTotalHeight);
        int textsize = getResources().getDimensionPixelSize(R.dimen.SmallestTextSize);
        mPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintGreen.setStyle(Style.FILL);
        mPaintGreen.setColor(getResources().getColor(R.color.phase_green));
        mPaintYellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintYellow.setColor(getResources().getColor(R.color.phase_yellow));
        mPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRed.setColor(getResources().getColor(R.color.phase_red));
        mPaintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBlack.setColor(getResources().getColor(R.color.black));
        mPaintBlack.setStrokeWidth(2f);
        mPaintBlack.setTextSize(textsize);

        //Log.i(TAG, "textsize=" + textsize);
        mTextPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaintRed.setColor(getResources().getColor(R.color.red));
        mTextPaintRed.setStrokeWidth(2f);
        mTextPaintRed.setTextSize(textsize);
        mPaintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintStroke.setColor(getResources().getColor(R.color.black));
        mPaintStroke.setStyle(Style.STROKE);
        mPaintStroke.setStrokeWidth(1F);
        mPaintClear = new Paint();
        mPaintClear.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mPaintCurrency = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCurrency.setColor(getResources().getColor(R.color.black));
        mPaintCurrency.setStrokeWidth(4f);
    }

    private void draw() {
        try {
            // 获得canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                drawBg();
                drawPoint();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private void drawBg() {
        if (mBufferBitmap == null) {
            return;
        }
        mCanvas.save();
        mCanvas.drawPaint(mPaintClear);
        mCanvas.drawBitmap(mBufferBitmap, 0, 0, null);
        mCanvas.restore();
    }

    private void createBufferBitmap() {
        if (mBufferBitmap == null) {
            return;
        }
        if (mRC == 0) {
            return;
        }
        Canvas bufferCanvas = new Canvas(mBufferBitmap);
        bufferCanvas.drawPaint(mPaintClear);
        int left = 0 + getPaddingLeft();
        int top = ROW_MARGIN;
        List<Integer> currencyList = new ArrayList<Integer>();
        int cIndex = 0;
        int lastCurrency = -1;
        int lastLeft = 0;// 最右侧的x坐标
        for (Cycle cycle : mCycles) {
            List<PhaseData> list = cycle.data;
            for (PhaseData data : list) {
                // 计算屏障位置
                if (cIndex == 0) {
                    if (lastCurrency != -1) {
                        if (data.currency != lastCurrency) {
                            currencyList.add(left);
                        }
                    }
                    lastCurrency = data.currency;
                }
                if (data.green > 0) {
                    // 绿
                    int width_green = (mWidth * data.green / mRC);
                    Rect rect_green = new Rect(left, top, left + width_green,
                            top + ROW_HEIGHT);
                    bufferCanvas.drawRect(rect_green, mPaintGreen);
                    // 画竖线
                    bufferCanvas.drawLine(left, top - ROW_MARGIN + 2, left,
                            top, mPaintBlack);
                    // 箭头
                    if (data.directions != null) {
                        int left_direction = left;
                        for (int i = 0; i < data.directions.length; i++) {
                            Bitmap bitmap = loadImageByDirection(data.directions[i]);
                            if (bitmap != null) {
                                bufferCanvas.drawBitmap(bitmap,
                                        left_direction + 2, top - ROW_MARGIN
                                                + 2, null);
                                left_direction += bitmap.getWidth() + 2;
                            }
                        }
                    } else {
                        //绘制虚相位
                        int left_direction = left;
                        FontMetricsInt fontMetrics = mPaintBlack
                                .getFontMetricsInt();
                        int baseline = (top + top - ROW_MARGIN
                                - fontMetrics.bottom - fontMetrics.top) / 2;
                        bufferCanvas.drawText("虚", left_direction + 2, baseline, mTextPaintRed);
                    }

                    // 边框
                    bufferCanvas.drawRect(rect_green, mPaintStroke);
                    String text = String.format("%s(%d,%d,%d)", data.name,
                            data.green + data.yellow + data.red, data.yellow,
                            data.red);
                    // 文字
                    FontMetricsInt fontMetrics = mPaintBlack
                            .getFontMetricsInt();
                    int baseline = (rect_green.bottom + rect_green.top
                            - fontMetrics.bottom - fontMetrics.top) / 2;
                    // 左对齐
                    mPaintBlack.setTextAlign(Paint.Align.LEFT);
                    bufferCanvas.drawText(text, rect_green.left + 1, baseline,
                            mPaintBlack);
                    left += width_green;
                }
                if (data.yellow > 0) {
                    // 黄
                    int width_yellow = (mWidth * data.yellow / mRC);
                    Rect rect_yellow = new Rect(left, top, left + width_yellow,
                            top + ROW_HEIGHT);
                    bufferCanvas.drawRect(rect_yellow, mPaintYellow);
                    bufferCanvas.drawRect(rect_yellow, mPaintStroke);
                    left += width_yellow;
                }
                if (data.red > 0) {
                    // 红
                    int width_red = (mWidth * data.red / mRC);
                    Rect rect_red = new Rect(left, top, left + width_red, top
                            + ROW_HEIGHT);
                    bufferCanvas.drawRect(rect_red, mPaintRed);
                    bufferCanvas.drawRect(rect_red, mPaintStroke);
                    left += width_red;
                }
            }
            if (cIndex == 0) {
                lastLeft = left;// 获取最右侧屏障坐标
            }
            left = 0 + getPaddingLeft();
            top += ROW_HEIGHT + ROW_MARGIN;
            cIndex++;
        }
        if (drawCurrency) {
            // 绘制屏障
            for (Integer cleft : currencyList) {
                bufferCanvas.drawLine(cleft, 2, cleft, TOTAL_HEIGHT
                        - ROW_MARGIN, mPaintCurrency);
            }
            bufferCanvas.drawLine(lastLeft, 2, lastLeft, TOTAL_HEIGHT
                    - ROW_MARGIN, mPaintCurrency);
        }
    }

    // 绘制文字
    private void drawText(String text, Rect rect) {
        FontMetricsInt fontMetrics = mPaintBlack.getFontMetricsInt();
        int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
        // 左对齐
        mPaintBlack.setTextAlign(Paint.Align.LEFT);
        mCanvas.drawText(text, rect.left + 1, baseline, mPaintBlack);
    }

    // 绘制指针
    private void drawPoint() {
        mSchemePoint.draw(mCanvas);
    }

    // 构造箭头元素
    private Bitmap loadImageByDirection(int direction) {
        int left = direction & 0X1F;
        int top = direction >> 5;
        int iPngIndex = 1;
        switch (left) {
            case 1:
                iPngIndex = 1;
                break;
            case 2:
                iPngIndex = 2;
                break;
            case 3:
                iPngIndex = 4;
                break;
            case 4:
                iPngIndex = 8;
                break;
            case 5:
                iPngIndex = 9;
                break;
            case 6:
                iPngIndex = 3;
                break;
            case 7:
                iPngIndex = 5;
                break;
            case 8:
                iPngIndex = 6;
                break;
            case 9:
                iPngIndex = 7;
                break;
            case 10:
                iPngIndex = 10;
                break;
            case 11:
                iPngIndex = 18;
                break;
            case 12:
                iPngIndex = 16;
                break;
            case 13:
                iPngIndex = 17;
                break;
            case 14:
                iPngIndex = 11;
                break;
            case 15:
                iPngIndex = 12;
                break;
            case 16:
                iPngIndex = 13;
                break;
            case 17:
                iPngIndex = 14;
                break;
            case 18:
                iPngIndex = 15;
                break;
        }
        String filename = "cycle_" + top + "_" + iPngIndex;
        int resId = getContext().getResources().getIdentifier(filename,
                "drawable", getContext().getPackageName());
        if (resId != 0) {
            return BitmapFactory.decodeResource(getResources(), resId);
        }
        return null;
    }

    // 计算总周期
    private int caculateRC(JSONArray CycleData) {
        int ret = 0;
        try {
            JSONObject object = CycleData.getJSONObject(0);
            JSONArray array = object.getJSONArray("Phase");
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                int GTime = item.getInt("GTime");
                ret += GTime;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, e.toString());
        }
        return ret;
    }

    /**
     * 构造环图数据
     *
     * @param CycleData
     * @return
     */
    private List<Cycle> createCycles(JSONArray CycleData) {
        List<Cycle> list = new ArrayList<Cycle>();
        for (int i = 0; i < CycleData.length(); i++) {
            try {
                JSONObject object = CycleData.getJSONObject(i);
                int index = object.getInt("Cycle");
                JSONArray array = object.getJSONArray("Phase");
                List<PhaseData> pList = new ArrayList<PhaseData>();
                for (int j = 0; j < array.length(); j++) {
                    JSONObject data = array.getJSONObject(j);
                    PhaseData phaseData = createPhaseData(data);
                    if (phaseData != null) {
                        pList.add(phaseData);
                    }
                }
                Cycle cycle = new Cycle();
                cycle.index = index;
                cycle.data = pList;

                list.add(cycle);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e(TAG, e.toString());
            }
        }
        return list;
    }

    /**
     * 构造相位数据 PName:相位号 GTime:绿信比 YTime:黄灯时间 RTime:红灯时间
     * Direction:方向,多个通道用逗号分割,CurrenCy:屏障 MGTime最小效率
     *
     * @param object { "Channel": "3", "MGTime": "6", "GTime": "9", "YTime": "3",
     *               "PName": "1", "Direction": "5", "RTime": "0", "Currency": "16"
     *               }
     * @return
     */
    private PhaseData createPhaseData(JSONObject object) {
        PhaseData data = null;
        try {
            int GTime = object.getInt("GTime");
            int YTime = object.getInt("YTime");
            int RTime = object.getInt("RTime");
            int PName = object.getInt("PName");
            int Currency = object.getInt("Currency");

            int green = GTime - YTime - RTime;
            int yellow = YTime;
            int red = RTime;

            data = new PhaseData();
            data.green = green;
            data.yellow = yellow;
            data.red = red;
            data.phaseno = PName;
            data.name = "P" + PName;
            data.currency = Currency;
            if (!object.isNull("Direction")) {
                String Direction = object.getString("Direction");// 此值可能为Null
                if (!TextUtils.isEmpty(Direction)) {
                    String[] s = Direction.split(",");
                    int[] directions = new int[s.length];
                    for (int i = 0; i < directions.length; i++) {
                        directions[i] = Integer.parseInt(s[i]);
                    }
                    data.directions = directions;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, e.toString());
        }
        return data;
    }

    // ******** start 7.0协议解析方式 废弃*********

    // /**
    // * 获取相位元素哈希表 键值为相位号
    // *
    // [{"MaxGreen1":60,"YellowTime":30,"MinGreen":14,"PhaseNo":1,"ChannelNo":1,
    // * "RedTime":0,"SpecialPhase":0,"Direction":1969,"PhaseType":2}]
    // *
    // * @param array
    // * @return
    // */
    // private Map<Integer, Phase> createPhaseMap(JSONArray array) {
    // Map<Integer, Phase> phaseMap = new HashMap<Integer, Phase>();
    // for (int i = 0; i < array.length(); i++) {
    // try {
    // JSONObject object = array.getJSONObject(i);
    // Phase phase = Phase.fromJson(object);
    // // 只取PhaseType=2的数据
    // if (phase.PhaseType == 2) {
    // // 相位号
    // Integer PhaseNo = phase.PhaseNo;
    // phaseMap.put(PhaseNo, phase);
    // }
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // Log.e(TAG, e.toString());
    //
    // }
    //
    // }
    // return phaseMap;
    // }
    //
    // /**
    // * 构造Cycle数组 [{"P5":109,"P4":104,"Cycle":1,"P1":101,"P3":103,"P2":102},
    // * {"P5":110,"P4":108,"Cycle":2,"P1":105,"P3":107,"P2":106}]
    // *
    // * @param array
    // * @return
    // */
    // private List<Cycle> createCycles(JSONArray array) {
    // List<Cycle> cycleArray = new ArrayList<Cycle>();
    // for (int i = 0; i < array.length(); i++) {
    // try {
    // JSONObject object = array.getJSONObject(i);
    // int cycleIndex = object.getInt("Cycle");
    // Iterator<String> iterator = object.keys();
    // // 相位数组
    // List<Phase> phasesArray = new ArrayList<Phase>();
    // // 遍历所有相位，构造相位数组
    // while (iterator.hasNext()) {
    // String key = iterator.next();
    // if (!key.equals("Cycle")) {
    // int value = object.getInt(key);
    // int phaseNo = value - 100;
    // if (mPhaseMap != null) {
    // // 获取对应相位实体类
    // Phase phase = mPhaseMap.get(phaseNo);
    // if (phase != null) {
    // phase.Name = String.format("P%d", phaseNo);// 显示的文字应该是P+phaseNo
    // phasesArray.add(phase);
    // }
    // }
    // }
    // }
    // // 相位升序排序
    // Collections.sort(phasesArray);
    // Cycle cycle = new Cycle();
    // cycle.index = cycleIndex;
    // cycle.list = phasesArray;
    // cycle.data = createPhaseData(phasesArray);
    // cycleArray.add(cycle);
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // Log.e(TAG, e.toString());
    // }
    //
    // }
    // return cycleArray;
    // }
    //
    // // 获取绘制方向图标的resourceId
    // private int[] createDiretionResource(int wDirection) {
    // int[] nTurnAry = { 16, 32, 64, 128 }; // 左、直、右、调头
    // int nDirection = 0;
    // int wD = 15;
    // List<Integer> array = new ArrayList<Integer>();
    // for (int i = 0; i < 8; i++) {
    // nDirection = (int) (wD & (wDirection >> (i * 8)));
    // if (nDirection > 0) {
    // for (int j = 0; j < 4; j++) {
    // if ((nTurnAry[j] & (wDirection >> (i * 8))) != 0) {
    // int result = j * 10 + nDirection;
    // if (!array.contains(result)) {
    // array.add(result);
    // }
    // }
    // }
    // }
    // }
    // return createDirectionResourceId(array);
    // }
    //
    // private int[] createDirectionResourceId(List<Integer> array) {
    // List<Integer> list = new ArrayList<Integer>();
    // for (Integer integer : array) {
    // if (integer > 0 && integer <= 38) {
    // String filename = String.format("ic_phase_direction_%02d",
    // integer);
    // int resId = getResources().getIdentifier(filename, "drawable",
    // getContext().getPackageName());
    // list.add(resId);
    // }
    // }
    //
    // int[] ret = new int[list.size()];
    // int j = 0;
    // for (Integer i : list) {
    // ret[j] = i;
    // j++;
    // }
    // return ret;
    // }
    //
    // /**
    // * 构造用于绘图的相位数据
    // *
    // * @param list
    // * @return
    // */
    // private List<PhaseData> createPhaseData(List<Phase> list) {
    // List<PhaseData> phaseDatas = new ArrayList<PhaseData>();
    // for (Phase phase : list) {
    // PhaseData data = new PhaseData();
    // data.name = phase.Name;
    // data.phaseno = phase.PhaseNo;
    // data.directions = createDiretionResource(phase.Direction);
    // data.yellow = phase.YellowTime / 10;
    // data.red = phase.RedTime / 10;
    // phaseDatas.add(data);
    // }
    // return phaseDatas;
    // }

    // ******** end 7.0协议解析方式 废弃*********
}
