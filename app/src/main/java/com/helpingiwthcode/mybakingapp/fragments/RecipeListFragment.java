package com.helpingiwthcode.mybakingapp.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.adapters.RecipeAdapter;
import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

/**
 * Created by helpingwithcode on 17/12/17.
 */

public class RecipeListFragment extends Fragment implements RecipeAdapter.RecipeAdapterOnClick{
    RecyclerView recipesRv;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showList();
        }
    };
    private boolean isTabletDevice = false;

    public RecipeListFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(broadcastReceiver, new IntentFilter(RecipeUtils.BROADCAST_SHOW_RECIPES));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_recipe_list, container, false);
        recipesRv = rootView.findViewById(R.id.rv_recipes);
        isTabletDevice = getResources().getBoolean(R.bool.isTablet);
        return rootView;
    }

    private  void showList(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), (isTabletDevice)?numberOfColumns():1);
        RecipeAdapter adapter = new RecipeAdapter(this, getContext());
        recipesRv.setLayoutManager(gridLayoutManager);
        recipesRv.setHasFixedSize(true);
        recipesRv.setAdapter(adapter);
        showLoadingStatus(false);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void showLoadingStatus(boolean b) {
        recipesRv.setVisibility((b) ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void thisClick(int thisRecipeId) {
        Intent recipeIntent = new Intent(RecipeUtils.BROADCAST_RECIPE_CLICKED);
        recipeIntent.putExtra(RecipeUtils.RECIPE_ID,thisRecipeId);
        BroadcastUtils.sendBroadcast(getContext(), recipeIntent);
    }
}
