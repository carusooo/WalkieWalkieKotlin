package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class WalkPhoto : PhotoItem {

    @PrimaryKey(autoGenerate = true)
    var photoId: Long = 0

    override var id: Long = 0
        get() = photoId

    override var photoUri: String = ""
    override var name: String = ""
    var walkId: Long = 0
}
