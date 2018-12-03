package com.example.macarus0.walkiewalkie.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.Dog;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckedPhotoListAdapter extends RecyclerView.Adapter<CheckedPhotoListAdapter.ViewHolder> {

    private static final String TAG = "CheckedPhotoListAdapter";
    List<Dog> mDogs;
    boolean supportChecks = true;
    private DogCheckHandler mDogCheckHandler;

    public void setDogCheckHandler(DogCheckHandler mDogCheckHandler) {
        this.mDogCheckHandler = mDogCheckHandler;
    }

    public void setSupportChecks(boolean supportChecks) {
        this.supportChecks = supportChecks;
    }

    public void setDogs(List<Dog> dogs) {
        mDogs = dogs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckedPhotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card, parent, false);
        return new CheckedPhotoListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckedPhotoListAdapter.ViewHolder holder, int position) {
        Dog dog = this.mDogs.get(position);
        holder.dogName.setText(dog.getName());
        Picasso.get().load(dog.getPhoto()).placeholder(R.drawable.ic_default_dog_24dp).into(holder.dogImage);
        holder.dog = dog;
    }

    @Override
    public int getItemCount() {
        if (mDogs == null) {
            Log.i(TAG, "getItemCount: No Dogs Yet");
            return 0;
        }
        Log.i(TAG, "getItemCount: dogCount " + mDogs.size());

        return mDogs.size();
    }


    interface DogCheckHandler {
        void onDogCheckChanged(Dog dog, boolean isChecked);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.contact_image)
        ImageView dogImage;
        @BindView(R.id.contact_name)
        TextView dogName;
        @BindView(R.id.contact_select)
        AppCompatCheckBox dogCheckBox;
        Dog dog;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (supportChecks) {
                dogCheckBox.setVisibility(View.VISIBLE);
                itemView.setOnClickListener(this::onClick);
            }
        }

        @Override
        public void onClick(View view) {
            mDogCheckHandler.onDogCheckChanged(dog, !dogCheckBox.isChecked());
            dogCheckBox.setChecked(!dogCheckBox.isChecked());
        }
    }

}
