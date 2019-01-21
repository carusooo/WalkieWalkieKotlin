package com.example.macarus0.walkiewalkie.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class WalkPhotoDao {

    @Query("select * from walkphoto where walkId = :walkId")
    abstract fun getAllWalkPhotos(walkId: Long): LiveData<List<WalkPhoto>>

    @Insert
    abstract fun addPhoto(walkPhoto: WalkPhoto)

    @Query("delete from walkphoto where walkphoto.photoId = :photoId")
    abstract fun deletePhoto(photoId: Long)
}
