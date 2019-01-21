package com.example.macarus0.walkiewalkie

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.WalkieDatabase
import com.example.macarus0.walkiewalkie.data.WalkieDatabaseProvider

class DogWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsService.RemoteViewsFactory {
        return DogRemoteViewsFactory(this.applicationContext, intent)
    }

    companion object {

        const val WALK_ID = "com.example.macarus0.walkiewalkie.widget.walk_id"
    }
}


internal class DogRemoteViewsFactory(private val mContext: Context, intent: Intent) : RemoteViewsService.RemoteViewsFactory {
    private val mWalkId: Long
    private var mDogs: List<Dog>? = null
    private var mDb: WalkieDatabase? = null


    init {
        mWalkId = intent.getLongExtra(DogWidgetService.WALK_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID.toLong())
    }

    override fun onCreate() {
        mDb = WalkieDatabaseProvider.getDatabase(mContext.applicationContext)
    }

    override fun onDataSetChanged() {
        // Get the dog Dao here
        mDogs = mDb!!.dogDao.getDogsOnWalk(mWalkId)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int {
        return if (mDogs == null) 0 else mDogs!!.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.dog_widget_card)
        rv.setTextViewText(R.id.dogName, mDogs!![position].name)
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }
}