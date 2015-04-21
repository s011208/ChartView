package com.asus.launcher.settings.developer.chart;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.asus.launcher.settings.developer.chart.pager.ChartInfoPager;


public class MainActivity extends Activity {
    private static final int MENU_GROUP_STATIC = 0;
    private static final int MENU_GROUP_DYNAMIC = 1;
    private static final int MENU_HIDE_EXTRA_INFO = Menu.FIRST + 1;
    private static final String PREFERENCE_KEY = "com.asus.launcher.settings.developer.chart.prefs";
    private static final String KEY_SHOW_EXTRA_INFO = "show_extra_info";
    private ChartView mChartView;
    private ChartInfoPager mChartInfoPager;

    private boolean mIsShowExtraInfo = true;

    private static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
    }

    public static boolean isShowExtraInfo(Context context) {
        return getPrefs(context).getBoolean(KEY_SHOW_EXTRA_INFO, true);
    }

    public static void setShowExtraInfo(Context context, boolean show) {
        getPrefs(context).edit().putBoolean(KEY_SHOW_EXTRA_INFO, show).commit();
    }

    public static boolean isShowProcess(Context context, String processName) {
        return getPrefs(context).getBoolean(processName, true);
    }

    public static void setShowProcess(Context context, String processName, boolean show) {
        getPrefs(context).edit().putBoolean(processName, show).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mChartView = (ChartView) findViewById(R.id.chart_view);
        mChartView.addCallback(new ChartView.Callback() {
            @Override
            public void onDataLoaded() {
                invalidateOptionsMenu();
            }
        });
        mChartInfoPager = (ChartInfoPager) findViewById(R.id.chart_pager);
        PagerTabStrip pagerTab = (PagerTabStrip) findViewById(R.id.pager_tab);
        mChartInfoPager.setPagerTabStrip(pagerTab);
        mChartView.setChartInfoPager(mChartInfoPager);
        mChartView.getLogDataAndDraw();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void addDynamicMenus(Menu menu) {
        int counter = 100;
        for (ChartSeries cs : mChartView.getSeries()) {
            menu.add(MENU_GROUP_DYNAMIC, counter, counter, cs.getSeriesName());
            ++counter;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        mIsShowExtraInfo = isShowExtraInfo(this);
        menu.add(MENU_GROUP_STATIC, MENU_HIDE_EXTRA_INFO, 0, "Show extra info");
        addDynamicMenus(menu);
        setMenuCheckStatus(menu);

        return true;
    }

    private void setMenuCheckStatus(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            final MenuItem item = menu.getItem(i).setCheckable(true);
            if (item.getItemId() == MENU_HIDE_EXTRA_INFO) {
                item.setChecked(mIsShowExtraInfo);
            } else {
                item.setChecked(isShowProcess(this, item.getTitle().toString()));
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_HIDE_EXTRA_INFO) {
            mIsShowExtraInfo = !mIsShowExtraInfo;
            item.setChecked(mIsShowExtraInfo);
            setShowExtraInfo(this, mIsShowExtraInfo);
            mChartInfoPager.setup(mChartView.getSeries());
        } else {
            final String processName = item.getTitle().toString();
            final boolean isShowProcess = isShowProcess(this, processName);
            item.setChecked(!isShowProcess);
            setShowProcess(this, processName, !isShowProcess);
            changeSeriesVisibility(processName, !isShowProcess);
            mChartView.requestReDraw();
        }
        return true;
    }

    private void changeSeriesVisibility(String process, boolean visible) {
        for (ChartSeries cs : mChartView.getSeries()) {
            if (process.equals(cs.getSeriesName())) {
                cs.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
            }
        }
    }

}
