package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.view.View;
import android.widget.Button;
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

    public static final int SELECT_OWNER_PICTURE = 102;

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

    @BindView(R.id.contact_save_button)
    Button saveButton;
    AppCompatImageButton editOwnerPhotoButton;

    ImageView ownerImageView;

    public void setOwnerId(long mOwnerId) {
        this.mOwnerId = mOwnerId;
    }

    private long mOwnerId;
    private Owner mOwner;
    private WalkieViewModel mWalkieViewModel;


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
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_OWNER_PICTURE) {
                Uri selectedImageUri = data.getData();
                Picasso.get().load(selectedImageUri).into(ownerImageView);
                mOwner.setPhoto(selectedImageUri.toString());
            }
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
        ownerFirstName.setText(owner.getFirstName());
        ownerLastName.setText(owner.getLastName());
        ownerAddress.setText(owner.getAddress());
        ownerEmail.setText(owner.getEmailAddress());
        ownerPhone.setText(owner.getPhoneNumber());
        Picasso.get().load(owner.getPhoto()).placeholder(R.drawable.ic_default_owner_24dp).into(ownerImageView);
    }

    void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_owner_photo_activity_title)), SELECT_OWNER_PICTURE);
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
            mWalkieViewModel.updateOwner(mOwner);
        }
    }


}
