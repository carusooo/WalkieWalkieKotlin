package com.example.macarus0.walkiewalkie.view;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.data.PhotoItem;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeletablePhotoListAdapter extends RecyclerView.Adapter<DeletablePhotoListAdapter.ViewHolder> {

    private static final String TAG = "DeletablePhotoListAdapter";
    List<? extends PhotoItem> mPhotos;
    DeleteHandler mDeleteHandler = null;
    boolean showPhotoLabels = false;

    public void setPlaceholderImage(Drawable placeholderImage) {
        this.placeholderImage = placeholderImage;
    }

    Drawable placeholderImage;

    public void setDeleteHandler(DeleteHandler mDeleteHandler) {
        this.mDeleteHandler = mDeleteHandler;
    }

    public void setShowPhotoLabels(boolean showPhotoLabels) {
        this.showPhotoLabels = showPhotoLabels;
    }

    public void setPhotos(List<? extends PhotoItem> photos) {
        mPhotos = photos;
        Log.i(TAG, "setPhotos: Loaded photos " + photos.size());
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DeletablePhotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_card, parent, false);
        return new DeletablePhotoListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DeletablePhotoListAdapter.ViewHolder holder, int position) {
        PhotoItem photoItem = this.mPhotos.get(position);
        Log.i(TAG, "onBindViewHolder: Loading image " + photoItem.getPhotoUri());
        Picasso.get().load(photoItem.getPhotoUri()).placeholder(this.placeholderImage).into(holder.walkImage);
        if(showPhotoLabels) {
            holder.tv.setText(photoItem.getName());
        }
        holder.removeButton.setOnClickListener((View v) ->
                mDeleteHandler.onDeletePress(photoItem.getId()));

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

    interface DeleteHandler {
        void onDeletePress(long id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.contact_name)
        TextView tv;
        @BindView(R.id.item_remove)
        AppCompatImageButton removeButton;

        @BindView(R.id.contact_image)
        ImageView walkImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            removeButton.setVisibility(View.VISIBLE);

            if (!showPhotoLabels) {
                tv.setVisibility(View.INVISIBLE);
            }
        }

    }

}
