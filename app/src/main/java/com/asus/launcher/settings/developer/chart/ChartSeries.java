package com.asus.launcher.settings.developer.chart;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartSeries implements Comparable<ChartSeries> {
    private static final String TAG = ChartView.TAG;
    private static final boolean DEBUG = ChartView.DEBUG;

    private String mSeriesName;

    private boolean mHasCustomizedPaintColor = false;

    private final Paint mSeriesPaint = new Paint();

    private int mPaintColor;

    private final Rect mSeriesRange = new Rect();

    private int mMinimumX = -1, mMaximumX = -1, mMinimumY = -1, mMaximumY = -1;

    private final SparseArray<ChartPoint> mSeriesPoints = new SparseArray<>();

    private final ArrayList<Path> mSeriesPath = new ArrayList<>();

    public ChartSeries(String seriesName) {
        mSeriesName = seriesName;
    }

    private void calculateSeriesRange(ChartPoint point) {
        if (DEBUG) {
            Log.v(TAG, "ChartSeries calculateSeriesRange, x: " + point.x + ", y: " + point.y);
        }
        if (mMinimumX == -1) {
            mMinimumX = point.x;
            mMaximumX = mMinimumX;
            mMinimumY = point.y;
            mMaximumY = mMinimumY;
        } else {
            mMinimumX = Math.min(mMinimumX, point.x);
            mMaximumX = Math.max(mMaximumX, point.x);
            mMinimumY = Math.min(mMinimumY, point.y);
            mMaximumY = Math.max(mMaximumY, point.y);
            mSeriesRange.set(mMinimumX, mMinimumY, mMaximumX, mMaximumY);
        }
    }

    public void addChartPoint(int index, ChartPoint point) {
        mSeriesPoints.put(index, point);
        calculateSeriesRange(point);
    }

    public void createPaths(Rect border, Rect visibleRect) {
        Path path = new Path();
        if (DEBUG)
            Log.d(TAG, "createPaths, border: " + border
                    + ", visibleRect: " + visibleRect);
        for (int i = visibleRect.left; i < visibleRect.right; i++) {
            final ChartPoint point = getChartPoint(i);
            if (point == null) {
                continue;
            }
            final int x = border.left + (i - visibleRect.left);
            final int y = border.height() - (int) ((border.height() / (float) Math.abs(visibleRect.height())) * point.y) + border.top;
            if (DEBUG)
                Log.d(TAG, "x: " + x + ", y: " + y + ", point.y: " + point.y);
            if (path.isEmpty()) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }
        if (path.isEmpty() == false) {
            mSeriesPath.add(path);
        }
        if (DEBUG)
            Log.v(TAG, "createPaths mSeriesPath size: " + mSeriesPath.size());
    }

    public ArrayList<Path> getSeriesPath() {
        return mSeriesPath;
    }

    public ChartPoint getChartPoint(int index) {
        return mSeriesPoints.get(index);
    }

    public SparseArray<ChartPoint> getChartPoints() {
        return mSeriesPoints;
    }

    public int getChartPointSize() {
        return mSeriesPoints.size();
    }

    public void setSeriesPaintColor(int color) {
        mHasCustomizedPaintColor = true;
        mPaintColor = color;
        mSeriesPaint.setColor(mPaintColor);
    }

    public Rect getSeriesRange() {
        if (DEBUG)
            Log.v(TAG, "mSeriesName: " + mSeriesName + ", mPointsRange: " + mSeriesRange);
        return mSeriesRange;
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public boolean hasCustomizedPaintColor() {
        return mHasCustomizedPaintColor;
    }

    public String getSeriesName() {
        return mSeriesName;
    }

    @Override
    public int compareTo(ChartSeries another) {
        return getSeriesName().compareTo(another.getSeriesName());
    }

    @Override
    public String toString() {
        return "series name: " + mSeriesName
                + ", point count: " + mSeriesPoints.size();
    }
}
