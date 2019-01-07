package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class DogOwnerDao {
    @Insert
    public abstract void insert(DogOwner... dogOwners);

    @Query("DELETE FROM DogOwner WHERE DogOwner.dogId = :dogId AND DogOwner.ownerId = :ownerId")
    public abstract void delete(long dogId, long ownerId);

}
