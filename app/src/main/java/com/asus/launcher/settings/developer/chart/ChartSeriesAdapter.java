package com.asus.launcher.settings.developer.chart;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.asus.launcher.log.LogBehaviorData;
import com.asus.launcher.log.LogConfigData;
import com.asus.launcher.log.LogData;
import com.asus.launcher.log.LogMemInfoData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartSeriesAdapter extends AsyncTask<Void, Void, Void> {

    private static final boolean DEBUG = ChartView.DEBUG;
    private static final String TAG = ChartView.TAG;

    public interface Callback {
        public void onPreExecute();

        public void onPostExecute(ArrayList<ChartSeries> series);
    }

    private final HashMap<String, ArrayList<LogData>> mLogDataMap = new HashMap<>();

    private final Callback mCallback;

    private final ArrayList<ChartSeries> mChartViewSeries = new ArrayList<>();

    public ChartSeriesAdapter(Callback callback) {
        mCallback = callback;
    }

    public ChartSeriesAdapter setLogDataMap(HashMap<String, ArrayList<LogData>> logDataMap) {
        mLogDataMap.clear();
        mLogDataMap.putAll(logDataMap);
        return this;
    }

    @Override
    public void onPreExecute() {
        if (mCallback != null) {
            mCallback.onPreExecute();
        }
    }

    @Override
    public void onPostExecute(Void result) {
        if (mCallback != null) {
            mCallback.onPostExecute(mChartViewSeries);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        mChartViewSeries.addAll(convertFromLogDataToSeries(mLogDataMap));
        return null;
    }

    private static ArrayList<ChartSeries> convertFromLogDataToSeries(
            HashMap<String, ArrayList<LogData>> logDataMap) {
        ArrayList<ChartSeries> rtn = new ArrayList<>();
        Iterator<String> iterator = logDataMap.keySet().iterator();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            if (DEBUG)
                Log.v(TAG, "logDataMap.get(" + key + ") size: " + logDataMap.get(key).size());
            ChartSeries cSeries = new ChartSeries(key);
            StringBuilder sb = new StringBuilder();
            for (LogData data : logDataMap.get(key)) {
                addAndCombineChartPoint(data, cSeries, sb);
            }
            if (DEBUG)
                Log.v(TAG, "cSeries.getChartPointSize(): " + cSeries.getChartPointSize());
            rtn.add(cSeries);
        }
        return rtn;
    }

    private static void addAndCombineChartPoint(LogData data, ChartSeries cSeries, StringBuilder sb) {
        final int index = (int) (data.mTime / 60000); // using minutes as index
        if (data instanceof LogBehaviorData) {
            if (isLogMeanful(data.toSimpleString())) {
                sb.append(data.toSimpleString());
            }
        } else if (data instanceof LogMemInfoData) {
            ChartPoint point = cSeries.getChartPoint(index);
            if (point == null) {
                String log = sb.toString();
                if (isLogMeanful(log)) {
                    sb.delete(0, sb.length());
                } else {
                    log = null;
                }
                point = new ChartPoint(index, ((LogMemInfoData) data).mTotalPss, log, data.toSimpleString());
                cSeries.addChartPoint(index, point);
            } else {
                // combine and get max total pass
                String log = sb.toString();
                if (isLogMeanful(log)) {
                    sb.delete(0, sb.length());
                } else {
                    log = null;
                }
                point.combinePoints(new ChartPoint(index, ((LogMemInfoData) data).mTotalPss, log, data.toSimpleString()));
                cSeries.addChartPoint(index, point);
            }
        } else if (data instanceof LogConfigData) {
            if (isLogMeanful(data.toSimpleString())) {
                sb.append(data.toSimpleString());
            }
        }
        if (DEBUG) {
            Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(data.mTime);
            Log.v(TAG, "index: " + index
                    + ", log: " + data.toSimpleString());
        }
    }

    private static boolean isLogMeanful(String log) {
        return TextUtils.isEmpty(log) == false && "".equals(log) == false;
    }
}
