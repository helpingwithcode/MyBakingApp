package com.helpingiwthcode.mybakingapp.util;

import android.content.Context;

import com.google.gson.Gson;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import timber.log.Timber;

/**
 * Created by root on 08/12/17.
 */

public class RecipeUtils {
    public static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";
    public static final String BROADCAST_PERMISSIONS_GRANTED = "recipe.permissions.granted";
    public static final String BROADCAST_PERMISSIONS_DENIED = "recipe.permissions.denied";

    public static void parseServerResponse(String responseString, Context context) {
        try {
            JSONArray response = new JSONArray(responseString);
            Timber.e("response.length: "+response.length());
            JSONObject recipe;
            int recipeId;
            for (int i = 0; i < response.length(); i++) {
                recipe = new JSONObject(String.valueOf(response.get(i)));
                recipeId = recipe.getInt("id");
                createRecipe(recipe);
                createIngredients(recipe.get("ingredients"),recipeId);
                createSteps(recipe.get("steps"),recipeId);
            }
        }
        catch (Exception e){
            Timber.e("expetion on parseServerResponse: "+e.getLocalizedMessage());
        }
    }

    private static void createSteps(Object steps, int recipeId) {
        Timber.e("Steps from Recipe["+recipeId+"]: "+steps.toString());
        try {
            JSONArray stepsArray = new JSONArray(steps.toString());
            JSONObject step;
            Steps realmSteps;
            for (int i = 0; i < stepsArray.length(); i++) {
                step = new JSONObject(String.valueOf(stepsArray.get(i)));
                realmSteps = new Gson().fromJson(step.toString(),Steps.class);
                realmSteps.setRecipeId(recipeId);
                RealmMethods.insertWithTransaction(realmSteps);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private static void createIngredients(Object ingredients, int recipeId) {
        Timber.e("Ingredients from Recipe["+recipeId+"]: "+ingredients.toString());
        try {
            JSONArray ingredientsArray = new JSONArray(ingredients.toString());
            JSONObject ingredient;
            Ingredients realmIngredient;
            for (int i = 0; i < ingredientsArray.length(); i++) {
                ingredient = new JSONObject(String.valueOf(ingredientsArray.get(i)));
                realmIngredient = new Gson().fromJson(ingredient.toString(),Ingredients.class);
                realmIngredient.setRecipeId(recipeId);
                RealmMethods.insertWithTransaction(realmIngredient);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static void createRecipe(JSONObject recipe) {
        Recipe realmRecipe = new Gson().fromJson(recipe.toString(),Recipe.class);
        RealmMethods.insertWithTransaction(realmRecipe);
    }
}
