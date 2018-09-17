package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerContactActivity extends AppCompatActivity {

    public static final String OWNER_ID = "owner_id";
    public static final int ADD_OWNER = -1;
    public static final int SELECT_OWNER_PICTURE = 201;
    public static final int SELECT_DOG1 = 202;
    public static final int SELECT_DOG2 = 203;
    private static final String TAG = "OwnerContactActivity";
    @BindView(R.id.owner_photo)
    ConstraintLayout ownerPhoto;
    @BindView(R.id.owner_first_name)
    EditText ownerFirstName;
    @BindView(R.id.owner_last_name)
    EditText ownerLastName;
    @BindView(R.id.owner_phone)
    EditText ownerPhone;
    @BindView(R.id.owner_email)
    EditText ownerEmail;
    @BindView(R.id.owner_address)
    EditText ownerAddress;
    @BindView(R.id.dog1_card)
    CardView dog1CardView;
    @BindView(R.id.dog2_card)
    CardView dog2CardView;
    @BindView(R.id.contact_save_button)
    Button saveButton;
    AppCompatImageButton editOwnerPhotoButton;

    ImageView ownerImageView;
    private long mOwnerId;
    private Owner mOwner;
    private WalkieViewModel mWalkieViewModel;

    public void setOwnerId(long mOwnerId) {
        this.mOwnerId = mOwnerId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_owner);
        ButterKnife.bind(this);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        ownerImageView = ownerPhoto.findViewById(R.id.photo_image);
        editOwnerPhotoButton = ownerPhoto.findViewById(R.id.edit_photo_button);
        editOwnerPhotoButton.setOnClickListener(new View.OnClickListener() {
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
        mOwnerId = intent.getLongExtra(OWNER_ID, ADD_OWNER);
        if (mOwnerId == ADD_OWNER) {
            showAddOwnerUI();
            mOwner = new Owner();
        } else {
            mWalkieViewModel.getOwnerById(mOwnerId).observe(this,
                    owner -> showOwnerUI(owner));
        }

        dog1CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDog(SELECT_DOG1);
            }
        });
        dog2CardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDog(SELECT_DOG2);
            }
        });
    }

    void showAddOwnerUI() {
        ownerFirstName.setHint(getString(R.string.add_owner_first_name_hint));
        ownerLastName.setHint(getString(R.string.add_owner_last_name_hint));
        ownerAddress.setHint(getString(R.string.add_address_hint));
        ownerEmail.setHint(getString(R.string.add_owner_email_hint));
        ownerPhone.setHint(getString(R.string.add_owner_phone_number_hint));
        ownerImageView.setImageResource(R.drawable.ic_default_owner_24dp);
        showAddDog1UI();
        showAddDog2UI();
    }

    void showOwnerUI(Owner owner) {
        mOwner = owner;
        Log.i(TAG, "showOwnerUI: " + owner.getDogId1());
        if (owner.getDogId1() != 0) {
            mWalkieViewModel.getDogById(owner.getDogId1()).observe(this, dog -> showDog1UI(dog));
        } else {
            showAddDog1UI();
        }

        if (owner.getDogId2() != 0) {
            mWalkieViewModel.getDogById(owner.getDogId2()).observe(this, dog -> showDog2UI(dog));
        } else {
            showAddDog2UI();
        }

        ownerFirstName.setText(owner.getFirstName());
        ownerLastName.setText(owner.getLastName());
        ownerAddress.setText(owner.getAddress());
        ownerEmail.setText(owner.getEmailAddress());
        ownerPhone.setText(owner.getPhoneNumber());
        Picasso.get().load(owner.getPhoto()).placeholder(R.drawable.ic_default_owner_24dp).into(ownerImageView);
    }

    void showDog1UI(Dog dog) {
        showDogUI(dog, dog1CardView);
    }

    void showDog2UI(Dog dog) {
        showDogUI(dog, dog2CardView);
    }

    void showDogUI(Dog dog, CardView cardView) {
        ImageView dogImageView = cardView.findViewById(R.id.contact_image);
        TextView dogNameTextView = cardView.findViewById(R.id.contact_name);
        ImageButton dogRemoveButton = cardView.findViewById(R.id.contact_remove);
        Picasso.get().load(dog.getPhoto()).placeholder(R.drawable.ic_default_dog_24dp).into(dogImageView);
        dogNameTextView.setText(dog.getName());
    }

    void showAddDog1UI() {
        showAddDogUI(dog1CardView);
    }

    void showAddDog2UI() {
        showAddDogUI(dog2CardView);
    }

    void showAddDogUI(CardView cardView) {
        ImageView dogImageView = cardView.findViewById(R.id.contact_image);
        TextView dogNameTextView = cardView.findViewById(R.id.contact_name);
        Picasso.get().load(R.drawable.ic_default_dog_24dp).into(dogImageView);
        dogNameTextView.setText(getText(R.string.add_dog));
    }

    void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_owner_photo_activity_title)), SELECT_OWNER_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_OWNER_PICTURE) {
                Uri selectedImageUri = data.getData();
                Picasso.get().load(selectedImageUri).into(ownerImageView);
                mOwner.setPhoto(selectedImageUri.toString());
            } else if (requestCode == SELECT_DOG1) {
                long dogId = data.getLongExtra(SelectDogActivity.DOG_ID, 0);
                mOwner.setDogId1(dogId);
                mWalkieViewModel.getDogById(dogId).observe(this, dog -> showDog1UI(dog));
                mWalkieViewModel.addOwnerToDog(mOwner.getOwnerId(), dogId);

            } else if (requestCode == SELECT_DOG2) {
                long dogId = data.getLongExtra(SelectDogActivity.DOG_ID, 0);
                mOwner.setDogId2(dogId);
                mWalkieViewModel.getDogById(dogId).observe(this, dog -> showDog2UI(dog));
                mWalkieViewModel.addOwnerToDog(mOwner.getOwnerId(), dogId);
            }
        }

    }


    void selectDog(int dogSlot) {
        Intent intent = new Intent(this, SelectDogActivity.class);
        startActivityForResult(intent, dogSlot);
    }

    void saveChanges() {
        mOwner.setFirstName(ownerFirstName.getText().toString());
        mOwner.setLastName(ownerLastName.getText().toString());
        mOwner.setAddress(ownerAddress.getText().toString());
        mOwner.setEmailAddress(ownerEmail.getText().toString());
        mOwner.setPhoneNumber(ownerEmail.getText().toString());
        if (mOwnerId == -1) {
            mWalkieViewModel.insertOwner(mOwner).observe(this, id -> setOwnerId(id));
        } else {
            new Thread(() -> {
                mWalkieViewModel.updateOwner(mOwner);
            });
        }
    }


}
