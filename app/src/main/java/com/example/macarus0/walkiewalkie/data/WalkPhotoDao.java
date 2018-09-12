package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class WalkPhotoDao {

    @Query("select * from walkphoto where walkId = :walkId")
    public abstract LiveData<List<WalkPhoto>> getAllWalkPhotos(int walkId);

}
