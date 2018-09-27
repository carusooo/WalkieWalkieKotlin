package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WalkWithDogs {

    @PrimaryKey(autoGenerate = true)
    long walkWithDogsId;

    long walkId;
    long dogId;

    @Ignore
    public void setWalkId(long walkId) {
        this.walkId = walkId;
    }

    @Ignore
    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

//    @Relation(parentColumn = "walkId",
//    entityColumn = "photoId")
//    public List<String> photos;

}
