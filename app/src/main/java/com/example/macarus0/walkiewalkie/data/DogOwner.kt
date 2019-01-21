package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DogOwner(var dogId: Long, var ownerId: Long) {

    @PrimaryKey(autoGenerate = true)
    var dogOwnerId : Long = 0

}
