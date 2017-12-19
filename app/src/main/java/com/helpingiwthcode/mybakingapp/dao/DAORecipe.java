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
        try{
            realm = RealmMethods.realm();
            recipesResults = realm.where(Recipe.class).findAll();
            recipesArray = realm.copyFromRealm(recipesResults);
        }
        catch (Exception e){
            Timber.e("Exception on getRecipes: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return recipesArray;
    }
}
