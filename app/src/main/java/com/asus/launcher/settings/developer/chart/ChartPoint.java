package com.asus.launcher.settings.developer.chart;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartPoint implements Comparable<ChartPoint> {
    public int x, y;
    private String mExtraInfo;

    public ChartPoint(int x, int y) {
        this(x, y, null);
    }

    public ChartPoint(int x, int y, String extraInfo) {
        this.x = x;
        this.y = y;
        mExtraInfo = extraInfo;
    }

    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void addExtraInfo(String extraInfo) {
        if (mExtraInfo == null) {
            mExtraInfo = extraInfo;
        } else {
            mExtraInfo += "\n" + extraInfo;
        }
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
