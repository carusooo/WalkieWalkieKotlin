package com.example.macarus0.walkiewalkie.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider;

import java.util.List;

public class WalkieViewModel extends AndroidViewModel {

    private final Context applicationContext;
    private WalkieDatabase mDb;

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

    public LiveData<List<Owner>> getAllOwners() {
        return getDb().getOwnerDao().getAllOwners();
    }

    public LiveData<List<Walk>> getAllWalks() {
        return getDb().getWalkDao().getAllWalks();
    }

    public LiveData<Dog> getDogById(long id) {
        return getDb().getDogDao().getDogById(id);
    }

    public void updateDog(Dog dog) {
        getDb().getDogDao().updateDog(dog);
    }

    public void addDogToOwner(long dogId, long ownerId) {
        // Get the owner object and add the dog in slot one or two
    }

    public void removeDogFromOwner(long dogId, long ownerId) {

    }

    public void addOwnerToDog(long ownerId, long dogId) {
        // Get the dog object and add the owner in slot one or two
    }

    public void removeOwnerFromDog(long ownerId, long dogId) {

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
        Runnable r = new Runnable() {
            @Override
            public void run() {
                getDb().getOwnerDao().updateOwner(owner);
            }
        };
        new Thread(r).start();
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
