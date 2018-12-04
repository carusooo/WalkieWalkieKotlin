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
import com.example.macarus0.walkiewalkie.data.Walk;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.macarus0.walkiewalkie.util.TimeStampUtil.getStringDate;

public class WalkListAdapter extends RecyclerView.Adapter<WalkListAdapter.ViewHolder> {

    private List<Walk> mWalks;

    private static final String TAG = "WalkListAdapter";

    public void setWalks(List<Walk> walks) {
        this.mWalks = walks;
        notifyDataSetChanged();
    }

    public void setWalkClickHandler(WalkClickHandler walkClickHandler) {
        this.walkClickHandler = walkClickHandler;
    }

    private WalkClickHandler walkClickHandler;

    @NonNull
    @Override
    public WalkListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.walk_summary_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkListAdapter.ViewHolder holder, int position) {
        Walk walk = this.mWalks.get(position);
        holder.walkDate.setText(getStringDate(walk.getWalkDate()));
        holder.walkDuration.setText(walk.getWalkDuration());
        holder.walkId = walk.getWalkId();
    }

    @Override
    public int getItemCount() {
        if(mWalks == null){
            Log.i(TAG, "getItemCount: No Walks Yet");
            return 0;
        }
        return mWalks.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        long walkId;
        @BindView(R.id.walk_thumb)
        ImageView walkThumb;
        @BindView(R.id.walk_date)
        TextView walkDate;
        @BindView(R.id.walk_duration)
        TextView walkDuration;
        @BindView(R.id.walk_distance_text)
        TextView walkDistanceText;
        @BindView(R.id.walk_distance_label)
        TextView walkDistanceLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            walkClickHandler.walkClick(this.walkId);
        }
    }
        interface WalkClickHandler {
            void walkClick(long id);
        }

    }
