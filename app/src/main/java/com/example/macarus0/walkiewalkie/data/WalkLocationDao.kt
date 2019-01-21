package com.example.macarus0.walkiewalkie.data


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WalkLocationDao {

    @Query("select * from walklocation where walkId = :walkId order by locationId ASC")
    fun getLiveWalkLocations(walkId: Long): LiveData<List<WalkLocation>>

    @Insert
    fun addWalkLocations(walkLocations: List<WalkLocation>)

}
