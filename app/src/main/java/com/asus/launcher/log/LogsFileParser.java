package com.asus.launcher.log;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

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
 * Created by yenhsunhuang on 15/4/21.
 */
public class LogsFileParser extends AsyncTask<Void, Void, Void> {
    private static final boolean DEBUG = true;
    private static final String TAG = "QQQQ";

    public interface Callback {
        public void onPreExecute();

        public void onPostExecute(HashMap<String, ArrayList<LogData>> logDataMap);
    }

    private final Callback mCallback;

    private final HashMap<String, ArrayList<LogData>> mLogDataMap = new HashMap<>();

    public LogsFileParser(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void onPreExecute() {
        if (mCallback != null) {
            mCallback.onPreExecute();
        }
    }

    private static String getP() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "launcherlogs";
    }

    private static ArrayList<String> getLogPaths() {
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
        mLogDataMap.putAll(logDataMap);
        return null;
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

    private static LogData retrieveLog(String raw) {
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

    private static ArrayList<LogData> retrieveLogDataFromFiles(ArrayList<String> paths) {
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

    private static HashMap<String, ArrayList<String>> splitPathByFileName(ArrayList<String> logPath) {
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
            mCallback.onPostExecute(mLogDataMap);
        }
    }
}