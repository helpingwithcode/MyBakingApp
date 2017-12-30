package com.helpingiwthcode.mybakingapp.realm;

import android.content.Context;
import android.os.Environment;

import com.helpingiwthcode.mybakingapp.util.BroadcastUtils;
import com.helpingiwthcode.mybakingapp.util.RecipeUtils;

import java.io.File;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import timber.log.Timber;

/**
 * Created by theCodeMaker on 10/05/2017.
 */

public class RealmMethods {

    public static int objectToInsert = 0;
    public static boolean isInited = false;

    public static Realm realm() {
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
                //.directory(file)
                .directory(new File(getRealmPath(context)))
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


    public static String getRealmPath(Context context) {

        String externalStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String packageName = context.getApplicationContext().getPackageName();
        return externalStorageDir + File.separator + "Android" + File.separator + "data" + File.separator + packageName + File.separator + "realm" + File.separator;
    }

    public static void insertWithTransaction(final ArrayList<RealmObject> objectArray, final Context context) {
        Timber.e("insertWithTransaction(Array,Context)\nArray size: " + objectArray.size());
        Realm thisRealm = null;
        try {
            thisRealm = realm();
            thisRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (RealmObject realmObject : objectArray)
                        realm.copyToRealmOrUpdate(realmObject);
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
        finally {
            closeInstance(thisRealm);
        }
    }

    private static void finishInsertion(Context context) {
        BroadcastUtils.sendBroadcast(context, RecipeUtils.BROADCAST_DONE_INSERTING);
    }

    public static void closeRealm() {
        try {
            realm().close();
            Realm.compactRealm(realm().getConfiguration());
        } catch (Exception e) {
            Timber.e("Exception thrown on closeRealm() " + e.getLocalizedMessage());
        }
    }

    public static void deleteRealm() {
        Realm realm = null;
        try {
            realm = realm();
            realm.close();
            Realm.deleteRealm(Realm.getDefaultConfiguration());
        } catch (Exception e) {
            Timber.e("exception on deleteRealm: " + e.getLocalizedMessage());
        }
    }

    public static void closeInstance(Realm realm) {
        try {
            if (!realm.isClosed())
                realm.close();
        }
        catch (Exception e){
            Timber.e("Exception on closeInstance: "+e.getLocalizedMessage());
        }
    }
}
