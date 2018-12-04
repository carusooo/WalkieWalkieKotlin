package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
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

public class DogContactActivity extends AppCompatActivity {

    public static final String DOG_ID = "dog_id";
    public static final int ADD_DOG = -1;
    public static final int SELECT_PICTURE = 101;
    public static final int SELECT_OWNER1 = 102;
    public static final int SELECT_OWNER2 = 103;
    private static final String TAG = "DogContactActivity";
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
    AppCompatImageButton saveButton;
    @BindView(R.id.contact_cancel_button)
    AppCompatImageButton cancelButton;
    AppCompatImageButton editDogPhotoButton;
    ImageView dogImageView;
    private long mDogId;
    private Dog mDog;
    private WalkieViewModel mWalkieViewModel;

    public void setDogId(long mDogId) {
        this.mDogId = mDogId;
        this.mDog.setDogId(mDogId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dog);
        ButterKnife.bind(this);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        dogImageView = dogPhoto.findViewById(R.id.photo_image);
        editDogPhotoButton = dogPhoto.findViewById(R.id.edit_photo_button);
        editDogPhotoButton.setOnClickListener(v -> selectImage());
        saveButton.setOnClickListener(v -> saveAndExit());
        cancelButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        mDogId = intent.getLongExtra(DOG_ID, ADD_DOG);
        if (mDogId == ADD_DOG) {
            showAddDogUI();
            mDog = new Dog();
        } else {
            mWalkieViewModel.getDogById(mDogId).observe(this, this::showDogUI);
        }
    }

    void showAddDogUI() {
        dogName.setHint(getString(R.string.add_dog_name_hint));
        dogAddress.setHint(getString(R.string.add_address_hint));
        dogImageView.setImageResource(R.drawable.ic_default_dog_24dp);
        showAddOwner1UI();
        showAddOwner2UI();
    }

    void showDogUI(Dog dog) {
        mDog = dog;
        Log.i(TAG, "showDogUI: " + dog.getOwnerId1());
        if (dog.getOwnerId1() != 0) {
            showOwner1UI(dog.getOwnerId1());
        } else {
            showAddOwner1UI();
        }
        if (dog.getOwnerId2() != 0) {
            showOwner2UI(dog.getOwnerId2());
        } else {
            showAddOwner2UI();
        }
        dogName.setText(dog.getName());
        dogAddress.setText(dog.getAddress());
        Picasso.get().load(dog.getPhoto()).placeholder(R.drawable.ic_default_dog_24dp).into(dogImageView);
    }

    void showOwner1UI(long ownerId) {
        new UpdateOwnerUI(ownerId, owner1CardView).execute();
    }

    void showOwner2UI(long ownerId) {
        new UpdateOwnerUI(ownerId, owner2CardView).execute();
    }

    void showOwnerUI(Owner owner, CardView cardView) {
        ImageView ownerImageView = cardView.findViewById(R.id.contact_image);
        TextView ownerNameTextView = cardView.findViewById(R.id.contact_name);
        ImageButton ownerRemoveButton = cardView.findViewById(R.id.contact_remove);
        ownerRemoveButton.setVisibility(View.VISIBLE);
        ownerRemoveButton.setOnClickListener(v -> removeOwner(owner.getOwnerId(), cardView));
        Picasso.get().load(owner.getPhoto()).placeholder(R.drawable.ic_default_owner_24dp).into(ownerImageView);
        ownerNameTextView.setText(owner.getFirstName());
    }

    void showAddOwner1UI() {
        showAddOwnerUI(owner1CardView);
        owner1CardView.setOnClickListener(v -> selectOwner(SELECT_OWNER1));

    }

    void showAddOwner2UI() {
        showAddOwnerUI(owner2CardView);
        owner2CardView.setOnClickListener(v -> selectOwner(SELECT_OWNER2));
    }

    void showAddOwnerUI(CardView cardView) {
        ImageView ownerImageView = cardView.findViewById(R.id.contact_image);
        TextView ownerNameTextView = cardView.findViewById(R.id.contact_name);
        ImageButton ownerRemoveButton = cardView.findViewById(R.id.contact_remove);
        ownerRemoveButton.setVisibility(View.INVISIBLE);
        Picasso.get().load(R.drawable.ic_default_owner_24dp).into(ownerImageView);
        ownerNameTextView.setText(getText(R.string.select_owner));
    }

    void selectImage() {
        Intent intent;
        intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_dog_photo_activity_title)), SELECT_PICTURE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_PICTURE:
                    Uri selectedImageUri = data.getData();
                    final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    ContentResolver resolver = getContentResolver();
                    resolver.takePersistableUriPermission(selectedImageUri, takeFlags);
                    Picasso.get().load(selectedImageUri).into(dogImageView);
                    mDog.setPhoto(selectedImageUri.toString());
                    break;
                case SELECT_OWNER1: {
                    long ownerId = data.getLongExtra(SelectOwnerActivity.OWNER_ID, 0);
                    if (ownerId == 0) break;
                    mDog.setOwnerId1(ownerId);
                    mWalkieViewModel.addDogToOwner(mDog.getDogId(), ownerId);
                    mWalkieViewModel.updateDog(mDog);
                    showOwner1UI(ownerId);
                    break;
                }
                case SELECT_OWNER2: {
                    long ownerId = data.getLongExtra(SelectOwnerActivity.OWNER_ID, 0);
                    if (ownerId == 0) break;
                    mDog.setOwnerId2(ownerId);
                    mWalkieViewModel.addDogToOwner(mDog.getDogId(), ownerId);
                    mWalkieViewModel.updateDog(mDog);
                    showOwner2UI(ownerId);
                    break;
                }
            }
        } else {
            Log.e(TAG, "onActivityResult: Result not OK" + resultCode);
        }
    }

    void selectOwner(int ownerSlot) {
        if (mDogId == -1) {
            mWalkieViewModel.insertDog(mDog).observe(this, this::setDogId);
        }
        Intent intent = new Intent(this, SelectOwnerActivity.class);
        startActivityForResult(intent, ownerSlot);
    }

    void saveAndExit() {
        mDog.setName(dogName.getText().toString());
        mDog.setAddress(dogAddress.getText().toString());
        new SaveAndExit().execute();
    }

    void removeOwner(long ownerId, CardView cardView) {
        if (mDog.getOwnerId1() == ownerId) {
            mDog.setOwnerId1(0);
        } else if (mDog.getOwnerId2() == ownerId) {
            mDog.setOwnerId2(0);
        }
        mWalkieViewModel.removeOwnerFromDog(mDog.getDogId(), ownerId);
        mWalkieViewModel.updateDog(mDog);
        showAddOwnerUI(cardView);
    }


    private class UpdateOwnerUI extends AsyncTask<Void, Owner, Owner> {
        long ownerId;
        CardView cardView;

        UpdateOwnerUI(long ownerId, CardView cardView) {
            this.cardView = cardView;
            this.ownerId = ownerId;
        }

        @Override
        protected Owner doInBackground(Void... voids) {
            Owner owner = mWalkieViewModel.getOwnerByIdSync(ownerId);
            return owner;
        }

        @Override
        protected void onPostExecute(Owner owner) {
            showOwnerUI(owner, cardView);
        }
    }

    private class SaveAndExit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mDogId == -1) {
                mDogId = mWalkieViewModel.insertDogSync(mDog);
                mDog.setDogId(mDogId);
            }
            mWalkieViewModel.updateDogSync(mDog);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }


}
