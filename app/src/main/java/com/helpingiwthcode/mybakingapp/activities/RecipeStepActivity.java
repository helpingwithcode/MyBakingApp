package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.dao.DAORecipe;
import com.helpingiwthcode.mybakingapp.fragments.RecipeStepFragment;
import com.helpingiwthcode.mybakingapp.idao.IDAORecipes;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.BROADCAST_STEP_CLICKED;

public class RecipeStepActivity extends AppCompatActivity {
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BROADCAST_STEP_CLICKED))
                showStep(intent.getExtras());
        }
    };

    private void showStep(Bundle extras) {
        Intent stepIntent = new Intent(this, RecipeStepActivity.class);
        stepIntent.putExtra(RecipeUtils.RECIPE_ID, extras.getInt(RecipeUtils.RECIPE_ID, 0));
        stepIntent.putExtra(RecipeUtils.STEP_ID, extras.getInt(RecipeUtils.STEP_ID, 0));
        startActivity(stepIntent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);
        RealmMethods.init(getApplicationContext());
        setTitle(String.format(getString(R.string.recipe_step_title), getRecipeName()));
        populateFragment(getIntent().getExtras());
    }

    private void populateFragment(Bundle extras) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();
        recipeStepFragment.setArguments(extras);
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_step, recipeStepFragment)
                .commit();
    }

    private String getRecipeName() {
        IDAORecipes idaoRecipes = new DAORecipe();
        return idaoRecipes.getRecipeName(getIntent().getExtras().getInt(RecipeUtils.RECIPE_ID,0));
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(BROADCAST_STEP_CLICKED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
}
