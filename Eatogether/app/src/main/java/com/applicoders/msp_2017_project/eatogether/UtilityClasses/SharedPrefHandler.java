package com.applicoders.msp_2017_project.eatogether.UtilityClasses;

import android.content.Context;
import android.content.SharedPreferences;


import java.util.HashMap;
import java.util.Map;

import static com.applicoders.msp_2017_project.eatogether.Constants.MyPREFERENCES;

/**
 * Created by rafay on 3/16/2017.
 */

public class SharedPrefHandler {
    public static void StorePref(Context context, String prefsKey, String prefValue){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(prefsKey, prefValue);
        editor.commit();
    }

    public static String getStoredPref(Context context, String prefName){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        return sharedpreferences.getString(prefName, "");
    }
}
