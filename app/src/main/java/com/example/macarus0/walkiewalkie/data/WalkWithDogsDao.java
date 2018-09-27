package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class WalkWithDogsDao {
    @Query("SELECT * from dog JOIN walkwithdogs on dog.dogId = walkWithDogs.dogId " +
            "WHERE walkId = :walkId")
    public abstract List<Dog> getDogsOnWalk(long walkId);

    @Insert
    public abstract void insert(List<WalkWithDogs> walkWithDogs);
}
