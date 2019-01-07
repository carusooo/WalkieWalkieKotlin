package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Owner implements PhotoItem {

    @PrimaryKey(autoGenerate = true)
    long ownerId;
    long dogId1;
    long dogId2;
    String photo;
    String firstName;
    String lastName;
    String phoneNumber;
    String emailAddress;
    String address;

    @Ignore
    public long getId() {
        return ownerId;
    }

    @Ignore
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Ignore
    public long getDogId1() {
        return dogId1;
    }

    @Ignore
    public void setDogId1(long dogId1) {
        this.dogId1 = dogId1;
    }

    @Ignore
    public long getDogId2() {
        return dogId2;
    }

    @Ignore
    public void setDogId2(long dogId2) {
        this.dogId2 = dogId2;
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
    public String getFirstName() {
        return firstName;
    }

    @Ignore
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Ignore
    public String getLastName() {
        return lastName;
    }

    @Ignore
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Ignore
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Ignore
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Ignore
    public String getEmailAddress() {
        return emailAddress;
    }

    @Ignore
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Ignore
    public String getAddress() {
        return address;
    }

    @Ignore
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getName() {
        return firstName;
    }
}
