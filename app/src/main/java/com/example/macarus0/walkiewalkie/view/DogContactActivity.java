package com.example.macarus0.walkiewalkie.view;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DogContactActivity extends AppCompatActivity {

    public static final String DOG_ID = "dog_id";
    public static final int ADD_DOG = -1;
    public static final int SELECT_PICTURE = 101;
    public static final int SELECT_OWNER = 102;

    private static final String TAG = "DogContactActivity";
    @BindView(R.id.dog_photo)
    ConstraintLayout dogPhoto;
    @BindView(R.id.dog_name)
    EditText dogName;
    @BindView(R.id.dog_address)
    EditText dogAddress;
    @BindView(R.id.owner_card_rv)
    RecyclerView ownerCardRecyclerView;
    @BindView(R.id.add_owner_button)
    AppCompatButton addOwnerButton;
    @BindView(R.id.contact_save_button)
    AppCompatImageButton saveButton;
    @BindView(R.id.contact_cancel_button)
    AppCompatImageButton cancelButton;
    AppCompatImageButton editDogPhotoButton;
    ImageView dogImageView;
    DeletablePhotoListAdapter ownersList;
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

        ownersList = new DeletablePhotoListAdapter();
        ownersList.setShowPhotoLabels(true);
        ownersList.setDeleteHandler(this::removeOwner);
        ownersList.setPlaceholderImage(getDrawable(R.drawable.ic_default_owner_24dp));
        ownerCardRecyclerView.setAdapter(ownersList);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        ownerCardRecyclerView.setLayoutManager(lm);
        addOwnerButton.setText(R.string.select_owner);
        addOwnerButton.setOnClickListener(view -> selectOwner());

        Intent intent = getIntent();
        mDogId = intent.getLongExtra(DOG_ID, ADD_DOG);
        if (mDogId == ADD_DOG) {
            showAddDogUI();
            mDog = new Dog();
        } else {
            mWalkieViewModel.getDogById(mDogId).observe(this, this::showDogUI);
            mWalkieViewModel.getDogOwners(mDogId).observe(this, owners -> ownersList.setPhotos(owners));
        }
    }

    void showAddDogUI() {
        dogName.setHint(getString(R.string.add_dog_name_hint));
        dogAddress.setHint(getString(R.string.add_address_hint));
        dogImageView.setImageResource(R.drawable.ic_default_dog_24dp);

    }

    void showDogUI(Dog dog) {
        mDog = dog;
        dogName.setText(dog.getName());
        dogAddress.setText(dog.getAddress());
        Picasso.get().load(dog.getPhotoUri()).placeholder(R.drawable.ic_default_dog_24dp).into(dogImageView);
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
                    mDog.setPhotoUri(selectedImageUri.toString());
                    break;
                case SELECT_OWNER: {
                    long ownerId = data.getLongExtra(SelectOwnerActivity.OWNER_ID, 0);
                    Log.i(TAG, "onActivityResult: Associating owner-dog " + ownerId + " "+ mDog.getDogId());
                    if (ownerId == 0) break;
                    mWalkieViewModel.addOwnerToDog(ownerId, mDog.getId());
                    break;
                }
            }
        } else {
            Log.e(TAG, "onActivityResult: Result not OK " + resultCode);
        }
    }

    void selectOwner() {
        if (mDogId == -1) {
            mWalkieViewModel.insertDog(mDog).observe(this, this::setDogIdAndSelectOwner);
        } else {
            Intent intent = new Intent(this, SelectOwnerActivity.class);
            intent.putExtra(SelectOwnerActivity.DOG_ID, mDogId);
            startActivityForResult(intent, SELECT_OWNER,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
    }

    void setDogIdAndSelectOwner(long dogId) {
        setDogId(dogId);
        mWalkieViewModel.getDogOwners(mDogId).observe(this, owners -> ownersList.setPhotos(owners));
        selectOwner();
    }

    void saveAndExit() {
        mDog.setName(dogName.getText().toString());
        mDog.setAddress(dogAddress.getText().toString());
        new SaveAndExit().execute();
    }

    void removeOwner(long ownerId) {
        Log.i(TAG, "removeOwner: Attempting to remove owner " + ownerId);
        mWalkieViewModel.removeOwnerFromDog(ownerId, mDog.getId());
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
