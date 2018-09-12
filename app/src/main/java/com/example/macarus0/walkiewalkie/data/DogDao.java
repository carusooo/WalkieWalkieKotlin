package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DogDao {

    @Query("Select * from dog")
    LiveData<List<Dog>> getAllDogs();

    @Query("Select * from dog where ownerId1 = :ownerId OR ownerId2 = :ownerId")
    LiveData<List<Dog>> getDogsByOwner(int ownerId);

    @Insert
    void insertDog(Dog dog);

}
