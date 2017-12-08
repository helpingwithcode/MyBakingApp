package com.helpingiwthcode.mybakingapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.helpingiwthcode.mybakingapp.R;
import com.helpingiwthcode.mybakingapp.realm.RealmMethods;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;
import com.helpingiwthcode.mybakingapp.util.Utils;
import com.helpingiwthcode.mybakingapp.util.VolleyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.button)
    Button button;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.e(intent.getAction());
            if (intent.getAction().equals(RecipeUtils.BROADCAST_PERMISSIONS_DENIED))
                Utils.checkPermissions(MainActivity.this, context);
            else if (intent.getAction().equals(RecipeUtils.BROADCAST_PERMISSIONS_GRANTED))
                runAsyncTask();
        }
    };
    private boolean firstPermissionCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Timber.plant(new Timber.DebugTree());
        ButterKnife.bind(this);
        RealmMethods.init(getApplicationContext());
        VolleyUtils.getRecipes(getApplicationContext());
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

    @OnClick({R.id.button})
    public void clickButton(View v){
        RealmMethods.logRecipes();
    }

    private IntentFilter getMainIntentFilters() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(RecipeUtils.BROADCAST_PERMISSIONS_DENIED);
        intentFilter.addAction(RecipeUtils.BROADCAST_PERMISSIONS_GRANTED);
        return intentFilter;
    }

    private void runAsyncTask() {
        Timber.e("Permissions granted");
    }
}
