package com.helpingiwthcode.mybakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.activities.MainActivity;
import com.helpingiwthcode.mybakingapp.activities.RecipeDetailActivity;
import com.helpingiwthcode.mybakingapp.dao.DAORecipe;
import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import timber.log.Timber;

public class IngredientsWidget extends AppWidgetProvider {

    private static IDAORecipes idaoRecipes = new DAORecipe();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Timber.e("updateAppWidget, gettingIngredientsGriRemoteView");
        appWidgetManager.updateAppWidget(appWidgetId, getIngredientsGrid(context));
    }

    private static RemoteViews getIngredientsGrid(Context context) {
        Timber.e("updateAppWidget, getIngredientsGrid");
        Recipe recipe = idaoRecipes.getRecipeToWidget();
        boolean hasRecipeToDisplay = (recipe != null);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        if (!hasRecipeToDisplay) {
            views.setTextViewText(R.id.recipe_name, context.getString(R.string.widget_message_no_ingredients));
            views.setViewVisibility(R.id.gv_ingredient, View.GONE);
        } else {
            views.setTextViewText(R.id.recipe_name, String.format(context.getString(R.string.widget_title), recipe.getName()));
            Intent intent = new Intent(context, IngredientsWidgetService.class);
            views.setRemoteAdapter(R.id.gv_ingredient, intent);
            views.setViewVisibility(R.id.gv_ingredient, View.VISIBLE);
        }
        Intent appIntent = getPendingIntent(context);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.gv_ingredient, appPendingIntent);
        views.setOnClickPendingIntent(R.id.widget_container, appPendingIntent);
        return views;
    }

    private static Intent getPendingIntent(Context context) {
        Timber.e("static setPendintIntent");
        Recipe recipe = idaoRecipes.getRecipeToWidget();
        boolean hasRecipeToDisplay = (recipe != null);
        Intent recipeIntent = new Intent(context, (hasRecipeToDisplay) ? RecipeDetailActivity.class : MainActivity.class);
        if (hasRecipeToDisplay)
            recipeIntent.putExtra(RecipeUtils.RECIPE_ID, idaoRecipes.getWidgetRecipeId());
        return recipeIntent;
    }

    public static void updateIngredientsWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Timber.e("updateIngredientsWidget");
        for (int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RecipeUtils.updateWidget(context);
    }
}

