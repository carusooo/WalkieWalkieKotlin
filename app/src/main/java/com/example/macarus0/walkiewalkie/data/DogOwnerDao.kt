package com.example.macarus0.walkiewalkie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
abstract class DogOwnerDao {
    @Insert
    abstract fun insert(vararg dogOwners: DogOwner)

    @Query("DELETE FROM DogOwner WHERE DogOwner.dogId = :dogId AND DogOwner.ownerId = :ownerId")
    abstract fun delete(dogId: Long, ownerId: Long)

}
