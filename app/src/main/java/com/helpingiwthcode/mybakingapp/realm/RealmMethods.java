package com.helpingiwthcode.mybakingapp.realm;

import android.content.Context;

import com.helpingiwthcode.mybakingapp.model.Recipe;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import timber.log.Timber;

/**
 * Created by theCodeMaker on 10/05/2017.
 */

public class RealmMethods {

    public static Realm appRealm() {
        return Realm.getDefaultInstance();
    }

    public static void init(Context context) {
            Realm.init(context);
            RealmMethods.buildAppReam(context);
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

    public static void insertWithTransaction(final RealmObject object) {
        Timber.e("insertWithTransaction: " + object.getClass());
        Realm thisRealm = null;
        try {
            thisRealm = appRealm();
            thisRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(object);
                }
            });
        } finally {
            if (thisRealm != null) {
                thisRealm.close();
            }
        }
    }

    public static void closeRealm() {
        try {
            appRealm().close();
            Realm.compactRealm(appRealm().getConfiguration());
        } catch (Exception e) {
            Timber.e("Exception thrown on closeRealm() " + e.getLocalizedMessage());
        }
    }

    public static void deleteRealm(){
        Realm realm = null;
        try{
            realm = appRealm();
            realm.close();
            Realm.deleteRealm(Realm.getDefaultConfiguration());
        }
        catch (Exception e){
            Timber.e("exception on deleteRealm: "+e.getLocalizedMessage());
        }
    }

    public static void logRecipes() {
        RealmResults<Recipe> recipesResult = appRealm().where(Recipe.class).findAll();
        for(Recipe recipe : recipesResult)
            Timber.e("Recipe\nName: "+recipe.getName()+"\nId: "+recipe.getId()+"\nImage: "+recipe.getImage()+"\nServings: "+recipe.getServings());
    }
}
