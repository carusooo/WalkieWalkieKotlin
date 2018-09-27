package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface WalkDao {

    @Query("select * from walk")
    LiveData<List<Walk>> getAllWalks();

    @Query("select * from walk where walkId = :walkId")
    Walk getWalkById(long walkId);

    @Insert
    long[] insertWalk(Walk... walks);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateWalk(Walk... walks);

}
