package com.example.macarus0.walkiewalkie.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DogListAdapter extends RecyclerView.Adapter<DogListAdapter.ViewHolder> {

    private List<Dog> mDogs;

    private static final String TAG = "DogListAdapter";

    public void setDogClickHandler(DogClickHandler mDogClickHandler) {
        this.mDogClickHandler = mDogClickHandler;
    }

    private DogClickHandler mDogClickHandler;

    public void setShowOwners(boolean mShowOwners) {
        this.mShowOwners = mShowOwners;
    }

    private boolean mShowOwners;

    public void setDogs(List<Dog> dogs) {
        mDogs = dogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dog_contact_list_item, parent ,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DogListAdapter.ViewHolder holder, int position) {
        Dog dog = this.mDogs.get(position);
        holder.dogName.setText(dog.getName());
        Picasso.get().load(dog.getPhotoUri()).placeholder(R.drawable.ic_default_dog_24dp).into(holder.dogImage);
        holder.dogId = dog.getId();
    }

    @Override
    public int getItemCount() {
        if(mDogs == null) {
            Log.i(TAG, "getItemCount: No Dogs Yet");
            return 0;
        }
        Log.i(TAG, "getItemCount: " + mDogs.size());
        return mDogs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        long dogId;

        @BindView(R.id.dog_image)
        ImageView dogImage;
        @BindView(R.id.owner1_image)
        ImageView owner1Image;
        @BindView(R.id.owner2_image)
        ImageView owner2Image;

        @BindView(R.id.dog_name)
        TextView dogName;
        @BindView(R.id.owner1_name)
        TextView owner1Name;
        @BindView(R.id.owner2_name)
        TextView owner2Name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            mDogClickHandler.dogClick(dogId);
        }
    }

    interface DogClickHandler {
        void dogClick(long id);
    }

}
