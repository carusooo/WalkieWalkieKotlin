package com.example.macarus0.walkiewalkie.view


import android.app.Activity
import android.app.ActivityOptions
import android.app.Dialog
import androidx.lifecycle.Observer

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_dog.*
import kotlinx.android.synthetic.main.contact_edit_header.*
import kotlinx.android.synthetic.main.photo_select.*
import kotlinx.android.synthetic.main.photo_select.view.*

class DogContactActivity : AppCompatActivity() {
    private lateinit var ownersList: DeletablePhotoListAdapter
    private var mDogId: Long = 0
    private lateinit var mDog: Dog
    private lateinit var mWalkieViewModel: WalkieViewModel

    private fun setDogId(mDogId: Long) {
        this.mDogId = mDogId
        this.mDog.dogId = mDogId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_dog)

        mWalkieViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)

        editPhotoButton.setOnClickListener { _ -> selectImage() }
        contactSaveButton.setOnClickListener { _ -> saveAndExit() }
        contactCancelButton.setOnClickListener { _ -> finish() }
        contactDeleteButton.setOnClickListener{ _ -> showDeleteConfirm() }

        ownersList = DeletablePhotoListAdapter()
        ownersList.showPhotoLabels = true
        ownersList.deleteHandler = { id -> removeOwner(id) }
        ownersList.placeholderImage = requireNotNull(getDrawable(R.drawable.ic_default_owner_24dp))
        val lm = LinearLayoutManager(this)
        lm.orientation = LinearLayoutManager.HORIZONTAL
        ownerCardRecyclerView.layoutManager = lm
        ownerCardRecyclerView.adapter = ownersList
        addOwnerButton.setText(R.string.select_owner)
        addOwnerButton.setOnClickListener { _ -> selectOwner() }

        val intent = intent
        mDogId = intent.getLongExtra(DOG_ID, ADD_DOG.toLong())
        if (mDogId == ADD_DOG.toLong()) {
            showAddDogUI()
            mDog = Dog()
        } else {
            val dogLiveData = mWalkieViewModel.getDogById(mDogId)
            dogLiveData.observe(this, Observer {
                showDogUI(it)
                dogLiveData.removeObservers(this)
            })
            mWalkieViewModel.getDogOwners(mDogId).observe(this, Observer<List<Owner>> { ownersList.photos = it })
        }
    }

    private fun showAddDogUI() {
        dogName!!.hint = getString(R.string.add_dog_name_hint)
        dogAddress!!.hint = getString(R.string.add_address_hint)
        dogPhoto.photoSelectImageView.setImageResource(R.drawable.ic_default_dog_24dp)
    }

    private fun showDogUI(dog: Dog) {
        mDog = dog
        dogName!!.setText(dog.name)
        dogAddress!!.setText(dog.address)
        Picasso.get().load(dog.photoUri).placeholder(R.drawable.ic_default_dog_24dp).into(dogPhoto.photoSelectImageView)
    }

    private fun selectImage() {
        val intent: Intent
        intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, resources.getString(R.string.select_dog_photo_activity_title)), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SELECT_PICTURE -> {
                    val selectedImageUri = data!!.data
                    val takeFlags = data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val resolver = contentResolver
                    resolver.takePersistableUriPermission(selectedImageUri!!, takeFlags)
                    Picasso.get().load(selectedImageUri).into(photoSelectImageView)
                    mDog.photoUri = selectedImageUri.toString()
                }
                SELECT_OWNER -> {
                    val ownerId = data!!.getLongExtra(SelectOwnerActivity.OWNER_ID, 0)
                    Log.i(TAG, "onActivityResult: Associating owner-dog " + ownerId + " " + mDog.dogId)
                    if (ownerId == 0L) return
                    mWalkieViewModel.addOwnerToDog(ownerId, mDog.id)
                }
            }
        } else {
            Log.e(TAG, "onActivityResult: Result not OK $resultCode")
        }
    }

    private fun selectOwner() {
        if (mDogId == -1L) {
            mWalkieViewModel.insertDog(mDog).observe(this, Observer<Long> { this.setDogIdAndSelectOwner(requireNotNull(it)) })
        } else {
            val intent = Intent(this, SelectOwnerActivity::class.java)
            intent.putExtra(SelectOwnerActivity.DOG_ID, mDogId)
            startActivityForResult(intent, SELECT_OWNER,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        }
    }

    private fun setDogIdAndSelectOwner(dogId: Long) {
        setDogId(dogId)
        mWalkieViewModel.getDogOwners(mDogId).observe(this, Observer<List<Owner>> { ownersList.photos = it })
        selectOwner()
    }

    private fun saveAndExit() {
        mDog.name = dogName!!.text.toString()
        mDog.address = dogAddress!!.text.toString()
        SaveAndExit().execute()
    }

    fun removeOwner(ownerId: Long) {
        Log.i(TAG, "removeOwner: Attempting to remove owner $ownerId")
        mWalkieViewModel.removeOwnerFromDog(ownerId, mDog.id)
    }


    private fun showDeleteConfirm() {
        val popupWindow = Dialog(this)
        popupWindow.setContentView(R.layout.popup_window)

        var textView = popupWindow.findViewById<TextView>(R.id.popupMessageTextView)
        textView.setText(R.string.confirm_delete_dog)
        var okButton = popupWindow.findViewById<Button>(R.id.popupOk)
        var cancelButton = popupWindow.findViewById<Button>(R.id.popupCancel)

        okButton.setText(R.string.ok)
        okButton.setOnClickListener{deleteDog()}
        cancelButton.setText(R.string.cancel)
        cancelButton.setOnClickListener{popupWindow.dismiss()}
        popupWindow.show()
    }

    private fun deleteDog() {
        Toast.makeText(this, R.string.dog_deleted, Toast.LENGTH_SHORT).show()
        DeleteAndExit().execute()
    }

    private inner class DeleteAndExit : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            if (mDogId != -1L) {
                Log.d("DogContactActivity", "Deleting dog")
                mWalkieViewModel.deleteDog(mDogId)
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            finish()
        }
    }


    private inner class SaveAndExit : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg voids: Void): Void? {
            if (mDogId == -1L) {
                mDogId = mWalkieViewModel.insertDogSync(mDog)
                mDog.dogId = mDogId
            }
            mWalkieViewModel.updateDogSync(mDog)
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            finish()
        }
    }

    companion object {

        const val DOG_ID = "dog_id"
        const val ADD_DOG = -1
        const val SELECT_PICTURE = 101
        const val SELECT_OWNER = 102

        private const val TAG = "DogContactActivity"
    }


}
