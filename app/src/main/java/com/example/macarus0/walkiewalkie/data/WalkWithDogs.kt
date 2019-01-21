package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class WalkWithDogs {
    @PrimaryKey(autoGenerate = true)
    var walkWithDogsId : Long = 0

    var walkId: Long = 0
    var dogId: Long = 0
}
