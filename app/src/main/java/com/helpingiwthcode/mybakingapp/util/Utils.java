
package com.helpingiwthcode.mybakingapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 08/12/17.
 */

public class Utils {
    public static void checkPermissions(Activity activity, Context context) {
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
//                Manifest.permission.CAMERA,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.ACCESS_NETWORK_STATE,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_WIFI_STATE,
//                Manifest.permission.CHANGE_WIFI_STATE,
        };

        List<String> list = new ArrayList<String>();
        for (String permission : permissions)
            if (!(ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED))
                list.add(permission);

        String[] newPermissions = new String[list.size()];
        list.toArray(newPermissions);

        if(list.size() > 0)
            ActivityCompat.requestPermissions(activity, newPermissions, 1);
        else
            BroadcastUtils.sendBroadcast(context,RecipeUtils.BROADCAST_PERMISSIONS_GRANTED);
    }
}
