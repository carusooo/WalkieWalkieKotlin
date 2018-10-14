package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

@Entity
public class Walk {

    @PrimaryKey(autoGenerate = true)
    long walkId;
    String walkDate;
    int walkDistance;
    String walkDuration;
    long walkStartTime;
    long walkEndTime;
    int walkDogsCount;
    @Ignore
    List<Dog> dogs;

    public long getWalkStartTime() {
        return walkStartTime;
    }

    public void setWalkStartTime(long walkStartTime) {
        this.walkStartTime = walkStartTime;
    }

    public long getWalkEndTime() {
        return walkEndTime;
    }

    public void setWalkEndTime(long walkEndTime) {
        this.walkEndTime = walkEndTime;
    }

    public String getWalkDuration() {
        return walkDuration;
    }

    public void setWalkDuration(String walkDuration) {
        this.walkDuration = walkDuration;
    }

    @Ignore
    public long getWalkId() {
        return walkId;
    }

    @Ignore
    public void setWalkId(long walkId) {
        this.walkId = walkId;
    }

    @Ignore
    public String getWalkDate() {
        return walkDate;
    }

    @Ignore
    public void setWalkDate(String walkDate) {
        this.walkDate = walkDate;
    }

    public int getWalkDogsCount() {
        return walkDogsCount;
    }

    public void setWalkDogsCount(int walkDogsCount) {
        this.walkDogsCount = walkDogsCount;
    }

    @Ignore
    public List<Dog> getDogs() {
        return dogs;
    }

    @Ignore
    public void setDogs(List<Dog> dogs) {
        this.dogs = dogs;
    }

}
