package com.helpingiwthcode.mybakingapp.util;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.widget.IngredientsWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.RealmObject;
import timber.log.Timber;

/**
 * Created by root on 08/12/17.
 */

public class RecipeUtils {
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public static final String BROADCAST_PERMISSIONS_GRANTED = "recipe.permissions.granted";
    public static final String BROADCAST_PERMISSIONS_DENIED = "recipe.permissions.denied";
    public static final String BROADCAST_DONE_INSERTING = "broadcast.insert.finish";
    public static final String BROADCAST_RECIPE_CLICKED = "broadcast.recipe.clicked";
    public static final String BROADCAST_STEP_CLICKED = "broadcast.step.clicked";
    public static final String BROADCAST_SHOW_RECIPES = "broadcast.show.recipes";
    public static final String RECIPE_ID = "recipeId";
    public static final String STEP_ID = "stepId";
    public static final String APP_NAME = "MyBakingApp";
    private static ArrayList<RealmObject> realmObjectsToInsert;
    private static int CLASSES_READY_TO_INSERT;

    public static void parseServerResponse(String responseString, Context context) {
        realmObjectsToInsert = new ArrayList<>();
        CLASSES_READY_TO_INSERT = 0;
        RealmMethods.objectToInsert = 0;
        try {
            JSONArray response = new JSONArray(responseString);
            JSONObject recipe;
            int recipeId;
            int TOTAL_CLASSES_TO_INSERT = 3;
            RealmMethods.objectToInsert = response.length() * TOTAL_CLASSES_TO_INSERT;
            for (int i = 0; i < response.length(); i++) {
                recipe = new JSONObject(String.valueOf(response.get(i)));
                recipeId = recipe.getInt("id");
                createRecipe(recipe, context);
                createIngredients(recipe.get("ingredients"), recipeId, context);
                createSteps(recipe.get("steps"), recipeId, context);
            }
        } catch (Exception e) {
            Timber.e("expetion on parseServerResponse: " + e.getLocalizedMessage());
        }
    }

    private static void createSteps(final Object steps, final int recipeId, final Context context) {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    JSONArray stepsArray = new JSONArray(steps.toString());
                    JSONObject step;
                    Steps realmSteps;
                    for (int i = 0; i < stepsArray.length(); i++) {
                        step = new JSONObject(String.valueOf(stepsArray.get(i)));
                        realmSteps = new Gson().fromJson(step.toString(),Steps.class);
                        realmSteps.setRecipeId(recipeId);
                        realmSteps.setOrder(Integer.parseInt(recipeId+""+i));
                        realmObjectsToInsert.add(realmSteps);
                    }
                } catch (JSONException e) {
                    Timber.e("Exception on createSteps: "+e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                startInsertion(context);
            }
        }.execute();
    }

    private static void createIngredients(final Object ingredients, final int recipeId, final Context context) {
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    JSONArray ingredientsArray = new JSONArray(ingredients.toString());
                    JSONObject ingredient;
                    Ingredients realmIngredient;
                    for (int i = 0; i < ingredientsArray.length(); i++) {
                        ingredient = new JSONObject(String.valueOf(ingredientsArray.get(i)));
                        realmIngredient = new Gson().fromJson(ingredient.toString(), Ingredients.class);
                        realmIngredient.setRecipeId(recipeId);
                        realmIngredient.setOrder(Integer.parseInt(recipeId+""+i));
                        realmObjectsToInsert.add(realmIngredient);
                    }
                } catch (JSONException e) {
                    Timber.e("Exception on createIngredients: "+e.getLocalizedMessage());
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                startInsertion(context);
            }
        }.execute();
    }

    private static void startInsertion(Context context) {
        CLASSES_READY_TO_INSERT++;
        if(CLASSES_READY_TO_INSERT == RealmMethods.objectToInsert)
            RealmMethods.insertWithTransaction(realmObjectsToInsert,context);
    }

    private static void createRecipe(JSONObject recipe, Context context) {
        Recipe realmRecipe = new Gson().fromJson(recipe.toString(), Recipe.class);
        realmObjectsToInsert.add(realmRecipe);
        startInsertion(context);
    }

    public static void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, IngredientsWidget.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.gv_ingredient);
        IngredientsWidget.updateIngredientsWidget(context, appWidgetManager,appWidgetIds);
    }
}
