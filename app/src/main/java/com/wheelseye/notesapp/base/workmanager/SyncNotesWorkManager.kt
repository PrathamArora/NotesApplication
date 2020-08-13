package com.wheelseye.notesapp.base.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.wheelseye.notesapp.base.activity.BaseActivity
import com.wheelseye.notesapp.base.activity.BaseActivity.Companion.WORK_MANAGER_TAG
import com.wheelseye.notesapp.base.workmanager.model.AddUpdateNoteModelInput
import com.wheelseye.notesapp.base.workmanager.model.NoteIdMapModel
import com.wheelseye.notesapp.base.workmanager.model.NoteServerModel
import com.wheelseye.notesapp.base.workmanager.service.ISyncNotesEndPoint
import com.wheelseye.notesapp.db.repository.NotesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SyncNotesWorkManager(mContext: Context, workerParameters: WorkerParameters) :
    Worker(mContext, workerParameters) {
    
    override fun doWork(): Result {
        Log.d(WORK_MANAGER_TAG, "doWork() called")

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
        pushAllNewNotes(retrofit, userID, notesRepository)


//        Sync up all the rows with isEdited as true.
        updateAllEditedNotes(retrofit, userID, notesRepository)

//        Push all the IDs with isDeleted as true.
        deleteAllDeletedNotes(retrofit, userID, notesRepository)


        Log.d(WORK_MANAGER_TAG, "doWork() end")
        return Result.success()
    }

    private fun deleteAllDeletedNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(WORK_MANAGER_TAG, "----------------------------------")
        Log.d(WORK_MANAGER_TAG, "deleteAllDeletedNotes() called")
        val allDeletedNotes = notesRepository.getAllDeletedNotes() as ArrayList<NoteIdMapModel>


        if (!allDeletedNotes.isNullOrEmpty()) {
            Log.d(WORK_MANAGER_TAG, "size of to-be-edited notes - ${allDeletedNotes.size}")
            Log.d(WORK_MANAGER_TAG, "userID - $userID")

            for (singleNote in allDeletedNotes) {
                Log.d(
                    WORK_MANAGER_TAG,
                    "note: " + singleNote.appNotesID.toString() + " " + singleNote.notesID
                )
            }


            val deletedNoteModel = AddUpdateNoteModelInput(userID, allDeletedNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val deletedNotesOutput = iSyncNotesEndPoint.deleteAllNotes(deletedNoteModel).execute()

            if (deletedNotesOutput.isSuccessful && deletedNotesOutput.body() != null && deletedNotesOutput.body()?.status == 200) {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.deleteAllNotes - Successful")
                val addedNotesList = deletedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(WORK_MANAGER_TAG, "size of successfully deleted notes - ${it.size}")

                    for (i in 0 until it.size) {
                        Log.d(
                            WORK_MANAGER_TAG,
                            "updated: appNotesID - ${it[i].appNotesID}, notesID - ${it[i].notesID}"
                        )
                        notesRepository.updateDeletedNotes(it[i].appNotesID)
                    }
                }
            } else {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.deleteAllNotes - Unsuccessful")
            }
        }
        Log.d(WORK_MANAGER_TAG, "deleteAllDeletedNotes() end")
    }

    private fun updateAllEditedNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(WORK_MANAGER_TAG, "----------------------------------")
        Log.d(WORK_MANAGER_TAG, "updateAllEditedNotes() called")
        val allEditedNotes = notesRepository.getAllEditedNotes() as ArrayList<NoteServerModel>

        if (!allEditedNotes.isNullOrEmpty()) {
            Log.d(WORK_MANAGER_TAG, "size of to-be-edited notes - ${allEditedNotes.size}")
            Log.d(WORK_MANAGER_TAG, "userID - $userID")

            for (singleNote in allEditedNotes) {
                Log.d(
                    WORK_MANAGER_TAG,
                    "note: " + singleNote.appNotesID.toString() + " " + singleNote.title
                )
            }

            val addUpdateNoteModel = AddUpdateNoteModelInput(userID, allEditedNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val editedNotesOutput = iSyncNotesEndPoint.updateAllNotes(addUpdateNoteModel).execute()

            if (editedNotesOutput.isSuccessful && editedNotesOutput.body() != null && editedNotesOutput.body()?.status == 200) {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.updateAllNotes - Successful")
                val addedNotesList = editedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(WORK_MANAGER_TAG, "size of successfully edited notes - ${it.size}")

                    for (i in 0 until it.size) {
                        Log.d(
                            WORK_MANAGER_TAG,
                            "updated: appNotesID - ${it[i].appNotesID}, notesID - ${it[i].notesID}"
                        )
                        notesRepository.updateEditedNotes(it[i].appNotesID, it[i].notesID)
                    }
                }
            } else {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.updateAllNotes - Unsuccessful")
            }
        }
        Log.d(WORK_MANAGER_TAG, "updateAllEditedNotes() end")
    }

    private fun pushAllNewNotes(
        retrofit: Retrofit,
        userID: Long,
        notesRepository: NotesRepository
    ) {
        Log.d(WORK_MANAGER_TAG, "----------------------------------")
        Log.d(WORK_MANAGER_TAG, "pushAllNewNotes() called")

        val allNewNotes = notesRepository.getAllNewNotes() as ArrayList<NoteServerModel>

        if (!allNewNotes.isNullOrEmpty()) {
            Log.d(WORK_MANAGER_TAG, "size of to-be-added notes - ${allNewNotes.size}")
            Log.d(WORK_MANAGER_TAG, "userID - $userID")

            for (singleNote in allNewNotes) {
                Log.d(
                    WORK_MANAGER_TAG,
                    "note: " + singleNote.appNotesID.toString() + " " + singleNote.title
                )
            }

            val addUpdateNoteModel = AddUpdateNoteModelInput(userID, allNewNotes)
            val iSyncNotesEndPoint = retrofit.create(ISyncNotesEndPoint::class.java)
            val addedNotesOutput = iSyncNotesEndPoint.addAllNotes(addUpdateNoteModel).execute()

            if (addedNotesOutput.isSuccessful && addedNotesOutput.body() != null && addedNotesOutput.body()?.status == 200) {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.addAllNotes - Successful")
                val addedNotesList = addedNotesOutput.body()?.data?.userNotes
                addedNotesList?.let {
                    Log.d(WORK_MANAGER_TAG, "size of successfully added notes - ${it.size}")

                    for (i in 0 until it.size) {
                        Log.d(
                            WORK_MANAGER_TAG,
                            "updated: appNotesID - ${it[i].appNotesID}, notesID - ${it[i].notesID}"
                        )
                        notesRepository.updateNewNotesServerNotesID(it[i].appNotesID, it[i].notesID)
                    }
                }
            } else {
                Log.d(WORK_MANAGER_TAG, "iSyncNotesEndPoint.addAllNotes - Unsuccessful")
            }
        }

        Log.d(WORK_MANAGER_TAG, "pushAllNewNotes() end")
    }


    override fun onStopped() {
        super.onStopped()
        Log.d(WORK_MANAGER_TAG, "onStopped() called")
    }

}