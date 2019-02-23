package com.example.macarus0.walkiewalkie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface  WalkWithDogsDao {
    @Query("SELECT * from Dog JOIN walkwithdogs on Dog.dogId = walkWithDogs.dogId " + "WHERE walkId = :walkId")
    fun getDogsOnWalk(walkId: Long): List<Dog>

    @Insert
    fun insert(walkWithDogs: List<WalkWithDogs>)

    @Query("DELETE FROM walkwithdogs WHERE walkwithdogs.dogId = :dogId")
    fun deleteDog(dogId: Long)
}
