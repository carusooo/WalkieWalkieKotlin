package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Dog {

    @PrimaryKey(autoGenerate = true)
    int dogId;

    int ownerId1;
    int ownerId2;
    String name;
    String note;
    String photo;
    String address;

    @Ignore
    public int getDogId() {
        return dogId;
    }
    @Ignore
    public void setDogId(int dogId) {
        this.dogId = dogId;
    }
    @Ignore
    public int getOwnerId1() {
        return ownerId1;
    }
    @Ignore
    public void setOwnerId1(int ownerId1) {
        this.ownerId1 = ownerId1;
    }
    @Ignore
    public int getOwnerId2() {
        return ownerId2;
    }
    @Ignore
    public void setOwnerId2(int ownerId2) {
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
