package com.helpingiwthcode.mybakingapp.dao;

import com.helpingiwthcode.mybakingapp.idao.IDAOIngredients;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public class DAOIngredients implements IDAOIngredients {
    @Override
    public List<Ingredients> getIngredientsFromRecipe(int recipeId) {
        List<Ingredients> ingredientsList = null;
        RealmResults<Ingredients> ingredientsResults = null;
        Realm realm = null;
        try{
            realm = RealmMethods.realm();
            ingredientsResults = realm.where(Ingredients.class).equalTo("recipeId", recipeId).findAllSorted("order", Sort.ASCENDING);
            ingredientsList = realm.copyFromRealm(ingredientsResults);
        }
        catch (Exception e){
            Timber.e("Exception on getIngredientsFromRecipe: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return ingredientsList;
    }
}
