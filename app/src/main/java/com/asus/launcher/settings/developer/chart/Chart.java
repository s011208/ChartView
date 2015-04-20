package com.asus.launcher.settings.developer.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class Chart extends View {
    private static final boolean DEBUG = ChartView.DEBUG;
    private static final String TAG = ChartView.TAG;

    // paints
    private static final Paint sHoloGridPaint = new Paint();

    static {
        sHoloGridPaint.setAntiAlias(true);
        sHoloGridPaint.setColor(Color.rgb(53, 178, 222));
        sHoloGridPaint.setStrokeWidth(3);
        sHoloGridPaint.setStyle(Paint.Style.STROKE);
    }

    private static final Paint sGayRowPaint = new Paint();

    static {
        sGayRowPaint.setAntiAlias(true);
        sGayRowPaint.setColor(Color.rgb(224, 224, 224));
        sGayRowPaint.setStrokeWidth(2);
    }

    private static final Paint sSelectionPaint = new Paint();

    static {

        sSelectionPaint.setAntiAlias(true);
        sSelectionPaint.setColor(Color.rgb(150, 150, 150));
        sSelectionPaint.setStrokeWidth(2);

    }

    private static final int ROW_COUNT = 15;

    // region rects
    private Rect mChartBorderRect;

    private Rect mYAxisRect;

    private int mWidth, mHeight;

    public Chart(Context context) {
        super(context);
        initComponents();
    }

    private void initComponents() {
    }

    public void setup() {
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                mWidth = getWidth();
                mHeight = getHeight();
                calculateRects();
                return false;
            }
        });
        invalidate();
    }

    private void calculateRects() {
        calculateYAxisRect();
        calculateChartBorderRect();
    }

    private void calculateChartBorderRect() {
        mChartBorderRect = new Rect();
    }

    private void calculateYAxisRect() {
        mYAxisRect = new Rect();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChartBorderRect == null) {
            return;
        }
    }
}
