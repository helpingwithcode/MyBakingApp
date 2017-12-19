package com.helpingiwthcode.mybakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.activities.RecipeActivity;
import com.helpingiwthcode.mybakingapp.activities.RecipeDetailActivity;
import com.helpingiwthcode.mybakingapp.adapters.RecipeAdapter;
import com.helpingiwthcode.mybakingapp.util.Preferences;

/**
 * Created by helpingwithcode on 17/12/17.
 */

public class RecipeListFragment extends Fragment implements RecipeAdapter.RecipeAdapterOnClick{
    RecyclerView recipesRv;
    Preferences preferences;
    //ProgressBar progressBar;
    // Mandatory empty constructor

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showList();
        }
    };

    public RecipeListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver, new IntentFilter("ShowRecipes"));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        //progressBar = (ProgressBar) rootView.findViewById(R.id.pb_loading);
        preferences = new Preferences(getContext());
        recipesRv = (RecyclerView) rootView.findViewById(R.id.rv_recipes);
        return rootView;
    }

    private  void showList(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecipeAdapter adapter = new RecipeAdapter(this, getContext());
        recipesRv.setLayoutManager(linearLayoutManager);
        recipesRv.setHasFixedSize(true);
        recipesRv.setAdapter(adapter);
        showLoadingStatus(false);
    }

    private void showLoadingStatus(boolean b) {
        //progressBar.setVisibility((!b) ? View.INVISIBLE : View.VISIBLE);
        recipesRv.setVisibility((b) ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void thisClick(int thisRecipeId) {
        preferences.addInt("recipeId",thisRecipeId);
        //startActivity(new Intent(getContext(), RecipeActivity.class));
        Intent recipeDetails = new Intent(getContext(), RecipeDetailActivity.class);
        recipeDetails.putExtra("recipeId", thisRecipeId);
        startActivity(recipeDetails);
    }
}
