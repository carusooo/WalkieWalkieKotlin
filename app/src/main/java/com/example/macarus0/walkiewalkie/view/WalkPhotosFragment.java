package com.example.macarus0.walkiewalkie.view;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.WalkPhoto;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkPhotosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalkPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkPhotosFragment extends Fragment {

    private static final String TAG = "WalkPhotosFragment";

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String ALLOW_NEW_PHOTOS = "allow_new_photos";
    @BindView(R.id.walk_photos_title)
    TextView mWalkPhotosTitle;
    @BindView(R.id.walk_take_photo_button)
    Button mWalkTakePhotoButton;
    @BindView(R.id.walk_photos_rv)
    public RecyclerView mWalkRecyclerView;
    private WalkPhotoListAdapter walkPhotoListAdapter;
    private boolean mAllowNewPhotos;
    private Uri mLastPhotoUri;
    private String mLastPhotoPath;
    private long mWalkId;

    public List<WalkPhoto> getWalkPhotos() {
        return mWalkPhotos;
    }

    private List<WalkPhoto> mWalkPhotos;
    private WalkieViewModel walkieViewModel;

    public WalkPhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param allowNewPhotos Whether this fragment should show new photos.
     * @return A new instance of fragment WalkPhotosFragment.
     */
    public static WalkPhotosFragment newInstance(boolean allowNewPhotos) {
        WalkPhotosFragment fragment = new WalkPhotosFragment();
        Bundle args = new Bundle();
        args.putBoolean(ALLOW_NEW_PHOTOS, allowNewPhotos);
        fragment.setArguments(args);
        return fragment;
    }

    public void setWalkId(long mWalkId) {
        this.mWalkId = mWalkId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAllowNewPhotos = getArguments().getBoolean(ALLOW_NEW_PHOTOS);
        }
        walkieViewModel = ViewModelProviders.of(getActivity()).get(WalkieViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_walk_photos, container, false);
        ButterKnife.bind(this, view);
        mWalkPhotosTitle.setText(getString(R.string.walk_photos_title));

        if (mAllowNewPhotos) {
            mWalkTakePhotoButton.setText(getString(R.string.walk_take_photo));
            mWalkTakePhotoButton.setOnClickListener(clickView -> takePhoto());
        } else {
            mWalkTakePhotoButton.setVisibility(View.GONE);
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                3);
        mWalkRecyclerView.setLayoutManager(gridLayoutManager);
        walkPhotoListAdapter = new WalkPhotoListAdapter();
        mWalkRecyclerView.setAdapter(walkPhotoListAdapter);
        walkieViewModel.getWalkPhotos(mWalkId).observe(this, this::showPhotos);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
            Log.e(TAG, "takePhoto: Unable to create the file "+ex.getMessage());
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            mLastPhotoUri = FileProvider.getUriForFile(getContext(),
                    "com.example.macarus0.walkiewalkie.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mLastPhotoUri);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i(TAG, "onActivityResult: Photo was received");
            WalkPhoto walkPhoto = new WalkPhoto();
            walkPhoto.setPhotoUri(mLastPhotoUri.toString());
            File photoFile = new File(mLastPhotoUri.toString());
            walkPhoto.setWalkId(mWalkId);
            walkieViewModel.addPhotoToWalk(walkPhoto);
        }
    }

    public void showPhotos(List<WalkPhoto> walkPhotos) {
        mWalkPhotos = walkPhotos;
        walkPhotoListAdapter.setPhotos(walkPhotos);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
