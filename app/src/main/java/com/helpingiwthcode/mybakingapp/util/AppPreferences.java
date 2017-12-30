package com.helpingiwthcode.mybakingapp.util;

/**
 * Created by helpingwithcode on 28/12/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class AppPreferences {

    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEditor;

    public AppPreferences(Context thisContext){
        preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        preferencesEditor = preferences.edit();
    }

    public void putInt(String keyName, int keyValue){
        Log.e("Saving", keyName+":"+keyValue);
        preferencesEditor.putInt(keyName,keyValue);
        preferencesEditor.apply();
    }

    public int rInt(String keyName){
        Log.e("Reading", keyName+":"+preferences.getInt(keyName,0));
        return preferences.getInt(keyName,0);
    }
}