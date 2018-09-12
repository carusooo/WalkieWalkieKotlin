package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WalkPhoto {
    @PrimaryKey(autoGenerate = true)
    int photoId;

    String photoPath;

    int walkId;

}
