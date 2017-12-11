package com.helpingiwthcode.mybakingapp.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * Created by helpingwithcode on 11/11/17.
 */

public class VolleyUtils {
    public static void getRecipes(final Context context){
        String urlPath = RecipeUtils.BASE_URL;
        //Timber.e("getRecipes from: "+urlPath);
        final RequestQueue requestFromServer = Volley.newRequestQueue(context);
        final StringRequest toSend = new StringRequest(Request.Method.GET , urlPath, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                RecipeUtils.parseServerResponse(response,context);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                BroadcastUtils.sendBroadcast(context, BroadcastUtils.RECIPE_LOAD_ERROR);
            }
        }){};

        requestFromServer.add(toSend);
    }
}
