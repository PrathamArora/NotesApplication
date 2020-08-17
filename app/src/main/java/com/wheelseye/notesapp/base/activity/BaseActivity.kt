package com.wheelseye.notesapp.base.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import androidx.work.*
import com.wheelseye.notesapp.base.workmanager.SyncNotesWorkManager
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.login.view.LoginActivity
import com.wheelseye.notesapp.utility.NoteLabel
import java.util.concurrent.TimeUnit


abstract class BaseActivity : AppCompatActivity(),
    IViewInitializer {

    companion object {
        const val BASE_URL = "http://192.168.15.149:8080"

        const val DB_NAME = "user_notes.db"

        const val TAG = "NotesAppTag"

        const val SHOW_LOADER = true
        const val HIDE_LOADER = false

        const val USER_DETAILS_SHARED_PREF = "USER_DETAILS_SHARED_PREF"
        const val USER_ID = "USER_ID"
        const val USER_EMAIL_ID = "USER_EMAIL_ID"
        const val GUEST_USER = "Guest User"

        const val SINGLE_NOTE_KEY = "SINGLE_NOTE_KEY"

        const val WORK_MANAGER_TAG = "SyncDataWorkManager"

        const val PORTRAIT_NOTES = 2
        const val LANDSCAPE_NOTES = 3

        const val DATE_FORMAT = "dd/MM/yyyy"
        const val CHOOSE_LABEL_TAG = "Choose Label"
        const val LABEL_ALL = "All"
        const val LABEL_WORK = "Work"
        const val LABEL_SELF = "Self"
        const val LABEL_OTHER = "Other"

        const val LABEL_ALL_INT = 0
        const val LABEL_SELF_INT = 1
        const val LABEL_WORK_INT = 2
        const val LABEL_OTHER_INT = 3

        const val STRING_INITIALIZING = "Initializing..."
        const val STRING_ADD_NOTE = "Adding Note..."
        const val STRING_UPDATE_NOTE = "Updating Note..."
        const val STRING_DELETE_NOTE = "Deleting Note..."
        const val STRING_UPDATE_NOTE_TO = "Updating Label to "
        const val STRING_LOG_OUT = "Logging Out"
        const val STRING_CHECK_CREDENTIALS = "Checking Credentials"
        const val STRING_YES = "Yes"
        const val STRING_NO = "No"
        const val STRING_EMPTY = ""

        fun manageLabel(imageView: ImageView?, context: Context, note: Note) {
            val colorDrawablePair = NoteLabel.getColorAndDrawable(note.label)
            if (NoteLabel.outOfLimits(note.label)) {
                note.label = NoteLabel.getDefaultKey()
            }

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
            PeriodicWorkRequest.Builder(SyncNotesWorkManager::class.java, 15, TimeUnit.MINUTES)
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
                Log.d(WORK_MANAGER_TAG, it.toString())
            })
    }

    fun stopWorkManager(workManagerTag: String) {
        try {
            WorkManager.getInstance(applicationContext).cancelAllWorkByTag(workManagerTag)
            Log.d(WORK_MANAGER_TAG, "WorkManager Stopped.....!")
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

    fun checkForValidUser(context: Context) {
        val sharedPref = getSharedPreferences(USER_DETAILS_SHARED_PREF, Context.MODE_PRIVATE)
        if (!sharedPref.contains(USER_ID) || sharedPref.getLong(USER_ID, -1) == (-1).toLong()) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }
}