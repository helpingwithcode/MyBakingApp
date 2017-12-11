package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.adapters.RecipeAdapter;
import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;
import com.helpingiwthcode.mybakingapp.util.Utils;
import com.helpingiwthcode.mybakingapp.util.VolleyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeAdapterOnClick{

    @BindView(R.id.rv_recipes)
    RecyclerView recipesRv;
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Timber.e(action);
            if (action.equals(RecipeUtils.BROADCAST_PERMISSIONS_DENIED))
                Utils.checkPermissions(MainActivity.this, context);
            else if (action.equals(RecipeUtils.BROADCAST_PERMISSIONS_GRANTED))
                getRecipes();
            else if(action.equals(RecipeUtils.BROADCAST_DONE_INSERTING))
                populateGridView();
        }
    };

    private void populateGridView() {
        Timber.e("Time TO POPULATE MOTHERFUCKERRRRRRRRRRRRRR");
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), numberOfColumns());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        RecipeAdapter adapter = new RecipeAdapter(this, RealmMethods.appRealm().where(Recipe.class).findAll());

        recipesRv.setLayoutManager(linearLayoutManager);
        recipesRv.setHasFixedSize(true);
        recipesRv.setAdapter(adapter);
        showLoadingStatus(false);
    }

    private void getRecipes() {
        showLoadingStatus(true);
        VolleyUtils.getRecipes(getApplicationContext());
    }

    private void showLoadingStatus(boolean b) {
        progressBar.setVisibility((!b) ? View.INVISIBLE : View.VISIBLE);
        recipesRv.setVisibility((b) ? View.INVISIBLE : View.VISIBLE);
    }

    private boolean firstPermissionCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        RealmMethods.init(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, getMainIntentFilters());
        if (firstPermissionCheck) {
            firstPermissionCheck = false;
            Utils.checkPermissions(MainActivity.this, getApplicationContext());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private IntentFilter getMainIntentFilters() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecipeUtils.BROADCAST_PERMISSIONS_DENIED);
        intentFilter.addAction(RecipeUtils.BROADCAST_PERMISSIONS_GRANTED);
        intentFilter.addAction(RecipeUtils.BROADCAST_DONE_INSERTING);
        return intentFilter;
    }

    private void setGridView() {

        Timber.e("Permissions granted");

    }

    @Override
    public void thisClick(int thisRecipeId) {
        Intent recipeDetailIntent = new Intent(this,RecipeDetailActivity.class);
        recipeDetailIntent.putExtra("recipeId",thisRecipeId);
        startActivity(recipeDetailIntent);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }
}
