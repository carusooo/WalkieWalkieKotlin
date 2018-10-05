package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkPhotosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalkPhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkPhotosFragment extends Fragment {

    private static final String ALLOW_NEW_PHOTOS = "allow_new_photos";
    @BindView(R.id.walk_photos_title)
    TextView mWalkPhotosTitle;
    @BindView(R.id.walk_take_photo_button)
    Button mWalkTakePhotoButton;
    private boolean mAllowNewPhotos;
    private long mWalkId;
    private WalkieViewModel walkieViewModel;
    private OnFragmentInteractionListener mListener;

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
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void takePhoto() {


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
