package com.helpingiwthcode.mybakingapp.idao;

import com.helpingiwthcode.mybakingapp.model.Steps;

import java.util.List;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public interface IDAOSteps {
    List<Steps> getStepsFromRecipe(int recipeId);
    String getVideoUrl(int recipeId);
    Steps getStepFromRecipe(int recipeId, int stepId);
    boolean isLastStep(int recipeId, int stepId);
}
