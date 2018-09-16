package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DogContactActivity extends AppCompatActivity {

    public static final String DOG_ID = "dog_id";
    public static final int ADD_DOG = -1;

    public static final int SELECT_PICTURE = 101;
    @BindView(R.id.dog_photo)
    ConstraintLayout dogPhoto;
    @BindView(R.id.dog_name)
    EditText dogName;
    @BindView(R.id.dog_address)
    EditText dogAddress;
    @BindView(R.id.owner1_card)
    CardView owner1CardView;
    @BindView(R.id.owner2_card)
    CardView owner2CardView;
    @BindView(R.id.contact_save_button)
    Button saveButton;
    AppCompatImageButton editDogPhotoButton;
    ImageView dogImageView;
    private long mDogId;
    private Dog mDog;
    private WalkieViewModel mWalkieViewModel;

    public void setmDogId(long mDogId) {
        this.mDogId = mDogId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dog);
        ButterKnife.bind(this);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        dogImageView = dogPhoto.findViewById(R.id.photo_image);
        editDogPhotoButton = dogPhoto.findViewById(R.id.edit_photo_button);
        editDogPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        saveButton.setText(R.string.save_contact_edits);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        Intent intent = getIntent();
        mDogId = intent.getLongExtra(DOG_ID, ADD_DOG);
        if (mDogId == ADD_DOG) {
            showAddDogUI();
            mDog = new Dog();
        } else {
            mWalkieViewModel.getDogById(mDogId).observe(this, dog -> showDogUI(dog));
        }
    }

    void showAddDogUI() {
        dogName.setHint(getString(R.string.add_dog_name_hint));
        dogAddress.setHint(getString(R.string.add_address_hint));
        dogImageView.setImageResource(R.drawable.ic_default_dog_24dp);
        owner1CardView.findViewById(R.id.owner1_image);
    }

    void showDogUI(Dog dog) {
        mDog = dog;
        dogName.setText(dog.getName());
        dogAddress.setText(dog.getAddress());
        Picasso.get().load(dog.getPhoto()).placeholder(R.drawable.ic_default_dog_24dp).into(dogImageView);
        owner1CardView.findViewById(R.id.owner1_image);
    }

    void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_dog_photo_activity_title)), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Picasso.get().load(selectedImageUri).into(dogImageView);
                mDog.setPhoto(selectedImageUri.toString());
            }
        }

    }

    void saveChanges() {
        mDog.setName(dogName.getText().toString());
        mDog.setAddress(dogAddress.getText().toString());
        if (mDogId == -1) {
            mWalkieViewModel.insertDog(mDog).observe(this, id -> setmDogId(id));
        }
    }


}
