package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WalkDao {

    @Query("select * from basewalk")
    LiveData<List<Walk>> getAllWalks();

    @Query("select * from basewalk where walkId = :walkId")
    LiveData<Walk> getWalkById(int walkId);


}
