package com.example.macarus0.walkiewalkie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class WalkWithDogsDao {
    @Query("SELECT * from Dog JOIN walkwithdogs on Dog.dogId = walkWithDogs.dogId " + "WHERE walkId = :walkId")
    abstract fun getDogsOnWalk(walkId: Long): List<Dog>

    @Insert
    abstract fun insert(walkWithDogs: List<WalkWithDogs>)
}
