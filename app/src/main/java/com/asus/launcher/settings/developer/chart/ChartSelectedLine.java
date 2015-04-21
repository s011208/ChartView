package com.asus.launcher.settings.developer.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yenhsunhuang on 15/4/21.
 */
public class ChartSelectedLine extends View {
    public interface Callback {
        public void onLinePositionChanged(int index, Rect border, Rect visible);
    }

    private static final Paint sSelectionPaint = new Paint();

    static {

        sSelectionPaint.setAntiAlias(true);
        sSelectionPaint.setColor(Color.rgb(150, 150, 150));
        sSelectionPaint.setStrokeWidth(3);

    }

    // control line
    private int mTouchX = -1;

    private Callback mCallback;

    private Chart mChart;

    public ChartSelectedLine(Context context) {
        super(context);
    }

    public void setup(Chart chart) {
        mChart = chart;
        mTouchX = -1;
        invalidate();
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChart == null || mChart.getChartBorderRect() == null || mTouchX < 0) {
            return;
        }
        drawSelectionLine(canvas);
    }

    private void drawSelectionLine(Canvas canvas) {
        final Rect chartBorderRect = mChart.getChartBorderRect();
        canvas.drawLine(mTouchX, chartBorderRect.top + 1, mTouchX + 1, chartBorderRect.bottom, sSelectionPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final boolean isPositionValid = isInChartBorder(x, y);
        if (isPositionValid) {
            mTouchX = x;
            if (mCallback != null) {
                mCallback.onLinePositionChanged(mChart.getSeriesIndex(x), mChart.getChartBorderRect(), mChart.getVisibleSeriesRect());
            }
            invalidate();
        }
        return isPositionValid;
    }

    private boolean isInChartBorder(int x, int y) {
        final Rect chartBorderRect = mChart.getChartBorderRect();
        return (x >= chartBorderRect.left && x <= chartBorderRect.right
                && y >= chartBorderRect.top && y <= chartBorderRect.bottom);
    }
}
