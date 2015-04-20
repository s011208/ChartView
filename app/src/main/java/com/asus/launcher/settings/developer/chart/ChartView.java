package com.asus.launcher.settings.developer.chart;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.asus.launcher.log.LogData;
import com.asus.launcher.log.LogsFileParser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartView extends RelativeLayout {
    protected static final boolean DEBUG = true;
    protected static final String TAG = "QQQQ";

    private static final Paint sBlackTextPaint = new Paint();

    static {
        sBlackTextPaint.setAntiAlias(true);
        sBlackTextPaint.setColor(Color.BLACK);
        sBlackTextPaint
                .setTextSize(20);
    }

    public static final Rect sTextRect = getTextBound("1234567890", sBlackTextPaint);

    public static Rect getTextBound(String txt, Paint paint) {
        Rect bounds = new Rect();
        paint.getTextBounds(txt, 0, txt.length(), bounds);
        return bounds;
    }

    /**
     * default series colors
     */
    private static final int[] COLORS = new int[]{
            Color.BLUE, Color.RED, Color.GREEN, Color.MAGENTA, Color.YELLOW
    };
    private Context mContext;

    private int mColorCounter = 0;
    // views
    private Chart mChart;
    private ChartSeriesName mChartSeriesName;
    private ChartSelectedLine mChartSelectedLine;
    // data
    private final ArrayList<ChartSeries> mChartSeries = new ArrayList<ChartSeries>();

    public ChartView(Context context) {
        this(context, null);
    }

    public ChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initComponents();
        getLogDataAndDraw();
    }

    public void getLogDataAndDraw() {
        new LogsFileParser(new LogsFileParser.Callback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(HashMap<String, ArrayList<LogData>> logDataMap) {
                new ChartSeriesAdapter(new ChartSeriesAdapter.Callback() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onPostExecute(ArrayList<ChartSeries> series) {
                        mChartSeries.clear();
                        mChartSeries.addAll(series);
                        if (DEBUG) {
                            Log.d(TAG, "series size: " + series.size());
                        }
                        setup();
                    }
                }).setLogDataMap(logDataMap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void initComponents() {
        getChart();
        getChartSeriesName();
        getChartSelectedLine();
    }

    private synchronized ChartSeriesName getChartSeriesName() {
        if (mChartSeriesName == null) {
            mChartSeriesName = new ChartSeriesName(mContext);
            mChartSeriesName.setId(ChartSeriesName.class.hashCode());
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            addView(mChartSeriesName, rl);
        }
        return mChartSeriesName;
    }

    private synchronized Chart getChart() {
        if (mChart == null) {
            mChart = new Chart(mContext);
            mChart.setId(Chart.class.hashCode());
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rl.addRule(RelativeLayout.ABOVE, ChartSeriesName.class.hashCode());
            rl.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rl.setMargins(1, 1, 1, 1);
            addView(mChart, rl);
        }
        return mChart;
    }

    private synchronized ChartSelectedLine getChartSelectedLine() {
        if (mChartSelectedLine == null) {
            mChartSelectedLine = new ChartSelectedLine(mContext);
            mChartSelectedLine.setId(ChartSelectedLine.class.hashCode());
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rl.addRule(RelativeLayout.ABOVE, ChartSeriesName.class.hashCode());
            rl.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rl.setMargins(1, 1, 1, 1);
            addView(mChartSelectedLine, rl);
        }
        return mChartSelectedLine;
    }

    private void setupPaintForSeriesIfNeeded() {
        for (ChartSeries cs : mChartSeries) {
            if (cs.hasCustomizedPaintColor() == false) {
                cs.setSeriesPaintColor(COLORS[mColorCounter++ % COLORS.length]);
            }
        }
    }

    public void setup() {
        setupPaintForSeriesIfNeeded();
        getChart().setup(mChartSeries);
        getChartSeriesName().setup(mChartSeries);
        getChartSelectedLine().setup(getChart());
        requestLayout();
        invalidate();
    }

    protected static Paint getBlackTextPaint() {
        return sBlackTextPaint;
    }
}
