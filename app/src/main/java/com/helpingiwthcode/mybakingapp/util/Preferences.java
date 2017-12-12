package com.helpingiwthcode.mybakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

/**
 * Created by helpingwithcode on 12/12/17.
 */

public class Preferences {

    public SharedPreferences preferences;
    public SharedPreferences.Editor preferencesEditor;

    public Preferences(Context thisContext){
        preferences = PreferenceManager.getDefaultSharedPreferences(thisContext);
        preferencesEditor = preferences.edit();
    }

    public void addString(String keyName, String keyValue){
        //Log.e("config.add", keyName+":"+keyValue);
        preferencesEditor.putString(keyName,keyValue);
        preferencesEditor.apply();
    }

    public void reset(String keyName){
        //Log.e("config.reset", keyName);
        preferencesEditor.putString(keyName,"");
        preferencesEditor.apply();
    }

    public void addBoolean(String keyName, boolean keyValue){
        //Log.e("Saving", keyName+":"+keyValue);
        preferencesEditor.putBoolean(keyName,keyValue);
        preferencesEditor.apply();
    }

    public void addInt(String keyName, int keyValue){
        //Log.e("Saving", keyName+":"+keyValue);
        preferencesEditor.putInt(keyName,keyValue);
        preferencesEditor.apply();
    }

    public String rString(String keyName){
        //Log.e("Reading", keyName+":"+preferences.getString(keyName,""));
        return preferences.getString(keyName,"");
    }

    public int rInt(String keyName){
        //Log.e("Reading", keyName+":"+preferences.getInt(keyName,0));
        return preferences.getInt(keyName,0);
    }

    public boolean rBoolean(String keyName){
        //Log.e("Reading", keyName+":"+preferences.getBoolean(keyName,true));
        return preferences.getBoolean(keyName,true);
    }
}