package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface OwnerDao {

    @Query("Select * from owner")
    LiveData<List<Owner>> getAllOwners();

    @Query("Select * from owner where dogId1 = :dogId OR dogId2= :dogId")
    LiveData<List<Owner>> getOwnersbyDog(int dogId);

    @Insert()
    void insertOwner(Owner owner);

}
