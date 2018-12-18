package com.example.macarus0.walkiewalkie.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public abstract class WalkPhotoDao {

    @Query("select * from walkphoto where walkId = :walkId")
    public abstract LiveData<List<WalkPhoto>> getAllWalkPhotos(long walkId);

    @Insert
    public abstract void addPhoto(WalkPhoto walkPhoto);

    @Query("delete from walkphoto where walkphoto.photoId = :photoId")
    public abstract void deletePhoto(long photoId);
}
