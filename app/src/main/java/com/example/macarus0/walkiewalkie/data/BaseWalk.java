package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class BaseWalk {

    @PrimaryKey(autoGenerate = true)
    int walkId;

    String walkDate;
    int walkDistance;
    int walkDuration;
    int walkDogsCount;

    int dogId;

}
