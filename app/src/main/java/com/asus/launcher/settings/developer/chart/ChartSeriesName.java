package com.asus.launcher.settings.developer.chart;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartSeriesName extends View {
    private static final boolean DEBUG = ChartView.DEBUG;
    private static final String TAG = ChartView.TAG;
    private static final Rect LINE_RECT = new Rect(0, 0, 50, 3);
    private static final int VERTICAL_PADDING = 5;
    private static final int HORIZONTAL_PADDING = 20;

    private final Paint mLinePaint = new Paint();
    private final ArrayList<Pair<String, Integer>> mSeries = new ArrayList<>();

    private int mDesiredWidth, mDesiredHeight;
    private int mColumns = 1;
    private Rect mItemRect;

    public ChartSeriesName(Context context) {
        super(context);
        mColumns = 1;
        setPadding(0, VERTICAL_PADDING, 0, VERTICAL_PADDING);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(LINE_RECT.height());
    }

    public void setColumns(int columns) {
        mColumns = columns;
    }

    public void setup(ArrayList<ChartSeries> series) {
        mSeries.clear();
        mItemRect = new Rect();
        if (DEBUG)
            Log.v(TAG, "ChartSeriesName setup, series size: " + series.size());
        for (ChartSeries cs : series) {
            Rect seriesRect = getItemRect(cs);
            if (DEBUG) {
                Log.v(TAG, seriesRect.toString());
            }
            mItemRect.union(seriesRect);
            mSeries.add(new Pair(cs.getSeriesName(), cs.getPaintColor()));
        }
        mDesiredWidth = calculateDesiredWidth();
        mDesiredHeight = calculateDesiredHeight();
        if (DEBUG)
            Log.d(TAG, "mDesiredWidth: " + mDesiredWidth
                    + ", mDesiredHeight: " + mDesiredHeight
                    + ", mItemRect: " + mItemRect);
        requestLayout();
        invalidate();
    }

    private static Rect getItemRect(ChartSeries series) {
        Rect rect = new Rect();
        Rect textRect = ChartView.getTextBound(series.getSeriesName(), ChartView.getBlackTextPaint());
        rect.set(0, 0, HORIZONTAL_PADDING * 3 + LINE_RECT.width() + textRect.width(),
                VERTICAL_PADDING * 2 + textRect.height() > LINE_RECT.height() ? textRect.height() : LINE_RECT.height());
        return rect;
    }

    // measure
    private int calculateDesiredWidth() {
        return mItemRect.width() * mColumns;
    }

    private int calculateDesiredHeight() {
        return mItemRect.height() * (int) Math.ceil(mSeries.size() / (float) mColumns);
    }

    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = mDesiredWidth + getPaddingLeft() + getPaddingRight();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = mDesiredHeight + getPaddingTop() + getPaddingBottom();
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    // draw
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mItemRect == null || mSeries.isEmpty()) {
            return;
        }
        drawSeriesName(canvas);
    }

    private void drawSeriesName(Canvas canvas) {
        for (int i = 0; i < mSeries.size(); i++) {
            Pair<String, Integer> series = mSeries.get(i);
            final int column = i % mColumns;
            final int row = i / mColumns;
            final int startX = mItemRect.width() * column;
            final int startY = mItemRect.height() * row;
            Rect textRect = ChartView.getTextBound(series.first, ChartView.getBlackTextPaint());
            final int lineWidth = HORIZONTAL_PADDING * 2 + LINE_RECT.width();
            canvas.drawText(series.first, startX + lineWidth, startY + textRect.height(), ChartView.getBlackTextPaint());

            mLinePaint.setColor(series.second);
            canvas.drawLine(startX + HORIZONTAL_PADDING, startY + textRect.height() / 2, startX + HORIZONTAL_PADDING + LINE_RECT.width(), startY + textRect.height() / 2, mLinePaint);
        }
    }
}

