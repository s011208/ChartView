package com.asus.launcher.settings.developer.chart;

import android.graphics.Point;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartPoint extends Point implements Comparable<ChartPoint> {
    private String mExtraInfo;

    public ChartPoint(int x, int y) {
        this(new Point(x, y));
    }

    public ChartPoint(int x, int y, String extraInfo) {
        this(new Point(x, y), extraInfo);
    }

    public ChartPoint(Point point) {
        this(point, null);
    }

    public ChartPoint(Point point, String extraInfo) {
        super(point);
        mExtraInfo = extraInfo;
    }

    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        mExtraInfo = extraInfo;
    }

    @Override
    public String toString() {
        return "x: " + x + ", y: " + y + ", extraInfo: " + mExtraInfo;
    }

    @Override
    public int compareTo(ChartPoint another) {
        if (x == another.x)
            return 0;
        return x < another.x ? -1 : 1;
    }
}
