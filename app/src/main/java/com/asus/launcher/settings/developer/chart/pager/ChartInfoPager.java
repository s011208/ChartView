package com.asus.launcher.settings.developer.chart.pager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import com.asus.launcher.settings.developer.chart.ChartSelectedLine;
import com.asus.launcher.settings.developer.chart.ChartSeries;

import java.util.ArrayList;

/**
 * @author yen-hsun_huang
 */
public class ChartInfoPager extends ViewPager implements ChartSelectedLine.Callback {

    public interface Callback {
        public void onChartSelectIndexChanged(int index);
    }

    private final ArrayList<Callback> mCallbacks = new ArrayList<Callback>();

    private PagerTabStrip mPagerTab;

    private ChartInfoPagerAdapter mAdapter;

    private Context mContext;

    private int mCurrentPage = -1;

    public ChartInfoPager(Context context) {
        this(context, null);
    }

    public ChartInfoPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void addCallback(Callback cb) {
        mCallbacks.add(cb);
    }

    public void removeCallback(Callback cb) {
        mCallbacks.remove(cb);
    }

    public void setPagerTabStrip(PagerTabStrip pagerTab) {
        if (pagerTab == null) {
            setOnPageChangeListener(null);
            return;
        }
        mCallbacks.clear();
        mPagerTab = pagerTab;
        mPagerTab.setTabIndicatorColor(Color.rgb(53, 178, 222));
        mPagerTab.setDrawFullUnderline(true);
        mPagerTab.setTextSpacing(50);
        setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                mCurrentPage = arg0;
            }
        });
    }

    public void setup(ArrayList<ChartSeries> drawingSeries) {
        mAdapter = new ChartInfoPagerAdapter(mContext, drawingSeries, this);
        setAdapter(mAdapter);
        if (mCurrentPage >= 0) {
            setCurrentItem(mCurrentPage);
        }
    }

    @Override
    public void onLinePositionChanged(int index, Rect border, Rect visible) {
        for (Callback cb : mCallbacks) {
            cb.onChartSelectIndexChanged(index);
        }
    }

}
