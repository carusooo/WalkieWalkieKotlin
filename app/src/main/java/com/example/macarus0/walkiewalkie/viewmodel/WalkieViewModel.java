package com.example.macarus0.walkiewalkie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider;

import java.util.List;

public class WalkieViewModel extends AndroidViewModel {

    private final Context applicationContext;
    private WalkieDatabase mDb;

    private String TAG = "WalkieViewModel";

    public WalkieViewModel(@NonNull Application application) {
        super(application);
        applicationContext = application.getApplicationContext();
    }

    private WalkieDatabase getDb() {
        if (mDb == null) {
            mDb = WalkieDatabaseProvider.getDatabase(applicationContext);
        }
        return mDb;
    }

    public LiveData<List<Dog>> getAllDogs() {
        return getDb().getDogDao().getAllDogs();
    }

    public LiveData<List<Dog>> getAllAvailableDogs() {
        return getDb().getDogDao().getAllDogs();
    }

    public LiveData<List<Owner>> getAllOwners() {
        return getDb().getOwnerDao().getAllOwners();
    }

    public LiveData<List<Owner>> getAllAvailableOwners() {
        return getDb().getOwnerDao().getAvailableOwners();
    }

    public LiveData<List<Walk>> getAllWalks() {
        return getDb().getWalkDao().getAllWalks();
    }

    public LiveData<Dog> getDogById(long id) {
        return getDb().getDogDao().getDogById(id);
    }

    public void updateDog(Dog dog) {
        new Thread(() ->
                getDb().getDogDao().updateDog(dog)
        ).start();
    }

    public void addDogToOwner(long dogId, long ownerId) {
        // Get the owner object and add the dog in slot one or two
        new Thread(() -> {
            Owner owner = getDb().getOwnerDao().getOwnerByIdSync(ownerId);
            if (owner.getDogId1() == 0) {
                owner.setDogId1(dogId);
            } else if (owner.getDogId2() == 0) {
                owner.setDogId2(dogId);
            }
            getDb().getOwnerDao().updateOwner(owner);
        }).start();
    }

    public void removeOwnerFromDog(long dogId, long ownerId) {
        new Thread(() -> {
            Owner owner = getDb().getOwnerDao().getOwnerByIdSync(ownerId);
            if (owner.getDogId1() == dogId) {
                owner.setDogId1(0);
            } else if (owner.getDogId2() == dogId) {
                owner.setDogId2(0);
            }
            getDb().getOwnerDao().updateOwner(owner);
        }).start();
    }

    public void addOwnerToDog(long ownerId, long dogId) {
        // Get the dog object and add the owner in slot one or two
        new Thread(() -> {
            Dog dog = getDb().getDogDao().getDogByIdSync(dogId);
            if (dog.getOwnerId1() == 0) {
                dog.setOwnerId1(ownerId);
            } else if (dog.getOwnerId2() == 0) {
                dog.setOwnerId2(ownerId);
            } else {
                Log.e(TAG, "addOwnerToDog: Couldn't find empty slot " + dog.getOwnerId1()
                        + " " + dog.getOwnerId2());
            }
            getDb().getDogDao().updateDog(dog);
        }).start();
    }

    public void removeDogFromOwner(long ownerId, long dogId) {
        new Thread(() -> {
            Dog dog = getDb().getDogDao().getDogByIdSync(dogId);
            if (dog.getOwnerId1() == ownerId) {
                dog.setOwnerId1(0);
            } else if (dog.getOwnerId2() == ownerId) {
                dog.setOwnerId2(0);
            }
            getDb().getDogDao().updateDog(dog);
        }).start();
    }

    public LiveData<Long> insertDog(Dog dog) {
        MutableLiveData<Long> rowId = new MutableLiveData<>();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long[] rowIds = getDb().getDogDao().insertDog(dog);
                rowId.postValue(rowIds[0]);
            }
        };
        new Thread(r).start();
        return rowId;
    }

    public LiveData<Owner> getOwnerById(long id) {
        return getDb().getOwnerDao().getOwnerById(id);
    }

    public void updateOwner(Owner owner) {
        new Thread(() ->
                getDb().getOwnerDao().updateOwner(owner)
        ).start();
    }

    public LiveData<Long> insertOwner(Owner owner) {
        MutableLiveData<Long> rowId = new MutableLiveData<>();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                long[] rowIds = getDb().getOwnerDao().insertOwner(owner);
                rowId.postValue(rowIds[0]);
            }
        };
        new Thread(r).start();
        return rowId;
    }

}
