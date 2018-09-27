package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class Walk {

    @PrimaryKey(autoGenerate = true)
    long walkId;

    @Ignore
    public String getWalkDate() {
        return walkDate;
    }

    @Ignore
    public void setWalkDate(String walkDate) {
        this.walkDate = walkDate;
    }

    String walkDate;
    int walkDistance;
    long walkDuration;
    int walkDogsCount;

    @Ignore
    public List<Dog> getDogs() {
        return dogs;
    }

    @Ignore
    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

    @Ignore
    List<Dog> dogs;

}
