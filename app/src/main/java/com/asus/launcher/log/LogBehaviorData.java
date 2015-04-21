package com.asus.launcher.log;

import org.json.JSONException;
import org.json.JSONObject;

public class LogBehaviorData extends LogData {
    private static final String CATEGORY = "category";

    private static final String ACTION = "action";

    private static final String LABEL = "label";

    public String mCategory;

    public String mAction;

    public String mLabel;

    public LogBehaviorData(String category) {
        this(category, null, null);
    }

    public LogBehaviorData(String category, String action, String label) {
        mCategory = category;
        mAction = action;
        mLabel = label;
    }

    public LogBehaviorData(JSONObject json) {
        try {
            mCategory = json.getString(CATEGORY);
            mAction = json.getString(ACTION);
            mLabel = json.getString(LABEL);
            mTime = json.getLong(TIME);
        } catch (JSONException e) {
        }
    }

    @Override
    public final JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put(CATEGORY, mCategory);
            json.put(ACTION, mAction);
            json.put(LABEL, mLabel);
            json.put(TIME, System.currentTimeMillis());
        } catch (JSONException e) {
        }
        return json;
    }

    @Override
    public String toSimpleString() {
        return "Category: " + mCategory
                + (mAction == null ? "" : ", Action: " + mAction)
                + (mLabel == null ? "" : ", Label: " + mLabel) + "\n";
    }
}
