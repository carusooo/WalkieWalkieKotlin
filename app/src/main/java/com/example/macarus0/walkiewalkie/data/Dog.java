package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Dog {

    @Ignore
    public void setDogId(long dogId) {
        this.dogId = dogId;
    }

    @PrimaryKey(autoGenerate = true)
    long dogId;

    long ownerId1;
    long ownerId2;
    String name;
    String note;
    String photo;
    String address;

    @Ignore
    public long getDogId() {
        return dogId;
    }
    @Ignore
    public long getOwnerId1() {
        return ownerId1;
    }
    @Ignore
    public void setOwnerId1(long ownerId1) {
        this.ownerId1 = ownerId1;
    }
    @Ignore
    public long getOwnerId2() {
        return ownerId2;
    }
    @Ignore
    public void setOwnerId2(long ownerId2) {
        this.ownerId2 = ownerId2;
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
    public String getNote() {
        return note;
    }
    @Ignore
    public void setNote(String note) {
        this.note = note;
    }
    @Ignore
    public String getPhoto() {
        return photo;
    }
    @Ignore
    public void setPhoto(String photo) {
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
