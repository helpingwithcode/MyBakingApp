package com.helpingiwthcode.mybakingapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.model.Ingredients;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmResults;
import io.realm.Sort;
import timber.log.Timber;

/**
 * Created by root on 12/12/17.
 */

public class IngredientsFragment extends Fragment {
    @BindView(R.id.tv_ingredients)
    TextView ingredientsTv;
    Preferences preferences;
    int recipeId;
    public IngredientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.e("OnCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        Timber.e("OnCreateView");
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.e("OnStart");
        preferences = new Preferences(getActivity());
        recipeId = preferences.rInt("recipeId");
        setIngredients();
    }

    private void setIngredients() {
        RealmResults<Ingredients> ingredientsFromThisRecipe = RealmMethods.realm()
                .where(Ingredients.class)
                .equalTo("recipeId", recipeId)
                .findAllSorted("order", Sort.ASCENDING);

        String ingredientsText = "";
        Timber.e("ingredientsFromThisRecipe: "+ingredientsFromThisRecipe.toString());
        int ingredientIndex = 0;
        for(Ingredients ingredients : ingredientsFromThisRecipe){
            ingredientIndex++;
            ingredientsText += ingredientIndex+": "+ingredients.getQuantity()+" "+ingredients.getMeasure()+" "+ingredients.getIngredient()+"\n";
        }
        ingredientsTv.setText(ingredientsText);
    }
}
