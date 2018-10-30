package com.example.macarus0.walkiewalkie.data;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WalkLocationDao {

    @Query("select * from walklocation where walkId = :walkId order by locationId ASC")
    List<WalkLocation> getWalkLocations(long walkId);

    @Query("select * from walklocation where walkId = :walkId order by locationId ASC")
    LiveData<List<WalkLocation>> getLiveWalkLocations(long walkId);

    @Insert
    void addWalkLocations(List<WalkLocation> walkLocations);

}
