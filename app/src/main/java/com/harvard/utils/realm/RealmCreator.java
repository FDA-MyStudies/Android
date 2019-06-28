package com.harvard.utils.realm;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * Created by yanis on 7/5/16.
 */
public class RealmCreator {

    private final static Logger LOGGER = Logger.getLogger(RealmCreator.class);


    public static synchronized Realm getRealmInstance() {
        return Realm.getInstance(getRealmConfig());
    }

    public static synchronized RealmConfiguration getRealmConfig() {
        return new RealmConfiguration.Builder()
                .name("default.realm")
                .modules(new TestModule())
                .schemaVersion(1)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                    }
                })
                .build();
    }

}
