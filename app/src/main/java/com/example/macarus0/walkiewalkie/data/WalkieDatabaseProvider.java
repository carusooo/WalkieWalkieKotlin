package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Room;
import android.content.Context;


public class WalkieDatabaseProvider {

    private static final String dbName = "WalkieDatabase";

    private static WalkieDatabase sDb;

    public static WalkieDatabase getDatabase(Context context) {
        if(sDb == null) {
            sDb = createDatabase(context);
        }
        return sDb;
    }

    private static WalkieDatabase createDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), WalkieDatabase.class, dbName).build();
    }

}
