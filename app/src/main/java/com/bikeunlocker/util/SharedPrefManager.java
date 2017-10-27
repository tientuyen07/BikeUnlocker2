package com.bikeunlocker.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 10/25/2017.
 */

public class SharedPrefManager {
    public static final String SP_BIKE_UNLOCKER = "spBikeUnlocker";
    public static final String SP_NAME = "spName"; // SharedPreferences
    public static final String SP_EMAIL = "spEmail";
    public static final String SP_LOGIN_SUCCEED = "spLoginSucceed";

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    public SharedPrefManager(Context context) {
        sp = context.getSharedPreferences(SP_BIKE_UNLOCKER, Context.MODE_PRIVATE);
        spEditor = sp.edit();
    }

    public void saveSPString(String keySP, String value) {
        spEditor.putString(keySP, value);
        spEditor.commit();
    }

    public void saveSPInt(String keySP, int value) {
        spEditor.putInt(keySP, value);
        spEditor.commit();
    }

    public void saveSPBoolean(String keySP, boolean value) {
        spEditor.putBoolean(keySP, value);
        spEditor.commit();
    }

    public String getSPName() {
        return sp.getString(SP_NAME, "");
    }

    public String getSPEmail() {
        return sp.getString(SP_EMAIL, "");
    }

    public Boolean getSPSucceedLogin() {
        return sp.getBoolean(SP_LOGIN_SUCCEED, false);
    }
}
