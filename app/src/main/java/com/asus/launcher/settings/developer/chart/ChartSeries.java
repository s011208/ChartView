package com.asus.launcher.settings.developer.chart;

import android.graphics.Paint;

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

    private final ArrayList<ChartPoint> mSeriesPoints = new ArrayList<ChartPoint>();

    public ChartSeries(String seriesName) {
        mSeriesName = seriesName;
    }

    public void addChartPoint(ChartPoint point) {
        mSeriesPoints.add(point);
    }

    public void setSeriesPaintColor(int color) {
        mHasCustomizedPaintColor = true;
        mPaintColor = color;
        mSeriesPaint.setColor(mPaintColor);
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public boolean hasCustomizedPaintColor() {
        return mHasCustomizedPaintColor;
    }

    public void setSeriesName(String name) {
        mSeriesName = name;
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
