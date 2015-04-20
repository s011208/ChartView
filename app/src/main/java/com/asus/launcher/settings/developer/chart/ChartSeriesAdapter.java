package com.asus.launcher.settings.developer.chart;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.asus.launcher.log.LogBehaviorData;
import com.asus.launcher.log.LogConfigData;
import com.asus.launcher.log.LogData;
import com.asus.launcher.log.LogMemInfoData;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

/**
 * Created by Yen-Hsun_Huang on 2015/4/20.
 */
public class ChartSeriesAdapter {

    private static final boolean DEBUG = ChartView.DEBUG;
    private static final String TAG = ChartView.TAG;

    public interface Callback {
        public void onPreExecute();

        public void onPostExecute(ArrayList<ChartSeries> series);
    }

    public ChartSeriesAdapter(Callback cb) {
        new LogFilesParser(cb).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class LogFilesParser extends AsyncTask<Void, Void, Void> {

        private final Callback mCallback;

        private final ArrayList<ChartSeries> mChartViewSeries = new ArrayList<>();

        public LogFilesParser(Callback callback) {
            mCallback = callback;
        }

        @Override
        public void onPreExecute() {
            if (mCallback != null) {
                mCallback.onPreExecute();
            }
        }

        private String getP(){
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + "launcherlogs";
        }

        private ArrayList<String> getLogPaths() {
            final ArrayList<String> rtn = new ArrayList<>();
            File f = new File(getP());
            for (String path : f.list()) {
                if (DEBUG)
                    Log.v(TAG, path);
                rtn.add(path);
            }
            return rtn;
        }

        @Override
        protected Void doInBackground(Void... params) {
//            final ArrayList<String> logPaths = LogHelper.getLogFilesPath();
            final ArrayList<String> logPaths = getLogPaths();
            HashMap<String, ArrayList<String>> processMap = splitPathByFileName(logPaths);
            HashMap<String, ArrayList<LogData>> logDataMap = retrieveLogData(processMap);
            mChartViewSeries.addAll(convertFromLogDataToSeries(logDataMap));
            return null;
        }

        private ArrayList<ChartSeries> convertFromLogDataToSeries(
                HashMap<String, ArrayList<LogData>> logDataMap) {
            ArrayList<ChartSeries> rtn = new ArrayList<>();
            Iterator<String> iterator = logDataMap.keySet().iterator();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                Log.e(TAG, "logDataMap.get("+key+") size: " + logDataMap.get(key).size());
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

        private HashMap<String, ArrayList<LogData>> retrieveLogData(
                HashMap<String, ArrayList<String>> processMap) {
            final HashMap<String, ArrayList<LogData>> rtn = new HashMap<>();
            Iterator<String> iterator = processMap.keySet().iterator();
            while (iterator.hasNext()) {
                final String key = iterator.next();
                ArrayList<LogData> logData = retrieveLogDataFromFiles(processMap.get(key));
                Collections.sort(logData);
                rtn.put(key, logData);
            }
            return rtn;
        }

        private LogData retrieveLog(String raw) {
            if (raw == null)
                return null;
            String[] splits = raw.split(": ");
            if (splits.length != 2) {
                return null;
            }
            try {
                if (raw.startsWith(Level.ALL.getName())) {
                    return new LogBehaviorData(new JSONObject(splits[1]));
                } else if (raw.startsWith(Level.INFO.getName())) {
                    return new LogMemInfoData(new JSONObject(splits[1]));
                } else if (raw.startsWith(Level.CONFIG.getName())) {
                    return new LogConfigData(new JSONObject(splits[1]));
                }
            } catch (Exception e) {
            }
            return null;
        }

        private ArrayList<LogData> retrieveLogDataFromFiles(ArrayList<String> paths) {
            final ArrayList<LogData> rtn = new ArrayList<>();
            FileInputStream is;
            BufferedReader reader;
            for (String path : paths) {
                final File file = new File(getP() + File.separator + path);
                if (file.exists()) {
                    try {
                        is = new FileInputStream(file);
                        reader = new BufferedReader(new InputStreamReader(is));
                        String line = reader.readLine();
                        while (line != null) {
                            rtn.add(retrieveLog(line));
                            line = reader.readLine();
                        }
                        reader.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            return rtn;
        }

        private HashMap<String, ArrayList<String>> splitPathByFileName(ArrayList<String> logPath) {
            final HashMap<String, ArrayList<String>> processMap = new HashMap<>();
            if (logPath == null || logPath.isEmpty())
                return processMap;
            for (String path : logPath) {
                if (!path.endsWith(".txt")) {
                    continue;
                }
                final int lastIndexOfSlashInPath = path.lastIndexOf(File.separator);
                final String fileName = path.substring(lastIndexOfSlashInPath + 2, path.length() - 4);
                ArrayList<String> fileLists = processMap.get(fileName);
                if (fileLists == null) {
                    fileLists = new ArrayList<>();
                }
                fileLists.add(path);
                processMap.put(fileName, fileLists);
            }
            return processMap;
        }

        @Override
        public void onPostExecute(Void result) {
            if (mCallback != null) {
                mCallback.onPostExecute(mChartViewSeries);
            }
        }
    }
}
