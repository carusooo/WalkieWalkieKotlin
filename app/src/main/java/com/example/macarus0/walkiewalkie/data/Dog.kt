package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 class Dog : PhotoItem{

    @PrimaryKey(autoGenerate = true)
    var dogId: Long = 0

    override var id: Long = 0
        get() = dogId

    override var name: String = ""
    override var photoUri: String = ""
    var address: String = ""

}
