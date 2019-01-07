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

    @Query("Select * from owner where ownerId = :ownerId")
    LiveData<Owner> getOwnerById(long ownerId);

    @Query("Select * from owner JOIN DogOwner on Dogowner.Ownerid = Owner.ownerId WHERE Dogowner.dogId = :dogId ")
    LiveData<List<Owner>> getOwnersbyDog(long dogId);

    @Query("Select * from owner WHERE owner.ownerId NOT IN (SELECT ownerId from Dogowner WHERE dogId = :dogId)")
    LiveData<List<Owner>> getAvailableOwners(long dogId);

    @Query("Select * from owner where ownerId = :ownerId")
    Owner getOwnerByIdSync(long ownerId);

    @Query("Select * from owner join DogOwner on walkwithdogs.dogId = DogOwner.dogId join walkwithdogs on walkwithdogs.dogId = " +
            "DogOwner.dogId where walkwithdogs.walkId = :walkId")
    LiveData<List<Owner>> getDogOwnersOnWalk(long walkId);

    @Insert
    long[] insertOwner(Owner...owners);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOwner(Owner...owners);
}

