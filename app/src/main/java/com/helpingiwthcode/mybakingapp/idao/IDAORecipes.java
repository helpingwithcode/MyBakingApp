package com.helpingiwthcode.mybakingapp.idao;

import com.helpingiwthcode.mybakingapp.model.Recipe;

import java.util.List;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public interface IDAORecipes {
    List<Recipe> getRecipes();
    String getRecipeName(int recipeId);
    void setAsWidget(int recipeId);
    Recipe getRecipeToWidget();

    boolean isRecipeOnWidget(int recipeId);
    void removeAsWidget(int recipeId);
    int getWidgetRecipeId();
}
