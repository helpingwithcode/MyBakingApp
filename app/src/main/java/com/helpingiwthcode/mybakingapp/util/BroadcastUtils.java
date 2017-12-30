package com.helpingiwthcode.mybakingapp.util;

import android.content.Context;
import android.content.Intent;

/**
 * Created by helpingwithcode on 01/10/17.
 */

public class BroadcastUtils {
    public static final String RECIPE_LOAD_ERROR = "network.recipe_load.error";
    public static final String RECIPE_LOAD_SUCCESS = "network.recipe_load.success";

    public static void sendBroadcast(Context context, String intentName){
        context.sendBroadcast(new Intent(intentName));
    }
    public static void sendBroadcast(Context context, Intent thisIntent){
        context.sendBroadcast(thisIntent);
    }
}
