package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Room;
import android.content.Context;


public class WalkieDatabaseProvider {

    private static WalkieDatabase sDb;

    public static WalkieDatabase getDatabase(Context context) {
        if(sDb == null) {
            sDb = createDatabase(context);
        }
        return sDb;
    }

    private static WalkieDatabase createDatabase(Context context) {
        WalkieDatabase walkieDatabase = Room.inMemoryDatabaseBuilder(context.getApplicationContext(), WalkieDatabase.class).build();
        return walkieDatabase;
    }

}
