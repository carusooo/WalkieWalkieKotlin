package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Dog.class, Owner.class, DogOwner.class, Walk.class, WalkWithDogs.class, WalkPhoto.class, WalkLocation.class}, version = 1)
public abstract class WalkieDatabase extends RoomDatabase {
    public abstract DogDao getDogDao();

    public abstract OwnerDao getOwnerDao();

    public abstract DogOwnerDao getDogOwnerDao();

    public abstract WalkDao getWalkDao();

    public abstract WalkWithDogsDao getWalkWithDogsDao();

    public abstract WalkLocationDao getWalkLocationDao();

    public abstract WalkPhotoDao getWalkPhotoDao();

}
