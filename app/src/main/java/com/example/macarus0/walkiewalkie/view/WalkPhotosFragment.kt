package com.example.macarus0.walkiewalkie.view

import android.app.Activity.RESULT_OK
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.WalkPhoto
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [WalkPhotosFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [WalkPhotosFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalkPhotosFragment : Fragment() {
    @BindView(R.id.walk_photos_rv)
    @JvmField var mWalkRecyclerView: RecyclerView? = null
    @BindView(R.id.walk_photos_title)
    @JvmField  var mWalkPhotosTitle: TextView? = null
    @BindView(R.id.walk_take_photo_button)
    @JvmField var mWalkTakePhotoButton: Button? = null
    private var walkPhotoListAdapter: DeletablePhotoListAdapter? = null
    private var mAllowNewPhotos: Boolean = false
    private var mLastPhotoUri: Uri? = null
    private var mWalkId: Long = 0
    var walkPhotos: List<WalkPhoto>? = null
        private set
    private var walkieViewModel: WalkieViewModel? = null

    fun setWalkId(mWalkId: Long) {
        this.mWalkId = mWalkId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mAllowNewPhotos = arguments!!.getBoolean(ALLOW_NEW_PHOTOS)
        }
        walkieViewModel = ViewModelProviders.of(activity!!).get(WalkieViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_walk_photos, container, false)
        ButterKnife.bind(this, view)
        mWalkPhotosTitle!!.text = getString(R.string.walk_photos_title)

        if (mAllowNewPhotos) {
            mWalkTakePhotoButton!!.text = getString(R.string.walk_take_photo)
            mWalkTakePhotoButton!!.setOnClickListener { clickView -> takePhoto() }
        } else {
            mWalkTakePhotoButton!!.visibility = View.GONE
        }

        val gridLayoutManager = GridLayoutManager(activity,
                3)
        mWalkRecyclerView!!.layoutManager = gridLayoutManager
        walkPhotoListAdapter = DeletablePhotoListAdapter()
        walkPhotoListAdapter!!.placeholderImage = requireNotNull(context?.getDrawable(R.drawable.ic_add_a_photo_grey_24dp))
        walkPhotoListAdapter!!.showPhotoLabels = false
        walkPhotoListAdapter!!.deleteHandler = { it -> this.onDeletePress(it) }
        mWalkRecyclerView!!.adapter = walkPhotoListAdapter
        walkieViewModel!!.getWalkPhotos(mWalkId).observe(this, Observer<List<WalkPhoto>> { this.showPhotos(requireNotNull(it)) })
        return view
    }


    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.e(TAG, "takePhoto: Unable to create the file " + ex.message)
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            mLastPhotoUri = FileProvider.getUriForFile(context!!,
                    "com.example.macarus0.walkiewalkie.fileprovider",
                    photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLastPhotoUri)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: Photo was received")
            val walkPhoto = WalkPhoto()
            walkPhoto.photoUri = mLastPhotoUri!!.toString()
            walkPhoto.walkId = mWalkId
            walkieViewModel!!.addPhotoToWalk(walkPhoto)
        }
    }

    private fun showPhotos(walkPhotos: List<WalkPhoto>) {
        this.walkPhotos = walkPhotos
        walkPhotoListAdapter!!.photos = walkPhotos
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )
    }

    private fun onDeletePress(id: Long) {
        walkieViewModel!!.deleteWalkPhoto(id)
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
    }

    companion object {

        internal const val REQUEST_IMAGE_CAPTURE = 1
        private const val TAG = "WalkPhotosFragment"
        private const val ALLOW_NEW_PHOTOS = "allow_new_photos"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param allowNewPhotos Whether this fragment should show new photos.
         * @return A new instance of fragment WalkPhotosFragment.
         */
        fun newInstance(allowNewPhotos: Boolean): WalkPhotosFragment {
            val fragment = WalkPhotosFragment()
            val args = Bundle()
            args.putBoolean(ALLOW_NEW_PHOTOS, allowNewPhotos)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
