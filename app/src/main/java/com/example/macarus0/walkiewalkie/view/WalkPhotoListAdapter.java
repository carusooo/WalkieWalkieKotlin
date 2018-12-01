package com.example.macarus0.walkiewalkie.view;

import android.net.Uri;
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
import com.example.macarus0.walkiewalkie.data.WalkPhoto;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalkPhotoListAdapter extends RecyclerView.Adapter<WalkPhotoListAdapter.ViewHolder> {

    private static final String TAG = "CheckedPhotoListAdapter";
    List<WalkPhoto> mPhotos;

    public void setPhotos(List<WalkPhoto> photos) {
        mPhotos = photos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WalkPhotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card, parent, false);
        return new WalkPhotoListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkPhotoListAdapter.ViewHolder holder, int position) {
        WalkPhoto walkPhoto = this.mPhotos.get(position);
        Log.i(TAG, "onBindViewHolder: Loading image "+walkPhoto.getPhotoUri());
        holder.walkImage.setImageURI(Uri.parse(walkPhoto.getPhotoUri()));
    }

    @Override
    public int getItemCount() {
        if (mPhotos == null) {
            Log.i(TAG, "getItemCount: No Photos Yet");
            return 0;
        }
        Log.i(TAG, "getItemCount: photoCount " + mPhotos.size());
        return mPhotos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_name)
        TextView tv;

        @BindView(R.id.contact_image)
        ImageView walkImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tv.setVisibility(View.INVISIBLE);
        }

    }

}
