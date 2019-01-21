package com.example.macarus0.walkiewalkie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Dog::class, Owner::class, DogOwner::class, Walk::class, WalkWithDogs::class, WalkPhoto::class, WalkLocation::class], version = 1)
abstract class WalkieDatabase : RoomDatabase() {
    abstract val dogDao: DogDao

    abstract val ownerDao: OwnerDao

    abstract val dogOwnerDao: DogOwnerDao

    abstract val walkDao: WalkDao

    abstract val walkWithDogsDao: WalkWithDogsDao

    abstract val walkLocationDao: WalkLocationDao

    abstract val walkPhotoDao: WalkPhotoDao

    companion object {
        @Volatile
        private var INSTANCE: WalkieDatabase? = null

        fun getDb(applicationContext: Context): WalkieDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        applicationContext,
                        WalkieDatabase::class.java,
                        "WalkieDatabase"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}
