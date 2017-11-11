package com.wuruoye.note.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.wuruoye.note.R
import com.wuruoye.note.view.MainActivity

/**
 * Created by wuruoye on 2017/9/2.
 * this file is to do
 */

class NoteWidgetProvider : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == CLICK_ACTION){
            val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
            val manager = AppWidgetManager.getInstance(context)

        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        (0..appWidgetIds.size)
                .map { appWidgetIds[it] }
                .forEach { onWidgetUpdate(context, appWidgetManager, it) }
    }

    fun onWidgetUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int){
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_layout)
        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
        remoteViews.setOnClickPendingIntent(R.id.iv_widget_edit, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    companion object {
        val CLICK_ACTION = "com.wuruoye.note.action.CLICK"
    }
}
