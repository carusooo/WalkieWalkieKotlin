package com.example.macarus0.walkiewalkie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WalkDao {

    @get:Query("select * from walk")
    val allWalks: LiveData<List<Walk>>

    @Query("select * from walk where walkId = :walkId")
    fun getWalkById(walkId: Long): Walk

    @Insert
    fun insertWalk(vararg walks: Walk): LongArray

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateWalk(vararg walks: Walk)

}
