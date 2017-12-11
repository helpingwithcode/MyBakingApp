package com.helpingiwthcode.mybakingapp.realm;

import android.content.Context;

import com.helpingiwthcode.mybakingapp.model.Recipe;
import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;
import com.helpingiwthcode.mybakingapp.util.Utils;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by theCodeMaker on 10/05/2017.
 */

public class RealmMethods {

    public static int objectToInsert = 0;
    public static boolean isInited = false;

    public static Realm appRealm() {
        return Realm.getDefaultInstance();
    }

    public static void init(Context context) {
        if(!isInited) {
            isInited = true;
            Realm.init(context);
            RealmMethods.buildAppReam(context);
        }
    }

    public static void buildAppReam(Context context) {
        File file = context.getExternalFilesDir("/bakingApp/Db/");
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .directory(file)
                .name("db")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .compactOnLaunch()
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);
        Realm.compactRealm(realmConfiguration);
        Timber.e("buildAppRealm\n" +
                "RealmDefaultConfiguration:\n" + Realm.getDefaultConfiguration() + "\n" +
                "RealmDirectory: " + Realm.getDefaultConfiguration().getRealmDirectory());
    }

    public static void insertWithTransaction(final ArrayList<RealmObject> objectArray, final Context context) {
        Timber.e("insertWithTransaction(Array,Context)\nArray size: " + objectArray.size());
        Realm thisRealm = null;
        try {
            thisRealm = appRealm();

            thisRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmObject realmObject : objectArray)
                        realm.insertOrUpdate(realmObject);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    finishInsertion(context);
                }
            });
        } catch (Exception e) {
            Timber.e("Exception on insertWithTransaction(Array,Context): " + e.getLocalizedMessage());
        }
    }

    private static void finishInsertion(Context context) {
        BroadcastUtils.sendBroadcast(context, RecipeUtils.BROADCAST_DONE_INSERTING);
    }

    public static void closeRealm() {
        try {
            appRealm().close();
            Realm.compactRealm(appRealm().getConfiguration());
        } catch (Exception e) {
            Timber.e("Exception thrown on closeRealm() " + e.getLocalizedMessage());
        }
    }

    public static void deleteRealm() {
        Realm realm = null;
        try {
            realm = appRealm();
            realm.close();
            Realm.deleteRealm(Realm.getDefaultConfiguration());
        } catch (Exception e) {
            Timber.e("exception on deleteRealm: " + e.getLocalizedMessage());
        }
    }

    public static void logRecipes() {
        RealmResults<Recipe> recipesResult = appRealm().where(Recipe.class).findAll();
        for (Recipe recipe : recipesResult)
            Timber.e("Recipe\nName: " + recipe.getName() + "\nId: " + recipe.getId() + "\nImage: " + recipe.getImage() + "\nServings: " + recipe.getServings());
    }
}
