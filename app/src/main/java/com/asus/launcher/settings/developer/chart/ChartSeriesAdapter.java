package com.asus.launcher.settings.developer.chart;

import android.os.AsyncTask;
import android.util.Log;

import com.asus.launcher.log.LogBehaviorData;
import com.asus.launcher.log.LogConfigData;
import com.asus.launcher.log.LogData;
import com.asus.launcher.log.LogMemInfoData;

import java.util.ArrayList;
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

    private ArrayList<ChartSeries> convertFromLogDataToSeries(
            HashMap<String, ArrayList<LogData>> logDataMap) {
        ArrayList<ChartSeries> rtn = new ArrayList<>();
        Iterator<String> iterator = logDataMap.keySet().iterator();
        while (iterator.hasNext()) {
            final String key = iterator.next();
            Log.e(TAG, "logDataMap.get(" + key + ") size: " + logDataMap.get(key).size());
            ChartSeries cSeries = new ChartSeries(key);
            StringBuilder extras = new StringBuilder();
            int index = 0;
            for (LogData data : logDataMap.get(key)) {
                extras.append(data.toSimpleString());
                if (data instanceof LogBehaviorData) {
                } else if (data instanceof LogMemInfoData) {
                    if (DEBUG)
                        Log.v(TAG, "time: " + data.mTime +
                                        ", pss: " + ((LogMemInfoData) data).mTotalPss
                        );
                    cSeries.addChartPoint(new ChartPoint(index,
                            ((LogMemInfoData) data).mTotalPss, extras.toString()));
                    extras = new StringBuilder();
                    ++index;
                } else if (data instanceof LogConfigData) {
                }
            }
            rtn.add(cSeries);
        }
        return rtn;
    }

}
