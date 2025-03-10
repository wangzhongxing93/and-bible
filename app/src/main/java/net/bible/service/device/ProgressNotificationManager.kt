/*
 * Copyright (c) 2020-2022 Martin Denham, Tuomas Airaksinen and the AndBible contributors.
 *
 * This file is part of AndBible: Bible Study (http://github.com/AndBible/and-bible).
 *
 * AndBible is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * AndBible is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with AndBible.
 * If not, see http://www.gnu.org/licenses/.
 */

package net.bible.service.device

import android.app.*
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.collection.ArraySet
import android.util.Log

import net.bible.android.BibleApplication
import net.bible.android.SharedConstants
import net.bible.android.activity.R
import net.bible.android.view.activity.download.ProgressStatus
import net.bible.service.common.CommonUtils

import org.apache.commons.lang3.StringUtils
import org.crosswire.common.progress.JobManager
import org.crosswire.common.progress.Progress
import org.crosswire.common.progress.WorkEvent
import org.crosswire.common.progress.WorkListener

/**Show all Progress status
 * see BibleDesktop JobsProgressBar for example use
 *
 * @author Martin Denham [mjdenham at gmail dot com]
 */

// only one instance initialised at startup to monitor for JSword Progress events and
// map them to Android Notifications

class ProgressNotificationManager {
    internal var progs: MutableSet<Progress> = ArraySet()
    private var workListener: WorkListener? = null

    // add it to the NotificationManager
    private lateinit var notificationManager: NotificationManager

    fun initialise() {
        Log.i(TAG, "Initializing")
        notificationManager = BibleApplication.application.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        val app = BibleApplication.application

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(PROGRESS_NOTIFICATION_CHANNEL,
                    app.getString(R.string.notification_channel_progress_status), NotificationManager.IMPORTANCE_LOW).apply {
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }


        workListener = object : WorkListener {

            override fun workProgressed(ev: WorkEvent) = synchronized(this) {
                val prog = ev.job
                val done = prog.work
                progs.add(prog)

                // updating notifications is really slow so we only update the notification manager every 5%
                // TODO is it still slow or was it only back in the days?
                if (prog.isFinished || done % 5 == 0) {
                    // compose a descriptive string showing job name and current section if relevant
                    var status = StringUtils.left(prog.jobName, 50) + SharedConstants.LINE_SEPARATOR
                    if (!StringUtils.isEmpty(prog.sectionName) && !prog.sectionName.equals(prog.jobName, ignoreCase = true)) {
                        status += prog.sectionName
                    }

                    buildNotification(prog)

                    if (prog.isFinished) {
                        finished(prog)
                    }
                }
            }

            override fun workStateChanged(ev: WorkEvent) {
                Log.i(TAG, "WorkState changed")
                // we don't care about these events
            }
        }
        JobManager.addWorkListener(workListener)

        Log.i(TAG, "Finished Initializing")
    }

    private fun finished(prog: Progress) {
        Log.i(TAG, "Finished")
        notificationManager.cancel(getNotificationId(prog.hashCode()))
        progs.remove(prog)
    }

    fun close() {
        Log.i(TAG, "Clearing Notifications")
        try {
            // clear map and all Notification objects
            for (prog in progs) {
                if (prog.isCancelable) {
                    Log.i(TAG, "Cancelling job")
                    prog.cancel()
                }
                finished(prog)
            }

            // de-register from notifications
            JobManager.removeWorkListener(workListener)
        } catch (e: Exception) {
            Log.e(TAG, "Error tidying up", e)
        }

    }

    private fun buildNotification(prog: Progress) {
        Log.i(TAG, "Creating Notification for progress Hash:" + prog.hashCode())
        val app = BibleApplication.application
        val intent = Intent(app, ProgressStatus::class.java)
        val pendingIntent = PendingIntent.getActivity(app, 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        val builder = NotificationCompat.Builder(app, PROGRESS_NOTIFICATION_CHANNEL)

        builder
            .setSmallIcon(R.drawable.ic_ichtys)
            .setContentTitle(prog.jobName)
            .setShowWhen(true)
            .setContentIntent(pendingIntent)
            .setProgress(100, prog.work, false)
            .setOngoing(true)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)

        if(CommonUtils.isDiscrete) {
            builder
                .setSmallIcon(R.drawable.ic_baseline_calculate_24)
                .setContentTitle(app.getString(R.string.progress_status))
            }

        val notification = builder.build()

        notificationManager.notify(getNotificationId(prog.hashCode()), notification)
    }

    private fun getNotificationId(hashCode: Int): Int {
        // Make some room for speak notification id (which is 1)
        var code = hashCode
        if(code > 0) {
            code += 1
        }
        return code
    }

    companion object {
        private const val TAG = "ProgressNotificatnMngr"

        const val PROGRESS_NOTIFICATION_CHANNEL="proggress-notifications"

        val instance = ProgressNotificationManager()
    }
}
