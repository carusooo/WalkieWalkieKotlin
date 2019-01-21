package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WalkLocation {
    @PrimaryKey(autoGenerate = true)
    var locationId: Long = 0

    var walkId: Long = 0
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    var timestamp: Long = 0

    var accuracy: Double = 0.toDouble()

    override fun toString(): String {
        return String.format("%s,%s", latitude, longitude)
    }
}
