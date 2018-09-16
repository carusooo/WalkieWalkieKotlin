package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface OwnerDao {

    @Query("Select * from owner")
    LiveData<List<Owner>> getAllOwners();

    @Query("Select * from owner where dogId1 = :dogId OR dogId2= :dogId")
    LiveData<List<Owner>> getOwnersbyDog(int dogId);

    @Query("Select * from owner where ownerId = :ownerId")
    LiveData<Owner> getOwnerById(long ownerId);

    @Insert
    long[] insertOwner(Owner...owners);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOwner(Owner...owners);
}

