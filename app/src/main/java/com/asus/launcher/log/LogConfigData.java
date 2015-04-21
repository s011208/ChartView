
package com.asus.launcher.log;

import org.json.JSONException;
import org.json.JSONObject;

public class LogConfigData extends LogData {

    public static final String DEVICE_MODEL = "device model";

    public static final String FONT_STYLE = "font style";

    public static final String WORKSPACE_PAGE_COUNT = "workspace page count";

    public static final String WORKSPACE_WIDGET_COUNT = "workspace widget count";

    private static final String CONFIG = "config";

    private String mConfig;

    public LogConfigData(String config) {
        mConfig = config;
        mTime = System.currentTimeMillis();
    }

    public LogConfigData(JSONObject json) {
        try {
            mConfig = json.getString(CONFIG);
            mTime = json.getLong(TIME);
        } catch (JSONException e) {
        }
    }

    @Override
    public final JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(CONFIG, mConfig);
            json.put(TIME, mTime);
        } catch (JSONException e) {
        }
        return json;
    }

    @Override
    public String toSimpleString() {
        return "";
    }

}
