package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.dao.DAORecipe;
import com.helpingiwthcode.mybakingapp.fragments.RecipeDetailFragment;
import com.helpingiwthcode.mybakingapp.fragments.RecipeStepFragment;
import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.AppPreferences;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import timber.log.Timber;

import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_STEP_CLICKED;
import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.RECIPE_ID;

public class RecipeDetailActivity extends AppCompatActivity {
    int recipeId, restoredRecipeId;
    private Menu menu;
    private boolean mTwoPane = false;
    AppPreferences preferences;
    private boolean isWidget;
    private Toast mToast;
    IDAORecipes idaoRecipes = new DAORecipe();
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BROADCAST_STEP_CLICKED))
                showRecipeSteps(intent.getExtras());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        RealmMethods.init(getApplicationContext());
        preferences = new AppPreferences(getApplicationContext());
        checkSavedInstance(savedInstanceState);
        checkTwoPaneStatus();
        setFragment();
        setActivityTitle();
    }

    private void checkSavedInstance(Bundle savedInstanceState) {
        restoredRecipeId = preferences.rInt(RecipeUtils.RECIPE_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        Timber.e("onSaveInstanceState");
        savedState.putInt(RecipeUtils.RECIPE_ID, recipeId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        boolean hasSavedInstance = savedInstanceState != null;
        Timber.e("onRestoreInstanceState\nhasSavedInstance: "+hasSavedInstance);
    }

    private void setActivityTitle() {
        Timber.e("setActivityTitle");
        Bundle recipeBundle = getIntent().getExtras();
        recipeId = (recipeBundle!=null) ? recipeBundle.getInt(RecipeUtils.RECIPE_ID, 0) : restoredRecipeId;
        IDAORecipes idaoRecipes = new DAORecipe();
        String recipeName = idaoRecipes.getRecipeName(recipeId);
        setTitle(String.format(getString(R.string.recipe_detail_title), recipeName));
    }

    private void checkTwoPaneStatus() {
        mTwoPane = (findViewById(R.id.ll_recipe_detail) != null);
    }

    private void showRecipeSteps(Bundle extras) {
        int thisRecipeId = extras.getInt(RecipeUtils.RECIPE_ID, 0);
        int thisStepId = extras.getInt(RecipeUtils.STEP_ID, 0);
        if (!mTwoPane) {
            Intent recipeDetails = new Intent(this, RecipeStepActivity.class);
            recipeDetails.putExtra(RecipeUtils.RECIPE_ID, thisRecipeId);
            recipeDetails.putExtra(RecipeUtils.STEP_ID, thisStepId);
            startActivity(recipeDetails);
        } else {
            populateRecipeStep(extras);
        }
    }

    private void setFragment() {
        Bundle recipeBundle = getIntent().getExtras();
        if (recipeBundle == null) {
            recipeBundle = new Bundle();
            recipeBundle.putInt(RECIPE_ID, restoredRecipeId);
        }
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        fragment.setArguments(recipeBundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_recipe_master, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.widget_status_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Timber.e("onPrepareOptionsMenu");
        setWidgetMenuStatus(recipeId);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setWidgetMenuStatus(int recipeId) {
        Timber.e("setWidgetMenuStatus");
        isWidget = idaoRecipes.isRecipeOnWidget(recipeId);
        MenuItem favItem = menu.findItem(R.id.item_widget_status);
        favItem.setIcon((isWidget) ? R.mipmap.ic_favorite_white_24dp : R.mipmap.ic_favorite_border_white_24dp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_widget_status)
            changeWidgetStatus();
        if (item.getItemId() == android.R.id.home)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        return super.onOptionsItemSelected(item);
    }

    private void changeWidgetStatus() {
        Timber.e("changeWidgetStatus");
        isWidget = idaoRecipes.isRecipeOnWidget(recipeId);
        if(!isWidget)
            idaoRecipes.setAsWidget(recipeId);
        else
            idaoRecipes.removeAsWidget(recipeId);
        RecipeUtils.updateWidget(getApplicationContext());
        showToastMessage();
    }

    private void showToastMessage() {
        setWidgetMenuStatus(recipeId);
        String toastMessage = getString((idaoRecipes.isRecipeOnWidget(recipeId)) ? R.string.recipe_added_widget : R.string.recipe_remove_widget);
        if (mToast != null)
            mToast.cancel();
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void populateRecipeStep(Bundle thisRecipeBundle) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setArguments(thisRecipeBundle);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_recipe_detail, recipeStepFragment)
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_STEP_CLICKED));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.putInt(RecipeUtils.RECIPE_ID, recipeId);
        Timber.e("OnDestroy");
    }
}
