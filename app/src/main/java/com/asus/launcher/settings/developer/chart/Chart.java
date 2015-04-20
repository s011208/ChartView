package com.asus.launcher.settings.developer.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class Chart extends View {
    private static final boolean DEBUG = ChartView.DEBUG;
    private static final String TAG = ChartView.TAG;

    private static final int HOLO_BORDER_WIDTH = 3;
    private static final int BORDER_VERTICAL_PADDING = 20;
    private static final int YAXIS_PADDING_RIGHT = 3;

    // paints
    private static final Paint sHoloGridPaint = new Paint();

    static {
        sHoloGridPaint.setAntiAlias(true);
        sHoloGridPaint.setColor(Color.rgb(53, 178, 222));
        sHoloGridPaint.setStrokeWidth(HOLO_BORDER_WIDTH);
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

    private static final int ROW_COUNT = 10;


    private int mHeightPerRow;

    // region rects
    private Rect mChartBorderRect;

    private Rect mYAxisRect;

    private int mWidth, mHeight;

    private int mMaximumY;

    private final ArrayList<ChartSeries> mSeries = new ArrayList<>();

    public Chart(Context context) {
        super(context);
        initComponents();
    }

    private void initComponents() {
    }

    private void calculateVariables() {
    }

    public void setup(ArrayList<ChartSeries> series) {
        mSeries.clear();
        mSeries.addAll(series);
        calculateVariables();
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                getViewTreeObserver().removeOnPreDrawListener(this);
                mWidth = getWidth();
                mHeight = getHeight();
                calculateRect();
                mHeightPerRow = mChartBorderRect.height() / ROW_COUNT;
                return false;
            }
        });
        invalidate();
    }

    private void calculateRect() {
        calculateYAxisRect();
        calculateChartBorderRect();
    }

    private void calculateChartBorderRect() {
        mChartBorderRect = new Rect(mYAxisRect.width(), BORDER_VERTICAL_PADDING, mWidth, mHeight - BORDER_VERTICAL_PADDING);
    }

    private void calculateYAxisRect() {
        mYAxisRect = new Rect(0, 0, 0, mHeight);
        for (ChartSeries cs : mSeries) {
            final int seriesHeight = cs.getSeriesRange().height();
            mMaximumY = Math.max(mMaximumY, seriesHeight);
            mYAxisRect.union(ChartView.getTextBound(String.valueOf(seriesHeight), ChartView.getBlackTextPaint()));
        }
        // expand padding
        mYAxisRect = new Rect(mYAxisRect.left, mYAxisRect.top, mYAxisRect.right + YAXIS_PADDING_RIGHT, mYAxisRect.bottom);
    }

    // draw
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChartBorderRect == null) {
            return;
        }
        drawRawSeparator(canvas);
        drawBorder(canvas);
        drawYAxisValues(canvas);
    }

    private void drawBorder(Canvas canvas) {
        if (DEBUG) {
            Log.d(TAG, "mChartBorderRect: " + mChartBorderRect);
        }
        canvas.drawRect(mChartBorderRect, sHoloGridPaint);
    }

    private void drawRawSeparator(Canvas canvas) {
        for (int i = 0; i < ROW_COUNT; i++) {
            canvas.drawLine(mChartBorderRect.left, mChartBorderRect.top + mHeightPerRow * i
                    , mChartBorderRect.right, mChartBorderRect.top + mHeightPerRow * i, sGayRowPaint);
        }
    }

    private void drawYAxisValues(Canvas canvas) {
        for (int i = 0; i <= ROW_COUNT; i++) {
            final int value = mMaximumY - (mMaximumY) * i / ROW_COUNT;
            final String strValue = String.valueOf(value);
            final Rect valueRect = ChartView.getTextBound(strValue, ChartView.getBlackTextPaint());
            // align center offset
            final int centerOffset = (mYAxisRect.width() - valueRect.width()) / 2;
            canvas.drawText(strValue, centerOffset, mChartBorderRect.top + mHeightPerRow * i + valueRect.height() / 2, ChartView.getBlackTextPaint());
        }
    }
}
