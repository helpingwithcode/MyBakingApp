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

    public String getVideoUrl(int recipeId) {
        Realm realm = null;
        String videoUrl = "";
        try{
            realm = RealmMethods.realm();
            Steps firstStep = realm.where(Steps.class).equalTo("recipeId",recipeId).findAllSorted("id", Sort.ASCENDING).first();
            videoUrl = firstStep.getVideoURL();
        }
        catch (Exception e){
            Timber.e("Exception on getVideoUrl: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return videoUrl;
    }

    @Override
    public Steps getStepFromRecipe(int recipeId, int stepId) {
        Realm realm = null;
        Steps step = null;
        Steps stepToReturn = null;
        try{
            realm = RealmMethods.realm();
            step = realm.where(Steps.class).equalTo("recipeId",recipeId).equalTo("id",stepId).findFirst();
            stepToReturn = realm.copyFromRealm(step);
        }
        catch (Exception e){
            Timber.e("Exception on getStepFromRecipe: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return stepToReturn;
    }

    @Override
    public boolean isLastStep(int recipeId, int stepId) {
        boolean isLastStep = false;
        Steps lastStep = null;
        Realm realm = null;
        try{
            realm = RealmMethods.realm();
            lastStep = realm.where(Steps.class).equalTo("recipeId",recipeId).findAllSorted("id", Sort.DESCENDING).first();
            isLastStep = (stepId == lastStep.getId());
        }
        catch (Exception e){
            Timber.e("Exception on isLastStep: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return isLastStep;
    }

    @Override
    public boolean isStepAvailable(int recipeId, int stepIndex, String direction) {
        boolean isAvailable = false;
        int stepIndexToCheck = (direction.equals("next")) ? stepIndex+1 : stepIndex-1;
        Realm realm = null;
        try{
            realm = RealmMethods.realm();
            Steps step = realm.where(Steps.class).equalTo("id", stepIndexToCheck).equalTo("recipeId",recipeId).findFirst();
            if(step != null)
                isAvailable = true;
        }
        catch (Exception e){
            Timber.e("Exception on isStepAvailable: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        Timber.e("isStep "+stepIndexToCheck+" Available? "+isAvailable);
        return isAvailable;
    }

    @Override
    public int getStepCount(int recipeId) {
        int stepsCount = 0;
        RealmResults<Steps> steps = null;
        Realm realm = null;
        try{
            realm = RealmMethods.realm();
            steps = realm.where(Steps.class).equalTo("recipeId",recipeId).findAll();
            stepsCount = steps.size()-1;
        }
        catch (Exception e){
            Timber.e("Exception on isLastStep: "+e.getLocalizedMessage());
        }
        finally {
            RealmMethods.closeInstance(realm);
        }
        return stepsCount;
    }

}
