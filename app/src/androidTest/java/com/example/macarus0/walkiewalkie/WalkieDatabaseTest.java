package com.example.macarus0.walkiewalkie;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.DogDao;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.data.OwnerDao;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;


@RunWith(AndroidJUnit4.class)
public class WalkieDatabaseTest {

    @Rule
    public TestRule rule = new InstantTaskExecutorRule();
    private DogDao mDogDao;
    private OwnerDao mOwnerDao;
    private WalkieDatabase mDb;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context, WalkieDatabase.class).allowMainThreadQueries().build();
        mDogDao = mDb.getDogDao();
        mOwnerDao = mDb.getOwnerDao();
    }

    @After
    public void deleteDb() {
        mDb.close();
    }


    public void testDogName(String name, List<Dog> dogs) {
        ArrayList<String> dogNames = new ArrayList<>();
        for (Dog dog :
                dogs) {
            dogNames.add(dog.getName());
        }
        assertThat(dogNames, hasItem(name));
    }

    public void testOwnerName(String name, List<Owner> owners) {
        ArrayList<String> ownerNames = new ArrayList<>();
        for (Owner owner :
                owners) {
            ownerNames.add(owner.getFirstName());
        }
        assertThat(ownerNames, hasItem(name));
    }

    @Test
    public void addDog() throws Exception {
        String dogName = "Pippin";
        Dog dog = new Dog();
        dog.setName(dogName);
        mDogDao.insertDog(dog);
        mDogDao.getAllDogs().observeForever(dogs -> testDogName(dogName, dogs));
    }

    @Test
    public void addOwner() throws Exception {
        String ownerName = "Andy";
        Owner owner = new Owner();
        owner.setFirstName(ownerName);
        mOwnerDao.insertOwner(owner);
        mOwnerDao.getAllOwners().observeForever(owners -> testOwnerName(ownerName, owners));
    }

}

