package com.example.macarus0.walkiewalkie

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.persistence.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.macarus0.walkiewalkie.data.*
import org.hamcrest.CoreMatchers.hasItem
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import java.util.*


@RunWith(AndroidJUnit4::class)
class WalkieDatabaseTest {

    @Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private var mDogDao: DogDao? = null
    private var mOwnerDao: OwnerDao? = null
    private var mDb: WalkieDatabase? = null

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getTargetContext()
        mDb = Room.inMemoryDatabaseBuilder<WalkieDatabase>(context, WalkieDatabase::class.java).allowMainThreadQueries().build()
        mDogDao = mDb!!.dogDao
        mOwnerDao = mDb!!.ownerDao
    }

    @After
    fun deleteDb() {
        mDb!!.close()
    }


    private fun testDogName(name: String, dogs: List<Dog>) {
        val dogNames = ArrayList<String>()
        for (dog in dogs) {
            dogNames.add(dog.name)
        }
        assertThat(dogNames, hasItem(name))
    }

    private fun testOwnerName(name: String, owners: List<Owner>) {
        val ownerNames = ArrayList<String>()
        for (owner in owners) {
            ownerNames.add(owner.firstName)
        }
        assertThat(ownerNames, hasItem(name))
    }

    @Test
    fun addDog() {
        val dogName = "Pippin"
        val dog = Dog()
        dog.name = dogName
        mDogDao!!.insertDog(dog)
        mDogDao!!.allDogs.observeForever { dogs -> testDogName(dogName, dogs) }
    }

    @Test
    fun addOwner() {
        val ownerName = "Andy"
        val owner = Owner()
        owner.firstName = ownerName
        mOwnerDao!!.insertOwner(owner)
        mOwnerDao!!.allOwners.observeForever { owners -> testOwnerName(ownerName, owners) }
    }

}

