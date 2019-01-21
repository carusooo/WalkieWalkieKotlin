package com.example.macarus0.walkiewalkie.view

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews

import com.example.macarus0.walkiewalkie.DogWidgetService
import com.example.macarus0.walkiewalkie.R

/**
 * Implementation of App Widget functionality.
 */
class WalkieDogsWidget : AppWidgetProvider() {

    internal var walkId: Long = 0

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager,
                                appWidgetId: Int) {
        val views: RemoteViews
        if (walkId != 0L) {
            // Construct the RemoteViews object
            Log.i(TAG, "updateAppWidget: Creating a widget for walk $walkId")
            views = getWalkRemoteViews(context, walkId)
        } else {
            Log.i(TAG, "updateAppWidget: Creating an empty widget")
            views = getEmptyRemoteViews(context)
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)

    }

    private fun getWalkRemoteViews(context: Context, walkId: Long): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.walkie_dogs_widget)
        views.setTextViewText(R.id.walk_widget_title, context.getText(R.string.widget_walk_title))
        val dogServiceIntent = Intent(context, DogWidgetService::class.java)
        // Set data here so the intent isn't filtered
        dogServiceIntent.data = Uri.fromParts("content", walkId.toString(), null)
        dogServiceIntent.putExtra(DogWidgetService.WALK_ID, walkId)
        views.setRemoteAdapter(R.id.walk_widget_list, dogServiceIntent)
        return views
    }

    private fun getEmptyRemoteViews(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.walkie_dogs_widget)
        views.setTextViewText(R.id.walk_widget_title, context.getText(R.string.widget_no_walk))
        return views
    }

    override fun onReceive(context: Context, intent: Intent) {
        walkId = intent.getLongExtra(DogWidgetService.WALK_ID, 0)
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled

    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        private const val TAG = "WalkieDogsWidget"
    }
}

