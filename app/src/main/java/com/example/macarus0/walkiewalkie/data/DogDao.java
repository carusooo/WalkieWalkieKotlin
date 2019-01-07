package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DogDao {

    @Query("Select * from dog")
    LiveData<List<Dog>> getAllDogs();

    @Query("Select * from dog where dogId = :dogId")
    LiveData<Dog> getDogById(long dogId);

    @Query("Select * from dog JOIN dogowner on dogOwner.dogId = dog.dogId WHERE dogOwner.ownerId = :ownerId ")
    LiveData<List<Dog>> getDogsbyOwner(long ownerId);

    @Query("Select * from dog WHERE dog.dogId NOT IN (SELECT dogId from Dogowner WHERE ownerId = :ownerId)")
    LiveData<List<Dog>> getAvailableDogs(long ownerId);

    @Query("Select * from dog where dogId = :dogId")
    Dog getDogByIdSync(long dogId);

    @Query("Select * from dog join walkwithdogs on dog.dogId = walkWithDogs.dogId where walkWithDogs.walkId = :walkId")
    List<Dog> getDogsOnWalk(long walkId);

    @Insert
    long[] insertDog(Dog...dogs);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDog(Dog...dogs);


}
