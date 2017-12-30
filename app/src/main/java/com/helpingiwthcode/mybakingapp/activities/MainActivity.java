package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.dao.DAORecipe;
import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;
import com.helpingiwthcode.mybakingapp.util.SimpleIdlingResource;
import com.helpingiwthcode.mybakingapp.util.Utils;
import com.helpingiwthcode.mybakingapp.util.VolleyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.*;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    IDAORecipes idaoRecipes = new DAORecipe();
    boolean firstPermissionCheck = true;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BROADCAST_PERMISSIONS_DENIED))
                Utils.checkPermissions(MainActivity.this, context);
            else if (action.equals(BROADCAST_PERMISSIONS_GRANTED))
                getRecipes();
            else if (action.equals(BROADCAST_DONE_INSERTING))
                inflateFragment();
            else if (action.equals(BROADCAST_RECIPE_CLICKED))
                showRecipeDetails(intent.getExtras());
        }
    };

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Nullable
    @VisibleForTesting
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        RealmMethods.init(getApplicationContext());
    }

    private void showRecipeDetails(Bundle extras) {
        int thisRecipeId = extras.getInt(RecipeUtils.RECIPE_ID, 0);
        Intent recipeDetails = new Intent(this, RecipeDetailActivity.class);
        recipeDetails.putExtra(RecipeUtils.RECIPE_ID, thisRecipeId);
        startActivity(recipeDetails);
        finish();
    }

    private void inflateFragment() {
        BroadcastUtils.sendBroadcast(getApplicationContext(),RecipeUtils.BROADCAST_SHOW_RECIPES);
        showLoadingStatus(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.getMainIntentFilters());
        if (firstPermissionCheck) {
            firstPermissionCheck = false;
            Utils.checkPermissions(MainActivity.this, getApplicationContext());
        } else
            getRecipes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void getRecipes() {
        showLoadingStatus(true);
        if(idaoRecipes.getRecipes().size() == 0)
            VolleyUtils.getRecipes(getApplicationContext());
        else
            inflateFragment();
    }

    private void showLoadingStatus(boolean b) {
        progressBar.setVisibility((!b) ? View.INVISIBLE : View.VISIBLE);
    }
}
