package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class WalkPhoto {
    @Ignore
    public long getPhotoId() {
        return photoId;
    }

    @PrimaryKey(autoGenerate = true)
    long photoId;

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public long getWalkId() {
        return walkId;
    }

    public void setWalkId(long walkId) {
        this.walkId = walkId;
    }

    String photoUri;
    long walkId;

}
