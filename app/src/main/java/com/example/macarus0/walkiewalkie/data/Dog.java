package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Dog implements PhotoItem {

    @PrimaryKey(autoGenerate = true)
    long dogId;
    String name;
    String note;
    String photo;
    String address;

    @Override
    public long getId() {
        return dogId;
    }

    @Ignore
    public long getDogId() {
        return dogId;
    }

    @Ignore
    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

    @Ignore
    public String getName() {
        return name;
    }

    @Ignore
    public void setName(String name) {
        this.name = name;
    }

    @Ignore
    public String getPhotoUri() {
        return photo;
    }

    @Ignore
    public void setPhotoUri(String photo) {
        this.photo = photo;
    }

    @Ignore
    public String getAddress() {
        return address;
    }

    @Ignore
    public void setAddress(String address) {
        this.address = address;
    }

}
