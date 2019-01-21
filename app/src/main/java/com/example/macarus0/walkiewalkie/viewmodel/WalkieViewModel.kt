package com.example.macarus0.walkiewalkie.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Log

import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.DogOwner
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.data.WalkLocation
import com.example.macarus0.walkiewalkie.data.WalkPhoto
import com.example.macarus0.walkiewalkie.data.WalkWithDogs
import com.example.macarus0.walkiewalkie.data.WalkieDatabase

import java.util.ArrayList

class WalkieViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "WalkieViewModel"
    private val db = WalkieDatabase.getDb(application.applicationContext)

    // Methods used in overall views
    fun getAllDogs(): LiveData<List<Dog>> {
        return db.dogDao.getAllDogs()
    }

    val allOwners: LiveData<List<Owner>>
        get() = db.ownerDao.allOwners

    val allWalks: LiveData<List<Walk>>
        get() = db.walkDao.allWalks

    // Methods used in Owner Views

    fun getAllAvailableDogs(ownerId: Long): LiveData<List<Dog>> {
        return db.dogDao.getAvailableDogs(ownerId)
    }

    fun getOwnerDogs(ownerId: Long): LiveData<List<Dog>> {
        return db.dogDao.getDogsbyOwner(ownerId)
    }


    fun removeOwnerFromDog(ownerId: Long, dogId: Long) {
        Thread { db.dogOwnerDao.delete(dogId, ownerId) }.start()
    }

    fun getDogById(id: Long): LiveData<Dog> {
        return db.dogDao.getDogById(id)
    }

    fun updateOwnerSync(owner: Owner) {
        db.ownerDao.updateOwner(owner)
    }

    fun insertOwner(owner: Owner): LiveData<Long> {
        val rowId = MutableLiveData<Long>()
        val r = {
            val rowIds = db.ownerDao.insertOwner(owner)
            rowId.postValue(rowIds[0])
        }
        Thread(r).start()
        return rowId
    }

    fun insertOwnerSync(owner: Owner): Long {
        return db.ownerDao.insertOwner(owner)[0]
    }

    // Methods used in Dog Views

    fun getAllAvailableOwners(dogId: Long): LiveData<List<Owner>> {
        return db.ownerDao.getAvailableOwners(dogId)
    }

    fun getDogOwners(dogId: Long): LiveData<List<Owner>> {
        return db.ownerDao.getOwnersbyDog(dogId)
    }

    fun updateDogSync(dog: Dog) {
        db.dogDao.updateDog(dog)
    }

    fun addOwnerToDog(ownerId: Long, dogId: Long) {
        Thread { db.dogOwnerDao.insert(DogOwner(dogId, ownerId)) }.start()
    }

    fun insertDog(dog: Dog): LiveData<Long> {
        val rowId = MutableLiveData<Long>()
        val r = {
            val rowIds = db.dogDao.insertDog(dog)
            rowId.postValue(rowIds[0])
        }
        Thread(r).start()
        return rowId
    }

    fun insertDogSync(dog: Dog): Long {
        return db.dogDao.insertDog(dog)[0]
    }

    fun getOwnerById(id: Long): LiveData<Owner> {
        return db.ownerDao.getOwnerById(id)
    }

    // Methods used in Walk Views
    fun insertWalkAndDogs(walk: Walk, dogs: List<Dog>): LiveData<Long> {

        val rowId = MutableLiveData<Long>()
        val r = {
            val rowIds = db.walkDao.insertWalk(walk)
            val walkId = rowIds[0]
            val walkWithDogsList = ArrayList<WalkWithDogs>()
            for (dog in dogs) {
                val walkWithDogs = WalkWithDogs()
                walkWithDogs.dogId = dog.id
                walkWithDogs.walkId = walkId
                walkWithDogsList.add(walkWithDogs)
            }
            db.walkWithDogsDao.insert(walkWithDogsList)
            rowId.postValue(walkId)
        }
        Thread(r).start()
        return rowId
    }

    fun getWalkById(walkId: Long): LiveData<Walk> {
        val liveDataWalk = MutableLiveData<Walk>()
        Thread {
            Log.e(TAG, "getWalkById: $walkId")
            val walk = db.walkDao.getWalkById(walkId)
            walk.dogs = db.walkWithDogsDao.getDogsOnWalk(walkId)
            liveDataWalk.postValue(walk)
        }.start()
        return liveDataWalk
    }

    fun updateWalk(walk: Walk) {
        Thread { db.walkDao.updateWalk(walk) }.start()
    }

    fun getLocations(walkId: Long): LiveData<List<WalkLocation>> {
        return db.walkLocationDao.getLiveWalkLocations(walkId)
    }


    fun addPhotoToWalk(walkPhoto: WalkPhoto) {
        Thread { db.walkPhotoDao.addPhoto(walkPhoto) }.start()
    }

    fun deleteWalkPhoto(id: Long) {
        Thread { db.walkPhotoDao.deletePhoto(id) }.start()
    }

    fun getWalkPhotos(walkId: Long): LiveData<List<WalkPhoto>> {
        return db.walkPhotoDao.getAllWalkPhotos(walkId)
    }

    fun getDogOwnersOnWalk(walkId: Long): LiveData<List<Owner>> {
        return db.ownerDao.getDogOwnersOnWalk(walkId)
    }

}

