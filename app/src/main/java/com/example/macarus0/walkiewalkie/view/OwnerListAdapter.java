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
import com.example.macarus0.walkiewalkie.data.Owner;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OwnerListAdapter extends RecyclerView.Adapter<OwnerListAdapter.ViewHolder> {

    private List<Owner> mOwners;

    public void setOwnerClickHandler(OwnerClickHandler mOwnerClickHandler) {
        this.mOwnerClickHandler = mOwnerClickHandler;
    }

    private OwnerClickHandler mOwnerClickHandler;


    public void setOwners(List<Owner> owners) {
        mOwners = owners;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OwnerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.owner_contact_list_item, parent ,false);
        return new OwnerListAdapter.ViewHolder(v);    }

    @Override
    public void onBindViewHolder(@NonNull OwnerListAdapter.ViewHolder holder, int position) {
        Owner owner = this.mOwners.get(position);
        holder.ownerName.setText(owner.getFirstName() + " " + owner.getLastName());
        Picasso.get().load(owner.getPhoto()).placeholder(R.drawable.ic_default_owner_24dp).into(holder.ownerImage);
        holder.ownerId = owner.getOwnerId();
    }

    @Override
    public int getItemCount() {
        if(mOwners == null) {
            String TAG = "OwnerListAdapter";
            Log.i(TAG, "getItemCount: No Owners Yet");
            return 0;
        }
        return mOwners.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        long ownerId;

        @BindView(R.id.owner_image)
        ImageView ownerImage;
        @BindView(R.id.dog1_image)
        ImageView dog1image;
        @BindView(R.id.dog2_image)
        ImageView dog2Image;

        @BindView(R.id.owner_name)
        TextView ownerName;
        @BindView(R.id.dog1_name)
        TextView dog1Name;
        @BindView(R.id.dog2_name)
        TextView dog2Name;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            mOwnerClickHandler.ownerClick(ownerId);
        }
    }

    interface OwnerClickHandler {
        void ownerClick(long id);
    }


}
