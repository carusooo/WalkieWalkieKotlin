package com.example.macarus0.walkiewalkie.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Owner : PhotoItem {

    @PrimaryKey(autoGenerate = true)
    var ownerId: Long = 0

    override var photoUri: String? = null

    override var id: Long = 0
        get() = ownerId

    var firstName: String = ""

    var lastName: String = ""
    var phoneNumber: String = ""

    var emailAddress: String = ""

    var address: String = ""

    override var name: String?
        get() = firstName
        set(value) {}

}
