
package com.helpingiwthcode.mybakingapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_DONE_INSERTING;
import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_PERMISSIONS_DENIED;
import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_PERMISSIONS_GRANTED;
import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_RECIPE_CLICKED;

/**
 * Created by root on 08/12/17.
 */

public class Utils {

    public static void checkPermissions(Activity activity, Context context) {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
        };

        List<String> list = new ArrayList<>();
        for (String permission : permissions)
            if (!(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED))
                list.add(permission);

        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        if(list.size() > 0)
            ActivityCompat.requestPermissions(activity, newPermissions, 1);
        else
            BroadcastUtils.sendBroadcast(context, BROADCAST_PERMISSIONS_GRANTED);
    }

    public static IntentFilter getMainIntentFilters() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BROADCAST_PERMISSIONS_DENIED);
        intentFilter.addAction(BROADCAST_PERMISSIONS_GRANTED);
        intentFilter.addAction(BROADCAST_DONE_INSERTING);
        intentFilter.addAction(BROADCAST_RECIPE_CLICKED);
        return intentFilter;
    }
}
