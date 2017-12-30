package com.helpingiwthcode.mybakingapp.dao;

import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public class DAORecipe implements IDAORecipes {

    @Override
    public List<Recipe> getRecipes() {
        List<Recipe> recipesArray = null;
        RealmResults<Recipe> recipesResults = null;
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            recipesResults = realm.where(Recipe.class).findAll();
            recipesArray = realm.copyFromRealm(recipesResults);
            Timber.e(recipesResults.toString());
        } catch (Exception e) {
            Timber.e("Exception on getRecipes: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
        return recipesArray;
    }

    @Override
    public String getRecipeName(int recipeId) {
        String recipeName = "";
        Recipe recipe = null;
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            recipe = realm.where(Recipe.class).equalTo("id", recipeId).findFirst();
            recipeName = recipe.getName();
        } catch (Exception e) {
            Timber.e("Exception on getRecipes: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
        return recipeName;
    }

    @Override
    public void setAsWidget(final int recipeId) {
        Timber.e("setAsWidget recipe["+recipeId+"]");
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            final RealmResults<Recipe> recipeAsWidget = realm.where(Recipe.class)
                    .equalTo("onWidget", true)
                    .findAll();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if(recipeAsWidget.size() != 0) {
                        for (Recipe recipe : recipeAsWidget)
                            recipe.setOnWidget(false);
                    }
                    Recipe recipeWidgetToBe = realm.where(Recipe.class)
                            .equalTo("id", recipeId)
                            .findFirst();
                    recipeWidgetToBe.setOnWidget(true);
                    Timber.e("Setting recipe with id["+recipeWidgetToBe.getId()+"] as widget");
                }
            });
        } catch (Exception e) {
            Timber.e("Exception on setAsWidget: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
    }

    @Override
    public Recipe getRecipeToWidget() {
        Recipe recipe = null;
        Recipe recipeCopy = null;
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            realm.refresh();
            recipe = realm.where(Recipe.class).equalTo("onWidget", true).findFirst();
            if(recipe != null) {
                Timber.e("Recipe id: " + recipe.getId());
                recipeCopy = realm.copyFromRealm(recipe);
            }
        } catch (Exception e) {
            Timber.e("Exception on getRecipeToWidget: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
        return recipeCopy;
    }

    @Override
    public boolean isRecipeOnWidget(int recipeId) {
        boolean isWidget = false;
        Recipe recipe = null;
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            realm.refresh();
            recipe = realm.where(Recipe.class).equalTo("id", recipeId).findFirst();
            isWidget = (recipe.isOnWidget());
        } catch (Exception e) {
            Timber.e("Exception on isRecipeOnWidget: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
        Timber.e("isRecipeOnWidget recipe["+recipeId+"]?"+isWidget);
        return isWidget;
    }

    @Override
    public void removeAsWidget(final int recipeId) {
        Timber.e("removeAsWidget recipe["+recipeId+"]");
        Realm realm = null;
        try {
            realm = RealmMethods.realm();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Recipe recipe = realm.where(Recipe.class).equalTo("id", recipeId).findFirst();
                    recipe.setOnWidget(false);
                }
            });
        } catch (Exception e) {
            Timber.e("Exception on isRecipeOnWidget: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
    }

    @Override
    public int getWidgetRecipeId() {
        Recipe recipe = null;
        Realm realm = null;
        int widgetRecipeId = 0;
        try {
            realm = RealmMethods.realm();
            recipe = realm.where(Recipe.class).equalTo("onWidget", true).findFirst();
            widgetRecipeId = recipe.getId();
        } catch (Exception e) {
            Timber.e("Exception on getWidgetRecipeId: " + e.getLocalizedMessage());
        } finally {
            RealmMethods.closeInstance(realm);
        }
        Timber.e("Recipe as widget: "+widgetRecipeId);
        return widgetRecipeId;
    }
}
