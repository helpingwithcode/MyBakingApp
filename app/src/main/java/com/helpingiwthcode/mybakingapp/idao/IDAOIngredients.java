package com.helpingiwthcode.mybakingapp.idao;

import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Recipe;

import java.util.List;

/**
 * Created by helpingwithcode on 16/12/17.
 */

public interface IDAOIngredients {
    List<Ingredients> getIngredientsFromRecipe(int recipeId);
}
