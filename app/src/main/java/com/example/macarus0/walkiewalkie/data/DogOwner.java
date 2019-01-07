package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class DogOwner {

    @PrimaryKey(autoGenerate = true)
    int dogOwnerId;
    long dogId;
    long ownerId;

    public DogOwner(long dogId, long ownerId) {
        this.dogId = dogId;
        this.ownerId = ownerId;
    }

    public long getDogId() {
        return dogId;
    }

    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

}
