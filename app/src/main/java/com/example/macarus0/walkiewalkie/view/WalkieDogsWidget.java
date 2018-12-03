package com.example.macarus0.walkiewalkie.view;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.macarus0.walkiewalkie.DogWidgetService;
import com.example.macarus0.walkiewalkie.R;

/**
 * Implementation of App Widget functionality.
 */
public class WalkieDogsWidget extends AppWidgetProvider {

    private static final String TAG = "WalkieDogsWidget";

    long walkId;

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {
        RemoteViews views;
        if(walkId != 0) {
            // Construct the RemoteViews object
            Log.i(TAG, "updateAppWidget: Creating a widget for walk "+ walkId);
            views = getWalkRemoteViews(context, walkId);
        } else {
            Log.i(TAG, "updateAppWidget: Creating an empty widget");
            views = getEmptyRemoteViews(context);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    private RemoteViews getWalkRemoteViews(Context context, long walkId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.walkie_dogs_widget);
        views.setTextViewText(R.id.walk_widget_title, context.getText(R.string.widget_walk_title));
        Intent dogServiceIntent = new Intent(context, DogWidgetService.class);
        // Set data here so the intent isn't filtered
        dogServiceIntent.setData(Uri.fromParts("content", String.valueOf(walkId), null));
        dogServiceIntent.putExtra(DogWidgetService.WALK_ID, walkId);
        views.setRemoteAdapter(R.id.walk_widget_list, dogServiceIntent);
        return views;
    }

    private RemoteViews getEmptyRemoteViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.walkie_dogs_widget);
        views.setTextViewText(R.id.walk_widget_title, context.getText(R.string.widget_no_walk));
        return views;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        walkId = intent.getLongExtra(DogWidgetService.WALK_ID, 0);
        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

