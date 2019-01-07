package com.example.macarus0.walkiewalkie.view;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Owner;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerContactActivity extends AppCompatActivity {

    public static final String OWNER_ID = "owner_id";
    public static final int ADD_OWNER = -1;
    public static final int SELECT_OWNER_PICTURE = 201;
    public static final int SELECT_DOG = 202;

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
    @BindView(R.id.dog_card_rv)
    RecyclerView dogCardRecyclerView;
    @BindView(R.id.add_dog_button)
    AppCompatButton addDogButton;
    @BindView(R.id.contact_save_button)
    AppCompatImageButton saveButton;
    @BindView(R.id.contact_cancel_button)
    AppCompatImageButton cancelButton;
    AppCompatImageButton editOwnerPhotoButton;
    DeletablePhotoListAdapter dogsList;

    ImageView ownerImageView;
    private long mOwnerId;
    private Owner mOwner;
    private WalkieViewModel mWalkieViewModel;

    public void setOwnerId(long mOwnerId) {
        this.mOwnerId = mOwnerId;
        this.mOwner.setOwnerId(mOwnerId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_owner);
        ButterKnife.bind(this);

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);

        ownerImageView = ownerPhoto.findViewById(R.id.photo_image);
        editOwnerPhotoButton = ownerPhoto.findViewById(R.id.edit_photo_button);
        editOwnerPhotoButton.setOnClickListener(v -> selectImage());

        saveButton.setOnClickListener(v -> saveAndExit());
        cancelButton.setOnClickListener(v -> finish());

        dogsList = new DeletablePhotoListAdapter();
        dogsList.setShowPhotoLabels(true);
        dogsList.setDeleteHandler(this::removeDog);
        dogsList.setPlaceholderImage(getDrawable(R.drawable.ic_default_dog_24dp));
        dogCardRecyclerView.setAdapter(dogsList);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        dogCardRecyclerView.setLayoutManager(lm);
        addDogButton.setText(R.string.select_dog);
        addDogButton.setOnClickListener(view -> selectDog());

        Intent intent = getIntent();
        mOwnerId = intent.getLongExtra(OWNER_ID, ADD_OWNER);
        if (mOwnerId == ADD_OWNER) {
            showAddOwnerUI();
            mOwner = new Owner();
        } else {
            mWalkieViewModel.getOwnerById(mOwnerId).observe(this,
                    this::showOwnerUI);
            mWalkieViewModel.getOwnerDogs(mOwnerId).observe(this, dogs -> dogsList.setPhotos(dogs));
        }
    }

    void showAddOwnerUI() {
        ownerFirstName.setHint(getString(R.string.add_owner_first_name_hint));
        ownerLastName.setHint(getString(R.string.add_owner_last_name_hint));
        ownerAddress.setHint(getString(R.string.add_address_hint));
        ownerEmail.setHint(getString(R.string.add_owner_email_hint));
        ownerPhone.setHint(getString(R.string.add_owner_phone_number_hint));
        ownerImageView.setImageResource(R.drawable.ic_default_owner_24dp);
    }

    void showOwnerUI(Owner owner) {
        mOwner = owner;
        Log.i(TAG, "showOwnerUI: " + owner.getDogId1());
        ownerFirstName.setText(owner.getFirstName());
        ownerLastName.setText(owner.getLastName());
        ownerAddress.setText(owner.getAddress());
        ownerEmail.setText(owner.getEmailAddress());
        ownerPhone.setText(owner.getPhoneNumber());
        Picasso.get().load(owner.getPhotoUri()).placeholder(R.drawable.ic_default_owner_24dp).into(ownerImageView);
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
            switch (requestCode) {
                case SELECT_OWNER_PICTURE:
                    Uri selectedImageUri = data.getData();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        final int takeFlags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        ContentResolver resolver = getContentResolver();
                        resolver.takePersistableUriPermission(selectedImageUri, takeFlags);
                    }
                    Picasso.get().load(selectedImageUri).into(ownerImageView);
                    mOwner.setPhotoUri(selectedImageUri.toString());
                    break;
                case SELECT_DOG: {
                    long dogId = data.getLongExtra(SelectDogActivity.DOG_ID, 0);
                    if (dogId == 0) break;
                    mWalkieViewModel.addOwnerToDog(mOwner.getId(), dogId);
                    break;
                }
            }
        }

    }


    void selectDog() {
        if (mOwnerId == -1) {
            mWalkieViewModel.insertOwner(mOwner).observe(this, this::setOwnerIdAndSelectDog);
        } else {
            Intent intent = new Intent(this, SelectDogActivity.class);
            intent.putExtra(SelectDogActivity.OWNER_ID, mOwnerId);
            startActivityForResult(intent, SELECT_DOG,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

    void setOwnerIdAndSelectDog(long ownerId) {
        setOwnerId(ownerId);
        mWalkieViewModel.getOwnerDogs(mOwnerId).observe(this, dogs -> dogsList.setPhotos(dogs));
        selectDog();
    }
    void saveAndExit() {
        mOwner.setFirstName(ownerFirstName.getText().toString());
        mOwner.setLastName(ownerLastName.getText().toString());
        mOwner.setAddress(ownerAddress.getText().toString());
        mOwner.setEmailAddress(ownerEmail.getText().toString());
        mOwner.setPhoneNumber(ownerEmail.getText().toString());
        new SaveAndExit().execute();
    }

    void removeDog(long dogId) {
        mWalkieViewModel.removeOwnerFromDog(mOwner.getId(), dogId);
    }

    private class SaveAndExit extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (mOwnerId == -1) {
                mOwnerId = mWalkieViewModel.insertOwnerSync(mOwner);
                mOwner.setOwnerId(mOwnerId);
            }
            mWalkieViewModel.updateOwnerSync(mOwner);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            finish();
        }
    }


}
