package com.helpingiwthcode.mybakingapp.dao;

import com.helpingiwthcode.mybakingapp.idao.IDAOSteps;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public class DAOSteps implements IDAOSteps {
    @Override
    public List<Steps> getStepsFromRecipe(int recipeId) {
        //RealmMethods.realm();
        List<Steps> stepsList = null;
        RealmResults<Steps> stepsResults = null;
        Realm realm = null;
        try{
            realm = RealmMethods.realm();
            stepsResults = realm.where(Steps.class).equalTo("recipeId",recipeId).findAllSorted("id", Sort.ASCENDING);
            stepsList = realm.copyFromRealm(stepsResults);
        }
        catch (Exception e){
            Timber.e("Exception on getStepsFromRecipe: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return stepsList;
    }
}
