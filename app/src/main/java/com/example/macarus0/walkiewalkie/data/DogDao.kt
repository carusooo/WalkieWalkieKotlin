package com.example.macarus0.walkiewalkie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DogDao {

    @Query("Select * from dog")
    fun getAllDogs(): LiveData<List<Dog>>

    @Query("Select * from dog where dogId = :dogId")
    fun getDogById(dogId: Long): LiveData<Dog>

    @Query("Select * from dog JOIN dogowner on dogOwner.dogId = dog.dogId WHERE dogOwner.ownerId = :ownerId ")
    fun getDogsbyOwner(ownerId: Long): LiveData<List<Dog>>

    @Query("Select * from dog WHERE dog.dogId NOT IN (SELECT dogId from Dogowner WHERE ownerId = :ownerId)")
    fun getAvailableDogs(ownerId: Long): LiveData<List<Dog>>

    @Query("Select * from dog where dogId = :dogId")
    fun getDogByIdSync(dogId: Long): Dog

    @Query("Select * from dog join walkwithdogs on dog.dogId = walkWithDogs.dogId where walkWithDogs.walkId = :walkId")
    fun getDogsOnWalk(walkId: Long): List<Dog>

    @Insert
    fun insertDog(vararg dogs: Dog): LongArray

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDog(vararg dogs: Dog)


}
