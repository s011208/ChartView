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
        public void onLinePositionChanged();
    }

    private static final Paint sSelectionPaint = new Paint();

    static {

        sSelectionPaint.setAntiAlias(true);
        sSelectionPaint.setColor(Color.rgb(150, 150, 150));
        sSelectionPaint.setStrokeWidth(2);

    }

    // control line
    private int mTouchX;

    private Callback mCallback;

    private Chart mChart;

    public ChartSelectedLine(Context context) {
        super(context);
    }

    public void setup(Chart chart) {
        mChart = chart;
    }

    public void setCallback(Callback cb) {
        mCallback = cb;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChart == null || mChart.getChartBorderRect() == null) {
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
                mCallback.onLinePositionChanged();
            }
            invalidate();
        }
        return isPositionValid;
    }

    private boolean isInChartBorder(int x, int y) {
        final Rect chartBorderRect = mChart.getChartBorderRect();
        if (x >= chartBorderRect.left && x <= chartBorderRect.right
                && y >= chartBorderRect.top && y <= chartBorderRect.bottom) {
            return true;
        }
        return false;
    }
}
