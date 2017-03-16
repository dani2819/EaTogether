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
    public static void StorePref(Context context, HashMap<String, String> prefsToStore){
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);;
        SharedPreferences.Editor editor = sharedpreferences.edit();
        for (Map.Entry<String, String> e : prefsToStore.entrySet()){
            editor.putString(e.getKey(), e.getValue());
        }
        editor.commit();
    }

    public static String getStoredPref(Context context, String prefName){
        return "";
    }
}
