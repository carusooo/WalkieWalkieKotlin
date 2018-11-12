package com.example.macarus0.walkiewalkie.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Walk;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalkDogsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkDogsFragment extends Fragment {

    private static final String WALK_ID = "walk_dogs_fragment_walk_id";

    @BindView(R.id.walk_dogs_list_title)
    public TextView mDogsListTitleTextView;
    @BindView(R.id.walk_dogs_rv)
    public RecyclerView mDogRecyclerView;
    private long mWalkId;
    private WalkieViewModel walkieViewModel;
    private CheckedPhotoListAdapter dogPhotoListAdapter;
    public WalkDogsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WalkDogsFragment.
     */
    public static WalkDogsFragment newInstance() {
        WalkDogsFragment fragment = new WalkDogsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public void setWalkId(long mWalkId) {
        this.mWalkId = mWalkId;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(WALK_ID, mWalkId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        walkieViewModel = ViewModelProviders.of(getActivity()).get(WalkieViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(savedInstanceState != null){
            mWalkId = savedInstanceState.getLong(WALK_ID);
        }

        View view = inflater.inflate(R.layout.fragment_walk_dogs, container, false);
        ButterKnife.bind(this, view);
        mDogsListTitleTextView.setText(getActivity().getText(R.string.walk_dogs_title));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),
                getActivity().getResources().getInteger(R.integer.walk_status_dog_grid_columns));
        mDogRecyclerView.setLayoutManager(gridLayoutManager);
        dogPhotoListAdapter = new CheckedPhotoListAdapter();
        dogPhotoListAdapter.setSupportChecks(false);
        mDogRecyclerView.setAdapter(dogPhotoListAdapter);
        walkieViewModel.getWalkById(mWalkId).observe(this, this::showDogs);

        return view;
    }

    private void showDogs(Walk walk) {
        dogPhotoListAdapter.setDogs(walk.getDogs());
    }

}
