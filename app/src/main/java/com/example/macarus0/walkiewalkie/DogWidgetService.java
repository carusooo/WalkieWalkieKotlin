package com.example.macarus0.walkiewalkie;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.macarus0.walkiewalkie.data.Dog;
import com.example.macarus0.walkiewalkie.data.WalkieDatabase;
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider;

import java.util.List;

public class DogWidgetService extends RemoteViewsService {

    public static final String WALK_ID = "com.example.macarus0.walkiewalkie.widget.walk_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new DogRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}


class DogRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private final long mWalkId;
    private List<Dog> mDogs;
    private WalkieDatabase mDb;


    public DogRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mWalkId = intent.getLongExtra(DogWidgetService.WALK_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        mDb = WalkieDatabaseProvider.getDatabase(mContext.getApplicationContext());
    }

    @Override
    public void onDataSetChanged() {
        // Get the dog Dao here
        mDogs = mDb.getDogDao().getDogsOnWalk(mWalkId);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if(mDogs == null) return 0;
        return mDogs.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.dog_widget_card);
        rv.setTextViewText(R.id.dog_name, mDogs.get(position).getName());
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}