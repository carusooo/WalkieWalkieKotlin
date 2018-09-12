package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Dog.class, Owner.class, BaseWalk.class, WalkPhoto.class}, version = 1)
public abstract class WalkieDatabase extends RoomDatabase {
    public abstract DogDao getDogDao();

    public abstract OwnerDao getOwnerDao();

    public abstract WalkDao getWalkDao();

}
