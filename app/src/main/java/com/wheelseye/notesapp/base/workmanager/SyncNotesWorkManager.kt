package com.wheelseye.notesapp.base.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.workmanager.service.ISyncNotesEndPoint
import com.wheelseye.notesapp.db.entity.Note
import com.wheelseye.notesapp.db.repository.NotesRepository
import com.wheelseye.notesapp.login.model.service.IUserNotesEndPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncNotesWorkManager(mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {

    private val workManagerTag = "SyncDataWorkManager"

    override fun doWork(): Result {
        Log.d(workManagerTag, "doWork() called")

        val context = applicationContext

        val notesRepository = NotesRepository(context)

        val retrofit = Retrofit.Builder()
            .baseUrl(BaseActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val userID = context.getSharedPreferences(
            BaseActivity.USER_DETAILS_SHARED_PREF,
            Context.MODE_PRIVATE
        ).getLong(BaseActivity.USER_ID, -1)


//        Push all the rows with serverNotesID as -1.
//        pushAllNewNotes(retrofit, userID, notesRepository)


//        Sync up all the rows with isEdited as true.
//        updateAllEditedNotes(retrofit, userID, notesRepository)

//        Push all the IDs with isDeleted as true.
//        deleteAllDeletedNotes(retrofit, userID, notesRepository)


        Log.d(workManagerTag, "doWork() end")
        return Result.success()
    }

    private fun deleteAllDeletedNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(workManagerTag, "deleteAllDeletedNotes() called")
        val allDeletedNotes = notesRepository.getAllDeletedNotes() as ArrayList<NoteIdMapModel>

        if (!allDeletedNotes.isNullOrEmpty()) {
            Log.d(workManagerTag, "size of to-be-edited notes - ${allDeletedNotes.size}")

            val deletedNoteModel = AddUpdateNoteModelInput(userID, allDeletedNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val deletedNotesOutput = iSyncNotesEndPoint.deleteAllNotes(deletedNoteModel).execute()

            if (deletedNotesOutput.isSuccessful && deletedNotesOutput.body() != null && deletedNotesOutput.body()?.status == 200) {
                Log.d(workManagerTag, "iSyncNotesEndPoint.deleteAllNotes - Successful")
                val addedNotesList = deletedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(workManagerTag, "size of successfully deleted notes - ${it.size}")

                    for (i in 0 until it.size) {
                        notesRepository.updateDeletedNotes(it[i].appNotesID)
                    }
                }
            } else {
                Log.d(workManagerTag, "iSyncNotesEndPoint.deleteAllNotes - Unsuccessful")
            }
        }
    }

    private fun updateAllEditedNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(workManagerTag, "updateAllEditedNotes() called")
        val allEditedNotes = notesRepository.getAllEditedNotes() as ArrayList<Note>

        if (!allEditedNotes.isNullOrEmpty()) {
            Log.d(workManagerTag, "size of to-be-edited notes - ${allEditedNotes.size}")
            val addUpdateNoteModel = AddUpdateNoteModelInput(userID, allEditedNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val editedNotesOutput = iSyncNotesEndPoint.updateAllNotes(addUpdateNoteModel).execute()

            if (editedNotesOutput.isSuccessful && editedNotesOutput.body() != null && editedNotesOutput.body()?.status == 200) {
                Log.d(workManagerTag, "iSyncNotesEndPoint.updateAllNotes - Successful")
                val addedNotesList = editedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(workManagerTag, "size of successfully edited notes - ${it.size}")

                    for (i in 0 until it.size) {
                        notesRepository.updateEditedNotes(it[i].appNotesID, it[i].notesID)
                    }
                }
            } else {
                Log.d(workManagerTag, "iSyncNotesEndPoint.updateAllNotes - Unsuccessful")
            }
        }
        Log.d(workManagerTag, "updateAllEditedNotes() end")
    }

    private fun pushAllNewNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(workManagerTag, "pushAllNewNotes() called")

        val allNewNotes = notesRepository.getAllNewNotes() as ArrayList<Note>

        if (!allNewNotes.isNullOrEmpty()) {
            Log.d(workManagerTag, "size of to-be-added notes - ${allNewNotes.size}")
            val addUpdateNoteModel = AddUpdateNoteModelInput(userID, allNewNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val addedNotesOutput = iSyncNotesEndPoint.addAllNotes(addUpdateNoteModel).execute()

            if (addedNotesOutput.isSuccessful && addedNotesOutput.body() != null && addedNotesOutput.body()?.status == 200) {
                Log.d(workManagerTag, "iSyncNotesEndPoint.addAllNotes - Successful")
                val addedNotesList = addedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(workManagerTag, "size of successfully added notes - ${it.size}")

                    for (i in 0 until it.size) {
                        notesRepository.updateNewNotesServerNotesID(it[i].appNotesID, it[i].notesID)
                    }
                }
            } else {
                Log.d(workManagerTag, "iSyncNotesEndPoint.addAllNotes - Unsuccessful")
            }
        }

        Log.d(workManagerTag, "pushAllNewNotes() end")
    }


    override fun onStopped() {
        super.onStopped()
        Log.d(workManagerTag, "onStopped() called")
    }

    data class AddUpdateNoteModelInput<T>(
        @Expose
        @SerializedName("userID")
        val userID: Long?,

        @Expose
        @SerializedName("userNotes")
        val userNotes: T
    )

    data class AddUpdateNoteModelOutput(
        @Expose
        @SerializedName("userNotes")
        val userNotes: ArrayList<NoteIdMapModel>
    )

    data class NoteIdMapModel(
        @Expose
        @SerializedName("appNotesID")
        val appNotesID: Long,

        @Expose
        @SerializedName("notesID")
        val notesID: Long
    )
}