package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Walk {

    @PrimaryKey(autoGenerate = true)
    var walkId: Long = 0

    var walkDate: String = ""
    var walkDistance: Float = 0.toFloat()
    var walkDuration: String = ""
    var walkPathUrl: String = ""
    var walkStartTime: Long = 0
    var walkEndTime: Long = 0
    var walkDogsCount: Int = 0
    var isDistanceTracked: Boolean = false
    @Ignore
    var dogs: List<Dog> = emptyList()

    fun setWalkPathLink(walkPathLink: String) {
        this.walkPathUrl = walkPathLink
    }

}
