package com.helpingiwthcode.mybakingapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.adapters.RecipeStepsAdapter;
import com.helpingiwthcode.mybakingapp.dao.DAOIngredients;
import com.helpingiwthcode.mybakingapp.dao.DAOSteps;
import com.helpingiwthcode.mybakingapp.idao.IDAOIngredients;
import com.helpingiwthcode.mybakingapp.idao.IDAOSteps;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.model.Steps;
import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by helpingwithcode on 17/12/17.
 */

public class RecipeDetailFragment extends Fragment implements RecipeStepsAdapter.RecipeStepAdapterOnClick{
    @BindView(R.id.rv_steps) RecyclerView stepsRv;
    @BindView(R.id.tv_ingredients) TextView ingredientsTv;
    @BindView(R.id.sv_holder) ScrollView holderSv;
    private int recipeId;
    IDAOIngredients idaoIngredients = new DAOIngredients();
    IDAOSteps idaoSteps = new DAOSteps();
    private int scrollViewPosition = 0;

    public RecipeDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.e("onActivityCreated: "+(savedInstanceState!=null));
        getRecipeIntent();
        if(savedInstanceState != null)
            scrollViewPosition = savedInstanceState.getInt(getString(R.string.key_scrollview_position));

    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putInt(getString(R.string.key_scrollview_position), holderSv.getScrollY());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void getRecipeIntent() {
        recipeId = getArguments().getInt(RecipeUtils.RECIPE_ID,0);
        setSteps();
        setIngredients();
        setScrollViewPosition();
    }

    private void setScrollViewPosition() {
        holderSv.scrollTo(0,scrollViewPosition);
    }

    private void setIngredients() {
        List<Ingredients> ingredientsList = idaoIngredients.getIngredientsFromRecipe(recipeId);
        String ingredientsText = "";
        String spacing = "";
        int ingredientIndex = 0;
        for(Ingredients ingredients : ingredientsList){
            ingredientIndex++;
            spacing = (ingredientIndex == ingredientsList.size()) ? "\n" : "\n\n";
            ingredientsText += ingredientIndex+": "+ingredients.getQuantity()+" "+ingredients.getMeasure()+" "+ingredients.getIngredient()+spacing;
        }
        ingredientsTv.setText(ingredientsText);
    }

    private void setSteps() {
        List<Steps> steps = idaoSteps.getStepsFromRecipe(recipeId);
        RecipeStepsAdapter stepsAdapter = new RecipeStepsAdapter(getContext(),this, steps);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        stepsRv.setLayoutManager(linearLayoutManager);
        stepsRv.setAdapter(stepsAdapter);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                stepsRv.getContext(),
                linearLayoutManager.getOrientation()
        );
        stepsRv.addItemDecoration(mDividerItemDecoration);
    }

    @Override
    public void thisClick(int thisStepId, int thisRecipeId) {
        Intent recipeStepIntent = new Intent(RecipeUtils.BROADCAST_STEP_CLICKED);
        recipeStepIntent.putExtra(RecipeUtils.STEP_ID, thisStepId);
        recipeStepIntent.putExtra(RecipeUtils.RECIPE_ID, thisRecipeId);
        BroadcastUtils.sendBroadcast(getContext(), recipeStepIntent);
    }
}