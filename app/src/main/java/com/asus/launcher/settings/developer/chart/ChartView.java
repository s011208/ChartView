package com.asus.launcher.settings.developer.chart;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.asus.launcher.log.LogData;
import com.asus.launcher.log.LogsFileParser;
import com.asus.launcher.settings.developer.chart.pager.ChartInfoPager;

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
    private ChartInfoPager mChartInfoPager;
    private ProgressBar mLoadingView;
    // data
    private final ArrayList<ChartSeries> mChartSeries = new ArrayList<ChartSeries>();

    private boolean mIsFirstTime = true;

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
    }

    public void getLogDataAndDraw() {
        new LogsFileParser(new LogsFileParser.Callback() {
            @Override
            public void onPreExecute() {
                showProgress(!mIsFirstTime);
                mIsFirstTime = false;
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
                        hideProgress(true);
                    }
                }).setLogDataMap(logDataMap).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showProgress(boolean animated) {
        final Runnable finalJob = new Runnable() {
            @Override
            public void run() {
                if (mChartInfoPager != null) {
                    mChartInfoPager.setVisibility(View.INVISIBLE);
                }
                getChart().setVisibility(View.INVISIBLE);
                getChartSeriesName().setVisibility(View.INVISIBLE);
                getChartSelectedLine().setVisibility(View.INVISIBLE);
                getLoadingView().setVisibility(View.VISIBLE);
            }
        };
        if (animated) {
            ValueAnimator va = ValueAnimator.ofFloat(0, 1);
            va.setDuration(250);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float value = (float) animation.getAnimatedValue();
                    getChart().setAlpha(1 - value);
                    getChartSeriesName().setAlpha(1 - value);
                    getChartSelectedLine().setAlpha(1 - value);
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setAlpha(1 - value);
                    }
                    getLoadingView().setAlpha(value);
                }
            });
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setVisibility(View.VISIBLE);
                    }
                    getChart().setVisibility(View.VISIBLE);
                    getChartSeriesName().setVisibility(View.VISIBLE);
                    getChartSelectedLine().setVisibility(View.VISIBLE);
                    getLoadingView().setVisibility(View.VISIBLE);
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setAlpha(1);
                    }
                    getChart().setAlpha(1);
                    getChartSeriesName().setAlpha(1);
                    getChartSelectedLine().setAlpha(1);
                    getLoadingView().setAlpha(0);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finalJob.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            va.start();
        } else {
            finalJob.run();
        }
    }

    private void hideProgress(boolean animated) {
        final Runnable finalJob = new Runnable() {
            @Override
            public void run() {
                if (mChartInfoPager != null) {
                    mChartInfoPager.setVisibility(View.VISIBLE);
                }
                getChart().setVisibility(View.VISIBLE);
                getChartSeriesName().setVisibility(View.VISIBLE);
                getChartSelectedLine().setVisibility(View.VISIBLE);
                getLoadingView().setVisibility(View.GONE);
            }
        };
        if (animated) {
            ValueAnimator va = ValueAnimator.ofFloat(0, 1);
            va.setDuration(250);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float value = (float) animation.getAnimatedValue();
                    getChart().setAlpha(value);
                    getChartSeriesName().setAlpha(value);
                    getChartSelectedLine().setAlpha(value);
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setAlpha(value);
                    }
                    getLoadingView().setAlpha(1 - value);
                }
            });
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setVisibility(View.VISIBLE);
                    }
                    getChart().setVisibility(View.VISIBLE);
                    getChartSeriesName().setVisibility(View.VISIBLE);
                    getChartSelectedLine().setVisibility(View.VISIBLE);
                    getLoadingView().setVisibility(View.VISIBLE);
                    if (mChartInfoPager != null) {
                        mChartInfoPager.setAlpha(0);
                    }
                    getChart().setAlpha(0);
                    getChartSeriesName().setAlpha(0);
                    getChartSelectedLine().setAlpha(0);
                    getLoadingView().setAlpha(1);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    finalJob.run();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            va.start();
        } else {
            finalJob.run();
        }
    }

    public void setChartInfoPager(ChartInfoPager pager) {
        mChartInfoPager = pager;
    }

    private void initComponents() {
        getChart();
        getChartSeriesName();
        getChartSelectedLine();
        getLoadingView();
    }

    private synchronized ProgressBar getLoadingView() {
        if (mLoadingView == null) {
            mLoadingView = new ProgressBar(mContext);
            mLoadingView.setIndeterminate(true);
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rl.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            addView(mLoadingView, rl);
        }
        return mLoadingView;
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

    private void setup() {
        setupPaintForSeriesIfNeeded();
        getChart().setup(mChartSeries);
        getChartSeriesName().setup(mChartSeries);
        getChartSelectedLine().setup(getChart());
        getChartSelectedLine().setCallback(mChartInfoPager);
        mChartInfoPager.setup(getChart().getSeries());
        requestLayout();
        invalidate();
    }

    protected static Paint getBlackTextPaint() {
        return sBlackTextPaint;
    }
}
