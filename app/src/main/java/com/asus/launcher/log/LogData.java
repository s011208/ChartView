
package com.asus.launcher.log;

import org.json.JSONObject;

/**
 * @author yen-hsun_huang
 */
public abstract class LogData implements Comparable<LogData> {

    static final String TIME = "time";

    public long mTime;

    public abstract JSONObject toJson();

    @Override
    public final String toString() {
        return toJson().toString();
    }

    public abstract String toSimpleString();

    @Override
    public int compareTo(LogData another) {
        if(mTime == another.mTime)
            return 0;
        return mTime < another.mTime ? -1 : 1;
    }
}
