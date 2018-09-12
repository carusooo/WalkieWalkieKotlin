package com.example.macarus0.walkiewalkie.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class Owner {

    @PrimaryKey(autoGenerate = true)
    int ownerId;
    int dogId1;
    int dogId2;
    String photo;
    String firstName;
    String lastName;
    String phoneNumber;
    String emailAddress;
    String address;

    @Ignore
    public int getOwnerId() {
        return ownerId;
    }

    @Ignore
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Ignore
    public int getDogId1() {
        return dogId1;
    }

    @Ignore
    public void setDogId1(int dogId1) {
        this.dogId1 = dogId1;
    }

    @Ignore
    public int getDogId2() {
        return dogId2;
    }

    @Ignore
    public void setDogId2(int dogId2) {
        this.dogId2 = dogId2;
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

}
