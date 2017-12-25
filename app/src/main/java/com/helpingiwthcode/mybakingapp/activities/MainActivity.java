package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.adapters.RecipeAdapter;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.Preferences;
import com.helpingiwthcode.mybakingapp.util.Utils;
import com.helpingiwthcode.mybakingapp.util.VolleyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.helpingiwthcode.mybakingapp.util.RecipeUtils.*;

public class MainActivity extends AppCompatActivity {
//    implements
//} RecipeAdapter.RecipeAdapterOnClick{

    @BindView(R.id.pb_loading)
    ProgressBar progressBar;
    Preferences preferences;
    boolean firstPermissionCheck = true;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BROADCAST_PERMISSIONS_DENIED))
                Utils.checkPermissions(MainActivity.this, context);
            else if (action.equals(BROADCAST_PERMISSIONS_GRANTED))
                getRecipes();
            else if(action.equals(BROADCAST_DONE_INSERTING))
                inflateFragment();
                //populateGridView();
        }
    };

    private void inflateFragment() {
        getApplicationContext().sendBroadcast(new Intent("ShowRecipes"));
        showLoadingStatus(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        RealmMethods.init(getApplicationContext());
        preferences = new Preferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, Utils.getMainIntentFilters());
        if (firstPermissionCheck) {
            firstPermissionCheck = false;
            Utils.checkPermissions(MainActivity.this, getApplicationContext());
        }
        else
            getRecipes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

//    @Override
//    public void thisClick(int thisRecipeId) {
//        preferences.addInt("recipeId",thisRecipeId);
//        startActivity(new Intent(this, RecipeActivity.class));
//    }

//    private void populateGridView() {
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        RecipeAdapter adapter = new RecipeAdapter(this, getApplicationContext());
//        recipesRv.setLayoutManager(linearLayoutManager);
//        recipesRv.setHasFixedSize(true);
//        recipesRv.setAdapter(adapter);
//        showLoadingStatus(false);
//    }

    private void getRecipes() {
        showLoadingStatus(true);
        VolleyUtils.getRecipes(getApplicationContext());
    }

    private void showLoadingStatus(boolean b) {
        progressBar.setVisibility((!b) ? View.INVISIBLE : View.VISIBLE);
       // recipesRv.setVisibility((b) ? View.INVISIBLE : View.VISIBLE);
    }
}
