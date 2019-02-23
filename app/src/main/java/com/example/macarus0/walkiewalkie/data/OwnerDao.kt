package com.example.macarus0.walkiewalkie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface OwnerDao {

    @get:Query("Select * from owner")
    val allOwners: LiveData<List<Owner>>

    @Query("Select * from owner where ownerId = :ownerId")
    fun getOwnerById(ownerId: Long): LiveData<Owner>

    @Query("Select * from owner JOIN DogOwner on Dogowner.Ownerid = Owner.ownerId WHERE Dogowner.dogId = :dogId ")
    fun getOwnersbyDog(dogId: Long): LiveData<List<Owner>>

    @Query("Select * from owner WHERE owner.ownerId NOT IN (SELECT ownerId from Dogowner WHERE dogId = :dogId)")
    fun getAvailableOwners(dogId: Long): LiveData<List<Owner>>

    @Query("Select * from owner where ownerId = :ownerId")
    fun getOwnerByIdSync(ownerId: Long): Owner

    @Query("Select * from owner join DogOwner on walkwithdogs.dogId = DogOwner.dogId join walkwithdogs on walkwithdogs.dogId = " + "DogOwner.dogId where walkwithdogs.walkId = :walkId")
    fun getDogOwnersOnWalk(walkId: Long): LiveData<List<Owner>>

    @Insert
    fun insertOwner(vararg owners: Owner): LongArray

    @Query("DELETE FROM owner WHERE owner.ownerId = :ownerId")
    fun deleteOwner(ownerId: Long)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateOwner(vararg owners: Owner)
}

