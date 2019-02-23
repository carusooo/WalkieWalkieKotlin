package com.example.macarus0.walkiewalkie.view

import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_dog.*
import kotlinx.android.synthetic.main.activity_edit_owner.*
import kotlinx.android.synthetic.main.contact_edit_header.*
import kotlinx.android.synthetic.main.photo_select.*
import kotlinx.android.synthetic.main.photo_select.view.*

class OwnerContactActivity : AppCompatActivity() {

    private lateinit var dogsList: DeletablePhotoListAdapter
    private var mOwnerId: Long = 0L
    private lateinit var mOwner: Owner
    private lateinit var mWalkieViewModel: WalkieViewModel

    private fun setOwnerId(mOwnerId: Long) {
        this.mOwnerId = mOwnerId
        this.mOwner.ownerId = mOwnerId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_owner)
        ButterKnife.bind(this)
        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)


        editPhotoButton.setOnClickListener { _ -> selectImage() }
        contactSaveButton.setOnClickListener { _ -> saveAndExit() }
        contactCancelButton.setOnClickListener { _ -> finish() }
        contactDeleteButton.setOnClickListener{ _ -> showDeleteConfirm() }

        dogsList = DeletablePhotoListAdapter()
        dogsList.showPhotoLabels = true
        dogsList.deleteHandler = {id ->  this.removeDog(id) }
        dogsList.placeholderImage = requireNotNull(getDrawable(R.drawable.ic_default_dog_24dp))
        dogCardRecyclerView.adapter = dogsList

        val lm = LinearLayoutManager(this)
        lm.orientation = LinearLayoutManager.HORIZONTAL
        dogCardRecyclerView.layoutManager = lm
        addDogButton.setText(R.string.select_dog)
        addDogButton.setOnClickListener { _ -> selectDog() }

        val intent = intent
        mOwnerId = intent.getLongExtra(OWNER_ID, ADD_OWNER.toLong())
        if (mOwnerId == ADD_OWNER.toLong()) {
            showAddOwnerUI()
            mOwner = Owner()
        } else {
            val ownerLiveData = mWalkieViewModel.getOwnerById(mOwnerId)
            ownerLiveData.observe(this, Observer {
                showOwnerUI(it)
                ownerLiveData.removeObservers(this)
            })
            mWalkieViewModel.getOwnerDogs(mOwnerId).observe(this, Observer<List<Dog>> { dogsList.photos = it })
        }
    }

    private fun showAddOwnerUI() {
        ownerFirstName.hint = getString(R.string.add_owner_first_name_hint)
        ownerLastName.hint = getString(R.string.add_owner_last_name_hint)
        ownerAddress.hint = getString(R.string.add_address_hint)
        ownerEmail.hint = getString(R.string.add_owner_email_hint)
        ownerPhone.hint = getString(R.string.add_owner_phone_number_hint)
        ownerPhoto.photoSelectImageView.setImageResource(R.drawable.ic_default_owner_24dp)
    }

    private fun showOwnerUI(owner: Owner) {
        mOwner = owner
        ownerFirstName!!.setText(owner.firstName)
        ownerLastName!!.setText(owner.lastName)
        ownerAddress!!.setText(owner.address)
        ownerEmail!!.setText(owner.emailAddress)
        ownerPhone!!.setText(owner.phoneNumber)
        Picasso.get().load(owner.photoUri).placeholder(R.drawable.ic_default_owner_24dp).into(ownerPhoto.photoSelectImageView)
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_owner_photo_activity_title)), SELECT_OWNER_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_OWNER_PICTURE -> {
                    val selectedImageUri = data!!.data
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        val takeFlags = data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                        val resolver = contentResolver
                        resolver.takePersistableUriPermission(selectedImageUri!!, takeFlags)
                    }
                    Picasso.get().load(selectedImageUri).into(ownerPhoto.photoSelectImageView)
                    mOwner.photoUri = selectedImageUri!!.toString()
                }
                SELECT_DOG -> {
                    val dogId = data!!.getLongExtra(SelectDogActivity.DOG_ID, 0)
                    if (dogId == 0L) return
                    mWalkieViewModel.addOwnerToDog(mOwner.id, dogId)
                }
            }
        }

    }


    private fun selectDog() {
        if (mOwnerId == -1L) {
            mWalkieViewModel.insertOwner(mOwner).observe(this, Observer<Long> { this.setOwnerIdAndSelectDog(requireNotNull(it)) })
        } else {
            val intent = Intent(this, SelectDogActivity::class.java)
            intent.putExtra(SelectDogActivity.OWNER_ID, mOwnerId)
            startActivityForResult(intent, SELECT_DOG,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    private fun setOwnerIdAndSelectDog(ownerId: Long) {
        setOwnerId(ownerId)
        mWalkieViewModel.getOwnerDogs(mOwnerId).observe(this, Observer<List<Dog>> { dogsList.photos = it })
        selectDog()
    }

    private fun saveAndExit() {
        mOwner.firstName = ownerFirstName!!.text.toString()
        mOwner.lastName = ownerLastName!!.text.toString()
        mOwner.address = ownerAddress!!.text.toString()
        mOwner.emailAddress = ownerEmail!!.text.toString()
        mOwner.phoneNumber = ownerEmail!!.text.toString()
        SaveAndExit().execute()
    }

    fun removeDog(dogId: Long) {
        mWalkieViewModel.removeOwnerFromDog(mOwner.id, dogId)
    }

    private fun showDeleteConfirm() {
        val popupWindow = Dialog(this)
        popupWindow.setContentView(R.layout.popup_window)

        var textView = popupWindow.findViewById<TextView>(R.id.popupMessageTextView)
        textView.setText(R.string.confirm_delete_owner)
        var okButton = popupWindow.findViewById<Button>(R.id.popupOk)
        var cancelButton = popupWindow.findViewById<Button>(R.id.popupCancel)

        okButton.setText(R.string.ok)
        okButton.setOnClickListener{deleteOwner()}
        cancelButton.setText(R.string.cancel)
        cancelButton.setOnClickListener{popupWindow.dismiss()}
        popupWindow.show()
    }

    private fun deleteOwner() {
        Toast.makeText(this, R.string.owner_deleted, Toast.LENGTH_SHORT).show()
        DeleteAndExit().execute()
    }

    private inner class DeleteAndExit : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            if (mOwnerId != -1L) {
                mWalkieViewModel.deleteOwner(mOwnerId)
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            finish()
        }
    }


    private inner class SaveAndExit : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            if (mOwnerId == -1L) {
                mOwnerId = mWalkieViewModel.insertOwnerSync(mOwner)
                mOwner.ownerId = mOwnerId
            }
            mWalkieViewModel.updateOwnerSync(mOwner)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            finish()
        }
    }

    companion object {

        const val OWNER_ID = "owner_id"
        const val ADD_OWNER = -1
        const val SELECT_OWNER_PICTURE = 201
        const val SELECT_DOG = 202

    }


}
