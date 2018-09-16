package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

@Dao
public class Walk {

    @Embedded
    BaseWalk baseWalk;

    @Relation(parentColumn = "walkId",
    entityColumn = "dogId")
    public List<Dog> dogs;

    @Relation(parentColumn = "walkId",
    entityColumn = "photoId")
    public List<WalkPhoto> photos;

}
