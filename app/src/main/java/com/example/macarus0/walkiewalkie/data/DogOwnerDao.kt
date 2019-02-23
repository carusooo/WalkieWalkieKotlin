package com.example.macarus0.walkiewalkie.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogOwnerDao {
    @Insert
    fun insert(vararg dogOwners: DogOwner)

    @Query("DELETE FROM DogOwner WHERE DogOwner.dogId = :dogId AND DogOwner.ownerId = :ownerId")
    fun delete(dogId: Long, ownerId: Long)

    @Query("DELETE FROM DogOwner WHERE DogOwner.dogId = :dogId")
    fun deleteDog(dogId: Long)

    @Query("DELETE FROM DogOwner WHERE DogOwner.ownerId = :ownerId")
    fun deleteOwner(ownerId: Long)

}
