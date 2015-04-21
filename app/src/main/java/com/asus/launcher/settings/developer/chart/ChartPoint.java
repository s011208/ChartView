package com.asus.launcher.settings.developer.chart;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartPoint implements Comparable<ChartPoint> {
    public int x, y;
    private String mExtraInfo;

    private String mFinalMessage;

    public ChartPoint(int x, int y) {
        this(x, y, null);
    }

    public ChartPoint(int x, int y, String extraInfo) {
        this(x, y, extraInfo, null);
    }

    public ChartPoint(int x, int y, String extraInfo, String finalMessage) {
        this.x = x;
        this.y = y;
        mExtraInfo = extraInfo;
        mFinalMessage = finalMessage;
    }

    public String getExtraInfo() {
        return mExtraInfo;
    }

    public void addExtraInfo(String extraInfo) {
        if (extraInfo == null)
            return;
        if (mExtraInfo == null) {
            mExtraInfo = extraInfo;
        } else {
            mExtraInfo += "\n" + extraInfo;
        }
    }

    public String getMessage() {
        return mExtraInfo == null ? "" : mExtraInfo + "\n" + mFinalMessage == null ? "" : mFinalMessage;
    }

    public void setFinalMessage(String msg) {
        mFinalMessage = msg;
    }

    public String getFinalMessage() {
        return mFinalMessage;
    }

    public void combinePoints(ChartPoint another) {
        addExtraInfo(another.getExtraInfo());
        y = Math.max(y, another.y);
        if (y == another.y) {
            setFinalMessage(another.getFinalMessage());
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
