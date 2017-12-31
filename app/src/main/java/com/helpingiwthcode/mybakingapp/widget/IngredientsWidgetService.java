package com.helpingiwthcode.mybakingapp.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.content.Intent;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.dao.DAOIngredients;
import com.helpingiwthcode.mybakingapp.dao.DAORecipe;
import com.helpingiwthcode.mybakingapp.idao.IDAOIngredients;
import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class IngredientsWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Timber.e("onGetViewFactory!");
        return new IngredientsRemoteViewsFactory(this.getApplicationContext());
    }
}

class IngredientsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private IDAOIngredients idaoIngredients = new DAOIngredients();
    private IDAORecipes idaoRecipes = new DAORecipe();
    private Context mContext;
    private ArrayList<String> ingredients;

    public IngredientsRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
        RealmMethods.init(mContext);
    }

    @Override
    public void onCreate() {}

    @Override
    public void onDataSetChanged() {
        ingredients = getIngredients();
    }

    private ArrayList<String> getIngredients() {
        int recipeId = idaoRecipes.getWidgetRecipeId();
        ingredients = new ArrayList<>();
        List<Ingredients> ingredientsList = idaoIngredients.getIngredientsFromRecipe(recipeId);
        String ingredientsText = "";
        int ingredientIndex = 0;
        for (Ingredients ingredient : ingredientsList) {
            ingredientIndex++;
            ingredientsText = ingredientIndex + ": " + ingredient.getQuantity() + " " + ingredient.getMeasure() + " " + ingredient.getIngredient();
            ingredients.add(ingredientsText);
            Timber.e("Adding ingredients for the widget: "+ingredientsText);
        }
        return ingredients;
    }

    @Override
    public void onDestroy() {}

    @Override
    public int getCount() {
        return ingredients.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (ingredients.size() == 0)
            return null;
        String ingredient = ingredients.get(position);
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.item_ingredient_widget);
        views.setTextViewText(R.id.tv_ingredient, ingredient);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}


