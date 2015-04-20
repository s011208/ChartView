
package com.asus.launcher.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Debug.MemoryInfo;

/**
 * @author yen-hsun_huang
 */
public class LogMemInfoData extends LogData {
    private static final String TOTAL_PSS = "total_pss";

    private static final String TOTAL_USS = "total_uss";

    private static final String DALVIK_PRIVATE_CLEAN = "dalvikPrivateClean";

    private static final String DALVIK_PRIVATE_DIRTY = "dalvikPrivateDirty";

    private static final String DALVIK_PSS = "dalvikPss";

    private static final String DALVIK_SHARED_CLEAN = "dalvikSharedClean";

    private static final String DALVIK_SHARED_DIRTY = "dalvikSharedDirty";

    private static final String NATIVE_PRIVATE_CLEAN = "nativePrivateClean";

    private static final String NATIVE_PRIVATE_DIRTY = "nativePrivateDirty";

    private static final String NATIVE_PSS = "nativePss";

    private static final String NATIVE_SHARED_CLEAN = "nativeSharedClean";

    private static final String NATIVE_SHARED_DIRTY = "nativeSharedDirty";

    private static final String NATIVE_SWAPPABLE_PSS = "nativeSwappablePss";

    private static final String NATIVE_SWAPPED_OUT = "nativeSwappedOut";

    private static final String OTHER_PRIVATE_CLEAN = "otherPrivateClean";

    private static final String OTHER_PRIVATE_DIRTY = "otherPrivateDirty";

    private static final String OTHER_PSS = "otherPss";

    private MemoryInfo mInfo;

    public int mTotalPss;

    public LogMemInfoData(MemoryInfo info) {
        mInfo = info;
    }

    public LogMemInfoData(JSONObject json) {
        try {
            mTotalPss = json.getInt(TOTAL_PSS);
            mTime = json.getLong(TIME);
        } catch (JSONException e) {
        }
    }

    @Override
    public final JSONObject toJson() {
        JSONObject rtn = new JSONObject();
        if (mInfo == null) {
            return rtn;
        }
        try {
            rtn.put(TOTAL_PSS, mInfo.getTotalPss());
            rtn.put(TOTAL_USS,
                    getMemoryInfoMethod(mInfo, "getTotalUss", new Class[] {}, new Object[] {}, -1));
            rtn.put(DALVIK_PRIVATE_CLEAN, getMemoryInfoField(mInfo, "dalvikPrivateClean", -1));
            rtn.put(DALVIK_PRIVATE_DIRTY, mInfo.dalvikPrivateDirty);
            rtn.put(DALVIK_PSS, mInfo.dalvikPss);
            rtn.put(DALVIK_SHARED_CLEAN, getMemoryInfoField(mInfo, "dalvikSharedClean", -1));
            rtn.put(DALVIK_SHARED_DIRTY, mInfo.dalvikSharedDirty);
            rtn.put(NATIVE_PRIVATE_CLEAN, getMemoryInfoField(mInfo, "nativePrivateClean", -1));
            rtn.put(NATIVE_PRIVATE_DIRTY, mInfo.nativePrivateDirty);
            rtn.put(NATIVE_PSS, mInfo.nativePss);
            rtn.put(NATIVE_SHARED_CLEAN, getMemoryInfoField(mInfo, "nativeSharedClean", -1));
            rtn.put(NATIVE_SHARED_DIRTY, mInfo.nativeSharedDirty);
            rtn.put(NATIVE_SWAPPABLE_PSS, getMemoryInfoField(mInfo, "nativeSwappablePss", -1));
            rtn.put(NATIVE_SWAPPED_OUT, getMemoryInfoField(mInfo, "nativeSwappedOut", -1));
            rtn.put(OTHER_PRIVATE_CLEAN, getMemoryInfoField(mInfo, "otherPrivateClean", -1));
            rtn.put(OTHER_PRIVATE_DIRTY, mInfo.otherPrivateDirty);
            rtn.put(OTHER_PSS, mInfo.otherPss);
            rtn.put(TIME, System.currentTimeMillis());
        } catch (JSONException e) {
        }
        return rtn;
    }

    private static int getMemoryInfoMethod(MemoryInfo info, String property, Class<?>[] params,
            Object[] args, int defaultValue) {
        int rtn = defaultValue;
        try {
            Class<?> c = MemoryInfo.class;
            Method m = c.getMethod(property, params);
            rtn = (Integer)m.invoke(info, args);
        } catch (Exception e) {
        }
        return rtn;
    }

    private static int getMemoryInfoField(MemoryInfo info, String fieldName, int defaultValue) {
        int rtn = defaultValue;
        try {
            Class<?> c = MemoryInfo.class;
            Field f = c.getField(fieldName);
            rtn = (Integer)f.get(info);
        } catch (Exception e) {
        }
        return rtn;
    }

    @Override
    public String toSimpleString() {
        return "mTotalPss: " + mTotalPss + "\n";
    }
}
