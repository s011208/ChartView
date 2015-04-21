package com.asus.launcher.settings.developer.chart.pager;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.util.Pair;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.asus.launcher.settings.developer.chart.ChartPoint;
import com.asus.launcher.settings.developer.chart.ChartSeries;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author yen-hsun_huang
 */
public class ChartInfoPagerAdapter extends PagerAdapter {
    private Context mContext;

    private ChartInfoPager mChartInfoPager;

    private final ArrayList<ChartSeries> mData = new ArrayList<>();

    public ChartInfoPagerAdapter(Context context, ArrayList<ChartSeries> drawingSeries,
                                 ChartInfoPager chartInfoPager) {
        mContext = context;
        mChartInfoPager = chartInfoPager;
        if (drawingSeries != null) {
            mData.addAll(drawingSeries);
            Collections.sort(mData);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mData.get(position).getSeriesName();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof ChartInfoPager.Callback) {
            mChartInfoPager.removeCallback((ChartInfoPager.Callback) object);
        }
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ListInfoView infoList = new ListInfoView(mContext);
        mChartInfoPager.addCallback(infoList);
        infoList.setAdapter(new ListInfoAdapter(mData.get(position)));
        container.addView(infoList);
        return infoList;
    }

    private class ListInfoView extends ListView implements ChartInfoPager.Callback {

        public ListInfoView(Context context) {
            super(context);
        }

        @Override
        public void onChartSelectIndexChanged(int chartPointIndex) {
            final int index = ((ListInfoAdapter) getAdapter()).getAdapterIndex(chartPointIndex);
            if (index == Integer.MAX_VALUE)
                return;
            setSelection(index);
            ((ListInfoAdapter) getAdapter()).setSelection(index);
        }
    }

    private class ListInfoAdapter extends BaseAdapter {
        private final ArrayList<Pair<Integer, ChartPoint>> mData = new ArrayList<>();

        private int mSelection = -1;

        public ListInfoAdapter(ChartSeries series) {
            SparseArray<ChartPoint> points = series.getChartPoints();
            ArrayList<Integer> keyList = new ArrayList<>();
            for (int i = 0; i < points.size(); i++) {
                keyList.add(points.keyAt(i));
            }
            Collections.sort(keyList);
            for (Integer key : keyList) {
                mData.add(new Pair(key, points.get(key)));
            }
        }

        public int getAdapterIndex(int chartPointIndex) {
            for (Pair<Integer, ChartPoint> item : mData) {
                if (item.first == chartPointIndex) {
                    return mData.indexOf(item);
                }
            }
            return Integer.MAX_VALUE;
        }

        public void setSelection(int index) {
            mSelection = index;
            notifyDataSetInvalidated();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Pair<Integer, ChartPoint> getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(mContext);
            }
            Pair<Integer, ChartPoint> item = getItem(position);
            ((TextView) convertView).setText(item.second.getExtraInfo());
            if (position == mSelection) {
                convertView.setBackgroundColor(Color.rgb(53, 178, 222));
            } else {
                convertView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            return convertView;
        }
    }
}
