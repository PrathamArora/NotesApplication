package com.wheelseye.notesapp.base.activity

import android.R.attr.tag
import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.google.common.util.concurrent.ListenableFuture
import com.wheelseye.notesapp.base.workmanager.SyncNotesWorkManager
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.utility.NoteLabel
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


abstract class BaseActivity : AppCompatActivity(),
    IViewInitializer {

    companion object {
//        const val BASE_URL = "https://demo1431226.mockable.io"
        const val BASE_URL = "http://192.168.15.149:8080"
        const val TAG = "NotesAppTag"

        const val SHOW_LOADER = true
        const val HIDE_LOADER = false

        const val USER_DETAILS_SHARED_PREF = "USER_DETAILS_SHARED_PREF"
        const val USER_ID = "USER_ID"
        const val USER_EMAIL_ID = "USER_EMAIL_ID"

        const val USER_NOTES_MODEL_KEY = "USER_NOTES_MODEL_KEY"
        const val SINGLE_NOTE_KEY = "SINGLE_NOTE_KEY"

        const val PORTRAIT_NOTES = 2
        const val LANDSCAPE_NOTES = 3

        const val DATE_FORMAT = "dd/MM/yyyy"

        fun manageLabel(imageView: ImageView?, context: Context, note: Note) {
            val colorDrawablePair = NoteLabel.getColorAndDrawable(note.label)
            if (NoteLabel.outOfLimits(note.label)) {
                note.label = NoteLabel.getDefaultKey()
            }

//            imageView?.setImageDrawable(
//                context.resources.getDrawable(
//                    colorDrawablePair.second,
//                    null
//                )
//            )

            imageView?.setImageDrawable(
                ResourcesCompat.getDrawable(context.resources, colorDrawablePair.second, null)
            )
        }
    }

    fun startWorkManager(workManagerTag: String) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicSyncWorkManager =
            PeriodicWorkRequest.Builder(SyncNotesWorkManager::class.java, 20, TimeUnit.SECONDS)
                .addTag(workManagerTag)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            workManagerTag,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncWorkManager
        )

        WorkManager.getInstance(applicationContext)
            .getWorkInfoByIdLiveData(periodicSyncWorkManager.id)
            .observe(this, Observer {
                Log.d("SyncDataWorkManager", it.toString())
            })
    }

    fun isWorkManagerScheduled(workManagerTag: String): Boolean {
        val instance = WorkManager.getInstance(applicationContext)
        val statuses: ListenableFuture<List<WorkInfo>> =
            instance.getWorkInfosByTag(workManagerTag)
        return try {
            var running = false
            val workInfoList: List<WorkInfo> = statuses.get()
            for (workInfo in workInfoList) {
                val state = workInfo.state
                running = (state == WorkInfo.State.RUNNING).or(state == WorkInfo.State.ENQUEUED)
            }
            running
        } catch (e: ExecutionException) {
            e.printStackTrace()
            false
        } catch (e: InterruptedException) {
            e.printStackTrace()
            false
        }
    }

    fun stopWorkManager(workManagerTag: String) {
        try {
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(workManagerTag)
        } catch (e: Exception) {
            Log.d(TAG, "Work Manager not found !")
        }
    }

    fun hideKeyboard() {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }
}